/*
 * Copyright (c) 2022, dillydill123 <https://github.com/dillydill123>
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

import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsSortingID;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.runelite.client.util.ImageUtil;

class InventorySetupsMoveButtons extends JPanel
{
	private static final ImageIcon UP_ICON;
	private static final ImageIcon UP_HOVER_ICON;
	private static final ImageIcon DOWN_ICON;
	private static final ImageIcon DOWN_HOVER_ICON;

	static
	{
		final BufferedImage down = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/down_arrow.png");
		final BufferedImage hover = ImageUtil.luminanceOffset(down, -150);
		DOWN_ICON = new ImageIcon(down);
		DOWN_HOVER_ICON = new ImageIcon(hover);
		UP_ICON = new ImageIcon(ImageUtil.rotateImage(down, Math.PI));
		UP_HOVER_ICON = new ImageIcon(ImageUtil.rotateImage(hover, Math.PI));
	}

	InventorySetupsMoveButtons(InventorySetupsPlugin plugin, String type, int index, int size,
		Runnable moveUp, Runnable moveDown)
	{
		super(new GridLayout(1, 2));
		setOpaque(false);
		add(createMoveButton(plugin, type, true, index > 0, moveUp));
		add(createMoveButton(plugin, type, false, index >= 0 && index < size - 1, moveDown));
	}

	private JButton createMoveButton(InventorySetupsPlugin plugin, String type, boolean up,
		boolean canMove, Runnable move)
	{
		final JButton button = new JButton(up ? UP_ICON : DOWN_ICON);
		button.setRolloverIcon(up ? UP_HOVER_ICON : DOWN_HOVER_ICON);
		button.setDisabledIcon(up ? UP_HOVER_ICON : DOWN_HOVER_ICON);
		button.setPreferredSize(new Dimension(20, 24));
		button.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		button.setContentAreaFilled(false);
		final String action = "Move " + type + (up ? " up" : " down");
		button.getAccessibleContext().setAccessibleName(action);
		final boolean defaultSorting = plugin.getConfig().sortingMode() == InventorySetupsSortingID.DEFAULT;
		button.setEnabled(defaultSorting && canMove);
		button.setToolTipText(defaultSorting ? action : "Switch to default sorting to move " + type + "s");
		button.addActionListener(e ->
		{
			if (plugin.getConfig().sortingMode() == InventorySetupsSortingID.DEFAULT)
			{
				move.run();
			}
		});
		return button;
	}
}
