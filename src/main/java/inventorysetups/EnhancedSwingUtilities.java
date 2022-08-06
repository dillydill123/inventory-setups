package inventorysetups;

import java.awt.Component;
import java.awt.Container;
import javax.swing.SwingUtilities;
import net.runelite.client.util.SwingUtil;

public class EnhancedSwingUtilities
{
	private EnhancedSwingUtilities()
	{

	}

	public static void fastRemoveAll(Container c)
	{
		fastRemoveAll(c, true);
	}

	private static void fastRemoveAll(Container c, boolean isMainParent)
	{
		// If we are not on the EDT this will deadlock, in addition to being totally unsafe
		assert SwingUtilities.isEventDispatchThread();

		// when a component is removed it has to be resized for some reason, but only if it's valid
		// so we make sure to invalidate everything before removing it
		c.invalidate();
		for (int i = 0; i < c.getComponentCount(); i++)
		{
			Component ic = c.getComponent(i);

			// removeAll and removeNotify are both recursive, so we have to recurse before them
			if (ic instanceof Container)
			{
				fastRemoveAll((Container) ic, false);
			}

			// each removeNotify needs to remove anything from the event queue that is for that widget
			// this however requires taking a lock, and is moderately slow, so we just execute all of
			// those events with a secondary event loop
			SwingUtil.pumpPendingEvents();

			// call removeNotify early; this is most of the work in removeAll, and generates events that
			// the next secondaryLoop will pickup
			ic.removeNotify();
		}

		if (isMainParent)
		{
			// Actually remove anything
			c.removeAll();
		}
	}
}