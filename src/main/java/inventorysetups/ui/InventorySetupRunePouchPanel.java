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

public class InventorySetupRunePouchPanel extends InventorySetupContainerPanel
{
	private ArrayList<InventorySetupSlot> runeSlots;

	InventorySetupRunePouchPanel(ItemManager itemManager, InventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Rune Pouch");
	}

	@Override
	public void setupContainerPanel(JPanel containerSlotsPanel)
	{
		runeSlots = new ArrayList<>();
		for (int i = 0; i < 3; i++)
		{
			runeSlots.add(new InventorySetupSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupSlotID.RUNE_POUCH, i));
		}

		final GridLayout gridLayout = new GridLayout(1, 4, 1, 1);
		containerSlotsPanel.setLayout(gridLayout);

		for (final InventorySetupSlot slot : runeSlots)
		{
			containerSlotsPanel.add(slot);
			super.addUpdateFromContainerMouseListenerToSlot(slot);
			super.addUpdateFromSearchMouseListenerToSlot(slot, true);
			super.addRemoveMouseListenerToSlot(slot);
		}
	}

	@Override
	public void highlightSlotDifferences(ArrayList<InventorySetupItem> currContainer, InventorySetup inventorySetup)
	{
		assert inventorySetup.getRune_pouch() != null : "Rune Pouch container is null.";

		assert currContainer.size() == 3 : "Incorrect size";

		isHighlighted = true;

		final ArrayList<InventorySetupItem> setupRunePouch = inventorySetup.getRune_pouch();

		for (int i = 0; i < setupRunePouch.size(); i++)
		{
			boolean shouldHighlight = false;
			boolean foundRune = false;
			int currContainerIndex = -1;
			for (int j = 0; j < currContainer.size(); j++)
			{
				if (setupRunePouch.get(i).getId() == currContainer.get(j).getId())
				{
					foundRune = true;
					currContainerIndex = j;
					break;
				}
			}

			if (foundRune)
			{
				int savedQty = setupRunePouch.get(i).getQuantity();
				int currQty = currContainer.get(currContainerIndex).getQuantity();
				if (highlightBasedOnStack(inventorySetup, savedQty, currQty))
				{
					shouldHighlight = true;
				}
			}
			else
			{
				shouldHighlight = true;
			}

			if (shouldHighlight)
			{
				runeSlots.get(i).setBackground(inventorySetup.getHighlightColor());
			}
			else
			{
				runeSlots.get(i).setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}

		}
	}

	@Override
	public void setSlots(InventorySetup setup)
	{

		if (setup.getRune_pouch() != null)
		{
			for (int i = 0; i < runeSlots.size(); i++)
			{
				super.setContainerSlot(i, runeSlots.get(i), setup, setup.getRune_pouch().get(i));
			}
		}
		else
		{
			for (final InventorySetupSlot slot : runeSlots)
			{
				slot.setImageLabel(null, null);
			}
		}

		validate();
		repaint();
	}

	@Override
	public void resetSlotColors()
	{
		if (!isHighlighted)
		{
			return;
		}
		for (final InventorySetupSlot slot : runeSlots)
		{
			slot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}
		isHighlighted = false;

	}

	public void highlightAllSlots(final InventorySetup setup)
	{
		for (final InventorySetupSlot slot : runeSlots)
		{
			slot.setBackground(setup.getHighlightColor());
		}
		isHighlighted = true;
	}
}
