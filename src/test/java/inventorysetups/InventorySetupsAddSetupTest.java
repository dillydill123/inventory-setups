package inventorysetups;

import com.google.inject.Guice;
import com.google.inject.name.Named;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ChatColorConfig;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.banktags.BankTagsService;
import net.runelite.client.plugins.banktags.TagManager;
import net.runelite.client.plugins.banktags.tabs.LayoutManager;
import net.runelite.client.ui.ClientToolbar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static inventorysetups.ui.InventorySetupsQuiverPanel.DIZANA_QUIVER_IDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

// The add-setup PluginMessage's setup building: every container and option a request may set, the
// defaults it takes from the Add Setup button, and the requests it refuses without saving anything.
@RunWith(MockitoJUnitRunner.class)
public class InventorySetupsAddSetupTest
{
	private static final int UNKNOWN_ITEM = 99999;

	@Mock
	@Bind
	private Client client;

	@Mock
	@Bind
	private ItemManager itemManager;

	@Mock
	@Bind
	private InventorySetupsConfig inventorySetupsConfig;

	@Mock
	@Bind
	private RuneLiteConfig runeLiteConfig;

	@Mock
	@Bind
	private PluginManager pluginManager;

	@Mock
	@Bind
	private ClientToolbar clientToolbar;

	@Mock
	@Bind
	private BankTagsService bankTagsService;

	@Mock
	@Bind
	private LayoutManager layoutManager;

	@Mock
	@Bind
	private TagManager tagManager;

	@Mock
	@Bind
	private ConfigManager configManager;

	@Inject
	private InventorySetupsPlugin inventorySetupsPlugin;

	@Mock
	@Bind
	private ChatColorConfig chatColorConfig;

	@Bind
	@Named("developerMode")
	private Boolean developerMode = false;

