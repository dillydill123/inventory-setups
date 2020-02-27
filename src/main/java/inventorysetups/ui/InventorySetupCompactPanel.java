package inventorysetups.ui;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupsPlugin;
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

public class InventorySetupCompactPanel extends InventorySetupPanel
{


	InventorySetupCompactPanel(InventorySetupsPlugin plugin, InventorySetupPluginPanel panel, InventorySetup invSetup)
	{
		super(plugin, panel, invSetup);

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		setPreferredSize(new Dimension(0, 24));

		JPanel nameWrapper = new JPanel(new BorderLayout());
		nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		final FlatTextField nameInput = new FlatTextField();
		nameInput.setText(inventorySetup.getName());
		nameInput.setBorder(null);
		nameInput.setEditable(false);
		nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameInput.setPreferredSize(new Dimension(0, 24));
		nameInput.getTextField().setForeground(Color.WHITE);
		nameInput.getTextField().setBorder(new EmptyBorder(0, 8, 0, 0));

		nameWrapper.add(nameInput, BorderLayout.CENTER);

		add(nameWrapper, BorderLayout.NORTH);

		nameInput.getTextField().setComponentPopupMenu(moveSetupPopupMenu);
		nameInput.getTextField().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					panel.setCurrentInventorySetup(inventorySetup, true);
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
}
