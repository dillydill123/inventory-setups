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

import inventorysetups.InventorySetupsStackCompareID;
import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsVariationMapping;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.game.ItemManager;
import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupsItem;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.util.ArrayList;

public abstract class InventorySetupsContainerPanel extends JPanel
{

	protected ItemManager itemManager;

	protected boolean isHighlighted;

	protected final InventorySetupsPlugin plugin;

	@Getter(AccessLevel.PROTECTED)
	private final JPanel containerSlotsPanel;

	InventorySetupsContainerPanel(final ItemManager itemManager, final InventorySetupsPlugin plugin, String captionText)
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

	// adds the menu option to update a slot from the container it presides in
	protected void addUpdateFromContainerMouseListenerToSlot(final InventorySetupsSlot slot)
	{
		setSlotComponentPopupMenu(slot);
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

	// adds the menu option to update a slot from item search
	protected void addUpdateFromSearchMouseListenerToSlot(final InventorySetupsSlot slot, boolean allowStackable)
	{
		setSlotComponentPopupMenu(slot);
		JPopupMenu popupMenu = slot.getComponentPopupMenu();
		JMenuItem updateFromSearch = new JMenuItem("Update Slot from Search");
		popupMenu.add(updateFromSearch);
		updateFromSearch.addActionListener(e ->
		{
			plugin.updateSlotFromSearch(slot, allowStackable);
		});
	}

	// adds the menu option to clear a slot
	protected void addRemoveMouseListenerToSlot(final InventorySetupsSlot slot)
	{
		setSlotComponentPopupMenu(slot);
		JPopupMenu popupMenu = slot.getComponentPopupMenu();
		JMenuItem removeSlot = new JMenuItem("Remove Item from Slot");
		popupMenu.add(removeSlot);
		removeSlot.addActionListener(e ->
		{
			plugin.removeItemFromSlot(slot);
		});
	}

	// adds the menu option to update set a slot to fuzzy
	protected void addFuzzyMouseListenerToSlot(final InventorySetupsSlot slot)
	{
		setSlotComponentPopupMenu(slot);
		JPopupMenu popupMenu = slot.getComponentPopupMenu();
		JMenuItem makeSlotFuzzy = new JMenuItem("Toggle Fuzzy");
		popupMenu.add(makeSlotFuzzy);
		makeSlotFuzzy.addActionListener(e ->
		{
			plugin.toggleFuzzyOnSlot(slot);
		});
	}

	// adds the menu option to update set a slot to fuzzy
	protected void addStackMouseListenerToSlot(final InventorySetupsSlot slot)
	{
		setSlotComponentPopupMenu(slot);
		JPopupMenu popupMenu = slot.getComponentPopupMenu();

		JMenuItem stackIndicatorNone = new JMenuItem("Stack Difference None");
		stackIndicatorNone.addActionListener(e ->
		{
			plugin.setStackCompareOnSlot(slot, InventorySetupsStackCompareID.None);
		});

		JMenuItem stackIndicatorStandard = new JMenuItem("Stack Difference Standard");
		stackIndicatorStandard.addActionListener(e ->
		{
			plugin.setStackCompareOnSlot(slot, InventorySetupsStackCompareID.Standard);
		});

		JMenuItem stackIndicatorGreaterThan = new JMenuItem("Stack Difference Greater Than");
		stackIndicatorGreaterThan.addActionListener(e ->
		{
			plugin.setStackCompareOnSlot(slot, InventorySetupsStackCompareID.Greater_Than);
		});

		JMenuItem stackIndicatorLessThan = new JMenuItem("Stack Difference Less Than");
		stackIndicatorLessThan.addActionListener(e ->
		{
			plugin.setStackCompareOnSlot(slot, InventorySetupsStackCompareID.Less_Than);
		});

		JMenu stackIndicatorMainMenu = new JMenu("Stack Indicator");
		stackIndicatorMainMenu.add(stackIndicatorNone);
		stackIndicatorMainMenu.add(stackIndicatorStandard);
		stackIndicatorMainMenu.add(stackIndicatorLessThan);
		stackIndicatorMainMenu.add(stackIndicatorGreaterThan);
		popupMenu.add(stackIndicatorMainMenu);
	}

	// creates a new component slot menu if the slot does not have one already
	private void setSlotComponentPopupMenu(final InventorySetupsSlot slot)
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

	// Sets the image and tooltip text for a slot
	protected void setSlotImageAndText(final InventorySetupsSlot containerSlot, final InventorySetup setup, final InventorySetupsItem item)
	{
		containerSlot.setParentSetup(setup);

		if (item.getId() == -1)
		{
			containerSlot.setImageLabel(null, null, item.isFuzzy(), item.getStackCompare());
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
		containerSlot.setImageLabel(toolTip, itemImg, item.isFuzzy(), item.getStackCompare());
	}

	// highlights the slot based on the configuration and the saved item vs item in the slot
	protected void highlightSlot(final InventorySetup setup, InventorySetupsItem savedItemFromSetup, InventorySetupsItem currentItemFromContainer, final InventorySetupsSlot containerSlot)
	{
		// important note: do not use item names for comparisons
		// they are all empty to avoid clientThread usage when highlighting

		// first check if stack differences are enabled and compare quantities
		if (shouldHighlightSlotBasedOnStack(savedItemFromSetup.getStackCompare(), savedItemFromSetup.getQuantity(), currentItemFromContainer.getQuantity()))
		{
			containerSlot.setBackground(setup.getHighlightColor());
			return;
		}

		// obtain the correct item ids using fuzzy mapping if applicable
		int currentItemId = currentItemFromContainer.getId();
		int savedItemId = savedItemFromSetup.getId();

		if (savedItemFromSetup.isFuzzy())
		{
			currentItemId = InventorySetupsVariationMapping.map(currentItemId);
			savedItemId = InventorySetupsVariationMapping.map(savedItemId);
		}

		// if the ids don't match, highlight the container slot
		if (currentItemId != savedItemId)
		{
			containerSlot.setBackground(setup.getHighlightColor());
			return;
		}

		// set the color back to the original, because they match
		containerSlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
	}

	protected boolean shouldHighlightSlotBasedOnStack(final InventorySetupsStackCompareID stackCompareType, final Integer savedItemQty, final Integer currItemQty)
	{
		final int stackCompareResult = Integer.compare(currItemQty, savedItemQty);
		return stackCompareType == InventorySetupsStackCompareID.Less_Than && stackCompareResult < 0 ||
				stackCompareType == InventorySetupsStackCompareID.Greater_Than && stackCompareResult > 0 ||
				stackCompareType == InventorySetupsStackCompareID.Standard && stackCompareResult != 0;
	}

	abstract public boolean isStackCompareForSlotAllowed(final int id);

	abstract public void setupContainerPanel(final JPanel containerSlotsPanel);

	abstract public void highlightSlots(final ArrayList<InventorySetupsItem> currContainer, final InventorySetup inventorySetup);

	abstract public void updatePanelWithSetupInformation(final InventorySetup setup);

	abstract public void resetSlotColors();
}
