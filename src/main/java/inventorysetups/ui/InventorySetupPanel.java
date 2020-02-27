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
import inventorysetups.InventorySetupsPlugin;
import javax.swing.JMenuItem;
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
		moveSetupPopupMenu.add(moveUp);
		moveSetupPopupMenu.add(moveDown);

		moveUp.addActionListener(e ->
		{
			plugin.moveSetupUp(invSetup);
		});

		moveDown.addActionListener(e ->
		{
			plugin.moveSetupDown(invSetup);
		});

		setComponentPopupMenu(moveSetupPopupMenu);
	}
}
