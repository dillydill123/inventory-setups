package inventorysetups.ui;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class InventorySetupsSelectionPanel
{
    private final JList<String> list;
    private ActionListener okEvent, cancelEvent;
    private final JDialog dialog;

    public InventorySetupsSelectionPanel(JPanel parent, String title, String message, String[] options)
    {
        this.list = new JList<>(options);
        this.list.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
        JLabel label = new JLabel(message);
        JLabel ctrlClickLabel = new JLabel("Ctrl + Click to select multiple");
        JPanel topLabels = new JPanel(new BorderLayout());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        ctrlClickLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topLabels.add(label, BorderLayout.NORTH);
        topLabels.add(ctrlClickLabel, BorderLayout.CENTER);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(this::handleOkButtonClick);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this::handleCancelButtonClick);

        // Center the list elements
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panel = new JPanel(new BorderLayout(5,5));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        panel.add(topLabels, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Compute width and height
        // width is based on longest option, with a maximum
        FontMetrics metrics = new FontMetrics(list.getFont()) {};
        String longestWidthString = Arrays.stream(options).max(Comparator.comparingInt(s -> (int)Math.ceil(metrics.getStringBounds(s, null).getWidth()))).get();

        Rectangle2D bounds = metrics.getStringBounds(longestWidthString, null);
        list.setFixedCellHeight((int)Math.ceil(bounds.getHeight() + 1));
        int widthInPixels = (int)Math.ceil(bounds.getWidth()) + 25;
        int max_char_height = metrics.getMaxAscent() + metrics.getMaxDescent();
        int heightInPixels = max_char_height * options.length + 35;

        int maxHeight = 400;
        int maxWidth = 500;
        panel.setPreferredSize(new Dimension(Math.min(widthInPixels, maxWidth), Math.min(heightInPixels, maxHeight)));

        JOptionPane optionPane = new JOptionPane(panel);
        optionPane.setOptions(new Object[]{okButton, cancelButton});

        dialog = optionPane.createDialog(parent,"Select option");
        dialog.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));
        dialog.setTitle(title);
    }

    public void setOnOk(ActionListener event)
    {
        okEvent = event;
    }

    public void setOnClose(ActionListener event)
    {
        cancelEvent  = event;
    }

    private void handleOkButtonClick(ActionEvent e)
    {
        if (okEvent != null)
        {
            okEvent.actionPerformed(e);
        }
        hide();
    }

    private void handleCancelButtonClick(ActionEvent e){
        if (cancelEvent != null)
        {
            cancelEvent.actionPerformed(e);
        }
        hide();
    }

    public void show()
    {
        dialog.setVisible(true);
    }

    private void hide()
    {
        dialog.setVisible(false);
    }

    public List<String> getSelectedItems()
    {
        return list.getSelectedValuesList();
    }
}