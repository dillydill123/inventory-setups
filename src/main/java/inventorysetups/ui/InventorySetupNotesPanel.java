package inventorysetups.ui;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupItem;
import inventorysetups.InventorySetupsPlugin;
import lombok.Setter;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

public class InventorySetupNotesPanel extends InventorySetupContainerPanel
{

	private JTextArea notesEditor;
	private UndoManager undoRedo;

	@Setter
	private InventorySetup currentInventorySetup;

	InventorySetupNotesPanel(ItemManager itemManager, InventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Notes");
	}

	@Override
	public void setupContainerPanel(JPanel containerSlotsPanel)
	{
		this.notesEditor = new JTextArea(10, 0);
		this.undoRedo = new UndoManager();
		this.currentInventorySetup = null;

		notesEditor.setTabSize(2);
		notesEditor.setLineWrap(true);
		notesEditor.setWrapStyleWord(true);
		notesEditor.setOpaque(false);

		// setting the limit to a 500 as UndoManager registers every key press,
		// which means that be default we would be able to undo only a sentence.
		// note: the default limit is 100
		undoRedo.setLimit(500);
		notesEditor.getDocument().addUndoableEditListener(e -> undoRedo.addEdit(e.getEdit()));
		notesEditor.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
		notesEditor.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

		notesEditor.getActionMap().put("Undo", new AbstractAction("Undo")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (undoRedo.canUndo())
					{
						undoRedo.undo();
					}
				}
				catch (CannotUndoException ex)
				{
				}
			}
		});

		notesEditor.getActionMap().put("Redo", new AbstractAction("Redo")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (undoRedo.canRedo())
					{
						undoRedo.redo();
					}
				}
				catch (CannotUndoException ex)
				{
				}
			}
		});

		notesEditor.addFocusListener(new FocusListener()
		{

			@Override
			public void focusGained(FocusEvent e)
			{

			}

			@Override
			public void focusLost(FocusEvent e)
			{
				notesChanged(getNotes());
			}

			private void notesChanged(String data)
			{
				plugin.updateNotesInSetup(currentInventorySetup, data);
			}
		});

		int width = 46 * 4 + 3;
		containerSlotsPanel.setLayout(new BorderLayout());
		containerSlotsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		containerSlotsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		notesEditor.setSize(new Dimension(width, 200));
		containerSlotsPanel.add(notesEditor);
	}

	@Override
	public void highlightSlotDifferences(ArrayList<InventorySetupItem> currContainer, InventorySetup inventorySetup)
	{
	}

	@Override
	public void setSlots(InventorySetup setup)
	{
		// Set the caret to not update right before setting the text
		// this stops it from scrolling down the parent scroll pane
		DefaultCaret caret = (DefaultCaret)notesEditor.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		notesEditor.setText(setup.getNotes());
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		currentInventorySetup = setup;
	}

	@Override
	public void resetSlotColors()
	{
	}

	public String getNotes()
	{
		try
		{
			Document doc = notesEditor.getDocument();
			return notesEditor.getDocument().getText(0, doc.getLength());
		}
		catch (BadLocationException ex)
		{
		}

		return "getNotes() Failed";
	}
}
