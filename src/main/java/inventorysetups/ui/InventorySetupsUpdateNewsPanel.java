package inventorysetups.ui;

import inventorysetups.InventorySetupsPlugin;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventorySetupsUpdateNewsPanel extends JPanel
{

	InventorySetupsUpdateNewsPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel)
	{
		final JPanel welcomePanel = new JPanel(new BorderLayout());
		final JLabel welcomeText = new JLabel("Welcome to Inventory Setups " + plugin.getCurrentVersionString());
		welcomePanel.add(welcomeText, BorderLayout.NORTH);
		welcomeText.setHorizontalAlignment(JLabel.CENTER);

		final JPanel latestUpdatePanelInfo = getLatestUpdateInfoPanel();

		final JPanel newUserPanelInfo = new JPanel();
		newUserPanelInfo.setLayout(new BorderLayout());

		final JPanel closePanel = new JPanel(new BorderLayout());
		final JButton returnToSetups = new JButton("Return to setups");
		returnToSetups.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				panel.rebuild(true);
			}
		});
		closePanel.add(new JLabel("Click this button"), BorderLayout.NORTH);
		closePanel.add(returnToSetups, BorderLayout.CENTER);

		final JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(welcomePanel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		contentPanel.add(latestUpdatePanelInfo);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		contentPanel.add(newUserPanelInfo);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		contentPanel.add(closePanel);

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		add(contentPanel, BorderLayout.CENTER);
	}


	private JPanel getLatestUpdateInfoPanel()
	{
		final JPanel updatePanel = new JPanel(new BorderLayout());
		final JLabel updateLabel = new JLabel("The new updates are the following:");
		updateLabel.setHorizontalAlignment(JLabel.CENTER);
		updatePanel.add(updateLabel, BorderLayout.CENTER);

		return updatePanel;
	}
}
