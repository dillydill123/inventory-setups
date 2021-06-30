package inventorysetups.ui;

import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsSection;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class InventorySetupsSectionPanel extends JPanel
{
	protected final InventorySetupsPlugin plugin;
	protected final InventorySetupsPluginPanel panel;
	protected final InventorySetupsSection section;

	InventorySetupsSectionPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetupsSection section)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.section = section;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		setPreferredSize(new Dimension(0, 24));

		JPanel nameWrapper = new JPanel(new BorderLayout());
		nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		final FlatTextField nameInput = new FlatTextField();
		nameInput.setText(section.getName());
		nameInput.setBorder(null);
		nameInput.setEditable(false);
		nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameInput.setPreferredSize(new Dimension(0, 24));
		nameInput.getTextField().setForeground(Color.WHITE);
		nameInput.getTextField().setBorder(new EmptyBorder(0, 0, 0, 0));

		nameWrapper.add(nameInput, BorderLayout.CENTER);

		add(nameWrapper, BorderLayout.NORTH);

		nameInput.getTextField().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					section.setMaximized(!section.isMaximized());
					panel.rebuild(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				nameInput.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}
		});

	}

	public void updateName(final String newName)
	{

	}

	public void addSetup(final Long newId)
	{

	}

	public void removeSetup(final Long idToBeRemoved)
	{

	}

}
