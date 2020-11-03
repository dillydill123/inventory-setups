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

import inventorysetups.InventorySetupSlotID;
import inventorysetups.InventorySetupsPlugin;
import net.runelite.api.ItemID;
import org.apache.commons.lang3.tuple.ImmutablePair;
import net.runelite.client.game.ItemManager;
import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupItem;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

public class InventorySetupInventoryPanel extends InventorySetupContainerPanel
{

	private static final int ITEMS_PER_ROW = 4;
	private static final int NUM_INVENTORY_ITEMS = 28;

	private ArrayList<InventorySetupSlot> inventorySlots;
	private InventorySetupRunePouchPanel rpPanel;

	InventorySetupInventoryPanel(final ItemManager itemManager, final InventorySetupsPlugin plugin, final InventorySetupRunePouchPanel rpPanel)
	{
		super(itemManager, plugin, "Inventory");
		this.rpPanel = rpPanel;
	}

	@Override
	public void setupContainerPanel(final JPanel containerSlotsPanel)
	{
		this.inventorySlots = new ArrayList<>();
		for (int i = 0; i < NUM_INVENTORY_ITEMS; i++)
		{
			inventorySlots.add(new InventorySetupSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupSlotID.INVENTORY, i));
		}

		int numRows = (NUM_INVENTORY_ITEMS + ITEMS_PER_ROW - 1) / ITEMS_PER_ROW;
		containerSlotsPanel.setLayout(new GridLayout(numRows, ITEMS_PER_ROW, 1, 1));
		for (int i = 0; i < NUM_INVENTORY_ITEMS; i++)
		{
			containerSlotsPanel.add(inventorySlots.get(i));
			super.addUpdateFromContainerMouseListenerToSlot(inventorySlots.get(i));
			super.addUpdateFromSearchMouseListenerToSlot(inventorySlots.get(i), true);
			super.addRemoveMouseListenerToSlot(inventorySlots.get(i));
		}
	}

	@Override
	public void setSlots(final InventorySetup setup)
	{
		for (int i = 0; i < NUM_INVENTORY_ITEMS; i++)
		{
			super.setContainerSlot(i, inventorySlots.get(i), setup, setup.getInventory().get(i));
		}

		validate();
		repaint();
	}

	@Override
	public void highlightSlotDifferences(final ArrayList<InventorySetupItem> currInventory, final InventorySetup inventorySetup)
	{
		final ArrayList<InventorySetupItem> inventoryToCheck = inventorySetup.getInventory();

		assert currInventory.size() == inventoryToCheck.size() : "size mismatch";

		isHighlighted = true;

		if (inventorySetup.isUnorderedHighlight())
		{
			doUnorderedHighlighting(currInventory, inventorySetup);
			return;
		}

		boolean currInvHasRunePouch = false;
		for (int i = 0; i < NUM_INVENTORY_ITEMS; i++)
		{
			InventorySetupItem currInvItem = currInventory.get(i);
			if (!currInvHasRunePouch && ItemVariationMapping.map(currInvItem.getId()) == ItemID.RUNE_POUCH)
			{
				currInvHasRunePouch = true;
			}
			super.highlightDifferentSlotColor(inventorySetup, inventoryToCheck.get(i), currInventory.get(i), inventorySlots.get(i));
		}

		final boolean currInvHasRunePouchFinal = currInvHasRunePouch;
		plugin.getClientThread().invokeLater(() ->
		{
			handleRunePouchHighlighting(inventorySetup, currInvHasRunePouchFinal);
		});

	}

	@Override
	public void resetSlotColors()
	{
		// Don't waste time resetting if we were never highlighted to begin with
		if (!isHighlighted)
		{
			return;
		}

		for (InventorySetupSlot inventorySlot : inventorySlots)
		{
			inventorySlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}

		rpPanel.resetSlotColors();

		isHighlighted = false;
	}

	private void doUnorderedHighlighting(final ArrayList<InventorySetupItem> currInventory, final InventorySetup inventorySetup)
	{
		HashMap<Integer, ArrayList<Integer>> currInvMap = new HashMap<>();

		boolean currInvHasRunePouch = false;
		for (final InventorySetupItem item : currInventory)
		{
			// Use variation mapping if necessary and set the quantity to 1 if ignoring stacks
			int itemId = inventorySetup.isVariationDifference() ? item.getId() : ItemVariationMapping.map(item.getId());
			if (ItemVariationMapping.map(item.getId()) == ItemID.RUNE_POUCH)
			{
				currInvHasRunePouch = true;
			}

			ArrayList<Integer> currItemList = currInvMap.get(itemId);
			if (currItemList == null)
			{
				currItemList = new ArrayList<>();
				currInvMap.put(itemId, currItemList);
			}
			currItemList.add(item.getQuantity());
		}

		final ArrayList<InventorySetupItem> setupInv = inventorySetup.getInventory();
		for (int i = 0; i < setupInv.size(); i++)
		{
			final InventorySetupItem item = setupInv.get(i);

			/*
			 don't count empty spaces. We only want to show items that are missing, not "extra items"
			 that would be indicated by highlighting empty slots.
			*/
			if (item.getId() == -1)
			{
				inventorySlots.get(i).setBackground(ColorScheme.DARKER_GRAY_COLOR);
				continue;
			}

			// Use variation mapping if necessary and set the quantity to 1 if ignoring stacks
			int itemId = inventorySetup.isVariationDifference() ? item.getId() : ItemVariationMapping.map(item.getId());
			final ArrayList<Integer> itemList = currInvMap.get(itemId);
			if (itemList == null || itemList.isEmpty())
			{
				inventorySlots.get(i).setBackground(inventorySetup.getHighlightColor());
				continue;
			}

			// This assumes the last item contains the correct quantity. This is done because
			// in the actual game, you can't have two stacks of stackable items. This assumption
			// is fine for stackable items as well.
			Integer currInventoryItemQty = itemList.get(itemList.size() - 1);
			itemList.remove(itemList.size() - 1);
			if (this.highlightBasedOnStack(inventorySetup, item.getQuantity(), currInventoryItemQty))
			{
				inventorySlots.get(i).setBackground(inventorySetup.getHighlightColor());
			}
			else
			{
				inventorySlots.get(i).setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}
		}

		final boolean currInvHasRunePouchFinal = currInvHasRunePouch;
		plugin.getClientThread().invokeLater(() ->
		{
			handleRunePouchHighlighting(inventorySetup, currInvHasRunePouchFinal);
		});

	}

	private void handleRunePouchHighlighting(final InventorySetup inventorySetup, boolean currInvHasRunePouch)
	{
		if (inventorySetup.getRune_pouch() != null)
		{

			// attempt to highlight if rune pouch is available
			if (currInvHasRunePouch)
			{
				ArrayList<InventorySetupItem> runePouchToCheck = plugin.getRunePouchData();
				rpPanel.highlightSlotDifferences(runePouchToCheck, inventorySetup);
			}
			else // if the current inventory doesn't have a rune pouch but the setup does, highlight the RP pouch
			{
				rpPanel.highlightAllSlots(inventorySetup);
			}
		}
		else
		{
			rpPanel.resetSlotColors();
		}
	}
}
