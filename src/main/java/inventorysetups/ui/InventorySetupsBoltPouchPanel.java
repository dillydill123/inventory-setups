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
import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsSlotID;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

public class InventorySetupsBoltPouchPanel extends InventorySetupsContainerPanel
{
	private static final int BOLT_POUCH_SLOTS_COUNT = 4;
	private List<InventorySetupsSlot> boltSlots;

	InventorySetupsBoltPouchPanel(ItemManager itemManager, InventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Bolt Pouch");
	}

	@Override
	public void setupContainerPanel(JPanel containerSlotsPanel)
	{
		boltSlots = new ArrayList<>();
		for (int i = 0; i < BOLT_POUCH_SLOTS_COUNT; i++)
		{
			boltSlots.add(new InventorySetupsSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupsSlotID.BOLT_POUCH, i));
		}

		final GridLayout gridLayout = new GridLayout(1, 4, 1, 1);
		containerSlotsPanel.setLayout(gridLayout);

		for (final InventorySetupsSlot slot : boltSlots)
		{
			containerSlotsPanel.add(slot);
			super.addStackMouseListenerToSlot(slot);
			super.addUpdateFromContainerMouseListenerToSlot(slot);
			super.addUpdateFromSearchMouseListenerToSlot(slot, true);
			super.addRemoveMouseListenerToSlot(slot);
		}
	}

	@Override
	public void highlightSlots(List<InventorySetupsItem> currentContainer, InventorySetup inventorySetup)
	{
		assert inventorySetup.getBoltPouch() != null : "Bolt Pouch container is null.";

		assert currentContainer.size() == BOLT_POUCH_SLOTS_COUNT : "Incorrect size";

		isHighlighted = true;

		final List<InventorySetupsItem> setupBoltPouch = inventorySetup.getBoltPouch();

		for (int i = 0; i < setupBoltPouch.size(); i++)
		{
			boolean shouldHighlightSlot = false;
			boolean foundBolt = false;
			int currentContainerIndex = -1;
			for (int j = 0; j < currentContainer.size(); j++)
			{
				if (setupBoltPouch.get(i).getId() == currentContainer.get(j).getId())
				{
					foundBolt = true;
					currentContainerIndex = j;
					break;
				}
			}

			if (foundBolt)
			{
				int savedQuantity = setupBoltPouch.get(i).getQuantity();
				int currentQuantity = currentContainer.get(currentContainerIndex).getQuantity();
				if (shouldHighlightSlotBasedOnStack(setupBoltPouch.get(i).getStackCompare(), savedQuantity, currentQuantity))
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
				boltSlots.get(i).setBackground(inventorySetup.getHighlightColor());
			}
			else
			{
				boltSlots.get(i).setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}

		}
	}

	@Override
	public void updatePanelWithSetupInformation(InventorySetup setup)
	{

		if (setup.getBoltPouch() != null)
		{
			for (int i = 0; i < boltSlots.size(); i++)
			{
				super.setSlotImageAndText(boltSlots.get(i), setup, setup.getBoltPouch().get(i));
			}
		}
		else
		{
			for (int i = 0; i < boltSlots.size(); i++)
			{
				super.setSlotImageAndText(boltSlots.get(i), setup, InventorySetupsItem.getDummyItem());
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
		for (final InventorySetupsSlot slot : boltSlots)
		{
			slot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}
		isHighlighted = false;

	}

	public void highlightAllSlots(final InventorySetup setup)
	{
		for (final InventorySetupsSlot slot : boltSlots)
		{
			slot.setBackground(setup.getHighlightColor());
		}
		isHighlighted = true;
	}

	public boolean isStackCompareForSlotAllowed(final int id)
	{
		return true;
	}
}
