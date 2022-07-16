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

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupsPlugin;
import static inventorysetups.InventorySetupsPlugin.CONFIG_KEY_UNASSIGNED_MAXIMIZED;
import inventorysetups.InventorySetupsSection;
import inventorysetups.InventorySetupsValidName;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import static inventorysetups.InventorySetupsPlugin.MAX_SETUP_NAME_LENGTH;

public class InventorySetupsSectionPanel extends JPanel implements InventorySetupsValidName, InventorySetupsMoveHandler<InventorySetupsSection>
{
	protected final InventorySetupsPlugin plugin;
	protected final InventorySetupsPluginPanel panel;
	private final InventorySetupsSection section;

	private final JLabel minMaxLabel;

	private static final ImageIcon MIN_MAX_SECTION_ICON;
	private static final ImageIcon MIN_MAX_SECTION_HOVER_ICON;
	private static final ImageIcon NO_MIN_MAX_SECTION_ICON;
	private static final ImageIcon NO_MIN_MAX_SECTION_HOVER_ICON;
	private boolean forceMaximization;

	static
	{
		final BufferedImage minMaxSectionImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/down_arrow.png");
		final BufferedImage minMaxSectionHoverImg = ImageUtil.luminanceOffset(minMaxSectionImg, -150);
		MIN_MAX_SECTION_ICON = new ImageIcon(minMaxSectionImg);
		MIN_MAX_SECTION_HOVER_ICON = new ImageIcon(minMaxSectionHoverImg);

		final BufferedImage noMinMaxSectionImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/right_arrow.png");
		final BufferedImage noMaxSectionHoverImg = ImageUtil.luminanceOffset(noMinMaxSectionImg, -150);
		NO_MIN_MAX_SECTION_ICON = new ImageIcon(noMinMaxSectionImg);
		NO_MIN_MAX_SECTION_HOVER_ICON = new ImageIcon(noMaxSectionHoverImg);
	}

	InventorySetupsSectionPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetupsSection section, boolean forceMaximization)
	{
		this(plugin, panel, section, forceMaximization, true);
	}

	InventorySetupsSectionPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetupsSection section, boolean forceMaximization, boolean allowEditable)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.section = section;
		this.forceMaximization = forceMaximization;

		this.setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		// Label that will be used to minimize or maximize setups in section
		this.minMaxLabel = new JLabel();
		updateMinMaxLabel();
		minMaxLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					if (allowEditable && !forceMaximization)
					{
						section.setMaximized(!section.isMaximized());
						plugin.updateConfig(false, true);
						panel.redrawOverviewPanel(false);
					}
					else
					{
						// This is for the unassigned section.
						plugin.setConfigValue(CONFIG_KEY_UNASSIGNED_MAXIMIZED, !section.isMaximized());
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (forceMaximization)
				{
					return;
				}

				minMaxLabel.setIcon(section.isMaximized() ? MIN_MAX_SECTION_HOVER_ICON : NO_MIN_MAX_SECTION_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if (forceMaximization)
				{
					return;
				}

				minMaxLabel.setIcon(section.isMaximized() ? MIN_MAX_SECTION_ICON : NO_MIN_MAX_SECTION_ICON);
			}
		});

		// Add the right click menu to delete sections
		JPopupMenu popupMenu = new InventorySetupsMoveMenu<>(plugin, panel, this, "Section", section);
		JMenuItem exportSection = new JMenuItem("Export Section");
		JMenuItem addSetupsToSection = new JMenuItem("Add setups to section..");
		JMenuItem deleteSection = new JMenuItem("Delete Section..");
		exportSection.addActionListener(e ->
		{
			plugin.exportSection(section);
		});
		addSetupsToSection.addActionListener(e ->
		{
			final String[] setupNames = plugin.getInventorySetups().stream().map(InventorySetup::getName).toArray(String[]::new);
			final String message = "Select setups to add to this section";
			final String title = "Select Setups";
			InventorySetupsSelectionPanel selectionDialog = new InventorySetupsSelectionPanel(panel, title, message, setupNames);
			selectionDialog.show();
			List<String> selectedSetups = selectionDialog.getSelectedItems();

			if (!selectedSetups.isEmpty())
			{
				plugin.addSetupsToSection(section, selectedSetups);
			}
		});
		deleteSection.addActionListener(e ->
		{
			plugin.removeSection(section);
		});
		popupMenu.add(addSetupsToSection);
		popupMenu.add(exportSection);
		popupMenu.add(deleteSection);

		// Add the button to nameActions so the color border will reach it as well
		final Color nameWrapperColor = new Color(20, 20, 20);
		final InventorySetupsNameActions<InventorySetupsSection> nameActions = new InventorySetupsNameActions<>(section,
																					plugin, panel, this,
																					popupMenu, MAX_SETUP_NAME_LENGTH,
																					nameWrapperColor, allowEditable);
		final JPanel westNameActions = new JPanel(new BorderLayout());
		westNameActions.setBackground(nameWrapperColor);
		westNameActions.add(Box.createRigidArea(new Dimension(6, 0)), BorderLayout.WEST);
		westNameActions.add(minMaxLabel, BorderLayout.CENTER);

		nameActions.add(westNameActions, BorderLayout.WEST);

		JPanel nameWrapper = new JPanel();
		nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameWrapper.setLayout(new BorderLayout());
		nameWrapper.add(nameActions, BorderLayout.CENTER);

		// If we are in unassigned mode, don't allow the user to edit with the right click pop menu
		if (allowEditable)
		{
			nameWrapper.setComponentPopupMenu(popupMenu);
		}

		add(nameWrapper, BorderLayout.NORTH);

	}

	@Override
	public boolean isNameValid(final String name)
	{
		return !name.isEmpty() &&
				!plugin.getCache().getSectionNames().containsKey(name) &&
				!section.getName().equals(name);
	}

	@Override
	public void updateName(final String newName)
	{
		plugin.updateSectionName(section, newName);
	}

	private void updateMinMaxLabel()
	{
		if (forceMaximization)
		{
			minMaxLabel.setToolTipText("");
			minMaxLabel.setIcon(MIN_MAX_SECTION_ICON);
		}
		else
		{
			minMaxLabel.setToolTipText(section.isMaximized() ? "Minimize section" : "Maximize section");
			minMaxLabel.setIcon(section.isMaximized() ? MIN_MAX_SECTION_ICON : NO_MIN_MAX_SECTION_ICON);
		}
	}

	@Override
	public void moveUp(final InventorySetupsSection section)
	{
		int sectionIndex = plugin.getSections().indexOf(section);
		plugin.moveSection(sectionIndex, sectionIndex - 1);
	}

	@Override
	public void moveDown(final InventorySetupsSection section)
	{
		int sectionIndex = plugin.getSections().indexOf(section);
		plugin.moveSection(sectionIndex, sectionIndex + 1);
	}

	@Override
	public void moveToTop(final InventorySetupsSection section)
	{
		int sectionIndex = plugin.getSections().indexOf(section);
		plugin.moveSection(sectionIndex, 0);
	}

	@Override
	public void moveToBottom(final InventorySetupsSection section)
	{
		int sectionIndex = plugin.getSections().indexOf(section);
		plugin.moveSection(sectionIndex, plugin.getSections().size() - 1);
	}

	@Override
	public void moveToPosition(final InventorySetupsSection section)
	{
		int sectionIndex = plugin.getSections().indexOf(section);
		final String posDialog = "Enter a position between 1 and " + String.valueOf(plugin.getSections().size()) +
				". Current section is in position " + String.valueOf(sectionIndex + 1) + ".";
		final String newPositionStr = JOptionPane.showInputDialog(panel,
				posDialog,
				"Move Section",
				JOptionPane.PLAIN_MESSAGE);

		// cancel button was clicked
		if (newPositionStr == null)
		{
			return;
		}

		try
		{
			int newPosition = Integer.parseInt(newPositionStr);
			if (newPosition < 1 || newPosition > plugin.getSections().size())
			{
				JOptionPane.showMessageDialog(panel,
						"Invalid position.",
						"Move Section Failed",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			plugin.moveSection(sectionIndex, newPosition - 1);
		}
		catch (NumberFormatException ex)
		{
			JOptionPane.showMessageDialog(panel,
					"Invalid position.",
					"Move Section Failed",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
