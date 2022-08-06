package inventorysetups.ui;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsSection;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemID;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;

public class InventorySetupsIconPanel extends InventorySetupsPanel
{
	InventorySetupsIconPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetup invSetup, InventorySetupsSection section)
	{
		this(plugin, panel, invSetup, section, true);
	}

	InventorySetupsIconPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetup invSetup, InventorySetupsSection section, boolean allowEditable)
	{
		super(plugin, panel, invSetup, section, allowEditable);

		setLayout(new GridBagLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		setPreferredSize(new Dimension(46, 42));

		JLabel imageLabel = new JLabel();
		int itemIDForImage = invSetup.getIconID();
		// ID 0 is "Dwarf Remains" meaning setups saved before iconID was added will default to 0
		// and a picture of "Dwarf Remains" will be used. So exclude 0 as well and select a weapon
		// Since most people will probably not want Dwarf Remains as the icon...
		if (itemIDForImage <= 0)
		{
			itemIDForImage = invSetup.getEquipment().get(EquipmentInventorySlot.WEAPON.getSlotIdx()).getId();
			if (itemIDForImage <= 0)
			{
				itemIDForImage = ItemID.CAKE_OF_GUIDANCE;
			}
		}

		AsyncBufferedImage itemImg = plugin.getItemManager().getImage(itemIDForImage, 1, false);
		itemImg.onLoaded(this::repaint);
		imageLabel.setIcon(new ImageIcon(itemImg));
		add(imageLabel);

		setToolTipText(invSetup.getName());
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					panel.setCurrentInventorySetup(invSetup, true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}
		});

		JMenuItem updateIcon = new JMenuItem("Update Icon..");
		updateIcon.addActionListener(e -> plugin.updateInventorySetupIcon(invSetup));
		popupMenu.add(updateIcon);
	}
}
