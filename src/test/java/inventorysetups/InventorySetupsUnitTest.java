package inventorysetups;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.game.ItemManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import net.runelite.client.plugins.banktags.BankTagsConfig;
import net.runelite.client.plugins.banktags.BankTagsPlugin;


import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.banktags.BankTagsService;
import net.runelite.client.plugins.banktags.TagManager;
import net.runelite.client.plugins.banktags.tabs.LayoutManager;
import net.runelite.client.plugins.banktags.tabs.TabInterface;
import net.runelite.client.ui.ClientToolbar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InventorySetupsUnitTest
{
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
	private BankTagsPlugin bankTagsPlugin;

	@Mock
	@Bind
	private BankTagsConfig bankTagsConfig;

	@Mock
	@Bind
	private TabInterface tabInterface;

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

	@Before
	public void before()
	{
		Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
		when(itemManager.canonicalize(ItemID.COAL)).thenReturn(ItemID.COAL);
	}

	@Test
	public void testInputText()
	{
		assertEquals(InventorySetupUtilities.parseTextInputAmount("1"), 1);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("0"), 1);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("1K"), 1000);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("10K"), 10000);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("1k"), 1000);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("1m"), 1000000);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("1M"), 1000000);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("1b"), 1000000000);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("1B"), 1000000000);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("10b"), 2147483647);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("10000M"), 2147483647);
		assertEquals(InventorySetupUtilities.parseTextInputAmount("102391273213291"), 2147483647);
	}

	@Test
	public void testSetupContainsItem()
	{
		List<InventorySetupsItem> inventory = new ArrayList<>(Collections.nCopies(28, InventorySetupsItem.getDummyItem()));
		List<InventorySetupsItem> equipment = new ArrayList<>(Collections.nCopies(13, InventorySetupsItem.getDummyItem()));
		List<InventorySetupsItem> runePouch = null;
		List<InventorySetupsItem> boltPouch = null;
		List<InventorySetupsItem> quiver = null;
		Map<Integer, InventorySetupsItem> addItems = new HashMap<>();
		InventorySetup setup = new InventorySetup(inventory, equipment, runePouch, boltPouch, quiver, addItems, "Test",
												"", inventorySetupsConfig.highlightColor(), false,
												inventorySetupsConfig.displayColor(), false,false, 0, false, -1);
		inventorySetupsPlugin.startUp();
		assertFalse(inventorySetupsPlugin.setupContainsItem(setup, ItemID.COAL, true, true));
	}
}
