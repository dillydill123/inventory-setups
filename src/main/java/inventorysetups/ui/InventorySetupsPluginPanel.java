/*
 * Copyright (c) 2019, dillydill123 <https://github.com/dillydill123>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package inventorysetups.ui;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupsFilteringModeID;
import inventorysetups.InventorySetupsItem;
import inventorysetups.InventorySetupsSlotID;
import inventorysetups.InventorySetupsSortingID;
import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsStackCompareID;
import lombok.Getter;
import net.runelite.api.InventoryID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static inventorysetups.InventorySetupsPlugin.TUTORIAL_LINK;

// The main panel of the plugin that contains all viewing components
public class InventorySetupsPluginPanel extends PluginPanel
{

	private static ImageIcon HELP_ICON;
	private static ImageIcon HELP_HOVER_ICON;
	private static ImageIcon COMPACT_VIEW_ICON;
	private static ImageIcon COMPACT_VIEW_HOVER_ICON;
	private static ImageIcon NO_COMPACT_VIEW_ICON;
	private static ImageIcon NO_COMPACT_VIEW_HOVER_ICON;
	private static ImageIcon ALPHABETICAL_ICON;
	private static ImageIcon ALPHABETICAL_HOVER_ICON;
	private static ImageIcon NO_ALPHABETICAL_ICON;
	private static ImageIcon NO_ALPHABETICAL_HOVER_ICON;
	private static ImageIcon ADD_ICON;
	private static ImageIcon ADD_HOVER_ICON;
	private static ImageIcon BACK_ICON;
	private static ImageIcon BACK_HOVER_ICON;
	private static ImageIcon IMPORT_ICON;
	private static ImageIcon IMPORT_HOVER_ICON;
	private static ImageIcon UPDATE_ICON;
	private static ImageIcon UPDATE_HOVER_ICON;

	private static String MAIN_TITLE;

	private final JPanel noSetupsPanel; // Panel that is displayed when there are no setups
	private final JPanel updateNewsPanel; // Panel that is displayed for plugin update/news
	private final JPanel setupDisplayPanel; // Panel that is displayed when a setup is selected
	private final JPanel overviewPanel; // Panel that is displayed during overview, contains all setups
	private JPanel northAnchoredPanel; // Anchored panel in the north that won't scroll
	private final JScrollPane contentWrapperPane; // Panel for wrapping any content so it can scroll

	private final JPanel overviewTopRightButtonsPanel;
	private final JPanel setupTopRightButtonsPanel;

	private final JLabel title;
	private final JLabel helpButton;
	private final JLabel compactViewMarker;
	private final JLabel sortingMarker;
	private final JLabel addMarker;
	private final JLabel importMarker;
	private final JLabel updateMarker;
	private final JLabel backMarker;

	private final IconTextField searchBar;

	private final InventorySetupsInventoryPanel inventoryPanel;
	private final InventorySetupsEquipmentPanel equipmentPanel;
	private final InventorySetupsRunePouchPanel runePouchPanel;
	private final InventorySetupsSpellbookPanel spellbookPanel;
	private final InventorySetupsAdditionalItemsPanel additionalFilteredItemsPanel;
	private final InventorySetupsNotesPanel notesPanel;

	@Getter
	private InventorySetup currentSelectedSetup;

	private int overviewPanelScrollPosition;

	private final InventorySetupsPlugin plugin;

	static
	{
		final BufferedImage helpIcon = ImageUtil.getResourceStreamFromClass(InventorySetupsPlugin.class, "/help_button.png");
		HELP_ICON = new ImageIcon(helpIcon);
		HELP_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(helpIcon, 0.53f));

		final BufferedImage compactIcon = ImageUtil.getResourceStreamFromClass(InventorySetupsPlugin.class, "/compact_mode_icon.png");
		final BufferedImage compactIconHover = ImageUtil.luminanceOffset(compactIcon, -150);
		COMPACT_VIEW_ICON = new ImageIcon(compactIcon);
		COMPACT_VIEW_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(compactIcon, 0.53f));

		NO_COMPACT_VIEW_ICON = new ImageIcon(compactIconHover);
		NO_COMPACT_VIEW_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(compactIconHover, -100));

		final BufferedImage alphabeticalIcon = ImageUtil.getResourceStreamFromClass(InventorySetupsPlugin.class, "/alphabetical_icon.png");
		final BufferedImage alphabeticalIconHover = ImageUtil.luminanceOffset(alphabeticalIcon, -150);
		ALPHABETICAL_ICON = new ImageIcon(alphabeticalIcon);
		ALPHABETICAL_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(alphabeticalIcon, 0.53f));

		NO_ALPHABETICAL_ICON = new ImageIcon(alphabeticalIconHover);
		NO_ALPHABETICAL_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(alphabeticalIconHover, -100));

		final BufferedImage addIcon = ImageUtil.getResourceStreamFromClass(InventorySetupsPlugin.class, "/add_icon.png");
		ADD_ICON = new ImageIcon(addIcon);
		ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));

		final BufferedImage importIcon = ImageUtil.getResourceStreamFromClass(InventorySetupsPlugin.class, "/import_icon.png");
		IMPORT_ICON = new ImageIcon(importIcon);
		IMPORT_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(importIcon, 0.53f));

		final BufferedImage updateIcon = ImageUtil.getResourceStreamFromClass(InventorySetupsPlugin.class, "/update_icon.png");
		UPDATE_ICON = new ImageIcon(updateIcon);
		UPDATE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(updateIcon, 0.53f));

		final BufferedImage backIcon = ImageUtil.getResourceStreamFromClass(InventorySetupsPlugin.class, "/back_arrow_icon.png");
		BACK_ICON = new ImageIcon(backIcon);
		BACK_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(backIcon, 0.53f));

		MAIN_TITLE = "Inventory Setups";
	}

	public InventorySetupsPluginPanel(final InventorySetupsPlugin plugin, final ItemManager itemManager)
	{
		super(false);
		this.currentSelectedSetup = null;
		this.plugin = plugin;
		this.runePouchPanel = new InventorySetupsRunePouchPanel(itemManager, plugin);
		this.inventoryPanel = new InventorySetupsInventoryPanel(itemManager, plugin, runePouchPanel);
		this.equipmentPanel = new InventorySetupsEquipmentPanel(itemManager, plugin);
		this.spellbookPanel = new InventorySetupsSpellbookPanel(itemManager, plugin);
		this.additionalFilteredItemsPanel = new InventorySetupsAdditionalItemsPanel(itemManager, plugin);
		this.notesPanel = new InventorySetupsNotesPanel(itemManager, plugin);
		this.noSetupsPanel = new JPanel();
		this.updateNewsPanel = new InventorySetupsUpdateNewsPanel(plugin, this);
		this.setupDisplayPanel = new JPanel();
		this.overviewPanel = new JPanel();
		this.overviewPanelScrollPosition = 0;

		// setup the title
		this.title = new JLabel();
		title.setText(MAIN_TITLE);
		title.setForeground(Color.WHITE);

		this.helpButton = new JLabel(HELP_ICON);
		helpButton.setToolTipText("Click for help. This button can be hidden in the config.");
		helpButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					LinkBrowser.browse(TUTORIAL_LINK);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				helpButton.setIcon(HELP_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				helpButton.setIcon(HELP_ICON);
			}
		});

		this.sortingMarker = new JLabel(ALPHABETICAL_ICON);
		sortingMarker.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					boolean isAlphabeticalMode = plugin.getConfig().sortingMode() == InventorySetupsSortingID.ALPHABETICAL;
					plugin.toggleAlphabeticalMode(isAlphabeticalMode ? InventorySetupsSortingID.DEFAULT : InventorySetupsSortingID.ALPHABETICAL);
					updateSortingMarker();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				boolean isAlphabeticalMode = plugin.getConfig().sortingMode() == InventorySetupsSortingID.ALPHABETICAL;
				sortingMarker.setIcon(isAlphabeticalMode ? ALPHABETICAL_HOVER_ICON : NO_ALPHABETICAL_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				boolean isAlphabeticalMode = plugin.getConfig().sortingMode() == InventorySetupsSortingID.ALPHABETICAL;
				sortingMarker.setIcon(isAlphabeticalMode ? ALPHABETICAL_ICON : NO_ALPHABETICAL_ICON);
			}
		});

		this.compactViewMarker = new JLabel(COMPACT_VIEW_ICON);
		compactViewMarker.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					plugin.switchViews(!plugin.getConfig().compactMode());
					updateCompactViewMarker();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				compactViewMarker.setIcon(plugin.getConfig().compactMode() ? COMPACT_VIEW_HOVER_ICON : NO_COMPACT_VIEW_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				compactViewMarker.setIcon(plugin.getConfig().compactMode() ? COMPACT_VIEW_ICON : NO_COMPACT_VIEW_ICON);
			}
		});

		this.importMarker = new JLabel(IMPORT_ICON);
		importMarker.setToolTipText("Import a new inventory setup");
		importMarker.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					plugin.importSetup();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				importMarker.setIcon(IMPORT_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				importMarker.setIcon(IMPORT_ICON);
			}
		});

		// setup the add marker (+ sign in the top right)
		this.addMarker = new JLabel(ADD_ICON);
		addMarker.setToolTipText("Add a new inventory setup");
		addMarker.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					plugin.addInventorySetup();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				addMarker.setIcon(ADD_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				addMarker.setIcon(ADD_ICON);
			}
		});

		this.updateMarker = new JLabel(UPDATE_ICON);
		updateMarker.setToolTipText("Update setup with current inventory and equipment");
		updateMarker.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					plugin.updateCurrentSetup(currentSelectedSetup);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				updateMarker.setIcon(UPDATE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				updateMarker.setIcon(UPDATE_ICON);
			}
		});

		this.backMarker = new JLabel(BACK_ICON);
		backMarker.setToolTipText("Return to setups");
		backMarker.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					returnToOverviewPanel(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				backMarker.setIcon(BACK_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				backMarker.setIcon(BACK_ICON);
			}
		});

		this.overviewTopRightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		overviewTopRightButtonsPanel.add(sortingMarker);
		overviewTopRightButtonsPanel.add(compactViewMarker);
		overviewTopRightButtonsPanel.add(importMarker);
		overviewTopRightButtonsPanel.add(addMarker);
		compactViewMarker.setBorder(new EmptyBorder(0, 8, 0, 0));
		importMarker.setBorder(new EmptyBorder(0, 8, 0, 0));
		addMarker.setBorder(new EmptyBorder(0, 8, 0, 0));

		this.setupTopRightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		setupTopRightButtonsPanel.add(updateMarker);
		setupTopRightButtonsPanel.add(backMarker);
		backMarker.setBorder(new EmptyBorder(0, 8, 0, 0));

		// the panel on the top right that holds the buttons
		final JPanel markersPanel = new JPanel();
		markersPanel.setLayout(new FlowLayout());
		markersPanel.add(overviewTopRightButtonsPanel);
		markersPanel.add(setupTopRightButtonsPanel);
		overviewTopRightButtonsPanel.setVisible(true);
		setupTopRightButtonsPanel.setVisible(false);

		final JPanel titleAndHelpButton = new JPanel();
		titleAndHelpButton.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		titleAndHelpButton.add(title);
		titleAndHelpButton.add(helpButton);
		helpButton.setBorder(new EmptyBorder(0, 8, 0, 0));

		// the top panel that has the title and the buttons, and search bar
		final JPanel titleAndMarkersPanel = new JPanel();
		titleAndMarkersPanel.setLayout(new BorderLayout());
		titleAndMarkersPanel.add(titleAndHelpButton, BorderLayout.WEST);
		titleAndMarkersPanel.add(markersPanel, BorderLayout.EAST);

		this.searchBar = new IconTextField();
		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.setMinimumSize(new Dimension(0, 30));
		searchBar.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				rebuild(true);
			}
		});
		searchBar.addClearListener(() -> rebuild(true));

		// the panel that stays at the top and doesn't scroll
		// contains the title and buttons
		this.northAnchoredPanel = new JPanel();
		northAnchoredPanel.setLayout(new BoxLayout(northAnchoredPanel, BoxLayout.Y_AXIS));
		northAnchoredPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
		northAnchoredPanel.add(titleAndMarkersPanel);
		northAnchoredPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		northAnchoredPanel.add(searchBar);

		// the panel that holds the inventory and equipment panels
		final BoxLayout invEqLayout = new BoxLayout(setupDisplayPanel, BoxLayout.Y_AXIS);
		setupDisplayPanel.setLayout(invEqLayout);
		setupDisplayPanel.add(inventoryPanel);
		setupDisplayPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		setupDisplayPanel.add(runePouchPanel);
		setupDisplayPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		setupDisplayPanel.add(equipmentPanel);
		setupDisplayPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		setupDisplayPanel.add(spellbookPanel);
		setupDisplayPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		setupDisplayPanel.add(additionalFilteredItemsPanel);
		setupDisplayPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		setupDisplayPanel.add(notesPanel);

		// setup the error panel. It's wrapped around a normal panel
		// so it doesn't stretch to fill the parent panel
		final PluginErrorPanel errorPanel = new PluginErrorPanel();
		errorPanel.setContent("Inventory Setups", "Create an inventory setup.");
		noSetupsPanel.add(errorPanel);

		// the panel that holds the inventory panels, error panel, and the overview panel
		final JPanel contentPanel = new JPanel();
		final BoxLayout contentLayout = new BoxLayout(contentPanel, BoxLayout.Y_AXIS);
		contentPanel.setLayout(contentLayout);
		contentPanel.add(setupDisplayPanel);
		contentPanel.add(noSetupsPanel);
		contentPanel.add(updateNewsPanel);
		contentPanel.add(overviewPanel);

		// wrapper for the main content panel to keep it from stretching
		final JPanel contentWrapper = new JPanel(new BorderLayout());
		contentWrapper.add(Box.createGlue(), BorderLayout.CENTER);
		contentWrapper.add(contentPanel, BorderLayout.NORTH);
		this.contentWrapperPane = new JScrollPane(contentWrapper);
		this.contentWrapperPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		add(northAnchoredPanel, BorderLayout.NORTH);
		add(this.contentWrapperPane, BorderLayout.CENTER);

		// make sure the invEq panel isn't visible upon startup
		setupDisplayPanel.setVisible(false);
		helpButton.setVisible(!plugin.getConfig().hideButton());
		updateCompactViewMarker();
		updateSortingMarker();
	}

	// Redraw the entire overview panel, considering the text in the search bar
	public void rebuild(boolean resetScrollBar)
	{
		returnToOverviewPanel(resetScrollBar);
		overviewPanel.removeAll();

		List<InventorySetup> setupsToAdd = null;
		if (!searchBar.getText().isEmpty())
		{
			setupsToAdd = plugin.filterSetups(searchBar.getText());
		}
		else
		{
			setupsToAdd = new ArrayList<>(plugin.getInventorySetups());
			moveFavoriteSetupsToTopOfList(setupsToAdd);
		}

		if (plugin.getConfig().sortingMode() == InventorySetupsSortingID.ALPHABETICAL)
		{
			setupsToAdd.sort(Comparator.comparing(InventorySetup::getName, String.CASE_INSENSITIVE_ORDER));
		}

		init(setupsToAdd);

		revalidate();
		repaint();
	}

	public void moveFavoriteSetupsToTopOfList(final List<InventorySetup> setupsToAdd)
	{
		List<InventorySetup> favSetups = setupsToAdd.stream().filter(InventorySetup::isFavorite).collect(Collectors.toList());
		setupsToAdd.removeAll(favSetups);
		for (final InventorySetup favoritedInvSetup : favSetups)
		{
			setupsToAdd.add(0, favoritedInvSetup);
		}
	}

	public void refreshCurrentSetup()
	{
		if (currentSelectedSetup != null)
		{
			setCurrentInventorySetup(currentSelectedSetup, false);
		}
	}

	public void setCurrentInventorySetup(final InventorySetup inventorySetup, boolean resetScrollBar)
	{
		overviewPanelScrollPosition = contentWrapperPane.getVerticalScrollBar().getValue();
		currentSelectedSetup = inventorySetup;
		inventoryPanel.updatePanelWithSetupInformation(inventorySetup);
		runePouchPanel.updatePanelWithSetupInformation(inventorySetup);
		equipmentPanel.updatePanelWithSetupInformation(inventorySetup);
		spellbookPanel.updatePanelWithSetupInformation(inventorySetup);
		additionalFilteredItemsPanel.updatePanelWithSetupInformation(inventorySetup);
		notesPanel.updatePanelWithSetupInformation(inventorySetup);

		overviewTopRightButtonsPanel.setVisible(false);
		setupTopRightButtonsPanel.setVisible(true);

		setupDisplayPanel.setVisible(true);
		noSetupsPanel.setVisible(false);
		overviewPanel.setVisible(false);

		title.setText(inventorySetup.getName());
		helpButton.setVisible(false);
		searchBar.setVisible(false);

		// only show the rune pouch if the setup has a rune pouch
		runePouchPanel.setVisible(currentSelectedSetup.getRune_pouch() != null);

		highlightInventory();
		highlightEquipment();
		highlightSpellbook();

		if (resetScrollBar)
		{
			// reset scrollbar back to top
			setScrollBarPosition(0);
		}

		plugin.setBankFilteringMode(InventorySetupsFilteringModeID.ALL);
		plugin.doBankSearch();

		validate();
		repaint();

	}

	public void highlightInventory()
	{
		// if the panel itself isn't visible, don't waste time doing any highlighting logic
		if (!setupDisplayPanel.isVisible())
		{
			return;
		}

		// if the panel is visible, check if highlighting is enabled on the setup and globally
		// if any of the two, reset the slots so they aren't highlighted
		if (!currentSelectedSetup.isHighlightDifference() || !plugin.isHighlightingAllowed())
		{
			inventoryPanel.resetSlotColors();
			return;
		}

		final ArrayList<InventorySetupsItem> inv = plugin.getNormalizedContainer(InventoryID.INVENTORY);
		inventoryPanel.highlightSlots(inv, currentSelectedSetup);
	}

	public void highlightEquipment()
	{
		// if the panel itself isn't visible, don't waste time doing any highlighting logic
		if (!setupDisplayPanel.isVisible())
		{
			return;
		}

		// if the panel is visible, check if highlighting is enabled on the setup and globally
		// if any of the two, reset the slots so they aren't highlighted
		if (!currentSelectedSetup.isHighlightDifference() || !plugin.isHighlightingAllowed())
		{
			equipmentPanel.resetSlotColors();
			return;
		}

		final ArrayList<InventorySetupsItem> eqp = plugin.getNormalizedContainer(InventoryID.EQUIPMENT);
		equipmentPanel.highlightSlots(eqp, currentSelectedSetup);
	}

	public void highlightSpellbook()
	{
		// if the panel itself isn't visible, don't waste time doing any highlighting logic
		if (!setupDisplayPanel.isVisible())
		{
			return;
		}

		if (!currentSelectedSetup.isHighlightDifference() || !plugin.isHighlightingAllowed())
		{
			spellbookPanel.resetSlotColors();
			return;
		}

		// pass it a dummy container because it only needs the current selected setup
		spellbookPanel.highlightSlots(new ArrayList<InventorySetupsItem>(), currentSelectedSetup);

	}

	// returns to the overview panel
	public void returnToOverviewPanel(boolean shouldResetScrollBar)
	{
		noSetupsPanel.setVisible(plugin.getInventorySetups().isEmpty());
		overviewPanel.setVisible(!plugin.getInventorySetups().isEmpty());
		setupDisplayPanel.setVisible(false);
		overviewTopRightButtonsPanel.setVisible(true);
		setupTopRightButtonsPanel.setVisible(false);
		title.setText(MAIN_TITLE);
		helpButton.setVisible(!plugin.getConfig().hideButton());
		searchBar.setVisible(true);

		if (shouldResetScrollBar)
		{
			overviewPanelScrollPosition = 0;
			setScrollBarPosition(overviewPanelScrollPosition);
		}
		else if (currentSelectedSetup != null)
		{
			setScrollBarPosition(overviewPanelScrollPosition);
		}

		currentSelectedSetup = null;
		plugin.resetBankSearch(true);
	}

	public boolean isStackCompareForSlotAllowed(final InventorySetupsSlotID inventoryID, final int slotId)
	{
		switch (inventoryID)
		{
			case INVENTORY:
				return inventoryPanel.isStackCompareForSlotAllowed(slotId);
			case EQUIPMENT:
				return equipmentPanel.isStackCompareForSlotAllowed(slotId);
			case RUNE_POUCH:
				return runePouchPanel.isStackCompareForSlotAllowed(slotId);
			case ADDITIONAL_ITEMS:
				return additionalFilteredItemsPanel.isStackCompareForSlotAllowed(slotId);
			case SPELL_BOOK:
				return spellbookPanel.isStackCompareForSlotAllowed(slotId);
			default:
				return false;
		}
	}

	private void updateCompactViewMarker()
	{
		compactViewMarker.setIcon(plugin.getConfig().compactMode() ? COMPACT_VIEW_ICON : NO_COMPACT_VIEW_ICON);
		compactViewMarker.setToolTipText("Switch to " + (plugin.getConfig().compactMode() ? "standard mode" : "compact mode"));
	}

	private void updateSortingMarker()
	{
		boolean isAlphabeticalMode = plugin.getConfig().sortingMode() == InventorySetupsSortingID.ALPHABETICAL;
		sortingMarker.setIcon(isAlphabeticalMode ? ALPHABETICAL_ICON : NO_ALPHABETICAL_ICON);
		sortingMarker.setToolTipText(isAlphabeticalMode ? "Remove alphabetical sorting" : "Alphabetically sort setups");
	}

	private void setScrollBarPosition(int scrollbarValue)
	{
		validate();
		repaint();
		contentWrapperPane.getVerticalScrollBar().setValue(scrollbarValue);
	}

	// Redraw the entire panel given the list of setups
	private void init(List<InventorySetup> setups)
	{
		overviewPanel.setLayout(new GridBagLayout());
		overviewPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		updateCompactViewMarker();
		updateSortingMarker();

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;

		for (final InventorySetup setup : setups)
		{
			InventorySetupsPanel newPanel = null;
			if (plugin.getConfig().compactMode())
			{
				newPanel = new InventorySetupsCompactPanel(plugin, this, setup);
			}
			else
			{
				newPanel = new InventorySetupsStandardPanel(plugin, this, setup);
			}
			overviewPanel.add(newPanel, constraints);
			constraints.gridy++;

			overviewPanel.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
			constraints.gridy++;
		}

		setupDisplayPanel.setVisible(false);

		if (!plugin.getSavedVersionString().equals(plugin.getCurrentVersionString()))
		{
			northAnchoredPanel.setVisible(false);
			updateNewsPanel.setVisible(true);
			overviewPanel.setVisible(false);
			noSetupsPanel.setVisible(false);
		}
		else
		{
			northAnchoredPanel.setVisible(true);
			updateNewsPanel.setVisible(false);
			noSetupsPanel.setVisible(plugin.getInventorySetups().isEmpty());
			overviewPanel.setVisible(!plugin.getInventorySetups().isEmpty());
		}
	}

}
