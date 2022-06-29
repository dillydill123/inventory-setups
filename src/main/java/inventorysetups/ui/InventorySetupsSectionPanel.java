package inventorysetups.ui;

import inventorysetups.*;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

import static inventorysetups.InventorySetupsPlugin.MAX_SETUP_NAME_LENGTH;

public class InventorySetupsSectionPanel extends JPanel implements InventorySetupsValidName
{
	protected final InventorySetupsPlugin plugin;
	protected final InventorySetupsPluginPanel panel;
	private InventorySetupsSection section;

	private final JLabel minMaxLabel;

	private static final ImageIcon MIN_MAX_SECTION_ICON;
	private static final ImageIcon MIN_MAX_SECTION_HOVER_ICON;
	private static final ImageIcon NO_MIN_MAX_SECTION_ICON;
	private static final ImageIcon NO_MIN_MAX_SECTION_HOVER_ICON;

	static
	{
		final BufferedImage minMaxSectionImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/back_arrow_icon.png");
		final BufferedImage minMaxSectionHoverImg = ImageUtil.luminanceOffset(minMaxSectionImg, -150);
		MIN_MAX_SECTION_ICON = new ImageIcon(minMaxSectionImg);
		MIN_MAX_SECTION_HOVER_ICON = new ImageIcon(minMaxSectionHoverImg);

		NO_MIN_MAX_SECTION_ICON = new ImageIcon(minMaxSectionHoverImg);
		NO_MIN_MAX_SECTION_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(minMaxSectionHoverImg, -100));
	}

	InventorySetupsSectionPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetupsSection section, Map<String, InventorySetup> includedSetups)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.section = section;

		this.setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		final InventorySetupsNameActions<InventorySetupsSection> nameActions = new InventorySetupsNameActions<>(section, plugin, panel, this, null, MAX_SETUP_NAME_LENGTH);

		// Label that will be used to minimize or maximize setups in section

		this.minMaxLabel = new JLabel();
		updateMinMaxLabel();
		minMaxLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				section.setMaximized(!section.isMaximized());
				updateMinMaxLabel();
				// TODO: Test scrollbar position with lots of sections. It shouldn't reset
				plugin.updateConfig(false, true);
				panel.rebuild(false);
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

		// Add the button to nameActions so the color border will reach it as well.
		nameActions.add(minMaxLabel, BorderLayout.WEST);

		JPanel nameWrapper = new JPanel();
		nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameWrapper.setLayout(new BorderLayout());
		nameWrapper.add(nameActions, BorderLayout.CENTER);

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

				// TODO: Pass section value here so panels know which section they are a part of
				if (plugin.getConfig().compactMode())
				{
					newPanel = new InventorySetupsCompactPanel(plugin, panel, setup);
				}
				else
				{
					newPanel = new InventorySetupsStandardPanel(plugin, panel, setup);
				}
				setupsPanel.add(newPanel, constraints);
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
		return true;
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

}
