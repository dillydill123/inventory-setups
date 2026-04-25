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

import com.google.common.base.Strings;
import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupsItem;
import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsSlotID;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lombok.Getter;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

// The panel that contains the equipment slots
public class InventorySetupsEquipmentPanel extends InventorySetupsContainerPanel
{
	private Map<EquipmentInventorySlot, InventorySetupsSlot> equipmentSlots;

	@Getter
	private InventorySetupsQuiverPanel quiverPanel;

	@Getter
	private JLabel attackOptionLabel;

	// Smaller font for the long attack option names.
	private Font smallFontForAttackOptionLabel;

	InventorySetupsEquipmentPanel(final ItemManager itemManager, final InventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Equipment");
	}

	@Override
	public void setupContainerPanel(final JPanel containerSlotsPanel)
	{
		this.equipmentSlots = new HashMap<>();
		for (final EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			final InventorySetupsSlot setupSlot = new InventorySetupsSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupsSlotID.EQUIPMENT, slot.getSlotIdx());
			InventorySetupsSlot.addFuzzyMouseListenerToSlot(plugin, setupSlot);

			// add stackable configurations for ammo and weapon slots
			if (slot == EquipmentInventorySlot.AMMO)
			{
				InventorySetupsSlot.addStackMouseListenerToSlot(plugin, setupSlot);
			}
			if (slot == EquipmentInventorySlot.WEAPON)
			{
				InventorySetupsSlot.addAttackOptionListenerToSlot(plugin, setupSlot);
				InventorySetupsSlot.addStackMouseListenerToSlot(plugin, setupSlot);
			}

			InventorySetupsSlot.addUpdateFromContainerMouseListenerToSlot(plugin, setupSlot);
			InventorySetupsSlot.addUpdateFromSearchMouseListenerToSlot(plugin, setupSlot, true);
			InventorySetupsSlot.addRemoveMouseListenerToSlot(plugin, setupSlot);

			// Shift menu
			InventorySetupsSlot.addUpdateFromContainerToAllInstancesMouseListenerToSlot(this, plugin, setupSlot);
			InventorySetupsSlot.addUpdateFromSearchToAllInstancesMouseListenerToSlot(this, plugin, setupSlot, true);

			equipmentSlots.put(slot, setupSlot);
		}

		final GridLayout gridLayout = new GridLayout(5, 3, 1, 1);
		containerSlotsPanel.setLayout(gridLayout);

		this.quiverPanel = new InventorySetupsQuiverPanel(itemManager, plugin);

		this.attackOptionLabel = new JLabel();
		this.smallFontForAttackOptionLabel = FontManager.getRunescapeSmallFont().deriveFont(13f);
		this.attackOptionLabel.setFont(FontManager.getRunescapeSmallFont());

		JPanel weaponWithLabel = new JPanel(new BorderLayout());
		Dimension size = new Dimension(InventorySetupsSlot.SLOT_WIDTH, InventorySetupsSlot.SLOT_HEIGHT);
		weaponWithLabel.setSize(size);
		weaponWithLabel.setPreferredSize(size);
		weaponWithLabel.add(attackOptionLabel, BorderLayout.NORTH);

		// add the grid layouts, including invisible ones
		containerSlotsPanel.add(new InventorySetupsSlot(ColorScheme.DARK_GRAY_COLOR, InventorySetupsSlotID.EQUIPMENT, -1));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.HEAD));
		// This slot (to the right of the HEAD) is the quiver slot. It will only show up if a user has a quiver.
		containerSlotsPanel.add(quiverPanel.getQuiverSlot());
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.CAPE));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.AMULET));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.AMMO));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.WEAPON));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.BODY));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.SHIELD));
		// We add the attack option label, so it can sit right below the weapon slot.
		containerSlotsPanel.add(weaponWithLabel);
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.LEGS));
		containerSlotsPanel.add(new InventorySetupsSlot(ColorScheme.DARK_GRAY_COLOR, InventorySetupsSlotID.EQUIPMENT, -1));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.GLOVES));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.BOOTS));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.RING));

	}

	@Override
	public void updatePanelWithSetupInformation(final InventorySetup setup)
	{
		for (final EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			int i = slot.getSlotIdx();
			InventorySetupsSlot.setSlotImageAndText(itemManager, equipmentSlots.get(slot), setup, setup.getEquipment().get(i));
		}

		// Longrange is the longest attack option and gets cut off. Since it's the only one, we can just use a small
		// font for it.
		if (setup.getAttackOption().length() >= 9)
		{
			attackOptionLabel.setFont(this.smallFontForAttackOptionLabel);
		}
		else
		{
			attackOptionLabel.setFont(FontManager.getRunescapeSmallFont());
		}
		attackOptionLabel.setText(setup.getAttackOption());

		validate();
		repaint();
	}

	@Override
	public void highlightSlots(final List<InventorySetupsItem> currentEquipment, final InventorySetup inventorySetup)
	{
		final List<InventorySetupsItem> savedEquipmentFromSetup = inventorySetup.getEquipment();

		assert currentEquipment.size() == savedEquipmentFromSetup.size() : "size mismatch";

		isHighlighted = true;

		for (final EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			int slotIdx = slot.getSlotIdx();
			if (slot.getSlotIdx() == EquipmentInventorySlot.WEAPON.getSlotIdx() && !Strings.isNullOrEmpty(inventorySetup.getAttackOption()))
			{
				// Highlight weapon special case based on attack option as well.
				String currentAttackStyleOption = plugin.getAttackStyleCache().getCurrentAttackOption();
				if (!currentAttackStyleOption.equals(inventorySetup.getAttackOption()))
				{
					InventorySetupsSlot.doHighlight(inventorySetup, equipmentSlots.get(slot));
					attackOptionLabel.setForeground(inventorySetup.getHighlightColor());
				}
				else
				{
					attackOptionLabel.setForeground(null);
					InventorySetupsSlot.highlightSlot(inventorySetup, savedEquipmentFromSetup.get(slotIdx), currentEquipment.get(slotIdx), equipmentSlots.get(slot));
				}
			}
			else
			{
				InventorySetupsSlot.highlightSlot(inventorySetup, savedEquipmentFromSetup.get(slotIdx), currentEquipment.get(slotIdx), equipmentSlots.get(slot));
			}
		}
	}

	@Override
	public void resetSlotColors()
	{
		// Don't waste time resetting if we were never highlighted to begin with
		if (!isHighlighted)
		{
			return;
		}

		for (final EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			equipmentSlots.get(slot).setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}

		attackOptionLabel.setForeground(null);
		isHighlighted = false;
	}

	public boolean isStackCompareForSlotAllowed(final int id)
	{
		return id == EquipmentInventorySlot.AMMO.getSlotIdx() || id == EquipmentInventorySlot.WEAPON.getSlotIdx();
	}
}
