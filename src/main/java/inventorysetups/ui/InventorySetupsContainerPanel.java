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
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.game.ItemManager;

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

	abstract public boolean isStackCompareForSlotAllowed(final int id);

	abstract public void setupContainerPanel(final JPanel containerSlotsPanel);

	abstract public void highlightSlots(final List<InventorySetupsItem> currContainer, final InventorySetup inventorySetup);

	abstract public void updatePanelWithSetupInformation(final InventorySetup setup);

	abstract public void resetSlotColors();
}
