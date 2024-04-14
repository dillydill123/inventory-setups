package inventorysetups;

import inventorysetups.ui.InventorySetupsPluginPanel;
import inventorysetups.ui.InventorySetupsRunePouchPanel;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.runelite.api.VarPlayer;

import static inventorysetups.ui.InventorySetupsBoltPouchPanel.BOLT_POUCH_AMOUNT_VARBIT_IDS;
import static inventorysetups.ui.InventorySetupsBoltPouchPanel.BOLT_POUCH_BOLT_VARBIT_IDS;
import static inventorysetups.ui.InventorySetupsRunePouchPanel.RUNE_POUCH_AMOUNT_VARBITS;
import static inventorysetups.ui.InventorySetupsRunePouchPanel.RUNE_POUCH_RUNE_VARBITS;

public class InventorySetupsAmmoHandler
{

	private Map<Integer, Consumer<InventorySetup>> updateDataHandler;

	private Map<Integer, Consumer<InventorySetup>> removeDataHandler;

	public static final List<Integer> DIZANA_QUIVER_IDS = Arrays.asList(ItemID.DIZANAS_QUIVER,
																		ItemID.DIZANAS_QUIVER_L,
																		ItemID.DIZANAS_QUIVER_UNCHARGED,
																		ItemID.DIZANAS_QUIVER_UNCHARGED_L,
																		ItemID.DIZANAS_MAX_CAPE,
																		ItemID.DIZANAS_MAX_CAPE_L,
																		ItemID.BLESSED_DIZANAS_QUIVER,
																		ItemID.BLESSED_DIZANAS_QUIVER_L);

	private final InventorySetupsPlugin plugin;
	private final Client client;

	private final ItemManager itemManager;

	private final InventorySetupsPluginPanel panel;

	private final InventorySetupsConfig config;

	public InventorySetupsAmmoHandler(InventorySetupsPlugin plugin, Client client, ItemManager itemManager,
										InventorySetupsPluginPanel panel, InventorySetupsConfig config)
	{
		this.plugin = plugin;
		this.client = client;
		this.itemManager = itemManager;
		this.panel = panel;
		this.config = config;

		this.updateDataHandler = new HashMap<>();
		this.removeDataHandler = new HashMap<>();

		for (final int itemID : InventorySetupsRunePouchPanel.RUNE_POUCH_IDS)
		{
			updateDataHandler.put(itemID, (setup) -> setup.updateRunePouch(getRunePouchData(InventorySetupsRunePouchType.NORMAL)));
			removeDataHandler.put(itemID, (setup) -> setup.updateRunePouch(null));
		}

		for (final int itemID : InventorySetupsRunePouchPanel.RUNE_POUCH_DIVINE_IDS)
		{
			updateDataHandler.put(itemID, (setup) -> setup.updateRunePouch(getRunePouchData(InventorySetupsRunePouchType.NORMAL)));
			removeDataHandler.put(itemID, (setup) -> setup.updateRunePouch(null));
		}

		updateDataHandler.put(ItemID.BOLT_POUCH, (setup) -> setup.updateBoltPouch(getBoltPouchData()));
		removeDataHandler.put(ItemID.BOLT_POUCH, (setup) -> setup.updateBoltPouch(null));

		for (final int itemID : DIZANA_QUIVER_IDS)
		{
			updateDataHandler.put(itemID, (setup) -> setup.updateQuiver(getQuiverData()));
			removeDataHandler.put(itemID, (setup) -> setup.updateQuiver(null));
		}
	}

	// Checks when updating a slot in a setup that it is part of a special ammo. If so, handle it.
	public void handleSpecialAmmo(final InventorySetup inventorySetup,
									final InventorySetupsItem oldItem, final InventorySetupsItem newItem)
	{
		int newID = newItem.getId();
		int oldID = oldItem.getId();
		if (updateDataHandler.containsKey(newID))
		{
			updateDataHandler.get(newID).accept(inventorySetup);
		}
		else if (updateDataHandler.containsKey(oldID))
		{
			removeDataHandler.get(oldID).accept(inventorySetup);
		}
	}

	public InventorySetupsRunePouchType getRunePouchTypeFromContainer(final List<InventorySetupsItem> container)
	{
		// Don't allow fuzzy when checking because it will incorrectly assume the type
		for (Integer id : InventorySetupsRunePouchPanel.RUNE_POUCH_IDS)
		{
			if (plugin.containerContainsItem(id, container, false, true))
			{
				return InventorySetupsRunePouchType.NORMAL;
			}
		}

		for (Integer id : InventorySetupsRunePouchPanel.RUNE_POUCH_DIVINE_IDS)
		{
			if (plugin.containerContainsItem(id, container, false, true))
			{
				return InventorySetupsRunePouchType.DIVINE;
			}
		}

		return InventorySetupsRunePouchType.NONE;
	}

	public List<InventorySetupsItem> getRunePouchDataIfInContainer(final List<InventorySetupsItem> container)
	{
		InventorySetupsRunePouchType runePouchType = getRunePouchTypeFromContainer(container);
		return runePouchType != InventorySetupsRunePouchType.NONE ? getRunePouchData(runePouchType) : null;
	}