	@Before
	public void before()
	{
		Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
		lenient().when(client.isClientThread()).thenReturn(true);
		lenient().when(inventorySetupsConfig.defaultLayout()).thenReturn(InventorySetupLayoutType.PRESET);
		lenient().when(client.getVarbitValue(anyInt())).thenReturn(1);
		lenient().when(itemManager.canonicalize(anyInt())).thenAnswer(invocation -> invocation.getArgument(0));
		lenient().when(itemManager.getItemComposition(anyInt())).thenAnswer(invocation ->
		{
			final int id = invocation.getArgument(0);
			final ItemComposition composition = mock(ItemComposition.class);
			lenient().when(composition.getName()).thenReturn(id == UNKNOWN_ITEM ? "null" : "Item " + id);
			return composition;
		});
		inventorySetupsPlugin.startUp();
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

	private static InventorySetupsAddSetupRequest.InventorySetupsAddSetupRequestBuilder minimal(final String name)
	{
		return InventorySetupsAddSetupRequest.builder()
			.name(name)
			.equipmentIds(slots(14))
			.inventoryIds(slots(28, ItemID.SHARK));
	}

	private InventorySetup saved(final String name)
	{
		return inventorySetupsPlugin.getCache().getInventorySetupNames().get(name);
	}

	@Test
	public void testAddSetupBuildsEveryContainerAndOption()
	{
		final int quiver = DIZANA_QUIVER_IDS.get(0);
		final List<Integer> equipment = slots(14);
		equipment.set(EquipmentInventorySlot.WEAPON.getSlotIdx(), ItemID.TWISTED_BOW);
		equipment.set(EquipmentInventorySlot.CAPE.getSlotIdx(), quiver);
		equipment.set(EquipmentInventorySlot.AMMO.getSlotIdx(), ItemID.DRAGON_ARROW);
		final List<Integer> equipmentQuantities = slots(14);
		equipmentQuantities.set(EquipmentInventorySlot.WEAPON.getSlotIdx(), 1);
		equipmentQuantities.set(EquipmentInventorySlot.CAPE.getSlotIdx(), 1);
		equipmentQuantities.set(EquipmentInventorySlot.AMMO.getSlotIdx(), 500);

		final String name = inventorySetupsPlugin.addInventorySetup(InventorySetupsAddSetupRequest.builder()
			.name("Zulrah - Ranged")
			.equipmentIds(equipment)
			.equipmentQuantities(equipmentQuantities)
			.inventoryIds(slots(28, ItemID.BH_RUNE_POUCH, ItemID.XBOWS_BOLT_POUCH, ItemID.SHARK))
			.inventoryQuantities(slots(28, 1, 1, 4))
			.additionalIds(Arrays.asList(ItemID.SKILLCAPE_MAGIC))
			.additionalQuantities(Arrays.asList(2))
			.runePouchIds(Arrays.asList(ItemID.DEATHRUNE, ItemID.BLOODRUNE, ItemID.SOULRUNE))
			.runePouchQuantities(Arrays.asList(100, 200, 300))
			.boltPouchIds(Arrays.asList(ItemID.DRAGON_BOLTS))
			.boltPouchQuantities(Arrays.asList(50))
			.quiverItemId(ItemID.AMETHYST_ARROW)
			.quiverQuantity(1000)
			.spellbook(3)
			.favorite(true)
			.filterBank(false)
			.highlightDifference(true)
			.unorderedHighlight(true)
			.fuzzy(true)
			.highlightColor(0xFF0000)
			.displayColor(0x00FF00)
			.iconId(ItemID.TWISTED_BOW)
			.notes("from a planner")
			.section("Loadout Lab")
			.attackOption("Rapid")
			.build());

		assertEquals("Zulrah - Ranged", name);
		final InventorySetup setup = saved(name);
		final InventorySetupsItem ammo = setup.getEquipment().get(EquipmentInventorySlot.AMMO.getSlotIdx());
		assertEquals(ItemID.DRAGON_ARROW, ammo.getId());
		assertEquals(500, ammo.getQuantity());
		assertTrue(ammo.isFuzzy());
		assertEquals(4, setup.getInventory().get(2).getQuantity());
		assertEquals(Arrays.asList(100, 200, 300), Arrays.asList(setup.getRune_pouch().get(0).getQuantity(),
			setup.getRune_pouch().get(1).getQuantity(), setup.getRune_pouch().get(2).getQuantity()));
		assertFalse(setup.getRune_pouch().get(0).isFuzzy());
		assertEquals(4, setup.getBoltPouch().size());
		assertEquals(ItemID.DRAGON_BOLTS, setup.getBoltPouch().get(0).getId());
		assertEquals(50, setup.getBoltPouch().get(0).getQuantity());
		assertTrue(InventorySetupsItem.itemIsDummy(setup.getBoltPouch().get(1)));
		assertEquals(ItemID.AMETHYST_ARROW, setup.getQuiver().get(0).getId());
		assertEquals(1000, setup.getQuiver().get(0).getQuantity());
		assertEquals(2, setup.getAdditionalFilteredItems().get(ItemID.SKILLCAPE_MAGIC).getQuantity());
		assertEquals(3, setup.getSpellBook());
		assertTrue(setup.isFavorite());
		assertFalse(setup.isFilterBank());
		assertTrue(setup.isHighlightDifference());
		assertTrue(setup.isUnorderedHighlight());
		assertEquals(new Color(0xFF0000), setup.getHighlightColor());
		assertEquals(new Color(0x00FF00), setup.getDisplayColor());
		assertEquals(ItemID.TWISTED_BOW, setup.getIconID());
		assertEquals("from a planner", setup.getNotes());
		assertEquals("Rapid", setup.getAttackOption());
		assertTrue(inventorySetupsPlugin.getCache().getSectionNames().get("Loadout Lab").getSetups().contains(name));
	}

	@Test
	public void testAddSetupDefaultsMatchTheAddSetupButton()
	{
		final String name = inventorySetupsPlugin.addInventorySetup(minimal("Vorkath").build());

		final InventorySetup setup = saved(name);
		assertEquals("no spellbook: nothing is read from the player", 4, setup.getSpellBook());
		assertFalse(setup.isFavorite());
		assertEquals(-1, setup.getIconID());
		assertEquals("", setup.getAttackOption());
		assertEquals("", setup.getNotes());
		assertEquals(1, setup.getInventory().get(0).getQuantity());
		assertNull(setup.getRune_pouch());
		assertNull(setup.getBoltPouch());
		assertNull(setup.getQuiver());
	}

	@Test
	public void testAddSetupNeverReplacesAnExistingSetup()
	{
		assertEquals("Vorkath", inventorySetupsPlugin.addInventorySetup(minimal("Vorkath").build()));
		assertEquals("Vorkath (2)", inventorySetupsPlugin.addInventorySetup(minimal("Vorkath").build()));
		assertEquals(2, inventorySetupsPlugin.getInventorySetups().size());
	}

	// Each request breaks one rule; each is refused with a reason naming the key, and nothing is saved.
	@Test
	public void testAddSetupRefusesABrokenRuleWithAReason()
	{
		final Object[][] cases = {
			{minimal(null), "'setup'"},
			{minimal(" "), "'setup'"},
			{minimal("A").equipmentIds(null), "'equipmentItemIds'"},
			{minimal("A").equipmentIds(slots(15)), "'equipmentItemIds'"},
			{minimal("A").inventoryIds(slots(29)), "'inventoryItemIds'"},
			{minimal("A").inventoryIds(Arrays.asList(-2)), "'inventoryItemIds'"},
			{minimal("A").inventoryQuantities(slots(29)), "'inventoryQuantities'"},
			{minimal("A").inventoryQuantities(Arrays.asList(0)), "'inventoryQuantities'"},
			{minimal("A").additionalQuantities(Arrays.asList(1)), "'additionalQuantities'"},
			{minimal("A").boltPouchIds(Arrays.asList(1, 2, 3, 4, 5)), "'boltPouchItemIds'"},
			{minimal("A").quiverQuantity(100), "'quiverQuantity'"},
			{minimal("A").spellbook(5), "'spellbook'"},
			{minimal("A").iconId(-2), "'iconId'"},
		};
		for (final Object[] c : cases)
		{
			try
			{
				inventorySetupsPlugin.addInventorySetup(((InventorySetupsAddSetupRequest.InventorySetupsAddSetupRequestBuilder) c[0]).build());
				fail(c[1] + " was not refused");
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(c[1] + " is refused: " + e.getMessage(), e.getMessage().contains((String) c[1]));
			}
		}
		assertTrue(inventorySetupsPlugin.getInventorySetups().isEmpty());
	}

	@Test
	public void testAddSetupRefusesUnknownItemsAndSavesNothing()
	{
		final List<InventorySetupsAddSetupRequest> requests = Arrays.asList(
			minimal("A").equipmentIds(slots(14, UNKNOWN_ITEM)).build(),
			minimal("B").inventoryIds(slots(28, UNKNOWN_ITEM)).build(),
			minimal("C").additionalIds(Arrays.asList(UNKNOWN_ITEM)).build(),
			minimal("D").iconId(UNKNOWN_ITEM).build());
		for (final InventorySetupsAddSetupRequest request : requests)
		{
			try
			{
				inventorySetupsPlugin.addInventorySetup(request);
				fail(request.getName() + " was saved");
			}
			catch (IllegalArgumentException e)
			{
				assertEquals("unknown item id " + UNKNOWN_ITEM, e.getMessage());
			}
		}
		assertTrue(inventorySetupsPlugin.getInventorySetups().isEmpty());
	}

	@Test
	public void testAddSetupRefusesContentsForAContainerTheSetupLacks()
	{
		final List<InventorySetupsAddSetupRequest> requests = Arrays.asList(
			minimal("Rune pouch").runePouchIds(Arrays.asList(ItemID.DEATHRUNE)).build(),
			minimal("Bolt pouch").boltPouchIds(Arrays.asList(ItemID.DRAGON_BOLTS)).build(),
			minimal("Quiver").quiverItemId(ItemID.AMETHYST_ARROW).build(),
			minimal("Big pouch").inventoryIds(slots(28, ItemID.BH_RUNE_POUCH))
				.runePouchIds(Arrays.asList(ItemID.DEATHRUNE, ItemID.BLOODRUNE, ItemID.SOULRUNE, ItemID.LAWRUNE)).build());
		for (final InventorySetupsAddSetupRequest request : requests)
		{
			try
			{
				inventorySetupsPlugin.addInventorySetup(request);
				fail(request.getName() + " was saved");
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(e.getMessage(), e.getMessage().contains("pouch") || e.getMessage().contains("quiver"));
			}
		}
		assertTrue(inventorySetupsPlugin.getInventorySetups().isEmpty());
	}

	@Test
	public void testAddSetupAcceptsEmptyContentsForAContainerTheSetupLacks()
	{
		// A sender may always send the keys, empty when its set has no pouch or quiver (Loadout Lab does).
		final String name = inventorySetupsPlugin.addInventorySetup(minimal("Vorkath")
			.runePouchIds(new ArrayList<>())
			.runePouchQuantities(new ArrayList<>())
			.boltPouchIds(Arrays.asList(-1, -1))
			.quiverItemId(-1)
			.build());

		final InventorySetup setup = saved(name);
		assertNull(setup.getRune_pouch());
		assertNull(setup.getBoltPouch());
		assertNull(setup.getQuiver());
	}
}
