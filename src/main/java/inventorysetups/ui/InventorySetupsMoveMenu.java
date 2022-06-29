package inventorysetups.ui;

import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsSortingID;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;


public class InventorySetupsMoveMenu<T> extends JPopupMenu
{
    private final InventorySetupsPluginPanel panel;
    private final InventorySetupsPlugin plugin;

    public InventorySetupsMoveMenu(final InventorySetupsPlugin plugin, final InventorySetupsPluginPanel panel, InventorySetupsMoveHandler<T> moveHandler, final String type, final T datum)
    {
        this.panel = panel;
        this.plugin = plugin;
        JMenuItem moveUp = new JMenuItem("Move " + type + " Up");
        JMenuItem moveDown = new JMenuItem("Move " + type + " Down");
        JMenuItem moveToTop = new JMenuItem("Move " + type + " to Top");
        JMenuItem moveToBottom = new JMenuItem("Move " + type + " to Bottom");
        JMenuItem moveToPosition = new JMenuItem("Move " + type + " to Position...");
        add(moveUp);
        add(moveDown);
        add(moveToTop);
        add(moveToBottom);
        add(moveToPosition);

        moveUp.addActionListener(e ->
        {
            if (!checkSortingMode())
            {
                return;
            }
            moveHandler.moveUp(datum);
        });

        moveDown.addActionListener(e ->
        {
            if (!checkSortingMode())
            {
                return;
            }
            moveHandler.moveDown(datum);
        });

        moveToTop.addActionListener(e ->
        {
            if (!checkSortingMode())
            {
                return;
            }
            moveHandler.moveToTop(datum);
        });
        moveToBottom.addActionListener(e ->
        {
            if (!checkSortingMode())
            {
                return;
            }
            moveHandler.moveToBottom(datum);
        });
        moveToPosition.addActionListener(e ->
        {
            if (!checkSortingMode()) {
                return;
            }
            moveHandler.moveToPosition(datum);
        });

    }

    private boolean checkSortingMode()
    {
        if (plugin.getConfig().sortingMode() != InventorySetupsSortingID.DEFAULT)
        {
            JOptionPane.showMessageDialog(panel,
                    "You cannot move setups while a sorting mode is enabled.",
                    "Move Setup Failed",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

}
