package inventorysetups.ui;

import inventorysetups.InventorySetupsPlugin;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

public class InventorySetupCycleButton<T> extends JLabel
{
	private final InventorySetupsPlugin plugin;
	private final ArrayList<T> states;
	private final ArrayList<ImageIcon> icons;
	private final ArrayList<ImageIcon> hoverIcons;
	private final ArrayList<String> tooltips;
	private final ArrayList<Runnable> runnables;
	private int currentIndex;

	InventorySetupCycleButton(final InventorySetupsPlugin plugin, final ArrayList<T> states, final ArrayList<ImageIcon> icons, final ArrayList<ImageIcon> hoverIcons, final ArrayList<String> tooltips)
	{
		this(plugin, states, icons, hoverIcons, tooltips, new ArrayList<>(Collections.nCopies(states.size(), () ->
		{
		}
		)));
	}

	InventorySetupCycleButton(final InventorySetupsPlugin plugin, final ArrayList<T> states, final ArrayList<ImageIcon> icons, final ArrayList<ImageIcon> hoverIcons, final ArrayList<String> tooltips, final ArrayList<Runnable> runnables)
	{
		super();
		this.plugin = plugin;
		this.states = states;
		this.icons = icons;
		this.hoverIcons = hoverIcons;
		this.runnables = runnables;
		this.tooltips = tooltips;
		this.currentIndex = 0;

		// sizes must be equal
		assert this.states.size() == this.icons.size();
		assert this.icons.size() == this.hoverIcons.size();
		assert this.hoverIcons.size() == this.runnables.size();
		assert this.runnables.size() == this.tooltips.size();

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					currentIndex = (currentIndex + 1) % states.size();
					runnables.get(currentIndex).run();
					setToolTipText(tooltips.get(currentIndex));
					setIcon(icons.get(currentIndex));
					plugin.updateConfig();
					//inventorySetup.setStackDifference(!inventorySetup.isStackDifference());
					//stackDifferenceIndicator.setToolTipText(inventorySetup.isStackDifference() ? "Disable highlighting for stack differences" : "Enable highlighting for stack differences");
					//updateStackDifferenceLabel();
					//plugin.updateConfig();
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
