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
import inventorysetups.InventorySetupsSection;
import inventorysetups.InventorySetupsSortingID;
import inventorysetups.InventorySetupsPlugin;

import javax.swing.*;
import java.util.List;

// The base class for panels that each display a setup
public class InventorySetupsPanel extends JPanel implements InventorySetupsMoveHandler<InventorySetup>
{

	protected final InventorySetupsPlugin plugin;
	protected final InventorySetupsPluginPanel panel;
	protected final InventorySetup inventorySetup;
	protected InventorySetupsSection section;
	protected final JPopupMenu popupMenu;

	InventorySetupsPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetup invSetup, InventorySetupsSection section)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.inventorySetup = invSetup;
		this.section = section;
		this.popupMenu = new InventorySetupsMoveMenu<>(plugin, panel, this, "Inventory Setup", invSetup);

		JMenuItem addToSection = new JMenuItem("Add Setup to Section...");
		popupMenu.add(addToSection);

		// If the section is not null, then add a menu to remove this setup from that section
		if (this.section != null)
		{
			JMenuItem removeFromSection = new JMenuItem("Remove from section");
			removeFromSection.addActionListener(e ->
			{
				plugin.removeInventorySetupFromSection(invSetup, section);
			});

			popupMenu.add(removeFromSection);
		}

		addToSection.addActionListener(e ->
		{
			final String[] sectionNames = plugin.getSections().stream().map(InventorySetupsSection::getName).toArray(String[]::new);
			final String message = "Select sections to add this setup to";
			final String title = "Select Sections";
			InventorySetupsSelectionPanel selectionDialog = new InventorySetupsSelectionPanel(panel, title, message, sectionNames);
			selectionDialog.show();
			List<String> selectedSections = selectionDialog.getSelectedItems();

			if (!selectedSections.isEmpty())
			{
				plugin.addSetupToSections(invSetup, selectedSections);
			}
		});

		setComponentPopupMenu(popupMenu);
	}

	@Override
	public void moveUp(final InventorySetup invSetup)
	{
		int invIndex = plugin.getInventorySetups().indexOf(invSetup);
		plugin.moveSetup(invIndex, invIndex - 1);
	}

	@Override
	public void moveDown(final InventorySetup invSetup)
	{
		int invIndex = plugin.getInventorySetups().indexOf(invSetup);
		plugin.moveSetup(invIndex, invIndex + 1);
	}

	@Override
	public void moveToTop(final InventorySetup invSetup)
	{
		int invIndex = plugin.getInventorySetups().indexOf(invSetup);
		plugin.moveSetup(invIndex, 0);
	}

	@Override
	public void moveToBottom(final InventorySetup invSetup)
	{
		int invIndex = plugin.getInventorySetups().indexOf(invSetup);
		plugin.moveSetup(invIndex, plugin.getInventorySetups().size() - 1);
	}

	@Override
	public void moveToPosition(final InventorySetup invSetup)
	{
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
	}

}
