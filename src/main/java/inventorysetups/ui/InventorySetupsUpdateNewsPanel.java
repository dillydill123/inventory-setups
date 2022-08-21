package inventorysetups.ui;

import inventorysetups.InventorySetupsPlugin;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.LinkBrowser;

import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import static inventorysetups.InventorySetupsPlugin.SUGGESTION_LINK;
import static inventorysetups.InventorySetupsPlugin.TUTORIAL_LINK;

public class InventorySetupsUpdateNewsPanel extends JPanel
{

	InventorySetupsUpdateNewsPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel)
	{
		final JLabel welcomeText = new JLabel("Inventory Setups " + plugin.getCurrentVersionString());
		welcomeText.setFont(FontManager.getRunescapeBoldFont());
		welcomeText.setHorizontalAlignment(JLabel.CENTER);

		final JPanel welcomePanel = new JPanel(new BorderLayout());
		welcomePanel.add(welcomeText, BorderLayout.NORTH);

		final JPanel latestUpdatePanelInfo = getLatestUpdateInfoPanel();

		final JLabel newUser = new JLabel("Are you a new user?");
		final JLabel newUser2 = new JLabel("For help and support, click here");
		final JButton linkToHelp = new JButton("View Guide");
		linkToHelp.addActionListener(e ->
		{
			LinkBrowser.browse(TUTORIAL_LINK);
		});
		newUser.setFont(FontManager.getRunescapeSmallFont());
		newUser2.setFont(FontManager.getRunescapeSmallFont());
		newUser.setHorizontalAlignment(JLabel.CENTER);
		newUser2.setHorizontalAlignment(JLabel.CENTER);

		final JPanel newUserPanelInfo = new JPanel();
		newUserPanelInfo.setLayout(new BorderLayout());
		newUserPanelInfo.add(newUser, BorderLayout.NORTH);
		newUserPanelInfo.add(newUser2, BorderLayout.CENTER);
		newUserPanelInfo.add(linkToHelp, BorderLayout.SOUTH);

		final JLabel suggestions = new JLabel("Have a suggestion? Found a bug?");
		final JLabel suggestions2 = new JLabel("Click here to create an issue");
		final JButton linkToSuggestion = new JButton("Make a Suggestion");
		linkToSuggestion.addActionListener(e ->
		{
			LinkBrowser.browse(SUGGESTION_LINK);
		});
		suggestions.setFont(FontManager.getRunescapeSmallFont());
		suggestions2.setFont(FontManager.getRunescapeSmallFont());
		suggestions.setHorizontalAlignment(JLabel.CENTER);
		suggestions2.setHorizontalAlignment(JLabel.CENTER);

		final JPanel suggestionPanelInfo = new JPanel();
		suggestionPanelInfo.setLayout(new BorderLayout());
		suggestionPanelInfo.add(suggestions, BorderLayout.NORTH);
		suggestionPanelInfo.add(suggestions2, BorderLayout.CENTER);
		suggestionPanelInfo.add(linkToSuggestion, BorderLayout.SOUTH);

		final JPanel closePanel = new JPanel(new BorderLayout());
		final JButton returnToSetups = new JButton("Return to Setups");
		returnToSetups.addActionListener(e ->
		{
			plugin.setSavedVersionString(plugin.getCurrentVersionString());
			panel.redrawOverviewPanel(true);
		});
		final JLabel clickButtonToLeave = new JLabel("Click here to hide this window");
		final JLabel clickButtonToLeave2 = new JLabel("until the next update");
		clickButtonToLeave.setFont(FontManager.getRunescapeSmallFont());
		clickButtonToLeave2.setFont(FontManager.getRunescapeSmallFont());
		clickButtonToLeave.setHorizontalAlignment(JLabel.CENTER);
		clickButtonToLeave2.setHorizontalAlignment(JLabel.CENTER);
		closePanel.add(clickButtonToLeave, BorderLayout.NORTH);
		closePanel.add(clickButtonToLeave2, BorderLayout.CENTER);
		closePanel.add(returnToSetups, BorderLayout.SOUTH);

		final JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(welcomePanel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		contentPanel.add(latestUpdatePanelInfo);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		contentPanel.add(closePanel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		contentPanel.add(newUserPanelInfo);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		contentPanel.add(suggestionPanelInfo);

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		add(contentPanel, BorderLayout.CENTER);
	}


	private JPanel getLatestUpdateInfoPanel()
	{
		final JLabel patchNotesLabel = new JLabel("Patch Notes");
		patchNotesLabel.setFont(FontManager.getRunescapeSmallFont());
		patchNotesLabel.setHorizontalAlignment(JLabel.CENTER);

		final JPanel patchTitlePanel = new JPanel(new BorderLayout());
		patchTitlePanel.add(patchNotesLabel, BorderLayout.NORTH);
		String updateText =		"Fixed an issue where alphabetical mode would cause setups to show up in the Unassigned section incorrectly.\n\n" +
								"Clicking on a section panel while editing the name will no longer minimize or maximize the section and cancel the edit.";

		JTextArea textArea = new JTextArea(2, 20);
		textArea.setText(updateText);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setOpaque(false);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setBackground(ColorScheme.DARK_GRAY_COLOR);
		Font textAreaFont = FontManager.getRunescapeSmallFont();
		textAreaFont = textAreaFont.deriveFont(textAreaFont.getStyle(), (float)textAreaFont.getSize() - (float)0.1);
		textArea.setFont(textAreaFont);

		textArea.setBorder(new EmptyBorder(0, 0, 0, 0));

		final JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(patchTitlePanel, BorderLayout.NORTH);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		contentPanel.add(getJSeparator(ColorScheme.LIGHT_GRAY_COLOR));
		contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		contentPanel.add(textArea);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		contentPanel.add(getJSeparator(ColorScheme.LIGHT_GRAY_COLOR));

		final JPanel updatePanel = new JPanel(new BorderLayout());
		updatePanel.add(contentPanel, BorderLayout.CENTER);

		return updatePanel;
	}

	private JSeparator getJSeparator(Color color)
	{
		JSeparator sep = new JSeparator();
		sep.setBackground(color);
		sep.setForeground(color);
		return sep;
	}

}
