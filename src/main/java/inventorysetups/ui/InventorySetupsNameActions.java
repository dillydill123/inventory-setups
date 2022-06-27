package inventorysetups.ui;

import inventorysetups.InventorySetupsDisplayAttributes;
import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsValidName;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static inventorysetups.ui.InventorySetupsStandardPanel.DISPLAY_COLOR_HOVER_ICON;
import static inventorysetups.ui.InventorySetupsStandardPanel.DISPLAY_COLOR_ICON;

public class InventorySetupsNameActions<T extends InventorySetupsDisplayAttributes> extends JPanel
{
    private static final Border NAME_BOTTOM_BORDER = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
            BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR));


    public final T datum;
    private final JLabel save = new JLabel("Save");
    private final JLabel cancel = new JLabel("Cancel");
    private final JLabel edit = new JLabel("Edit");
    private final JLabel displayColorIndicator = new JLabel();
    private final FlatTextField nameInput = new FlatTextField();

    public InventorySetupsNameActions(final T datum,
                                      final InventorySetupsPlugin plugin,
                                      final InventorySetupsPluginPanel panel,
                                      final InventorySetupsValidName validNameImplementer,
                                      final JPopupMenu movePopupMenu, int maxLength)
    {
        setLayout(new BorderLayout());

        this.datum = datum;

        JPanel nameWrapper = new JPanel(new BorderLayout());
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        Color currentDisplayColor;
        if (datum.getDisplayColor() == null)
        {
            nameWrapper.setBorder(NAME_BOTTOM_BORDER);
            currentDisplayColor = null;
        }
        else
        {
            nameWrapper.setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, datum.getDisplayColor()),
                    BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR)));
            currentDisplayColor = datum.getDisplayColor();
        }

        JPanel nameActions = new JPanel(new BorderLayout(3, 0));
        nameActions.setBorder(new EmptyBorder(0, 0, 0, 8));
        nameActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Limit character input
        AbstractDocument doc = (AbstractDocument)nameInput.getDocument();
        doc.setDocumentFilter(new DocumentFilter()
        {
            @Override
            public void insertString(FilterBypass fb, int offset, String str, AttributeSet a) throws BadLocationException
            {
                if ((fb.getDocument().getLength() + str.length()) <= maxLength)
                {
                    super.insertString(fb, offset, str, a);
                }
            }

            // Replace handles pasting
            @Override
            public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet a) throws BadLocationException
            {
                if ((fb.getDocument().getLength() + str.length() - length) >= maxLength)
                {
                    // If the user pastes a huge amount of text, cut it out until the maximum length is achieved
                    int chars_available = maxLength - (fb.getDocument().getLength() - length);
                    int chars_to_cut = str.length() - chars_available;
                    str = str.substring(0, str.length() - chars_to_cut);
                }
                super.replace(fb, offset, length, str, a);
            }
        });

        // Add document listener to disable save button when the name isn't valid
        nameInput.getDocument().addDocumentListener(new DocumentListener()
        {
            private void checkIsNameValid()
            {
                if (!validNameImplementer.isNameValid(nameInput.getText()))
                {
                    save.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
                    save.setEnabled(false);
                }
                else
                {
                    save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
                    save.setEnabled(true);
                }
            }
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                checkIsNameValid();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                checkIsNameValid();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                checkIsNameValid();
            }
        });

        save.setVisible(false);
        save.setFont(FontManager.getRunescapeSmallFont());
        save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
        save.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (SwingUtilities.isLeftMouseButton(mouseEvent) && save.isEnabled())
                {
                    validNameImplementer.updateName(nameInput.getText());
                    Color newDisplayColor = null;
                    if (displayColorIndicator.getBorder() != null)
                    {
                        Color currentDisplayColor = ((MatteBorder)((CompoundBorder) displayColorIndicator.getBorder()).getInsideBorder()).getMatteColor();
                        if (currentDisplayColor != JagexColors.MENU_TARGET)
                        {
                            newDisplayColor = currentDisplayColor;
                        }
                    }

                    datum.setDisplayColor(newDisplayColor);

                    plugin.updateConfig();

                    nameInput.setEditable(false);
                    updateNameActions(false);
                    requestFocusInWindow();
                    panel.rebuild(false);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                if (validNameImplementer.isNameValid(nameInput.getText()))
                {
                    save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
                }
                else
                {
                    save.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                if (validNameImplementer.isNameValid(nameInput.getText()))
                {
                    save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
                }
                else
                {
                    save.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
                }
            }
        });

        cancel.setVisible(false);
        cancel.setFont(FontManager.getRunescapeSmallFont());
        cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
        cancel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (SwingUtilities.isLeftMouseButton(mouseEvent))
                {
                    nameInput.setEditable(false);
                    nameInput.setText(datum.getName());
                    updateNameActions(false);
                    requestFocusInWindow();
                    updateDisplayColorLabel(currentDisplayColor);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
            }
        });

        edit.setFont(FontManager.getRunescapeSmallFont());
        edit.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
        edit.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (SwingUtilities.isLeftMouseButton(mouseEvent))
                {
                    nameInput.setEditable(true);
                    updateNameActions(true);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                edit.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                edit.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
            }
        });

        nameActions.add(save, BorderLayout.EAST);
        nameActions.add(cancel, BorderLayout.WEST);
        nameActions.add(edit, BorderLayout.CENTER);

        nameInput.setText(datum.getName());
        nameInput.setBorder(null);
        nameInput.setEditable(false);
        nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameInput.setPreferredSize(new Dimension(0, 24));
        nameInput.getTextField().setForeground(Color.WHITE);
        nameInput.getTextField().setBorder(new EmptyBorder(0, 6, 0, 0));
        nameInput.getTextField().setComponentPopupMenu(movePopupMenu);

        displayColorIndicator.setToolTipText("Edit the color of the name");
        displayColorIndicator.setIcon(DISPLAY_COLOR_ICON);
        displayColorIndicator.setVisible(false);

        // Right click menu to remove the color on the setup
        JPopupMenu displayColorMenu = new JPopupMenu();
        JMenuItem removeColor = new JMenuItem("Remove the color of the name");
        displayColorMenu.add(removeColor);
        removeColor.addActionListener(e -> updateDisplayColorLabel(null));

        displayColorIndicator.setComponentPopupMenu(displayColorMenu);

        updateDisplayColorLabel(currentDisplayColor);
        displayColorIndicator.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                if (SwingUtilities.isLeftMouseButton(mouseEvent))
                {
                    plugin.openColorPicker("Choose a Display color", currentDisplayColor == null ? JagexColors.MENU_TARGET : currentDisplayColor,
                            c ->
                            {
                                updateDisplayColorLabel(c);
                            }
                    );
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                displayColorIndicator.setIcon(DISPLAY_COLOR_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                displayColorIndicator.setIcon(DISPLAY_COLOR_ICON);
            }
        });

        nameWrapper.add(nameInput, BorderLayout.CENTER);
        nameWrapper.add(nameActions, BorderLayout.EAST);
        nameWrapper.add(displayColorIndicator, BorderLayout.WEST);

        add(nameWrapper, BorderLayout.CENTER);

    }

    private void updateNameActions(boolean saveAndCancel)
    {
        save.setVisible(saveAndCancel);
        cancel.setVisible(saveAndCancel);
        edit.setVisible(!saveAndCancel);
        displayColorIndicator.setVisible(saveAndCancel);

        if (saveAndCancel)
        {
            nameInput.getTextField().requestFocusInWindow();
            nameInput.getTextField().selectAll();
        }
    }

    private void updateDisplayColorLabel(Color color)
    {
        displayColorIndicator.setBorder(new CompoundBorder(
                new EmptyBorder(0, 4, 0, 0),
                new MatteBorder(0, 0, 3, 0, color)));

    }

}
