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

import inventorysetups.InventorySetupsSlotID;
import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsVariationMapping;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupsItem;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

// The panel that contains the inventory slots
public class InventorySetupsInventoryPanel extends InventorySetupsContainerPanel
{

	private static final int ITEMS_PER_ROW = 4;
	private static final int NUM_INVENTORY_ITEMS = 28;

	private ArrayList<InventorySetupsSlot> inventorySlots;
	private InventorySetupsRunePouchPanel runePouchPanel;

	InventorySetupsInventoryPanel(final ItemManager itemManager, final InventorySetupsPlugin plugin, final InventorySetupsRunePouchPanel runePouchPanel)
	{
		super(itemManager, plugin, "Inventory");
		this.runePouchPanel = runePouchPanel;
	}

	@Override
	public void setupContainerPanel(final JPanel containerSlotsPanel)
	{
		this.inventorySlots = new ArrayList<>();
		for (int i = 0; i < NUM_INVENTORY_ITEMS; i++)
		{
			inventorySlots.add(new InventorySetupsSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupsSlotID.INVENTORY, i));
		}

		int numRows = (NUM_INVENTORY_ITEMS + ITEMS_PER_ROW - 1) / ITEMS_PER_ROW;
		containerSlotsPanel.setLayout(new GridLayout(numRows, ITEMS_PER_ROW, 1, 1));
		for (int i = 0; i < NUM_INVENTORY_ITEMS; i++)
		{
			containerSlotsPanel.add(inventorySlots.get(i));
			super.addFuzzyMouseListenerToSlot(inventorySlots.get(i));
			super.addStackMouseListenerToSlot(inventorySlots.get(i));
			super.addUpdateFromContainerMouseListenerToSlot(inventorySlots.get(i));
			super.addUpdateFromSearchMouseListenerToSlot(inventorySlots.get(i), true);
			super.addRemoveMouseListenerToSlot(inventorySlots.get(i));
		}
	}

	@Override
	public void updatePanelWithSetupInformation(final InventorySetup setup)
	{
		for (int i = 0; i < NUM_INVENTORY_ITEMS; i++)
		{
			super.setSlotImageAndText(inventorySlots.get(i), setup, setup.getInventory().get(i));
		}

		validate();
		repaint();
	}

	@Override
	public void highlightSlots(final ArrayList<InventorySetupsItem> currInventory, final InventorySetup inventorySetup)
	{
		final ArrayList<InventorySetupsItem> inventoryToCheck = inventorySetup.getInventory();

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
			InventorySetupsItem currInvItem = currInventory.get(i);
			if (!currInvHasRunePouch && InventorySetupsVariationMapping.map(currInvItem.getId()) == ItemID.RUNE_POUCH)
			{
				currInvHasRunePouch = true;
			}
			super.highlightSlot(inventorySetup, inventoryToCheck.get(i), currInventory.get(i), inventorySlots.get(i));
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

		for (InventorySetupsSlot inventorySlot : inventorySlots)
		{
			inventorySlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}

		runePouchPanel.resetSlotColors();

		isHighlighted = false;
	}

	public boolean isStackCompareForSlotAllowed(final int id)
	{
		return true;
	}

	private void doUnorderedHighlighting(final ArrayList<InventorySetupsItem> currInventory, final InventorySetup inventorySetup)
	{
		HashMap<Integer, ArrayList<Integer>> currentInventoryMapping = new HashMap<>();

		// collect items in current inventory in the form of a HashMap -> ArrayList of stack sizes
		boolean currInvHasRunePouch = false;
		for (final InventorySetupsItem item : currInventory)
		{
			// Use fuzzy mapping
			if (InventorySetupsVariationMapping.map(item.getId()) == ItemID.RUNE_POUCH)
			{
				currInvHasRunePouch = true;
			}

			ArrayList<Integer> currentItemList = currentInventoryMapping.get(item.getId());
			if (currentItemList == null)
			{
				currentItemList = new ArrayList<>();
				currentInventoryMapping.put(item.getId(), currentItemList);
			}
			currentItemList.add(item.getQuantity());
		}

		final ArrayList<InventorySetupsItem> setupInventory = inventorySetup.getInventory();

		ArrayList<Boolean> processedInventoryItems = new ArrayList<>(Arrays.asList(new Boolean[setupInventory.size()]));
		Collections.fill(processedInventoryItems, Boolean.FALSE);

		// First process non fuzzy exact items, then fuzzy exact items
		processExactItems(inventorySetup, currentInventoryMapping, processedInventoryItems, false);
		processExactItems(inventorySetup, currentInventoryMapping, processedInventoryItems, true);

		// now process any items left which may use fuzzy mappings
		for (int i = 0; i < setupInventory.size(); i++)
		{
			// the item was already processed above using exact match
			if (processedInventoryItems.get(i))
			{
				continue;
			}

			final InventorySetupsItem savedItemFromInventory = setupInventory.get(i);

			// Exact items have been handled, try to find fuzzy items
			int savedItemId = savedItemFromInventory.getId();
			ArrayList<Integer> currentItemListForSpecificId = currentInventoryMapping.get(savedItemId);
			if (currentItemListForSpecificId == null && savedItemFromInventory.isFuzzy()) // item list should always return null, exact matches handled above
			{
				// if the item is fuzzy, attempt to find a suitable item
				for (final int currentItemIdFromMapping : currentInventoryMapping.keySet())
				{
					if (InventorySetupsVariationMapping.map(currentItemIdFromMapping) == InventorySetupsVariationMapping.map(savedItemId))
					{
						currentItemListForSpecificId = currentInventoryMapping.get(currentItemIdFromMapping);
						savedItemId = currentItemIdFromMapping; // Needed to delete the correct list later
						break;
					}
				}
			}

			// if we could not find a item Id that is sufficient for that item, highlight it
			if (currentItemListForSpecificId == null)
			{
				inventorySlots.get(i).setBackground(inventorySetup.getHighlightColor());
				continue;
			}

			updateCurrentUnorderedSlot(savedItemId, inventorySetup, inventorySlots.get(i), savedItemFromInventory, currentItemListForSpecificId, currentInventoryMapping);
		}

		final boolean currInvHasRunePouchFinal = currInvHasRunePouch;
		plugin.getClientThread().invokeLater(() ->
		{
			handleRunePouchHighlighting(inventorySetup, currInvHasRunePouchFinal);
		});

	}

	private void processExactItems(final InventorySetup inventorySetup,
									final HashMap<Integer, ArrayList<Integer>> currentInventoryMapping,
									final ArrayList<Boolean> processedInvItems,
									final boolean allowFuzzy)
	{
		ArrayList<InventorySetupsItem> setupInventory = inventorySetup.getInventory();

		// Handle exact non fuzzy items first
		// Exact items will be preferred first
		for (int i = 0; i < setupInventory.size(); i++)
		{
			if (processedInvItems.get(i))
			{
				continue;
			}

			final InventorySetupsItem savedItemFromInventory = setupInventory.get(i);

			// don't count empty spaces. We only want to show items that are missing, not "extra items"
			// that would be indicated by highlighting empty slots.
			if (savedItemFromInventory.getId() == -1)
			{
				inventorySlots.get(i).setBackground(ColorScheme.DARKER_GRAY_COLOR);
				processedInvItems.set(i, Boolean.TRUE);
				continue;
			}

			// Only mark it as processed if the item exists. If it doesn't there may be a fuzzy variant available
			ArrayList<Integer> currentItemListForSpecificId = currentInventoryMapping.get(savedItemFromInventory.getId());
			if (currentItemListForSpecificId != null && (allowFuzzy || !savedItemFromInventory.isFuzzy()))
			{
				updateCurrentUnorderedSlot(savedItemFromInventory.getId(), inventorySetup, inventorySlots.get(i), savedItemFromInventory, currentItemListForSpecificId, currentInventoryMapping);
				processedInvItems.set(i, Boolean.TRUE);
			}

		}
	}


	private void updateCurrentUnorderedSlot(int itemId, final InventorySetup inventorySetup,
											final InventorySetupsSlot currentSlot,
											final InventorySetupsItem savedItemFromInventory,
											final ArrayList<Integer> currentItemListForSpecificId,
											final HashMap<Integer, ArrayList<Integer>> currentInventoryMapping)
	{
		// This assumes the last item contains the correct quantity. This is done because
		// in the actual game, you can't have two stacks of stackable items. This assumption
		// is fine for stackable items as well.
		Integer currentInventoryItemQty = currentItemListForSpecificId.get(currentItemListForSpecificId.size() - 1);

		// delete the list if it's empty to simplify things
		currentItemListForSpecificId.remove(currentItemListForSpecificId.size() - 1);
		if (currentItemListForSpecificId.isEmpty())
		{
			currentInventoryMapping.remove(itemId);
		}

		if (this.shouldHighlightSlotBasedOnStack(savedItemFromInventory.getStackCompare(), savedItemFromInventory.getQuantity(), currentInventoryItemQty))
		{
			currentSlot.setBackground(inventorySetup.getHighlightColor());
		}
		else
		{
			currentSlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}
	}

	private void handleRunePouchHighlighting(final InventorySetup inventorySetup, boolean doesCurrentInventoryHasRunePouch)
	{
		if (inventorySetup.getRune_pouch() != null)
		{

			// attempt to highlight if rune pouch is available
			if (doesCurrentInventoryHasRunePouch)
			{
				ArrayList<InventorySetupsItem> runePouchToCheck = plugin.getRunePouchData();
				runePouchPanel.highlightSlots(runePouchToCheck, inventorySetup);
			}
			else // if the current inventory doesn't have a rune pouch but the setup does, highlight the RP pouch
			{
				runePouchPanel.highlightAllSlots(inventorySetup);
			}
		}
		else
		{
			runePouchPanel.resetSlotColors();
		}
	}
}
