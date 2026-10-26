package inventorysetups;

import inventorysetups.ui.InventorySetupsPluginPanel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.PluginMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InventorySetupsPluginMessageHandlerTest
{
	@Mock
	private InventorySetupsPlugin plugin;

	@Mock
	private ClientThread clientThread;

	@Mock
	private EventBus eventBus;

	@Mock
	private InventorySetupsPluginPanel panel;

	@Mock
	private Client client;

	private InventorySetupsPluginMessageHandler handler;

	@Before
	public void before()
	{
		handler = new InventorySetupsPluginMessageHandler(plugin, clientThread, eventBus, panel);
		lenient().when(plugin.getClient()).thenReturn(client);
		lenient().when(client.isClientThread()).thenReturn(true);
	}

	// Posting from another thread: the work is queued for the client thread, run here at once.
	private void postFromAnotherThread()
	{
		when(client.isClientThread()).thenReturn(false);
		doAnswer(invocation ->
		{
			((Runnable) invocation.getArgument(0)).run();
			return null;
		}).when(clientThread).invokeLater(any(Runnable.class));
	}

	private static List<Integer> slots(final int size, final int... leading)
	{
		final List<Integer> ids = new ArrayList<>(Collections.nCopies(size, -1));
		for (int i = 0; i < leading.length; i++)
		{
			ids.set(i, leading[i]);
		}
		return ids;
	}

	@Test
	public void testUniqueNameKeepsAFreeName()
	{
		assertEquals("Zulrah - Magic",
			InventorySetupsPluginMessageHandler.uniqueName("Zulrah - Magic", Collections.emptyList(), 50));
	}

	@Test
	public void testUniqueNameNumbersATakenName()
	{
		assertEquals("Zulrah - Magic (2)",
			InventorySetupsPluginMessageHandler.uniqueName("Zulrah - Magic", Arrays.asList("Zulrah - Magic"), 50));
		assertEquals("Zulrah - Magic (3)",
			InventorySetupsPluginMessageHandler.uniqueName("Zulrah - Magic",
				Arrays.asList("Zulrah - Magic", "Zulrah - Magic (2)"), 50));
	}

	@Test
	public void testUniqueNameFitsTheSuffixInTheMaximumLength()
	{
		final String requested = "Abcdefghij";
		assertEquals("Abcdefg (2)",
			InventorySetupsPluginMessageHandler.uniqueName(requested, Arrays.asList("Abcdefghij"), 11));
		assertEquals("Abcdefghij",
			InventorySetupsPluginMessageHandler.uniqueName(requested + "klm", Collections.emptyList(), 10));
	}

	private static Map<String, Object> minimalAddSetup(final String name)
	{
		final Map<String, Object> data = new HashMap<>();
		data.put("setup", name);
		data.put("equipmentItemIds", slots(14));
		data.put("inventoryItemIds", slots(28, 385));
		return data;
	}

	// The add-setup-result broadcast, or null when none was posted.
	private Map<String, Object> addSetupResult()
	{
		final ArgumentCaptor<PluginMessage> captor = ArgumentCaptor.forClass(PluginMessage.class);
		verify(eventBus).post(captor.capture());
		assertEquals("inventory-setups", captor.getValue().getNamespace());
		assertEquals("add-setup-result", captor.getValue().getName());
		return captor.getValue().getData();
	}

	@Test
	public void testAddSetupPassesEveryFieldToThePluginAndReportsTheSavedName()
	{
		when(plugin.addInventorySetup(any(InventorySetupsAddSetupRequest.class))).thenReturn("Zulrah - Magic (2)");
		final Map<String, Object> data = new HashMap<>();
		data.put("requestId", 7);
		data.put("setup", "Zulrah - Magic");
		data.put("equipmentItemIds", slots(14, 12931, 21795));
		data.put("equipmentQuantities", slots(14, 1, 1));
		data.put("inventoryItemIds", slots(28, 385, 12791, 9433));
		data.put("inventoryQuantities", slots(28, 4, 1, 1));
		data.put("additionalItemIds", Arrays.asList(9763, 9790));
		data.put("additionalQuantities", Arrays.asList(1, 1));
		data.put("runePouchItemIds", Arrays.asList(560, 565, 566));
		data.put("runePouchQuantities", Arrays.asList(100, 200, 300));
		data.put("boltPouchItemIds", Arrays.asList(9244, 9243));
		data.put("boltPouchQuantities", Arrays.asList(500, 400));
		data.put("quiverItemId", 11212);
		data.put("quiverQuantity", 1000);
		data.put("spellbook", 3);
		data.put("favorite", true);
		data.put("filterBank", false);
		data.put("highlightDifference", true);
		data.put("unorderedHighlight", true);
		data.put("fuzzy", true);
		data.put("highlightColor", 0xFF0000);
		data.put("displayColor", 0x00FF00);
		data.put("iconId", 12931);
		data.put("section", "Loadout Lab");
		data.put("notes", "from a planner");
		data.put("attackOption", "Accurate");

		handler.handleMessage(new PluginMessage("inventory-setups", "add-setup", data));

		final ArgumentCaptor<InventorySetupsAddSetupRequest> captor = ArgumentCaptor.forClass(InventorySetupsAddSetupRequest.class);
		verify(plugin).addInventorySetup(captor.capture());
		final InventorySetupsAddSetupRequest request = captor.getValue();
		assertEquals("Zulrah - Magic", request.getName());
		assertEquals(slots(14, 12931, 21795), request.getEquipmentIds());
		assertEquals(slots(14, 1, 1), request.getEquipmentQuantities());
		assertEquals(slots(28, 385, 12791, 9433), request.getInventoryIds());
		assertEquals(slots(28, 4, 1, 1), request.getInventoryQuantities());
		assertEquals(Arrays.asList(9763, 9790), request.getAdditionalIds());
		assertEquals(Arrays.asList(1, 1), request.getAdditionalQuantities());
		assertEquals(Arrays.asList(560, 565, 566), request.getRunePouchIds());
		assertEquals(Arrays.asList(100, 200, 300), request.getRunePouchQuantities());
		assertEquals(Arrays.asList(9244, 9243), request.getBoltPouchIds());
		assertEquals(Arrays.asList(500, 400), request.getBoltPouchQuantities());
		assertEquals(Integer.valueOf(11212), request.getQuiverItemId());
		assertEquals(Integer.valueOf(1000), request.getQuiverQuantity());
		assertEquals(Integer.valueOf(3), request.getSpellbook());
		assertEquals(Boolean.TRUE, request.getFavorite());
		assertEquals(Boolean.FALSE, request.getFilterBank());
		assertEquals(Boolean.TRUE, request.getHighlightDifference());
		assertEquals(Boolean.TRUE, request.getUnorderedHighlight());
		assertEquals(Boolean.TRUE, request.getFuzzy());
		assertEquals(Integer.valueOf(0xFF0000), request.getHighlightColor());
		assertEquals(Integer.valueOf(0x00FF00), request.getDisplayColor());
		assertEquals(Integer.valueOf(12931), request.getIconId());
		assertEquals("Loadout Lab", request.getSection());
		assertEquals("from a planner", request.getNotes());
		assertEquals("Accurate", request.getAttackOption());

		assertEquals("Zulrah - Magic (2)", data.get("setup"));
		final Map<String, Object> result = addSetupResult();
		assertEquals(7, result.get("requestId"));
		assertEquals("Zulrah - Magic (2)", result.get("setup"));
		assertNull(result.get("error"));
	}

	@Test
	public void testAddSetupOptionalKeysMayBeOmitted()
	{
		when(plugin.addInventorySetup(any(InventorySetupsAddSetupRequest.class))).thenReturn("Vorkath");
		final Map<String, Object> data = minimalAddSetup("Vorkath");

		handler.handleMessage(new PluginMessage("inventory-setups", "add-setup", data));

		final ArgumentCaptor<InventorySetupsAddSetupRequest> captor = ArgumentCaptor.forClass(InventorySetupsAddSetupRequest.class);
		verify(plugin).addInventorySetup(captor.capture());
		final InventorySetupsAddSetupRequest request = captor.getValue();
		assertNull(request.getEquipmentQuantities());
		assertNull(request.getAdditionalIds());
		assertNull(request.getRunePouchIds());
		assertNull(request.getBoltPouchIds());
		assertNull(request.getQuiverItemId());
		assertNull(request.getSpellbook());
		assertNull(request.getFavorite());
		assertNull(request.getHighlightColor());
		assertNull(request.getSection());
		assertEquals("Vorkath", data.get("setup"));
		final Map<String, Object> result = addSetupResult();
		assertEquals("Vorkath", result.get("setup"));
		assertTrue("no requestId was given, none is echoed", !result.containsKey("requestId"));
	}

	// Each payload has one key of the wrong type; each is refused with a reason naming the key, and the plugin
	// is never asked. Every other rule is the plugin's (InventorySetupsAddSetupTest).
	@Test
	public void testAddSetupRefusesAWrongTypeWithAReason()
	{
		final Object[][] cases = {
			{"setup", 5, "'setup'"},
			{"equipmentItemIds", Arrays.asList("whip"), "'equipmentItemIds'"},
			{"inventoryItemIds", 385, "'inventoryItemIds'"},
			{"inventoryQuantities", Arrays.asList(4L), "'inventoryQuantities'"},
			{"additionalItemIds", Arrays.asList("magic cape"), "'additionalItemIds'"},
			{"runePouchItemIds", Arrays.asList("death rune"), "'runePouchItemIds'"},
			{"boltPouchQuantities", "50", "'boltPouchQuantities'"},
			{"quiverItemId", "amethyst arrow", "'quiverItemId'"},
			{"spellbook", "Lunar", "'spellbook'"},
			{"favorite", "yes", "'favorite'"},
			{"highlightColor", "red", "'highlightColor'"},
			{"notes", 3, "'notes'"},
		};
		for (final Object[] c : cases)
		{
			reset(eventBus);
			final Map<String, Object> data = minimalAddSetup("Vorkath");
			data.put("requestId", "r-" + c[0]);
			data.put((String) c[0], c[1]);

			handler.handleMessage(new PluginMessage("inventory-setups", "add-setup", data));

			assertTrue(c[0] + " is refused: " + data.get("error"), String.valueOf(data.get("error")).contains((String) c[2]));
			final Map<String, Object> result = addSetupResult();
			assertEquals("r-" + c[0], result.get("requestId"));
			assertEquals(data.get("error"), result.get("error"));
		}
		verify(plugin, never()).addInventorySetup(any());
	}

	@Test
	public void testAddSetupReportsWhatThePluginRefuses()
	{
		when(plugin.addInventorySetup(any(InventorySetupsAddSetupRequest.class)))
			.thenThrow(new IllegalArgumentException("unknown item id 99999"));
		final Map<String, Object> data = minimalAddSetup("Vorkath");
		data.put("requestId", 3L);

		handler.handleMessage(new PluginMessage("inventory-setups", "add-setup", data));

		assertEquals("unknown item id 99999", data.get("error"));
		final Map<String, Object> result = addSetupResult();
		assertEquals(3L, result.get("requestId"));
		assertEquals("Vorkath", result.get("setup"));
		assertEquals("unknown item id 99999", result.get("error"));
	}

	@Test
	public void testAddSetupAcceptsAnImmutableMap()
	{
		// The result broadcast still reports the outcome to a sender that cannot be replied to in place.
		when(plugin.addInventorySetup(any(InventorySetupsAddSetupRequest.class))).thenReturn("Vorkath");

		handler.handleMessage(new PluginMessage("inventory-setups", "add-setup",
			Map.of("setup", "Vorkath", "equipmentItemIds", slots(14), "inventoryItemIds", slots(28), "requestId", 9)));

		final Map<String, Object> result = addSetupResult();
		assertEquals(9, result.get("requestId"));
		assertEquals("Vorkath", result.get("setup"));
	}

	@Test
	public void testAddSetupFromAnotherThreadOnlyBroadcasts()
	{
		// Written later from the client thread, a reply in the map could race the sender reading it.
		postFromAnotherThread();
		when(plugin.addInventorySetup(any(InventorySetupsAddSetupRequest.class))).thenReturn("Vorkath (2)");
		final Map<String, Object> data = minimalAddSetup("Vorkath");
		data.put("requestId", 4);

		handler.handleMessage(new PluginMessage("inventory-setups", "add-setup", data));

		verify(clientThread).invokeLater(any(Runnable.class));
		assertEquals("Vorkath", data.get("setup"));
		assertFalse(data.containsKey("error"));
		final Map<String, Object> result = addSetupResult();
		assertEquals(4, result.get("requestId"));
		assertEquals("Vorkath (2)", result.get("setup"));
	}

	@Test
	public void testOwnResultBroadcastIsIgnored()
	{
		handler.handleMessage(new PluginMessage("inventory-setups", "add-setup-result", new HashMap<>(Map.of("setup", "Vorkath"))));

		verify(plugin, never()).addInventorySetup(any());
		verify(eventBus, never()).post(any());
	}

	@Test
	public void testGetSetupsReportsTheVersion()
	{
		final Map<String, Object> data = new HashMap<>();
		data.put("setups", new ArrayList<String>());

		handler.handleMessage(new PluginMessage("inventory-setups", "get-setups", data));

		assertEquals(InventorySetupsPluginMessageHandler.API_VERSION, data.get("version"));
		assertEquals(2, InventorySetupsPluginMessageHandler.API_VERSION);
	}

	@Test
	public void testGetSetupsAcceptsAnImmutableMap()
	{
		// Senders that only want the names keep working with Map.of.
		handler.handleMessage(new PluginMessage("inventory-setups", "get-setups", Map.of("setups", new ArrayList<String>())));
	}
}
