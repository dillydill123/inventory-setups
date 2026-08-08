package inventorysetups;

import inventorysetups.ui.InventorySetupsPluginPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.PluginMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

// PluginMessage API for other plugins to list and open/clear inventory setups. See #415.
//
// To use this from another plugin, post a PluginMessage on the EventBus with the "inventory-setups"
// namespace, e.g.:
//
//   Map<String, Object> data = new HashMap<>();
//   data.put("setup", "Vorkath");
//   eventBus.post(new PluginMessage("inventory-setups", "view", data));
//
// Supported messages are documented on the constants below. To track the available setups, subscribe
// to PluginMessage and listen for "setups-changed" broadcasts, and post "get-setups" once on startup
// (posting is synchronous, so the collection you pass is filled before post() returns). To track the
// active setup (including live edits to it), listen for "active-setup-changed" broadcasts and post
// "get-active-setup-contents" whenever you need its current contents. Payload values are plain JDK
// types (String, int, boolean, Collection<Integer>/<String>) so they are visible across plugin
// classloaders.
@Slf4j
public class InventorySetupsPluginMessageHandler
{
	public static final String API_NAMESPACE = "inventory-setups";
	// Bumped when the contract below changes in a breaking way. Shipped in setups-changed as data["version"].
	public static final int API_VERSION = 1;
	// out: broadcast when the setups change. data["setups"] = List<String> of names, data["version"] = int.
	public static final String API_MSG_SETUPS_CHANGED = "setups-changed";
	// in: list setups on demand (for plugins that start after us). Put a mutable Collection<String> under
	// "setups"; it is filled synchronously with the current setup names.
	public static final String API_MSG_GET_SETUPS = "get-setups";
	// in: open a setup, filtering the bank like the worn items menu. data["setup"] = name.
	public static final String API_MSG_VIEW = "view";
	// in: clear the current setup (like worn items "Close current setup"). data["setup"] = name to clear
	// only when it is the active setup; omit to clear whatever is active.
	public static final String API_MSG_CLEAR = "clear";
	// out: broadcast when the active setup changes - on opening, closing and editing
	// of the setup that's still active (add/remove an item, toggle fuzzy, etc.)
	// data["setup"] = the active setup's name; the key is absent when setup is closed.
	public static final String API_MSG_ACTIVE_SETUP_CHANGED = "active-setup-changed";
	// in: get the active setup's contents by slot, e.g. for a plugin that wants to mirror its layout elsewhere.
	// Put mutable Collection<Integer> under "equipmentItemIds" (EquipmentInventorySlot order,
	// size 14), "inventoryItemIds" (size 28), and "additionalItemIds" (no position semantics). Posting is synchronous.
	// data["hasActiveSetup"] is set to false when no setup is active or the active setup has bank filtering disabled.
	public static final String API_MSG_GET_ACTIVE_SETUP_CONTENTS = "get-active-setup-contents";
	public static final String API_DATA_SETUPS = "setups";
	public static final String API_DATA_SETUP = "setup";
	public static final String API_DATA_VERSION = "version";
	public static final String API_DATA_HAS_ACTIVE_SETUP = "hasActiveSetup";
	public static final String API_DATA_EQUIPMENT_ITEM_IDS = "equipmentItemIds";
	public static final String API_DATA_INVENTORY_ITEM_IDS = "inventoryItemIds";
	public static final String API_DATA_ADDITIONAL_ITEM_IDS = "additionalItemIds";

	private final InventorySetupsPlugin plugin;
	private final ClientThread clientThread;
	private final EventBus eventBus;
	private final InventorySetupsPluginPanel panel;

	// Immutable snapshot of the setup names, republished on every change. Lets get-setups answer from any
	// thread without touching the live list. Only written on the client thread (see broadcastSetupsChanged).
	private volatile List<String> setupNamesSnapshot = List.of();

	public InventorySetupsPluginMessageHandler(InventorySetupsPlugin plugin, ClientThread clientThread,
												EventBus eventBus, InventorySetupsPluginPanel panel)
	{
		this.plugin = plugin;
		this.clientThread = clientThread;
		this.eventBus = eventBus;
		this.panel = panel;
	}

	// Refresh the snapshot and notify listeners. Serialized onto the client thread because setups are
	// mutated from both the client thread and the Swing EDT. Skips the post when the name list is unchanged,
	// since updateConfig also fires on slot and note edits.
	public void broadcastSetupsChanged()
	{
		clientThread.invoke(() ->
		{
			final List<String> names = buildSetupNames();
			if (names.equals(setupNamesSnapshot))
			{
				return;
			}
			setupNamesSnapshot = names;
			eventBus.post(new PluginMessage(API_NAMESPACE, API_MSG_SETUPS_CHANGED,
				Map.of(API_DATA_SETUPS, names, API_DATA_VERSION, API_VERSION)));
		});
	}

	// Broadcasts the active setup's name, or clears it. Called on selection change and on every content
	// edit of the setup that's still active. Deliberately not deduped like broadcastSetupsChanged() -
	// content edits don't change the name, but callers still need to know to re-read the contents.
	public void broadcastActiveSetupChanged()
	{
		clientThread.invoke(() ->
		{
			final InventorySetup active = panel.getCurrentSelectedSetup();
			final Map<String, Object> data = active == null ? Map.of() : Map.of(API_DATA_SETUP, active.getName());
			eventBus.post(new PluginMessage(API_NAMESPACE, API_MSG_ACTIVE_SETUP_CHANGED, data));
		});
	}

