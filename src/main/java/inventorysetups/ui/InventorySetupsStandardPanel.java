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
import inventorysetups.InventorySetupsPlugin;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

// Standard panel for inventory setups, which contains all the configuration buttons
public class InventorySetupsStandardPanel extends InventorySetupsPanel
{

	private static final Border NAME_BOTTOM_BORDER = new CompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
			BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR));

	private static final int H_GAP_BTN = 4;

	private static final ImageIcon BANK_FILTER_ICON;
	private static final ImageIcon BANK_FILTER_HOVER_ICON;
	private static final ImageIcon NO_BANK_FILTER_ICON;
	private static final ImageIcon NO_BANK_FILTER_HOVER_ICON;

	private static final ImageIcon HIGHLIGHT_COLOR_ICON;
	private static final ImageIcon HIGHLIGHT_COLOR_HOVER_ICON;
	private static final ImageIcon NO_HIGHLIGHT_COLOR_ICON;
	private static final ImageIcon NO_HIGHLIGHT_COLOR_HOVER_ICON;

	private static final ImageIcon TOGGLE_HIGHLIGHT_ICON;
	private static final ImageIcon TOGGLE_HIGHLIGHT_HOVER_ICON;
	private static final ImageIcon NO_TOGGLE_HIGHLIGHT_ICON;
	private static final ImageIcon NO_TOGGLE_HIGHLIGHT_HOVER_ICON;

	private static final ImageIcon UNORDERED_HIGHLIGHT_ICON;
	private static final ImageIcon UNORDERED_HIGHLIGHT_HOVER_ICON;
	private static final ImageIcon NO_UNORDERED_HIGHLIGHT_ICON;
	private static final ImageIcon NO_UNORDERED_HIGHLIGHT_HOVER_ICON;

	private static final ImageIcon FAVORITE_ICON;
	private static final ImageIcon FAVORITE_HOVER_ICON;
	private static final ImageIcon NO_FAVORITE_ICON;
	private static final ImageIcon NO_FAVORITE_HOVER_ICON;

	private static final ImageIcon VIEW_SETUP_ICON;
	private static final ImageIcon VIEW_SETUP_HOVER_ICON;

	private static final ImageIcon DELETE_ICON;
	private static final ImageIcon DELETE_HOVER_ICON;

	private static final ImageIcon EXPORT_ICON;
	private static final ImageIcon EXPORT_HOVER_ICON;

	private static final ImageIcon DISPLAY_COLOR_ICON;
	private static final ImageIcon DISPLAY_COLOR_HOVER_ICON;

	private final JLabel bankFilterIndicator = new JLabel();
	private final JLabel highlightColorIndicator = new JLabel();
	private final JLabel unorderedHighlightIndicator = new JLabel();
	private final JLabel favoriteIndicator = new JLabel();
	private final JLabel highlightIndicator = new JLabel();
	private final JLabel viewSetupLabel = new JLabel();
	private final JLabel exportLabel = new JLabel();
	private final JLabel deleteLabel = new JLabel();
	private final JLabel displayColorIndicator = new JLabel();

	private final FlatTextField nameInput = new FlatTextField();
	private final JLabel save = new JLabel("Save");
	private final JLabel cancel = new JLabel("Cancel");
	private final JLabel edit = new JLabel("Edit");

	static
	{
		final BufferedImage bankFilterImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/filter_icon.png");
		final BufferedImage bankFilterHover = ImageUtil.luminanceOffset(bankFilterImg, -150);
		BANK_FILTER_ICON = new ImageIcon(bankFilterImg);
		BANK_FILTER_HOVER_ICON = new ImageIcon(bankFilterHover);

		NO_BANK_FILTER_ICON = new ImageIcon(bankFilterHover);
		NO_BANK_FILTER_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(bankFilterHover, -100));

		final BufferedImage unorderedHighlightImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/unordered_highlight_icon.png");
		final BufferedImage unorderedHighlightHover = ImageUtil.luminanceOffset(unorderedHighlightImg, -150);
		UNORDERED_HIGHLIGHT_ICON = new ImageIcon(unorderedHighlightImg);
		UNORDERED_HIGHLIGHT_HOVER_ICON = new ImageIcon(unorderedHighlightHover);

		NO_UNORDERED_HIGHLIGHT_ICON = new ImageIcon(unorderedHighlightHover);
		NO_UNORDERED_HIGHLIGHT_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(unorderedHighlightHover, -100));

		final BufferedImage favoriteImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/favorite_icon.png");
		final BufferedImage favoriteHover = ImageUtil.luminanceOffset(favoriteImg, -150);
		FAVORITE_ICON = new ImageIcon(favoriteImg);
		FAVORITE_HOVER_ICON = new ImageIcon(favoriteHover);

		NO_FAVORITE_ICON = new ImageIcon(favoriteHover);
		NO_FAVORITE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(favoriteHover, -100));

		final BufferedImage highlightToggleImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/highlight_icon.png");
		final BufferedImage highlightToggleHover = ImageUtil.luminanceOffset(highlightToggleImg, -150);
		TOGGLE_HIGHLIGHT_ICON = new ImageIcon(highlightToggleImg);
		TOGGLE_HIGHLIGHT_HOVER_ICON = new ImageIcon(highlightToggleHover);

		NO_TOGGLE_HIGHLIGHT_ICON = new ImageIcon(highlightToggleHover);
		NO_TOGGLE_HIGHLIGHT_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(highlightToggleHover, -100));

		final BufferedImage highlightImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/highlight_color_icon.png");
		final BufferedImage highlightHover = ImageUtil.luminanceOffset(highlightImg, -150);
		HIGHLIGHT_COLOR_ICON = new ImageIcon(highlightImg);
		HIGHLIGHT_COLOR_HOVER_ICON = new ImageIcon(highlightHover);

		NO_HIGHLIGHT_COLOR_ICON = new ImageIcon(highlightHover);
		NO_HIGHLIGHT_COLOR_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(highlightHover, -100));

		final BufferedImage viewImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/visible_icon.png");
		final BufferedImage viewImgHover = ImageUtil.luminanceOffset(viewImg, -150);
		VIEW_SETUP_ICON = new ImageIcon(viewImg);
		VIEW_SETUP_HOVER_ICON = new ImageIcon(viewImgHover);

		final BufferedImage exportImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/export_icon.png");
		final BufferedImage exportImgHover = ImageUtil.luminanceOffset(exportImg, -150);
		EXPORT_ICON = new ImageIcon(exportImg);
		EXPORT_HOVER_ICON = new ImageIcon(exportImgHover);

		final BufferedImage deleteImg = ImageUtil.loadImageResource(InventorySetupsPlugin.class, "/delete_icon.png");
		DELETE_ICON = new ImageIcon(deleteImg);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.luminanceOffset(deleteImg, -100));

		DISPLAY_COLOR_ICON = new ImageIcon(highlightImg);
		DISPLAY_COLOR_HOVER_ICON = new ImageIcon(highlightHover);
	}

	InventorySetupsStandardPanel(InventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetup invSetup)
	{
		super(plugin, panel, invSetup);

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel nameWrapper = new JPanel(new BorderLayout());
		nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		Color currentDisplayColor;
		if (invSetup.getDisplayColor() == null)
		{
			nameWrapper.setBorder(NAME_BOTTOM_BORDER);
			currentDisplayColor = null;
		}
		else
		{
			nameWrapper.setBorder(new CompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 2, 0, invSetup.getDisplayColor()),
					BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR)));
			currentDisplayColor = invSetup.getDisplayColor();
		}

		JPanel nameActions = new JPanel(new BorderLayout(3, 0));
		nameActions.setBorder(new EmptyBorder(0, 0, 0, 8));
		nameActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		save.setVisible(false);
		save.setFont(FontManager.getRunescapeSmallFont());
		save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
		save.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					inventorySetup.setName(nameInput.getText());
					Color newDisplayColor = null;
					if (displayColorIndicator.getBorder() != null)
					{
						Color currentDisplayColor = ((MatteBorder)((CompoundBorder) displayColorIndicator.getBorder()).getInsideBorder()).getMatteColor();
						if (currentDisplayColor != JagexColors.MENU_TARGET)
						{
							newDisplayColor = currentDisplayColor;
						}
					}

					inventorySetup.setDisplayColor(newDisplayColor);

					plugin.updateConfig();

					nameInput.setEditable(false);
					updateNameActions(false);
					requestFocusInWindow();
					panel.rebuild(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
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
					nameInput.setText(inventorySetup.getName());
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

		nameInput.setText(inventorySetup.getName());
		nameInput.setBorder(null);
		nameInput.setEditable(false);
		nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameInput.setPreferredSize(new Dimension(0, 24));
		nameInput.getTextField().setForeground(Color.WHITE);
		nameInput.getTextField().setBorder(new EmptyBorder(0, 6, 0, 0));
		nameInput.getTextField().setComponentPopupMenu(moveSetupPopupMenu);

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
					openColorPicker("Choose a Display color", currentDisplayColor == null ? JagexColors.MENU_TARGET : currentDisplayColor,
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

		JPanel bottomContainer = new JPanel(new BorderLayout());
		bottomContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
		bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		bankFilterIndicator.setToolTipText("Enable bank filtering");
		bankFilterIndicator.setIcon(inventorySetup.isFilterBank() ? BANK_FILTER_ICON : NO_BANK_FILTER_ICON);
		bankFilterIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					inventorySetup.setFilterBank(!inventorySetup.isFilterBank());
					bankFilterIndicator.setToolTipText(inventorySetup.isFilterBank() ? "Disable bank filtering" : "Enable bank filtering");
					updateBankFilterLabel();
					plugin.updateConfig();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				bankFilterIndicator.setIcon(inventorySetup.isFilterBank() ? BANK_FILTER_HOVER_ICON : NO_BANK_FILTER_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				bankFilterIndicator.setIcon(inventorySetup.isFilterBank() ? BANK_FILTER_ICON : NO_BANK_FILTER_ICON);
			}
		});

		unorderedHighlightIndicator.setToolTipText("Only highlight items that are missing from the inventory and ignore order");
		unorderedHighlightIndicator.setIcon(inventorySetup.isUnorderedHighlight() ? UNORDERED_HIGHLIGHT_ICON : NO_UNORDERED_HIGHLIGHT_ICON);
		unorderedHighlightIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					inventorySetup.setUnorderedHighlight(!inventorySetup.isUnorderedHighlight());
					unorderedHighlightIndicator.setToolTipText(inventorySetup.isUnorderedHighlight() ? "Enable default ordered highlighting" : "Only highlight items that are missing from the inventory and ignore order");
					updateUnorderedHighlightIndicator();
					plugin.updateConfig();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				unorderedHighlightIndicator.setIcon(inventorySetup.isUnorderedHighlight() ? UNORDERED_HIGHLIGHT_HOVER_ICON : NO_UNORDERED_HIGHLIGHT_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				unorderedHighlightIndicator.setIcon(inventorySetup.isUnorderedHighlight() ? UNORDERED_HIGHLIGHT_ICON : NO_UNORDERED_HIGHLIGHT_ICON);
			}
		});

		favoriteIndicator.setToolTipText(inventorySetup.isFavorite() ? "Remove this setup from the list of favorites" : "Favorite this setup so it appears at the top of the list");
		favoriteIndicator.setIcon(inventorySetup.isFavorite() ? FAVORITE_ICON : NO_FAVORITE_ICON);
		favoriteIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					inventorySetup.setFavorite(!inventorySetup.isFavorite());
					favoriteIndicator.setToolTipText(inventorySetup.isFavorite() ? "Remove this setup from the list of favorites" : "Favorite this setup so it appears at the top of the list");
					updateFavoriteIndicator();
					plugin.updateConfig();
					// rebuild the panel so this panel will move positions from being favorited/unfavorited
					panel.rebuild(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				favoriteIndicator.setIcon(inventorySetup.isFavorite() ? FAVORITE_HOVER_ICON : NO_FAVORITE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				favoriteIndicator.setIcon(inventorySetup.isFavorite() ? FAVORITE_ICON : NO_FAVORITE_ICON);
			}
		});

		highlightIndicator.setToolTipText("Enable highlighting");
		highlightIndicator.setIcon(inventorySetup.isHighlightDifference() ? TOGGLE_HIGHLIGHT_ICON : NO_TOGGLE_HIGHLIGHT_ICON);
		highlightIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					inventorySetup.setHighlightDifference(!inventorySetup.isHighlightDifference());
					highlightIndicator.setToolTipText(inventorySetup.isHighlightDifference() ? "Disable highlighting" : "Enable highlighting");
					updateToggleHighlightLabel();
					updateHighlightColorLabel();
					plugin.updateConfig();
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				highlightIndicator.setIcon(inventorySetup.isHighlightDifference() ? TOGGLE_HIGHLIGHT_HOVER_ICON : NO_TOGGLE_HIGHLIGHT_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				highlightIndicator.setIcon(inventorySetup.isHighlightDifference() ? TOGGLE_HIGHLIGHT_ICON : NO_TOGGLE_HIGHLIGHT_ICON);
			}
		});

		highlightColorIndicator.setToolTipText("Edit highlight color");
		highlightColorIndicator.setIcon(inventorySetup.isHighlightDifference() ? HIGHLIGHT_COLOR_ICON : NO_HIGHLIGHT_COLOR_ICON);
		highlightColorIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					openColorPicker("Choose a Highlight color", invSetup.getHighlightColor(),
						c ->
						{
							inventorySetup.setHighlightColor(c);
							updateHighlightColorLabel();
						}
					);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				highlightColorIndicator.setIcon(inventorySetup.isHighlightDifference() ? HIGHLIGHT_COLOR_HOVER_ICON : NO_HIGHLIGHT_COLOR_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				highlightColorIndicator.setIcon(inventorySetup.isHighlightDifference() ? HIGHLIGHT_COLOR_ICON : NO_HIGHLIGHT_COLOR_ICON);
			}
		});

		JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, H_GAP_BTN, 0));
		leftActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		leftActions.add(bankFilterIndicator);
		leftActions.add(unorderedHighlightIndicator);
		leftActions.add(highlightIndicator);
		leftActions.add(highlightColorIndicator);
		leftActions.add(favoriteIndicator);

		viewSetupLabel.setToolTipText("View setup");
		viewSetupLabel.setIcon(VIEW_SETUP_ICON);
		viewSetupLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					panel.setCurrentInventorySetup(inventorySetup, true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				viewSetupLabel.setIcon(VIEW_SETUP_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				viewSetupLabel.setIcon(VIEW_SETUP_ICON);
			}
		});

		exportLabel.setToolTipText("Export setup");
		exportLabel.setIcon(EXPORT_ICON);
		exportLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					plugin.exportSetup(inventorySetup);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				exportLabel.setIcon(EXPORT_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				exportLabel.setIcon(EXPORT_ICON);
			}
		});

		deleteLabel.setToolTipText("Delete setup");
		deleteLabel.setIcon(DELETE_ICON);
		deleteLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					plugin.removeInventorySetup(inventorySetup);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				deleteLabel.setIcon(DELETE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				deleteLabel.setIcon(DELETE_ICON);
			}
		});

		JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, H_GAP_BTN, 0));
		rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		rightActions.add(viewSetupLabel);
		rightActions.add(exportLabel);
		rightActions.add(deleteLabel);

		bottomContainer.add(leftActions, BorderLayout.WEST);
		bottomContainer.add(rightActions, BorderLayout.EAST);

		add(nameWrapper, BorderLayout.NORTH);
		add(bottomContainer, BorderLayout.CENTER);

		updateHighlightColorLabel();
		updateToggleHighlightLabel();
		updateFavoriteIndicator();

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

	private void updateHighlightColorLabel()
	{
		Color color = inventorySetup.getHighlightColor();
		highlightColorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, color));
		highlightColorIndicator.setIcon(inventorySetup.isHighlightDifference() ? HIGHLIGHT_COLOR_ICON : NO_HIGHLIGHT_COLOR_ICON);
	}

	private void updateDisplayColorLabel(Color color)
	{
		displayColorIndicator.setBorder(new CompoundBorder(
				new EmptyBorder(0, 4, 0, 0),
				new MatteBorder(0, 0, 3, 0, color)));

	}

	private void updateBankFilterLabel()
	{
		bankFilterIndicator.setIcon(inventorySetup.isFilterBank() ? BANK_FILTER_ICON : NO_BANK_FILTER_ICON);
	}

	private void updateUnorderedHighlightIndicator()
	{
		unorderedHighlightIndicator.setIcon(inventorySetup.isUnorderedHighlight() ? UNORDERED_HIGHLIGHT_ICON : NO_UNORDERED_HIGHLIGHT_ICON);
	}

	private void updateFavoriteIndicator()
	{
		favoriteIndicator.setIcon(inventorySetup.isFavorite() ? FAVORITE_ICON : NO_FAVORITE_ICON);
	}

	private void updateToggleHighlightLabel()
	{
		highlightIndicator.setIcon(inventorySetup.isHighlightDifference() ? TOGGLE_HIGHLIGHT_ICON : NO_TOGGLE_HIGHLIGHT_ICON);
	}

	private void openColorPicker(String title, Color startingColor, Consumer<Color> onColorChange)
	{

		RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
				SwingUtilities.windowForComponent(this),
				startingColor,
				title,
				false);

		colorPicker.setLocation(getLocationOnScreen());
		colorPicker.setOnColorChange(onColorChange);

		colorPicker.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				plugin.updateConfig();
			}
		});

		colorPicker.setVisible(true);
	}
}
