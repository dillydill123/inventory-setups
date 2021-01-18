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
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

public class InventorySetupsAdditionalItemsPanel extends InventorySetupsContainerPanel
{
	private final ArrayList<InventorySetupsSlot> additionalFilteredSlots;

	InventorySetupsAdditionalItemsPanel(ItemManager itemManager, InventorySetupsPlugin plugin)
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
	public void highlightSlotDifferences(ArrayList<InventorySetupsItem> currContainer, InventorySetup inventorySetup)
	{
		// No highlighting for this panel
	}

	@Override
	public void setSlots(InventorySetup setup)
	{
		final HashMap<Integer, InventorySetupsItem> setupAdditionalItems = setup.getAdditionalFilteredItems();
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
				final InventorySetupsSlot newSlot = new InventorySetupsSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupsSlotID.ADDITIONAL_ITEMS, i);
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
				InventorySetupsItem dummy = new InventorySetupsItem(-1, null, 0, false);
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