	public void handleMessage(final PluginMessage message)
	{
		if (!API_NAMESPACE.equals(message.getNamespace()))
		{
			return;
		}
		if (API_MSG_SETUPS_CHANGED.equals(message.getName()) || API_MSG_ACTIVE_SETUP_CHANGED.equals(message.getName()))
		{
			// Our own outgoing broadcast.
			return;
		}

		switch (message.getName())
		{
			case API_MSG_GET_SETUPS:
			{
				handleGetSetups(message);
				break;
			}
			case API_MSG_VIEW:
			{
				handleView(message);
				break;
			}
			case API_MSG_CLEAR:
			{
				handleClear(message);
				break;
			}
			case API_MSG_GET_ACTIVE_SETUP_CONTENTS:
			{
				handleGetActiveSetupContents(message);
				break;
			}
			default:
			{
				log.warn("Ignoring unsupported message '{}' in the {} namespace", message.getName(), API_NAMESPACE);
				break;
			}
		}
	}

	private void handleGetSetups(final PluginMessage message)
	{
		final Object container = message.getData().getOrDefault(API_DATA_SETUPS, null);
		if (container instanceof Collection)
		{
			// eventBus.post is synchronous, so the caller's collection is filled before its own
			// post() call returns.
			//noinspection unchecked
			((Collection<String>) container).addAll(setupNamesSnapshot);
		}
		else
		{
			log.warn("Ignoring {} message without a Collection under '{}'", API_MSG_GET_SETUPS, API_DATA_SETUPS);
		}
	}

	private void handleView(final PluginMessage message)
	{
		final Object nameObj = message.getData().getOrDefault(API_DATA_SETUP, null);
		if (!(nameObj instanceof String))
		{
			log.warn("Ignoring {} message without a String under '{}'", API_MSG_VIEW, API_DATA_SETUP);
			return;
		}
		final String targetName = (String) nameObj;
		// Resolve and apply on the client thread, where the setups are otherwise accessed.
		clientThread.invoke(() ->
		{
			final InventorySetup target = plugin.getInventorySetups().stream()
				.filter(setup -> setup.getName().equals(targetName))
				.findFirst()
				.orElse(null);
			if (target == null)
			{
				log.warn("Ignoring view request for unknown setup '{}'", targetName);
				return;
			}
			panel.setCurrentInventorySetup(target, true);
		});
	}

	private void handleClear(final PluginMessage message)
	{
		final Object nameObj = message.getData().getOrDefault(API_DATA_SETUP, null);
		if (nameObj != null && !(nameObj instanceof String))
		{
			log.warn("Ignoring {} message with a non-String value under '{}'", API_MSG_CLEAR, API_DATA_SETUP);
			return;
		}
		clientThread.invoke(() ->
		{
			final InventorySetup current = panel.getCurrentSelectedSetup();
			if (current == null)
			{
				return;
			}
			if (nameObj == null)
			{
				// No name given: clear whatever setup is active.
				panel.returnToOverviewPanel(false);
				return;
			}
			// A name was given: only clear when it is the setup currently shown, so a caller never
			// closes a setup the user switched to themselves.
			if (current.getName().equals(nameObj))
			{
				panel.returnToOverviewPanel(false);
			}
		});
	}

	private void handleGetActiveSetupContents(final PluginMessage message)
	{
		final Object equipmentObj = message.getData().getOrDefault(API_DATA_EQUIPMENT_ITEM_IDS, null);
		final Object inventoryObj = message.getData().getOrDefault(API_DATA_INVENTORY_ITEM_IDS, null);
		final Object additionalObj = message.getData().getOrDefault(API_DATA_ADDITIONAL_ITEM_IDS, null);
		if (!(equipmentObj instanceof Collection) || !(inventoryObj instanceof Collection) || !(additionalObj instanceof Collection))
		{
			log.warn("Ignoring {} message without the expected mutable collections", API_MSG_GET_ACTIVE_SETUP_CONTENTS);
			return;
		}

		clientThread.invoke(() ->
		{
			final InventorySetup setup = panel.getCurrentSelectedSetup();
			final boolean hasActiveSetup = setup != null && setup.isFilterBank() && plugin.isFilteringAllowed();
			message.getData().put(API_DATA_HAS_ACTIVE_SETUP, hasActiveSetup);
			if (!hasActiveSetup)
			{
				return;
			}

			final Collection<Integer> equipmentItemIds = asIntegerCollection(equipmentObj);
			final Collection<Integer> inventoryItemIds = asIntegerCollection(inventoryObj);
			final Collection<Integer> additionalItemIds = asIntegerCollection(additionalObj);

			for (final InventorySetupsItem item : setup.getEquipment())
			{
				equipmentItemIds.add(InventorySetupsItem.itemIsDummy(item) ? -1 : item.getId());
			}
			for (final InventorySetupsItem item : setup.getInventory())
			{
				inventoryItemIds.add(InventorySetupsItem.itemIsDummy(item) ? -1 : item.getId());
			}
			for (final InventorySetupsItem item : setup.getAdditionalFilteredItems().values())
			{
				additionalItemIds.add(item.getId());
			}
		});
	}

	@SuppressWarnings("unchecked")
	private static Collection<Integer> asIntegerCollection(final Object obj)
	{
		return (Collection<Integer>) obj;
	}

	private List<String> buildSetupNames()
	{
		final List<String> names = new ArrayList<>(plugin.getInventorySetups().size());
		for (final InventorySetup setup : plugin.getInventorySetups())
		{
			names.add(setup.getName());
		}
		return List.copyOf(names);
	}
}
