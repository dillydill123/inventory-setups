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

	private static final String DONATION_LINK = "https://www.buymeacoffee.com/dillydill123";

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
			panel.showCorrectPanelBasedOnVersion();
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

		final JLabel donations = new JLabel("Want to make a donation?");
		final JLabel donations2 = new JLabel("Click here to buy me a coffee");
		final JButton linkToDonations = new JButton("Donate");
		linkToDonations.addActionListener(e ->
		{
			LinkBrowser.browse(DONATION_LINK);
		});
		donations.setFont(FontManager.getRunescapeSmallFont());
		donations2.setFont(FontManager.getRunescapeSmallFont());
		donations.setHorizontalAlignment(JLabel.CENTER);
		donations2.setHorizontalAlignment(JLabel.CENTER);

		final JPanel donationPanelInfo = new JPanel();
		donationPanelInfo.setLayout(new BorderLayout());
		donationPanelInfo.add(donations, BorderLayout.NORTH);
		donationPanelInfo.add(donations2, BorderLayout.CENTER);
		donationPanelInfo.add(linkToDonations, BorderLayout.SOUTH);

		final JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(welcomePanel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
		contentPanel.add(latestUpdatePanelInfo);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
		contentPanel.add(closePanel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
		contentPanel.add(newUserPanelInfo);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
		contentPanel.add(suggestionPanelInfo);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
		contentPanel.add(donationPanelInfo);

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 10, 5, 10));
		add(contentPanel, BorderLayout.NORTH);
	}


	private JPanel getLatestUpdateInfoPanel()
	{
		final JLabel patchNotesLabel = new JLabel("Patch Notes");
		patchNotesLabel.setFont(FontManager.getRunescapeSmallFont());
		patchNotesLabel.setHorizontalAlignment(JLabel.CENTER);

		final JPanel patchTitlePanel = new JPanel(new BorderLayout());
		patchTitlePanel.add(patchNotesLabel, BorderLayout.NORTH);

		String updateText =		"Added support for Dizana's Quiver. If you had a quiver in an existing setup, you will need to update your setup after gearing up. Right click the new quiver slot above the ammo slot or refresh your setup.";

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
		contentPanel.add(patchTitlePanel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		contentPanel.add(getJSeparator(ColorScheme.LIGHT_GRAY_COLOR));
		contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		contentPanel.add(textArea);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		contentPanel.add(getJSeparator(ColorScheme.LIGHT_GRAY_COLOR));

		final JPanel updatePanel = new JPanel(new BorderLayout());
		updatePanel.add(contentPanel, BorderLayout.CENTER);

		// DO NOT TOUCH - For some reason this stops the panel from expanding
		updatePanel.getPreferredSize();

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
