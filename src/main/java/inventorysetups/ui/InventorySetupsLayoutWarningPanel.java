package inventorysetups.ui;

import inventorysetups.InventorySetupsPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;


// Displays a warning that layouts will not work.
public class InventorySetupsLayoutWarningPanel extends JPanel
{

	InventorySetupsLayoutWarningPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel)
	{
		final String warningText = "Inventory Setups has detected that layouts will not work with your current settings.\n\n" +
				"Bank Tags Plugin must be ON and Hub Plugin Bank Tag Layouts 'Use with Inventory Setups' setting must be OFF.\n\n" +
				"Inventory Setups can adjust your settings so bank layouts will work. This is HIGHLY recommended so you can use all features of Inventory Setups.\n\n" +
				"Hub Plugin Bank Tag Layouts is not required anymore for layouts with Inventory Setups. You can disable this warning in the settings.";

		JTextArea textArea = new JTextArea(2, 20);
		textArea.setText(warningText);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setOpaque(false);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setBackground(ColorScheme.DARK_GRAY_COLOR);
		Font textAreaFont = FontManager.getRunescapeFont();
		textAreaFont = textAreaFont.deriveFont(textAreaFont.getStyle(), (float)textAreaFont.getSize() - (float)0.1);
		textArea.setFont(textAreaFont);

		textArea.setBorder(new EmptyBorder(0, 0, 0, 0));

		final JLabel configureSettings = new JLabel("Do you want Inventory Setups to ");
		final JLabel configureSettings2 = new JLabel("change your settings to use layouts?");
		final JButton configureSettingsButton = new JButton("Change my settings.");
		configureSettingsButton.setForeground(Color.GREEN);
		configureSettingsButton.addActionListener(e ->
		{
			plugin.enableLayouts();
			panel.showCorrectPanel();
		});
		configureSettings.setFont(FontManager.getRunescapeSmallFont());
		configureSettings2.setFont(FontManager.getRunescapeSmallFont());
		configureSettings.setHorizontalAlignment(JLabel.CENTER);
		configureSettings2.setHorizontalAlignment(JLabel.CENTER);

		final JPanel configureSettingsPanelInfo = new JPanel();
		configureSettingsPanelInfo.setLayout(new BorderLayout());
		configureSettingsPanelInfo.add(configureSettings, BorderLayout.NORTH);
		configureSettingsPanelInfo.add(configureSettings2, BorderLayout.CENTER);
		configureSettingsPanelInfo.add(configureSettingsButton, BorderLayout.SOUTH);

		final JLabel noConfigureSettings = new JLabel("Use Inventory Setups without layouts.");
		final JPanel noConfigureSettingsPanel = new JPanel(new BorderLayout());
		final JButton noConfigureSettingsButton = new JButton("Do not use layouts.");
		noConfigureSettingsButton.setForeground(Color.RED);
		noConfigureSettingsButton.addActionListener(e ->
		{
			panel.showCorrectPanel();
		});
		noConfigureSettings.setFont(FontManager.getRunescapeSmallFont());
		noConfigureSettings.setHorizontalAlignment(JLabel.CENTER);
		noConfigureSettingsPanel.add(noConfigureSettings, BorderLayout.NORTH);
		noConfigureSettingsPanel.add(noConfigureSettingsButton, BorderLayout.SOUTH);


		final JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(textArea);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 24)));
		contentPanel.add(configureSettingsPanelInfo);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
		contentPanel.add(noConfigureSettingsPanel);

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 10, 5, 10));
		add(contentPanel, BorderLayout.NORTH);
	}

}
