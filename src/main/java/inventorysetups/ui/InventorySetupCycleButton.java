package inventorysetups.ui;

import inventorysetups.InventorySetupsPlugin;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class InventorySetupCycleButton<T> extends JLabel
{
	private final InventorySetupsPlugin plugin;
	private final ArrayList<T> states;
	private final ArrayList<ImageIcon> icons;
	private final ArrayList<ImageIcon> hoverIcons;
	private final ArrayList<String> tooltips;
	private final Runnable runnable;
	private int currentIndex;

	InventorySetupCycleButton(final InventorySetupsPlugin plugin, final ArrayList<T> states, final ArrayList<ImageIcon> icons, final ArrayList<ImageIcon> hoverIcons, final ArrayList<String> tooltips)
	{
		this(plugin, states, icons, hoverIcons, tooltips, () ->
		{
		});
	}

	InventorySetupCycleButton(final InventorySetupsPlugin plugin, final ArrayList<T> states, final ArrayList<ImageIcon> icons, final ArrayList<ImageIcon> hoverIcons, final ArrayList<String> tooltips, final Runnable runnable)
	{
		super();
		this.plugin = plugin;
		this.states = states;
		this.icons = icons;
		this.hoverIcons = hoverIcons;
		this.runnable = runnable;
		this.tooltips = tooltips;
		this.currentIndex = 0;

		// sizes must be equal
		assert this.states.size() == this.icons.size();
		assert this.icons.size() == this.hoverIcons.size();
		assert this.hoverIcons.size() == this.tooltips.size();

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					currentIndex = (currentIndex + 1) % states.size();
					runnable.run();
					setToolTipText(tooltips.get(currentIndex));
					setIcon(icons.get(currentIndex));
					plugin.updateConfig();
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				setIcon(hoverIcons.get(currentIndex));
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				setIcon(icons.get(currentIndex));
			}
		});
	}

	public void setCurrentState(final T state)
	{
		for (int i = 0; i < this.states.size(); i++)
		{
			if (this.states.get(i) == state)
			{
				this.currentIndex = i;
				break;
			}
		}
		setIcon(icons.get(currentIndex));
		setToolTipText(tooltips.get(currentIndex));
	}

	public T getCurrentState()
	{
		return states.get(this.currentIndex);
	}
}
