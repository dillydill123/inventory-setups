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

import inventorysetups.InventorySetupStackCompare;
import inventorysetups.InventorySetupsPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupItem;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.util.ArrayList;

public abstract class InventorySetupContainerPanel extends JPanel
{

	protected ItemManager itemManager;

	protected boolean isHighlighted;

	protected final InventorySetupsPlugin plugin;

	@Getter(AccessLevel.PROTECTED)
	private final JPanel containerSlotsPanel;

	InventorySetupContainerPanel(final ItemManager itemManager, final InventorySetupsPlugin plugin, String captionText)
	{
		this.itemManager = itemManager;
		this.plugin = plugin;
		this.isHighlighted = false;
		JPanel containerPanel = new JPanel();

		this.containerSlotsPanel = new JPanel();

		// sets up the custom container panel
		setupContainerPanel(containerSlotsPanel);

		// caption
		final JLabel caption = new JLabel(captionText);
		caption.setHorizontalAlignment(JLabel.CENTER);
		caption.setVerticalAlignment(JLabel.CENTER);

		// panel that holds the caption and any other graphics
		final JPanel captionPanel = new JPanel();
		captionPanel.add(caption);

		containerPanel.setLayout(new BorderLayout());
		containerPanel.add(captionPanel, BorderLayout.NORTH);
		containerPanel.add(containerSlotsPanel, BorderLayout.CENTER);

		add(containerPanel);
	}

	protected void addUpdateFromContainerMouseListenerToSlot(final InventorySetupSlot slot)
	{
		setComponentPopupMenuToSlot(slot);
		JPopupMenu popupMenu = slot.getComponentPopupMenu();

		String updateContainerFrom = "";
		switch (slot.getSlotID())
		{
			case INVENTORY:
				updateContainerFrom = "Inventory";
				break;
			case EQUIPMENT:
				updateContainerFrom = "Equipment";
				break;
			case RUNE_POUCH:
				updateContainerFrom = "Rune Pouch";
				break;
			default:
				assert false : "Wrong slot ID!";
				break;
		}
		JMenuItem updateFromContainer = new JMenuItem("Update Slot from " + updateContainerFrom);
		popupMenu.add(updateFromContainer);
		updateFromContainer.addActionListener(e ->
		{
			plugin.updateSlotFromContainer(slot);
		});
	}

	protected void addUpdateFromSearchMouseListenerToSlot(final InventorySetupSlot slot, boolean allowStackable)
	{
		setComponentPopupMenuToSlot(slot);
		JPopupMenu popupMenu = slot.getComponentPopupMenu();
		JMenuItem updateFromSearch = new JMenuItem("Update Slot from Search");
		popupMenu.add(updateFromSearch);
		updateFromSearch.addActionListener(e ->
		{
			plugin.updateSlotFromSearch(slot, allowStackable);
		});
	}

	protected void addRemoveMouseListenerToSlot(final InventorySetupSlot slot)
	{
		setComponentPopupMenuToSlot(slot);
		JPopupMenu popupMenu = slot.getComponentPopupMenu();
		JMenuItem removeSlot = new JMenuItem("Remove Item from Slot");
		popupMenu.add(removeSlot);
		removeSlot.addActionListener(e ->
		{
			plugin.removeItemFromSlot(slot);
		});
	}

	private void setComponentPopupMenuToSlot(final InventorySetupSlot slot)
	{
		if (slot.getComponentPopupMenu() == null)
		{
			// both the panel and image label need adapters
			// because the image will cover the entire panel
			JPopupMenu newMenu = new JPopupMenu();
			slot.setComponentPopupMenu(newMenu);
			slot.getImageLabel().setComponentPopupMenu(newMenu);
		}
	}

	protected void setContainerSlot(int index, final InventorySetupSlot containerSlot, final InventorySetup setup, final InventorySetupItem item)
	{
		containerSlot.setParentSetup(setup);

		if (item.getId() == -1)
		{
			containerSlot.setImageLabel(null, null);
			return;
		}

		int itemId = item.getId();
		int quantity = item.getQuantity();
		final String itemName = item.getName();
		AsyncBufferedImage itemImg = itemManager.getImage(itemId, quantity, quantity > 1);
		String toolTip = itemName;
		if (quantity > 1)
		{
			toolTip += " (" + quantity + ")";
		}
		containerSlot.setImageLabel(toolTip, itemImg);
	}

	protected void highlightDifferentSlotColor(final InventorySetup setup, InventorySetupItem savedItem, InventorySetupItem currItem, final InventorySetupSlot containerSlot)
	{
		// important note: do not use item names for comparisons
		// they are all empty to avoid clientThread usage when highlighting

		// first check if stack differences are enabled and compare quantities
		if (highlightBasedOnStack(setup, savedItem.getQuantity(), currItem.getQuantity()))
		{
			containerSlot.setBackground(setup.getHighlightColor());
			return;
		}

		// obtain the correct item ids using the variation difference if applicable
		int currId = currItem.getId();
		int checkId = savedItem.getId();

		if (!setup.isVariationDifference())
		{
			currId = ItemVariationMapping.map(currId);
			checkId = ItemVariationMapping.map(checkId);
		}

		// if the ids don't match, highlight the container slot
		if (currId != checkId)
		{
			containerSlot.setBackground(setup.getHighlightColor());
			return;
		}

		// set the color back to the original, because they match
		containerSlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
	}

	protected boolean highlightBasedOnStack(final InventorySetup setup, final Integer savedItemQty, final Integer currItemQty)
	{
		final int stackRes = Integer.compare(currItemQty, savedItemQty);
		final InventorySetupStackCompare stackCompareType = InventorySetupStackCompare.getValues().get(setup.getStackDifference());
		return stackCompareType == InventorySetupStackCompare.Less_Than && stackRes < 0 ||
				stackCompareType == InventorySetupStackCompare.Greater_Than && stackRes > 0 ||
				stackCompareType == InventorySetupStackCompare.Standard && stackRes != 0;
	}

	abstract public void setupContainerPanel(final JPanel containerSlotsPanel);

	abstract public void highlightSlotDifferences(final ArrayList<InventorySetupItem> currContainer, final InventorySetup inventorySetup);

	abstract public void setSlots(final InventorySetup setup);

	abstract public void resetSlotColors();
}
