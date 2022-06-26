package inventorysetups.ui;

import inventorysetups.*;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import static inventorysetups.InventorySetupsPlugin.MAX_SETUP_NAME_LENGTH;

public class InventorySetupsSectionPanel extends JPanel implements InventorySetupsValidName
{
	protected final InventorySetupsPlugin plugin;
	protected final InventorySetupsPluginPanel panel;
	protected final InventorySetupsSection section;

	InventorySetupsSectionPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetupsSection section, Map<String, InventorySetup> includedSetups)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.section = section;

		this.setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		final InventorySetupsNameActions<InventorySetupsSection> nameActions = new InventorySetupsNameActions<>(section, plugin, panel, this, null, MAX_SETUP_NAME_LENGTH);

		JPanel setupsPanel = new JPanel();
		setupsPanel.setLayout(new GridBagLayout());
		setupsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;

		for (final String setupName : section.getSetups())
		{
			if (!includedSetups.containsKey(setupName))
			{
				continue;
			}

			final InventorySetup setup = includedSetups.get(setupName);
			InventorySetupsPanel newPanel = null;
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

		add(nameActions, BorderLayout.NORTH);
		add(setupsPanel, BorderLayout.SOUTH);

	}

	@Override
	public boolean isNameValid(final String name)
	{
		return true;
	}

}
