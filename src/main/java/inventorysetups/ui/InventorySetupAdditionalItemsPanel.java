package inventorysetups.ui;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupItem;
import inventorysetups.InventorySetupSlotID;
import inventorysetups.InventorySetupsPlugin;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;

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
		final HashSet<Integer> setupAdditionalItems = setup.getAdditionalFilteredItems();
		final JPanel containerSlotsPanel = this.getContainerSlotsPanel();

		// Make final size a multiple of 4
		int finalSize = setupAdditionalItems.size();
		int remainder = finalSize % 4;
		if (finalSize == 0 || finalSize % 4 != 0)
		{
			finalSize = finalSize + 4 - remainder;
		}

		if (additionalFilteredSlots.size() < finalSize)
		{
			for (int i = additionalFilteredSlots.size(); i < finalSize; i++)
			{
				additionalFilteredSlots.add(new InventorySetupSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupSlotID.ADDITIONAL_ITEMS, i));
				containerSlotsPanel.add(additionalFilteredSlots.get(additionalFilteredSlots.size() - 1));
			}
		}
		else
		{
			for (int i = additionalFilteredSlots.size() - 1; i >= finalSize; i--)
			{
				additionalFilteredSlots.get(i).setVisible(false);
			}
		}

		for (int i = 0; i < finalSize; i++)
		{
			additionalFilteredSlots.get(i).setVisible(true);
		}

		validate();
		repaint();
	}

	@Override
	public void resetSlotColors()
	{
	}
}
