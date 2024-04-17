package inventorysetups.ui;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupsItem;
import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsSlotID;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JPopupMenu;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class InventorySetupsQuiverPanel
{
	// Shows up when a quiver is equipped or in inventory
	@Getter
	private InventorySetupsSlot quiverSlot;

	@Getter
	private final int QUIVER_SLOT_IDX = 0;

	private JPopupMenu quiverSlotRightClickMenu;
	private final JPopupMenu emptyJPopMenu = new JPopupMenu();
	private final InventorySetupsPlugin plugin;
	private final ItemManager itemManager;

	public static final List<Integer> DIZANA_QUIVER_IDS = Arrays.asList(ItemID.DIZANAS_QUIVER,
			ItemID.DIZANAS_QUIVER_L,
			ItemID.DIZANAS_QUIVER_UNCHARGED,
			ItemID.DIZANAS_QUIVER_UNCHARGED_L,
			ItemID.DIZANAS_MAX_CAPE,
			ItemID.DIZANAS_MAX_CAPE_L,
			ItemID.BLESSED_DIZANAS_QUIVER,
			ItemID.BLESSED_DIZANAS_QUIVER_L);

	public static final Set<Integer> DIZANA_QUIVER_IDS_SET = new HashSet<>(DIZANA_QUIVER_IDS);


	InventorySetupsQuiverPanel(final ItemManager itemManager, final InventorySetupsPlugin plugin)
	{
		this.plugin = plugin;
		this.itemManager = itemManager;
		quiverSlot = new InventorySetupsSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupsSlotID.QUIVER, QUIVER_SLOT_IDX);
		InventorySetupsSlot.addUpdateFromContainerMouseListenerToSlot(plugin, quiverSlot);
		InventorySetupsSlot.addUpdateFromSearchMouseListenerToSlot(plugin, quiverSlot, true);
		InventorySetupsSlot.addRemoveMouseListenerToSlot(plugin, quiverSlot);
		this.quiverSlotRightClickMenu = quiverSlot.getRightClickMenu();
		quiverSlot.setComponentPopupMenu(new JPopupMenu());
	}

	public void handleQuiverHighlighting(final InventorySetup setup, boolean doesCurrentInventoryHaveQuiver)
	{
		if (!setup.isHighlightDifference() || !plugin.isHighlightingAllowed())
		{
			quiverSlot.setBackground(setup.getQuiver() != null ? ColorScheme.DARKER_GRAY_COLOR : ColorScheme.DARK_GRAY_COLOR);
			quiverSlot.setComponentPopupMenu(setup.getQuiver() != null ? quiverSlotRightClickMenu : emptyJPopMenu);
			return;
		}

		// This must be run on the client thread!
		if (setup.getQuiver() != null)
		{
			InventorySetupsSlot.setSlotImageAndText(itemManager, quiverSlot, setup, setup.getQuiver().get(0));
			quiverSlot.setComponentPopupMenu(quiverSlotRightClickMenu);

			if (doesCurrentInventoryHaveQuiver)
			{
				List<InventorySetupsItem> currentQuiverDataInInvEqp = plugin.getAmmoHandler().getQuiverData();
				final int indexInSlot = quiverSlot.getIndexInSlot();
				InventorySetupsSlot.highlightSlot(setup, setup.getQuiver().get(indexInSlot), currentQuiverDataInInvEqp.get(indexInSlot), quiverSlot);
			}
			else
			{
				quiverSlot.setBackground(setup.getHighlightColor());
			}
		}
		else
		{
			InventorySetupsSlot.setSlotImageAndText(itemManager, quiverSlot, setup, InventorySetupsItem.getDummyItem());
			quiverSlot.setBackground(ColorScheme.DARK_GRAY_COLOR);
			quiverSlot.setComponentPopupMenu(emptyJPopMenu);
		}
	}


}
