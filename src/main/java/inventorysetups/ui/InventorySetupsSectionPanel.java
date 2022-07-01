package inventorysetups.ui;

import inventorysetups.*;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

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

	InventorySetupsSectionPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetupsSection section, Map<String, InventorySetup> includedSetups)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.section = section;

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
					section.setMaximized(!section.isMaximized());
					updateMinMaxLabel();
					plugin.updateConfig(false, true);
					panel.redrawOverviewPanel(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				minMaxLabel.setIcon(section.isMaximized() ? MIN_MAX_SECTION_HOVER_ICON : NO_MIN_MAX_SECTION_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
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
																					popupMenu, MAX_SETUP_NAME_LENGTH, nameWrapperColor);
		final JPanel westNameActions = new JPanel(new BorderLayout());
		westNameActions.setBackground(nameWrapperColor);
		westNameActions.add(Box.createRigidArea(new Dimension(6, 0)), BorderLayout.WEST);
		westNameActions.add(minMaxLabel, BorderLayout.CENTER);

		nameActions.add(westNameActions, BorderLayout.WEST);

		JPanel nameWrapper = new JPanel();
		nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameWrapper.setLayout(new BorderLayout());
		nameWrapper.add(nameActions, BorderLayout.CENTER);

		nameWrapper.setComponentPopupMenu(popupMenu);
		add(nameWrapper, BorderLayout.NORTH);

		// Only add the section if it's maximized
		if (section.isMaximized())
		{
			// This panel will contain all the setups that are part of the section
			JPanel setupsPanel = new JPanel();
			setupsPanel.setLayout(new GridBagLayout());
			setupsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1;
			constraints.gridx = 0;
			constraints.gridy = 0;

			setupsPanel.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
			constraints.gridy++;
			for (final String setupName : section.getSetups())
			{
				if (!includedSetups.containsKey(setupName))
				{
					continue;
				}

				final InventorySetup setup = includedSetups.get(setupName);
				InventorySetupsPanel newPanel = null;

				final JPanel wrapperPanelForSetup = new JPanel();
				wrapperPanelForSetup.setLayout(new BorderLayout());
				if (plugin.getConfig().compactMode())
				{
					newPanel = new InventorySetupsCompactPanel(plugin, panel, setup, section);
				}
				else
				{
					newPanel = new InventorySetupsStandardPanel(plugin, panel, setup, section);
				}
				// Add an indentation to the setup
				wrapperPanelForSetup.add(Box.createRigidArea(new Dimension(12, 0)), BorderLayout.WEST);
				wrapperPanelForSetup.add(newPanel, BorderLayout.CENTER);

				setupsPanel.add(wrapperPanelForSetup, constraints);
				constraints.gridy++;

				setupsPanel.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
				constraints.gridy++;
			}

			add(setupsPanel, BorderLayout.SOUTH);
		}

	}

	@Override
	public boolean isNameValid(final String name)
	{
		return !name.isEmpty() &&
				!plugin.getSectionNames().contains(name) &&
				!section.getName().equals(name);
	}

	@Override
	public void updateName(final String newName)
	{
		plugin.updateSectionName(section, newName);
	}

	private void updateMinMaxLabel()
	{
		minMaxLabel.setToolTipText(section.isMaximized() ? "Minimize section" : "Maximize section");
		minMaxLabel.setIcon(section.isMaximized() ? MIN_MAX_SECTION_ICON : NO_MIN_MAX_SECTION_ICON);
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
