/*
 * Copyright (c) 2019, dillydill123 <https://github.com/dillydill123>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package inventorysetups.ui;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupsItem;
import inventorysetups.InventorySetupsSlotID;
import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsStackCompareID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.ArrayList;

public class InventorySetupsRunePouchPanel extends InventorySetupsContainerPanel
{
	private ArrayList<InventorySetupsSlot> runeSlots;

	InventorySetupsRunePouchPanel(ItemManager itemManager, InventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Rune Pouch");
	}

	@Override
	public void setupContainerPanel(JPanel containerSlotsPanel)
	{
		runeSlots = new ArrayList<>();
		for (int i = 0; i < 3; i++)
		{
			runeSlots.add(new InventorySetupsSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupsSlotID.RUNE_POUCH, i));
		}

		final GridLayout gridLayout = new GridLayout(1, 4, 1, 1);
		containerSlotsPanel.setLayout(gridLayout);

		for (final InventorySetupsSlot slot : runeSlots)
		{
			containerSlotsPanel.add(slot);
			super.addUpdateFromContainerMouseListenerToSlot(slot);
			super.addStackMouseListenerToSlot(slot);
			super.addUpdateFromSearchMouseListenerToSlot(slot, true);
			super.addRemoveMouseListenerToSlot(slot);
		}
	}

	@Override
	public void highlightSlots(ArrayList<InventorySetupsItem> currentContainer, InventorySetup inventorySetup)
	{
		assert inventorySetup.getRune_pouch() != null : "Rune Pouch container is null.";

		assert currentContainer.size() == 3 : "Incorrect size";

		isHighlighted = true;

		final ArrayList<InventorySetupsItem> setupRunePouch = inventorySetup.getRune_pouch();

		for (int i = 0; i < setupRunePouch.size(); i++)
		{
			boolean shouldHighlightSlot = false;
			boolean foundRune = false;
			int currentContainerIndex = -1;
			for (int j = 0; j < currentContainer.size(); j++)
			{
				if (setupRunePouch.get(i).getId() == currentContainer.get(j).getId())
				{
					foundRune = true;
					currentContainerIndex = j;
					break;
				}
			}

			if (foundRune)
			{
				int savedQuantity = setupRunePouch.get(i).getQuantity();
				int currentQuantity = currentContainer.get(currentContainerIndex).getQuantity();
				if (shouldHighlightSlotBasedOnStack(inventorySetup, savedQuantity, currentQuantity))
				{
					shouldHighlightSlot = true;
				}
			}
			else
			{
				shouldHighlightSlot = true;
			}

			if (shouldHighlightSlot)
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
	public void updatePanelWithSetupInformation(InventorySetup setup)
	{

		if (setup.getRune_pouch() != null)
		{
			for (int i = 0; i < runeSlots.size(); i++)
			{
				super.setSlotImageAndText(runeSlots.get(i), setup, setup.getRune_pouch().get(i));
			}
		}
		else
		{
			for (int i = 0; i < runeSlots.size(); i++)
			{
				super.setSlotImageAndText(runeSlots.get(i), setup, InventorySetupsItem.getDummyItem());
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
		for (final InventorySetupsSlot slot : runeSlots)
		{
			slot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}
		isHighlighted = false;

	}

	public void highlightAllSlots(final InventorySetup setup)
	{
		for (final InventorySetupsSlot slot : runeSlots)
		{
			slot.setBackground(setup.getHighlightColor());
		}
		isHighlighted = true;
	}
}