	public List<InventorySetupsItem> getRunePouchData(final InventorySetupsRunePouchType runePouchType)
	{
		List<InventorySetupsItem> runePouchData = new ArrayList<>();
		EnumComposition runepouchEnum = client.getEnum(982);
		for (int i = 0; i < runePouchType.getSize(); i++)
		{
			final int varbitVal = client.getVarbitValue(RUNE_POUCH_RUNE_VARBITS.get(i));
			if (varbitVal == 0)
			{
				runePouchData.add(InventorySetupsItem.getDummyItem());
			}
			else
			{
				final int runeId = runepouchEnum.getIntValue(varbitVal);
				int runeAmount = client.getVarbitValue(RUNE_POUCH_AMOUNT_VARBITS.get(i));
				String runeName = itemManager.getItemComposition(runeId).getName();
				InventorySetupsStackCompareID stackCompareType = panel.isStackCompareForSlotAllowed(InventorySetupsSlotID.RUNE_POUCH, i) ? config.stackCompareType() : InventorySetupsStackCompareID.None;
				runePouchData.add(new InventorySetupsItem(runeId, runeName, runeAmount, false, stackCompareType));
			}
		}

		return runePouchData;
	}

	public boolean containerContainsBoltPouch(final List<InventorySetupsItem> container)
	{
		return plugin.containerContainsItem(ItemID.BOLT_POUCH, container, false, true);
	}

	public List<InventorySetupsItem> getBoltPouchDataIfInContainer(final List<InventorySetupsItem> container)
	{
		return containerContainsBoltPouch(container) ? getBoltPouchData() : null;
	}

	public List<InventorySetupsItem> getBoltPouchData()
	{
		List<InventorySetupsItem> boltPouchData = new ArrayList<>();

		for (int i = 0; i < BOLT_POUCH_BOLT_VARBIT_IDS.size(); i++)
		{
			int boltVarbitId = client.getVarbitValue(BOLT_POUCH_BOLT_VARBIT_IDS.get(i));
			Bolts bolt = Bolts.getBolt(boltVarbitId);
			boolean boltNotFound = bolt == null;
			int boltAmount = boltNotFound ? 0 : client.getVarbitValue(BOLT_POUCH_AMOUNT_VARBIT_IDS.get(i));
			String boltName = boltNotFound ? "" : itemManager.getItemComposition(bolt.getItemId()).getName();
			int boltItemId = boltNotFound ? -1 : bolt.getItemId();

			if (boltItemId == -1)
			{
				boltPouchData.add(InventorySetupsItem.getDummyItem());
			}
			else
			{
				InventorySetupsStackCompareID stackCompareType =
						panel.isStackCompareForSlotAllowed(InventorySetupsSlotID.BOLT_POUCH, i)
								? config.stackCompareType() : InventorySetupsStackCompareID.None;
				boltPouchData.add(new InventorySetupsItem(boltItemId, boltName, boltAmount, false, stackCompareType));
			}
		}

		return boltPouchData;
	}

	public boolean setupContainsQuiver(final List<InventorySetupsItem> inv, final List<InventorySetupsItem> eq)
	{
		for (Integer id : DIZANA_QUIVER_IDS)
		{
			final boolean inventoryHasQuiver = plugin.containerContainsItem(id, inv, false, true);
			final boolean equipmentHasQuiver = plugin.containerContainsItem(id, eq, false, true);
			if (inventoryHasQuiver || equipmentHasQuiver)
			{
				return true;
			}
		}
		return false;
	}

	public List<InventorySetupsItem> getQuiverDataIfInSetup(final List<InventorySetupsItem> inv, final List<InventorySetupsItem> eq)
	{
		return setupContainsQuiver(inv, eq) ? getQuiverData() : null;
	}

	public List<InventorySetupsItem> getQuiverData()
	{
		// replace with VarPlayer when RL adds it.
		final int DIZANAS_QUIVER_ITEM_ID = 4142;
		final int DIZANAS_QUIVER_ITEM_COUNT = 4141;

		List<InventorySetupsItem> quiverData = new ArrayList<>();
		final int quiverAmmoId = client.getVarpValue(DIZANAS_QUIVER_ITEM_ID);
		final int quiverAmmoCount = Math.max(0, client.getVarpValue(DIZANAS_QUIVER_ITEM_COUNT));

		final String ammoName = quiverAmmoId == -1 ? "" : itemManager.getItemComposition(quiverAmmoId).getName();

		InventorySetupsStackCompareID stackCompareType =
				panel.isStackCompareForSlotAllowed(InventorySetupsSlotID.QUIVER, 1)
					? config.stackCompareType() : InventorySetupsStackCompareID.None;

		final InventorySetupsItem quiverItem = new InventorySetupsItem(quiverAmmoId, ammoName,
														quiverAmmoCount, false, stackCompareType);

		quiverData.add(quiverItem);

		return quiverData;
	}

}
