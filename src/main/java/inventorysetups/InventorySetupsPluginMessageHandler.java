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
	public static final String API_DATA_SETUPS = "setups";
	public static final String API_DATA_SETUP = "setup";
	public static final String API_DATA_VERSION = "version";

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

	public void handleMessage(final PluginMessage message)
	{
		if (!API_NAMESPACE.equals(message.getNamespace()))
		{
			return;
		}
		if (API_MSG_SETUPS_CHANGED.equals(message.getName()))
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
			default:
			{
				log.debug("Ignoring unsupported message '{}' in the {} namespace", message.getName(), API_NAMESPACE);
				break;
			}
		}
	}

	private void handleGetSetups(final PluginMessage message)
	{
		final Object container = message.getData().get(API_DATA_SETUPS);
		if (container instanceof Collection)
		{
			// eventBus.post is synchronous, so the caller's collection is filled before its own
			// post() call returns.
			//noinspection unchecked
			((Collection<String>) container).addAll(setupNamesSnapshot);
		}
	}

	private void handleView(final PluginMessage message)
	{
		final Object nameObj = message.getData().get(API_DATA_SETUP);
		if (!(nameObj instanceof String))
		{
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
				log.debug("Ignoring view request for unknown setup '{}'", targetName);
				return;
			}
			panel.setCurrentInventorySetup(target, true);
		});
	}

	private void handleClear(final PluginMessage message)
	{
		final Object nameObj = message.getData().get(API_DATA_SETUP);
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
