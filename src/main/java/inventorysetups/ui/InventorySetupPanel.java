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
import inventorysetups.InventorySetupSorting;
import inventorysetups.InventorySetupsPlugin;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class InventorySetupPanel extends JPanel
{

	protected final InventorySetupsPlugin plugin;
	protected final InventorySetupPluginPanel panel;
	protected final InventorySetup inventorySetup;
	protected final JPopupMenu moveSetupPopupMenu;

	InventorySetupPanel(InventorySetupsPlugin plugin, InventorySetupPluginPanel panel, InventorySetup invSetup)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.inventorySetup = invSetup;
		this.moveSetupPopupMenu = new JPopupMenu();

		JMenuItem moveUp = new JMenuItem("Move Inventory Setup Up");
		JMenuItem moveDown = new JMenuItem("Move Inventory Setup Down");
		JMenuItem moveToTop = new JMenuItem("Move Inventory Setup to Top");
		JMenuItem moveToBottom = new JMenuItem("Move Inventory Setup to Bottom");
		JMenuItem moveToPosition = new JMenuItem("Move Inventory Setup to Position...");
		moveSetupPopupMenu.add(moveUp);
		moveSetupPopupMenu.add(moveDown);
		moveSetupPopupMenu.add(moveToTop);
		moveSetupPopupMenu.add(moveToBottom);
		moveSetupPopupMenu.add(moveToPosition);

		moveUp.addActionListener(e ->
		{
			if (!checkSortingMode())
			{
				return;
			}
			int invIndex = plugin.getInventorySetups().indexOf(invSetup);
			plugin.moveSetup(invIndex, invIndex - 1);
		});

		moveDown.addActionListener(e ->
		{
			if (!checkSortingMode())
			{
				return;
			}
			int invIndex = plugin.getInventorySetups().indexOf(invSetup);
			plugin.moveSetup(invIndex, invIndex + 1);
		});

		moveToTop.addActionListener(e ->
		{
			if (!checkSortingMode())
			{
				return;
			}
			int invIndex = plugin.getInventorySetups().indexOf(invSetup);
			plugin.moveSetup(invIndex, 0);
		});
		moveToBottom.addActionListener(e ->
		{
			if (!checkSortingMode())
			{
				return;
			}
			int invIndex = plugin.getInventorySetups().indexOf(invSetup);
			plugin.moveSetup(invIndex, plugin.getInventorySetups().size() - 1);
		});
		moveToPosition.addActionListener(e ->
		{
			if (!checkSortingMode())
			{
				return;
			}
			int invIndex = plugin.getInventorySetups().indexOf(invSetup);
			final String posDialog = "Enter a position between 1 and " + String.valueOf(plugin.getInventorySetups().size()) +
									". Current setup is in position " + String.valueOf(invIndex + 1) + ".";
			final String newPositionStr = JOptionPane.showInputDialog(panel,
					posDialog,
					"Move Setup",
					JOptionPane.PLAIN_MESSAGE);

			// cancel button was clicked
			if (newPositionStr == null)
			{
				return;
			}

			try
			{
				int newPosition = Integer.parseInt(newPositionStr);
				if (newPosition < 1 || newPosition > plugin.getInventorySetups().size())
				{
					JOptionPane.showMessageDialog(panel,
							"Invalid position.",
							"Move Setup Failed",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				plugin.moveSetup(invIndex, newPosition - 1);
			}
			catch (NumberFormatException ex)
			{
				JOptionPane.showMessageDialog(panel,
						"Invalid position.",
						"Move Setup Failed",
						JOptionPane.ERROR_MESSAGE);
			}

		});

		setComponentPopupMenu(moveSetupPopupMenu);
	}

	private boolean checkSortingMode()
	{
		if (plugin.getConfig().sortingMode() != InventorySetupSorting.DEFAULT)
		{
			JOptionPane.showMessageDialog(panel,
					"You cannot move setups while a sorting mode is enabled.",
					"Move Setup Failed",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}
}
