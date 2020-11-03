package inventorysetups.ui;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupItem;
import inventorysetups.InventorySetupSlotID;
import inventorysetups.InventorySetupsPlugin;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

public class InventorySetupAdditionalItemsPanel extends InventorySetupContainerPanel
{
	private final ArrayList<InventorySetupSlot> additionalFilteredSlots;

	InventorySetupAdditionalItemsPanel(ItemManager itemManager, InventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Additional Filtered Items");
		additionalFilteredSlots = new ArrayList<>();
	}

	@Override
	public void setupContainerPanel(JPanel containerSlotsPanel)
	{
		containerSlotsPanel.setLayout(new GridLayout(0, 4, 1, 1));
	}

	@Override
	public void highlightSlotDifferences(ArrayList<InventorySetupItem> currContainer, InventorySetup inventorySetup)
	{
		// No highlighting for this panel
	}

	@Override
	public void setSlots(InventorySetup setup)
	{
		final HashMap<Integer, InventorySetupItem> setupAdditionalItems = setup.getAdditionalFilteredItems();
		final JPanel containerSlotsPanel = this.getContainerSlotsPanel();

		// Make final size a multiple of 4
		int finalSize = setupAdditionalItems.size();
		int remainder = finalSize % 4;
		if (finalSize % 4 != 0)
		{
			finalSize = finalSize + 4 - remainder;
		}

		// saturated the row, increase it now
		if (finalSize == setupAdditionalItems.size())
		{
			finalSize += 4;
		}

		// new component creation must be on event dispatch thread, hence invoke later
		final int finalSizeLambda = finalSize;
		SwingUtilities.invokeLater(() ->
		{

			// add new slots if the final size is larger than the number of slots
			for (int i = additionalFilteredSlots.size(); i < finalSizeLambda; i++)
			{
				final InventorySetupSlot newSlot = new InventorySetupSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupSlotID.ADDITIONAL_ITEMS, i);
				super.addUpdateFromSearchMouseListenerToSlot(newSlot, false);
				super.addRemoveMouseListenerToSlot(newSlot);
				additionalFilteredSlots.add(newSlot);
			}

			// remove the extra slots from the layout if needed so the panel fits as small as possible
			for (int i = containerSlotsPanel.getComponentCount() - 1; i >= finalSizeLambda; i--)
			{
				containerSlotsPanel.remove(i);
			}

			// remove the images and tool tips for the inventory slots that are not part of this setup
			for (int i = finalSizeLambda - 1; i >= setupAdditionalItems.size(); i--)
			{
				InventorySetupItem dummy = new InventorySetupItem(-1, null, 0);
				this.setContainerSlot(i, additionalFilteredSlots.get(i), setup, dummy);
			}

			// add slots back to the layout if we need to
			for (int i = containerSlotsPanel.getComponentCount(); i < finalSizeLambda; i++)
			{
				containerSlotsPanel.add(additionalFilteredSlots.get(i));
			}

			// finally set the slots with the items and tool tips
			int j = 0;
			for (final Integer key : setupAdditionalItems.keySet())
			{
				this.setContainerSlot(j, additionalFilteredSlots.get(j), setup, setupAdditionalItems.get(key));
				j++;
			}

			validate();
			repaint();
		});

	}

	@Override
	public void resetSlotColors()
	{
	}

}
