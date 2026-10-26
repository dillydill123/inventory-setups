package inventorysetups;

import inventorysetups.ui.InventorySetupsPluginPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.PluginMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
	// Bumped when the contract below changes in a breaking way, or gains a message a sender needs to detect.
	// Shipped in setups-changed as data["version"], and in the get-setups reply.
	// 2: add-setup; get-setups puts data["version"].
	public static final int API_VERSION = 2;
	// out: broadcast when the setups change. data["setups"] = List<String> of names, data["version"] = int.
	public static final String API_MSG_SETUPS_CHANGED = "setups-changed";
	// in: list setups on demand (for plugins that start after us). Put a mutable Collection<String> under
	// "setups"; it is filled synchronously with the current setup names. When the data map itself is
	// mutable, data["version"] is set to API_VERSION too, so a sender can tell which messages exist.
	public static final String API_MSG_GET_SETUPS = "get-setups";
	// in: open a setup, filtering the bank like the worn items menu. data["setup"] = name.
	public static final String API_MSG_VIEW = "view";
	// in: clear the current setup (like worn items "Close current setup"). data["setup"] = name to clear
	// only when it is the active setup; omit to clear whatever is active.
	public static final String API_MSG_CLEAR = "clear";
	// out: broadcast when the active setup changes - on opening, closing and editing
	// of the setup that's still active (add/remove an item, toggle fuzzy, etc.)
	// data["activeSetup"] = the active setup's name, or "" when setup is closed.
	public static final String API_MSG_ACTIVE_SETUP_CHANGED = "active-setup-changed";
	// in: get the active setup's contents by slot, e.g. for a plugin that wants to mirror its layout elsewhere.
	// Put mutable Collection<Integer> under "equipmentItemIds" (EquipmentInventorySlot order, size 14),
	// "inventoryItemIds" (size 28) and "additionalItemIds" (no position semantics).
	// Empty slots are included with id -1 to avoid breaking proper order. Posting is synchronous.
	// data["activeSetup"] is set to the active setup's name (String) when one
	// is active and bank filtering is allowed, or "" otherwise.
	public static final String API_MSG_GET_ACTIVE_SETUP_CONTENTS = "get-active-setup-contents";
	// in (version 2): create a setup from item ids, e.g. a gear planner saving the set it recommends.
	// Required: data["setup"] = the requested name (String); "equipmentItemIds" = Collection<Integer>
	// (EquipmentInventorySlot order, at most 14, -1 for an empty slot); "inventoryItemIds" = Collection<Integer>
	// (at most 28, -1 empty). Missing trailing slots are empty.
	// Optional - each one left out takes the Add Setup button's config default, but nothing is read from the
	// player (a planned setup is not the player's current one): no spellbook and no attack option.
	//   "requestId" = any value, echoed in the add-setup-result broadcast
	//   "equipmentQuantities", "inventoryQuantities" = Collection<Integer> matching the ids (default 1)
	//   "additionalItemIds" + "additionalQuantities" = Collection<Integer>, the additional filtered items
	//   "runePouchItemIds" + "runePouchQuantities" = Collection<Integer>, the rune pouch's runes in slot order,
	//     when the inventory holds a rune pouch (normal or divine)
	//   "boltPouchItemIds" + "boltPouchQuantities" = Collection<Integer>, the bolt pouch's slots (at most 4:
	//     three bolt types and the extra ammo slot), when the inventory holds a bolt pouch
	//   "quiverItemId" + "quiverQuantity" = Integer, Dizana's quiver's ammo, when the setup holds the quiver
	//   "spellbook" = Integer: 0 Standard, 1 Ancient, 2 Lunar, 3 Arceuus, 4 none (default 4)
	//   "favorite", "filterBank", "highlightDifference", "unorderedHighlight", "fuzzy" = Boolean
	//   "highlightColor", "displayColor" = Integer RGB; "iconId" = Integer, an item id
	//   "section" = String, the section to put the setup in (created when absent)
	//   "notes", "attackOption" = String
	// Contents for a pouch or quiver the setup lacks are refused, but an empty list (or only -1) is accepted.
	// Quantities must be at least 1, and every id must be a real item. A request that breaks a rule saves
	// nothing. An existing setup is never replaced: the name gets " (2)", " (3)"... when taken.
	// The setup is created on the client thread, and an add-setup-result is always broadcast. Posted from the
	// client thread, the outcome is also in the data map when post() returns (if it is mutable): data["setup"]
	// holds the name actually saved, or data["error"] the reason it was refused.
	public static final String API_MSG_ADD_SETUP = "add-setup";
	// out (version 2): the outcome of an add-setup. data["requestId"] = the request's requestId (when it had one),
	// data["setup"] = the name saved (on failure, the name requested), data["error"] = String, only on failure.
	public static final String API_MSG_ADD_SETUP_RESULT = "add-setup-result";
	public static final String API_DATA_REQUEST_ID = "requestId";
	public static final String API_DATA_ERROR = "error";
	// Keys only add-setup reads. Package-private: other plugins post the strings, and only this package
	// (the handler and the plugin's request checks) needs the constants.
	static final String API_DATA_EQUIPMENT_QUANTITIES = "equipmentQuantities";
	static final String API_DATA_INVENTORY_QUANTITIES = "inventoryQuantities";
	static final String API_DATA_ADDITIONAL_QUANTITIES = "additionalQuantities";
	static final String API_DATA_RUNE_POUCH_ITEM_IDS = "runePouchItemIds";
	static final String API_DATA_RUNE_POUCH_QUANTITIES = "runePouchQuantities";
	static final String API_DATA_BOLT_POUCH_ITEM_IDS = "boltPouchItemIds";
	static final String API_DATA_BOLT_POUCH_QUANTITIES = "boltPouchQuantities";
	static final String API_DATA_QUIVER_ITEM_ID = "quiverItemId";
	static final String API_DATA_QUIVER_QUANTITY = "quiverQuantity";
	static final String API_DATA_SPELLBOOK = "spellbook";
	static final String API_DATA_FAVORITE = "favorite";
	static final String API_DATA_FILTER_BANK = "filterBank";
	static final String API_DATA_HIGHLIGHT_DIFFERENCE = "highlightDifference";
	static final String API_DATA_UNORDERED_HIGHLIGHT = "unorderedHighlight";
	static final String API_DATA_FUZZY = "fuzzy";
	static final String API_DATA_HIGHLIGHT_COLOR = "highlightColor";
	static final String API_DATA_DISPLAY_COLOR = "displayColor";
	static final String API_DATA_ICON_ID = "iconId";
	static final String API_DATA_SECTION = "section";
	static final String API_DATA_NOTES = "notes";
	static final String API_DATA_ATTACK_OPTION = "attackOption";
	public static final String API_DATA_SETUPS = "setups";
	public static final String API_DATA_SETUP = "setup";
	public static final String API_DATA_VERSION = "version";
	public static final String API_DATA_ACTIVE_SETUP = "activeSetup";
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
			final InventorySetup currentSetup = panel.getCurrentSelectedSetup();
			final Map<String, Object> data = Map.of(API_DATA_ACTIVE_SETUP, currentSetup == null ? "" : currentSetup.getName());
			eventBus.post(new PluginMessage(API_NAMESPACE, API_MSG_ACTIVE_SETUP_CHANGED, data));
		});
	}

	public void handleMessage(final PluginMessage message)
	{
		if (!API_NAMESPACE.equals(message.getNamespace()))
		{
			return;
		}
		if (API_MSG_SETUPS_CHANGED.equals(message.getName()) || API_MSG_ACTIVE_SETUP_CHANGED.equals(message.getName())
			|| API_MSG_ADD_SETUP_RESULT.equals(message.getName()))
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
			case API_MSG_ADD_SETUP:
			{
				handleAddSetup(message);
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
		try
		{
			message.getData().put(API_DATA_VERSION, API_VERSION);
		}
		catch (UnsupportedOperationException e)
		{
			// An immutable data map (Map.of): the caller did not ask for the version.
		}
	}

	private void handleAddSetup(final PluginMessage message)
	{
		final Map<String, Object> data = message.getData();
		final Object requestId = data.get(API_DATA_REQUEST_ID);
		final Object nameObj = data.get(API_DATA_SETUP);
		// The posted map is only written while post() is still running on the client thread; a later write from
		// the client thread could race the sender reading it.
		final boolean replyInMap = plugin.getClient().isClientThread();
		final InventorySetupsAddSetupRequest request;
		try
		{
			request = parseAddSetup(data);
		}
		catch (IllegalArgumentException e)
		{
			finishAddSetup(replyInMap ? data : null, requestId, nameObj instanceof String ? (String) nameObj : "",
				null, e.getMessage());
			return;
		}

		final Runnable add = () ->
		{
			String saved = null;
			String error = null;
			try
			{
				saved = plugin.addInventorySetup(request);
			}
			catch (IllegalArgumentException e)
			{
				error = e.getMessage();
			}
			finishAddSetup(replyInMap ? data : null, requestId, request.getName(), saved, error);
		};
		if (replyInMap)
		{
			add.run();
		}
		else
		{
			clientThread.invokeLater(add);
		}
	}

	// Broadcasts add-setup-result, and replies in the posted map when one is given (and it is mutable).
	private void finishAddSetup(final Map<String, Object> data, final Object requestId, final String requested,
		final String saved, final String error)
	{
		if (error != null)
		{
			log.warn("Ignoring {} message: {}", API_MSG_ADD_SETUP, error);
		}
		if (data != null)
		{
			try
			{
				data.put(error == null ? API_DATA_SETUP : API_DATA_ERROR, error == null ? saved : error);
			}
			catch (UnsupportedOperationException e)
			{
				// An immutable data map: the caller reads the result broadcast instead.
			}
		}

		final Map<String, Object> result = new HashMap<>();
		if (requestId != null)
		{
			result.put(API_DATA_REQUEST_ID, requestId);
		}
		result.put(API_DATA_SETUP, error == null ? saved : requested);
		if (error != null)
		{
			result.put(API_DATA_ERROR, error);
		}
		eventBus.post(new PluginMessage(API_NAMESPACE, API_MSG_ADD_SETUP_RESULT, result));
	}

	// Reads an add-setup payload into a request, checking only the type under each key; throws
	// IllegalArgumentException naming the first key of the wrong type. Every other rule is checked by the plugin
	// on the client thread (InventorySetupsPlugin#checkAddSetupRequest).
	static InventorySetupsAddSetupRequest parseAddSetup(final Map<String, Object> data)
	{
		return InventorySetupsAddSetupRequest.builder()
			.name(string(data, API_DATA_SETUP))
			.equipmentIds(integers(data, API_DATA_EQUIPMENT_ITEM_IDS))
			.equipmentQuantities(integers(data, API_DATA_EQUIPMENT_QUANTITIES))
			.inventoryIds(integers(data, API_DATA_INVENTORY_ITEM_IDS))
			.inventoryQuantities(integers(data, API_DATA_INVENTORY_QUANTITIES))
			.additionalIds(integers(data, API_DATA_ADDITIONAL_ITEM_IDS))
			.additionalQuantities(integers(data, API_DATA_ADDITIONAL_QUANTITIES))
			.runePouchIds(integers(data, API_DATA_RUNE_POUCH_ITEM_IDS))
			.runePouchQuantities(integers(data, API_DATA_RUNE_POUCH_QUANTITIES))
			.boltPouchIds(integers(data, API_DATA_BOLT_POUCH_ITEM_IDS))
			.boltPouchQuantities(integers(data, API_DATA_BOLT_POUCH_QUANTITIES))
			.quiverItemId(integer(data, API_DATA_QUIVER_ITEM_ID))
			.quiverQuantity(integer(data, API_DATA_QUIVER_QUANTITY))
			.notes(string(data, API_DATA_NOTES))
			.section(string(data, API_DATA_SECTION))
			.attackOption(string(data, API_DATA_ATTACK_OPTION))
			.spellbook(integer(data, API_DATA_SPELLBOOK))
			.iconId(integer(data, API_DATA_ICON_ID))
			.highlightColor(integer(data, API_DATA_HIGHLIGHT_COLOR))
			.displayColor(integer(data, API_DATA_DISPLAY_COLOR))
			.favorite(bool(data, API_DATA_FAVORITE))
			.filterBank(bool(data, API_DATA_FILTER_BANK))
			.highlightDifference(bool(data, API_DATA_HIGHLIGHT_DIFFERENCE))
			.unorderedHighlight(bool(data, API_DATA_UNORDERED_HIGHLIGHT))
			.fuzzy(bool(data, API_DATA_FUZZY))
			.build();
	}

	private static List<Integer> integers(final Map<String, Object> data, final String key)
	{
		final Object obj = data.get(key);
		if (obj == null)
		{
			return null;
		}
		if (!(obj instanceof Collection))
		{
			throw new IllegalArgumentException("'" + key + "' must be a Collection<Integer>");
		}
		final List<Integer> values = new ArrayList<>();
		for (final Object element : (Collection<?>) obj)
		{
			if (!(element instanceof Integer))
			{
				throw new IllegalArgumentException("'" + key + "' must be a Collection<Integer>");
			}
			values.add((Integer) element);
		}
		return values;
	}

	private static Integer integer(final Map<String, Object> data, final String key)
	{
		final Object obj = data.get(key);
		if (obj != null && !(obj instanceof Integer))
		{
			throw new IllegalArgumentException("'" + key + "' must be an Integer");
		}
		return (Integer) obj;
	}

	private static Boolean bool(final Map<String, Object> data, final String key)
	{
		final Object obj = data.get(key);
		if (obj != null && !(obj instanceof Boolean))
		{
			throw new IllegalArgumentException("'" + key + "' must be a Boolean");
		}
		return (Boolean) obj;
	}

	private static String string(final Map<String, Object> data, final String key)
	{
		final Object obj = data.get(key);
		if (obj != null && !(obj instanceof String))
		{
			throw new IllegalArgumentException("'" + key + "' must be a String");
		}
		return (String) obj;
	}

	// The requested name, cut to the maximum length, made unique against the taken names with
	// " (2)", " (3)"... (the base is shortened so the suffix still fits).
	static String uniqueName(final String requested, final Collection<String> taken, final int maxLength)
	{
		final String base = requested.trim().length() > maxLength ? requested.trim().substring(0, maxLength) : requested.trim();
		if (!taken.contains(base))
		{
			return base;
		}
		for (int n = 2; ; n++)
		{
			final String suffix = " (" + n + ")";
			final String head = base.length() + suffix.length() > maxLength ? base.substring(0, maxLength - suffix.length()) : base;
			if (!taken.contains(head + suffix))
			{
				return head + suffix;
			}
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
			message.getData().put(API_DATA_ACTIVE_SETUP, hasActiveSetup ? setup.getName() : "");
			if (!hasActiveSetup)
			{
				return;
			}

			addItemIds(equipmentObj, setup.getEquipment());
			addItemIds(inventoryObj, setup.getInventory());
			addItemIds(additionalObj, setup.getAdditionalFilteredItems().values());
		});
	}

	private static void addItemIds(final Object obj, final Collection<InventorySetupsItem> items)
	{
		@SuppressWarnings("unchecked")
		final Collection<Integer> ids = (Collection<Integer>) obj;

		items.stream()
			.map(InventorySetupsItem::getId)
			.forEach(ids::add);
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
