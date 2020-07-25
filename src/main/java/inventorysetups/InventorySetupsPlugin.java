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
package inventorysetups;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import inventorysetups.ui.InventorySetupPluginPanel;
import inventorysetups.ui.InventorySetupSlot;
import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.SpriteID;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.vars.InputType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.account.AccountSession;
import net.runelite.client.account.SessionManager;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.SessionClose;
import net.runelite.client.events.SessionOpen;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.chatbox.ChatboxItemSearch;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.runepouch.Runes;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageUtil;


import javax.inject.Inject;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@PluginDescriptor(
		name = "Inventory Setups",
		description = "Save gear setups for specific activities"
)

@Slf4j
public class InventorySetupsPlugin extends Plugin
{

	public static final String CONFIG_GROUP = "inventorysetups";
	public static final String CONFIG_KEY = "setups";
	public static final String CONFIG_KEY_COMPACT_MODE = "compactMode";
	public static final String CONFIG_KEY_SORTING_MODE = "sortingMode";
	public static final String CONFIG_KEY_HIDE_BUTTON = "hideHelpButton";
	public static final String INV_SEARCH = "inv:";
	private static final String OPEN_SETUP_MENU_ENTRY = "Open setup";
	private static final String RETURN_TO_OVERVIEW_ENTRY = "Close current setup";
	private static final int NUM_INVENTORY_ITEMS = 28;
	private static final int NUM_EQUIPMENT_ITEMS = 14;
	private static final int SPELLBOOK_VARBIT = 4070;

	@Inject
	@Getter
	private Client client;

	@Inject
	private SessionManager sessionManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	@Getter
	private SpriteManager spriteManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	@Getter
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	@Getter
	private InventorySetupConfig config;

	@Inject
	@Getter
	private ColorPickerManager colorPickerManager;

	private InventorySetupPluginPanel panel;

	@Getter
	private ArrayList<InventorySetup> inventorySetups;

	private NavigationButton navButton;

	@Inject
	private InventorySetupBankSearch bankSearch;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatboxItemSearch itemSearch;

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	private ChatboxTextInput searchInput;

	private boolean doBankSearchOnNextGameTick;

	private static final Varbits[] RUNE_POUCH_AMOUNT_VARBITS =
			{
					Varbits.RUNE_POUCH_AMOUNT1, Varbits.RUNE_POUCH_AMOUNT2, Varbits.RUNE_POUCH_AMOUNT3
			};
	private static final Varbits[] RUNE_POUCH_RUNE_VARBITS =
			{
					Varbits.RUNE_POUCH_RUNE1, Varbits.RUNE_POUCH_RUNE2, Varbits.RUNE_POUCH_RUNE3
			};



	private final HotkeyListener returnToSetupsHotkeyListener = new HotkeyListener(() -> config.returnToSetupsHotkey())
	{
		@Override
		public void hotkeyPressed()
		{
			panel.returnToOverviewPanel(false);
		}
	};

	private final HotkeyListener filterBankHotkeyListener = new HotkeyListener(() -> config.filterBankHotkey())
	{
		@Override
		public void hotkeyPressed()
		{
			// you must wait at least one game tick otherwise
			// the bank filter will work but then go back to the previous tab.
			// For some reason this can still happen but it is very rare,
			// and only when the user clicks a tab and the hot key extremely shortly after.
			int gameTick = client.getTickCount();
			clientThread.invokeLater(() ->
			{
				int gameTick2 = client.getTickCount();
				if (gameTick2 <= gameTick)
				{
					return false;
				}

				doBankSearch(InputType.SEARCH);
				return true;
			});
		}
	};

	@Provides
	InventorySetupConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventorySetupConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(CONFIG_GROUP))
		{
			if (event.getKey().equals(CONFIG_KEY_COMPACT_MODE) || event.getKey().equals(CONFIG_KEY_HIDE_BUTTON) ||
				event.getKey().equals(CONFIG_KEY_SORTING_MODE))
			{
				panel.rebuild(true);
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		Widget bankWidget = client.getWidget(WidgetInfo.BANK_TITLE_BAR);
		if (bankWidget == null || bankWidget.isHidden())
		{
			return;
		}

		// Adds menu entries to show worn items button
		if (event.getOption().equals("Show worn items"))
		{
			MenuEntry[] menuEntries = client.getMenuEntries();
			final int oldMenuSize = menuEntries.length;
			menuEntries = Arrays.copyOf(menuEntries, oldMenuSize + inventorySetups.size() + 1);

			for (int i = 0; i < inventorySetups.size(); i++)
			{
				MenuEntry menuEntry = menuEntries[oldMenuSize + i] = new MenuEntry();
				menuEntry.setOption(OPEN_SETUP_MENU_ENTRY);
				menuEntry.setTarget(ColorUtil.prependColorTag(inventorySetups.get(inventorySetups.size() - 1 - i).getName(), JagexColors.MENU_TARGET));

				// The param will used to find the correct setup if a menu entry is clicked
				menuEntry.setIdentifier(inventorySetups.size() - 1 - i);
				menuEntry.setType(MenuAction.RUNELITE.getId());
			}

			// add menu entry to close setup
			MenuEntry menuEntryCloseSetup = menuEntries[menuEntries.length - 1] = new MenuEntry();
			menuEntryCloseSetup.setOption(RETURN_TO_OVERVIEW_ENTRY);
			menuEntryCloseSetup.setType(MenuAction.RUNELITE.getId());
			menuEntryCloseSetup.setTarget("");
			menuEntryCloseSetup.setIdentifier(0);

			client.setMenuEntries(menuEntries);
		}
	}

	public void switchViews(boolean compactMode)
	{
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_COMPACT_MODE, compactMode);
	}

	public void toggleAlphabeticalMode(InventorySetupSorting mode)
	{
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_SORTING_MODE, mode);
	}

	@Override
	public void startUp()
	{
		this.doBankSearchOnNextGameTick = false;
		this.panel = new InventorySetupPluginPanel(this, itemManager);
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "/inventorysetups_icon.png");

		navButton = NavigationButton.builder()
				.tooltip("Inventory Setups")
				.icon(icon)
				.priority(6)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
		keyManager.registerKeyListener(returnToSetupsHotkeyListener);
		keyManager.registerKeyListener(filterBankHotkeyListener);

		// load all the inventory setups from the config file
		clientThread.invokeLater(() ->
		{
			switch (client.getGameState())
			{
				case STARTING:
				case UNKNOWN:
					return false;
			}

			loadConfig();

			SwingUtilities.invokeLater(() ->
			{
				panel.rebuild(true);
			});

			return true;
		});

	}

	public void addInventorySetup()
	{
		final String name = JOptionPane.showInputDialog(panel,
				"Enter the name of this setup.",
				"Add New Setup",
				JOptionPane.PLAIN_MESSAGE);

		// cancel button was clicked
		if (name == null)
		{
			return;
		}

		clientThread.invokeLater(() ->
		{
			ArrayList<InventorySetupItem> inv = getNormalizedContainer(InventoryID.INVENTORY);
			ArrayList<InventorySetupItem> eqp = getNormalizedContainer(InventoryID.EQUIPMENT);

			ArrayList<InventorySetupItem> runePouchData = null;
			if (checkIfContainerContainsItem(ItemID.RUNE_POUCH, inv, false, true))
			{
				runePouchData = getRunePouchData();
			}

			int spellbook = getCurrentSpellbook();

			final InventorySetup invSetup = new InventorySetup(inv, eqp, runePouchData, name, "",
													config.highlightColor(),
													config.highlightStackDifference(),
													config.highlightVariationDifference(),
													config.highlightDifference(),
													config.bankFilter(),
													config.highlightUnorderedDifference(),
													spellbook);
			addInventorySetupClientThread(invSetup);
		});
	}

	public void moveSetup(int invIndex, int newPosition)
	{
		// Setup is already in the specified position or is out of position
		if (invIndex == newPosition || newPosition < 0 || newPosition >= inventorySetups.size())
		{
			return;
		}
		InventorySetup setup = inventorySetups.remove(invIndex);
		inventorySetups.add(newPosition, setup);
		panel.rebuild(false);
		updateConfig();
	}

	public List<InventorySetup> filterSetups(String textToFilter)
	{
		final String textToFilterLower = textToFilter.toLowerCase();
		return inventorySetups.stream()
				.filter(i -> i.getName().toLowerCase().contains(textToFilterLower))
				.collect(Collectors.toList());
	}

	public void doBankSearch(InputType type)
	{
		final InventorySetup currentSelectedSetup = panel.getCurrentSelectedSetup();

		if (currentSelectedSetup != null && currentSelectedSetup.isFilterBank())
		{
			// When tab is selected with search window open, the search window closes but the search button
			// stays highlighted, this solves that issue
			clientThread.invoke(() ->
			{
				client.setVarbit(Varbits.CURRENT_BANK_TAB, 0);
				bankSearch.search(type, INV_SEARCH + currentSelectedSetup.getName(), true);

				Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
				if (bankContainer != null && !bankContainer.isHidden())
				{
					Widget searchBackground = client.getWidget(WidgetInfo.BANK_SEARCH_BUTTON_BACKGROUND);
					searchBackground.setSpriteId(SpriteID.EQUIPMENT_SLOT_TILE);
				}
			});
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// filter the bank if the requirements are met
		// this is for when withdraw-x or deposit-x are clicked.
		if (doBankSearchOnNextGameTick
			&& panel.getCurrentSelectedSetup() != null
			&& client.getVar(VarClientInt.INPUT_TYPE) == InputType.NONE.getType()
			&& !bankIsFiltered())
		{
			doBankSearch(InputType.NONE);
			doBankSearchOnNextGameTick = false;
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{

		if (event.getMenuAction() == MenuAction.RUNELITE)
		{
			if (event.getMenuOption().equals(OPEN_SETUP_MENU_ENTRY))
			{
				assert event.getId() >= 0 && event.getId() < inventorySetups.size() : "Action param out of range";

				resetBankSearch();
				panel.setCurrentInventorySetup(inventorySetups.get(event.getId()), true);
				return;
			}

			if (event.getMenuOption().equals(RETURN_TO_OVERVIEW_ENTRY))
			{
				panel.returnToOverviewPanel(false);
				return;
			}
		}

		if (panel.getCurrentSelectedSetup() == null)
		{
			return;
		}

		if (event.getWidgetId() == WidgetInfo.BANK_ITEM_CONTAINER.getId() && event.getMenuOption().startsWith("View tab"))
		{
			if (config.disableBankTabBar())
			{
				event.consume();
			}
			return;
		}

		if (event.getWidgetId() == WidgetInfo.BANK_SEARCH_BUTTON_BACKGROUND.getId()
				&& client.getWidget(WidgetInfo.BANK_SEARCH_BUTTON_BACKGROUND).getSpriteId() != SpriteID.EQUIPMENT_SLOT_SELECTED)
		{
			// This ensures that when clicking Search when tab is selected, the search input is opened rather
			// than client trying to close it first
			client.setVar(VarClientStr.INPUT_TEXT, "");
			client.setVar(VarClientInt.INPUT_TYPE, 0);

			// don't allow the bank to retry a filter if the search button is clicked
			doBankSearchOnNextGameTick = false;
		}

		// if withdraw or deposit x is clicked, we need to refilter the bank
		if ((event.getWidgetId() == WidgetInfo.BANK_ITEM_CONTAINER.getId()
				|| event.getWidgetId() == WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId())
				&& event.getMenuAction() == MenuAction.CC_OP_LOW_PRIORITY
				&& (event.getMenuOption().equalsIgnoreCase("withdraw-x")
				|| event.getMenuOption().equalsIgnoreCase("deposit-x"))
				&& bankIsFiltered())
		{
			doBankSearchOnNextGameTick = true;
		}

		// if a bank tab is clicked, then make sure we do not try to filter
		if (doBankSearchOnNextGameTick
				&& event.getWidgetId() == WidgetInfo.BANK_TAB_CONTAINER.getId()
				&& event.getMenuOption().startsWith("View"))
		{
			doBankSearchOnNextGameTick = false;
		}

	}

	@Subscribe
	public void onVarClientIntChanged(VarClientIntChanged event)
	{
		if (event.getIndex() == 386)
		{
			// must be invoked later otherwise causes freezing.
			clientThread.invokeLater(() ->
			{
				// Note: small bug here because when the settings or equip items button is pressed
				// it will re filter the bank even if the user has clicked somewhere to remove the filter
				// possible fix: use MenuOptionClicked for buttons and check spriteId to see if they are being unclicked
				// and go back to widgetOpened to check if the bank was opened.

				// checks to see if the hide worn items button was clicked or bank was opened
				int value = client.getVarcIntValue(386);
				if (value == 0)
				{
					doBankSearch(InputType.SEARCH);
				}
			});
		}

	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{

		if (event.getIndex() == 439 && client.getGameState() == GameState.LOGGED_IN)
		{
			// must be invoked later otherwise causes freezing.
			clientThread.invokeLater(() ->
			{
				panel.highlightSpellbook();
			});
		}

	}

	public void resetBankSearch()
	{
		bankSearch.reset(true);
	}

	public ArrayList<InventorySetupItem> getRunePouchData()
	{
		ArrayList<InventorySetupItem> runePouchData = new ArrayList<>();

		for (int i = 0; i < RUNE_POUCH_RUNE_VARBITS.length; i++)
		{
			int runeId = client.getVar(RUNE_POUCH_RUNE_VARBITS[i]);
			Runes rune = Runes.getRune(runeId);
			int runeAmount = rune == null ? 0 : client.getVar(RUNE_POUCH_AMOUNT_VARBITS[i]);
			String runeName = rune == null ? "" : rune.getName();
			int runeItemId = rune == null ? -1 : rune.getItemId();

			runePouchData.add(new InventorySetupItem(runeItemId, runeName, runeAmount));
		}

		return runePouchData;
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		String eventName = event.getEventName();

		int[] intStack = client.getIntStack();
		String[] stringStack = client.getStringStack();
		int intStackSize = client.getIntStackSize();
		int stringStackSize = client.getStringStackSize();

		switch (eventName)
		{
			case "bankSearchFilter":
			{
				String search = stringStack[stringStackSize - 1];

				boolean invSearch = search.startsWith(INV_SEARCH);
				if (invSearch)
				{
					final InventorySetup currentSetup = panel.getCurrentSelectedSetup();

					if (currentSetup != null)
					{
						int itemId = intStack[intStackSize - 1];

						if (setupContainsItem(currentSetup, itemId))
						{
							// return true
							intStack[intStackSize - 2] = 1;
						}
						else
						{
							intStack[intStackSize - 2] = 0;
						}
					}
				}
				break;
			}
			case "getSearchingTagTab":
				if (panel.getCurrentSelectedSetup() == null)
				{
					return;
				}

				// check if the bank is filtered. If it is, don't lay out again.
				intStack[intStackSize - 1] = bankIsFiltered() ? 1 : 0;
				break;
		}


	}

	public void updateCurrentSetup(InventorySetup setup)
	{
		int confirm = JOptionPane.showConfirmDialog(panel,
				"Are you sure you want update this inventory setup?",
				"Warning", JOptionPane.OK_CANCEL_OPTION);

		// cancel button was clicked
		if (confirm != JOptionPane.YES_OPTION)
		{
			return;
		}

		// must be on client thread to get names
		clientThread.invokeLater(() ->
		{
			ArrayList<InventorySetupItem> inv = getNormalizedContainer(InventoryID.INVENTORY);
			ArrayList<InventorySetupItem> eqp = getNormalizedContainer(InventoryID.EQUIPMENT);

			ArrayList<InventorySetupItem> runePouchData = null;
			if (checkIfContainerContainsItem(ItemID.RUNE_POUCH, inv, false, true))
			{
				runePouchData = getRunePouchData();
			}

			setup.updateRunePouch(runePouchData);
			setup.updateInventory(inv);
			setup.updateEquipment(eqp);
			setup.updateSpellbook(getCurrentSpellbook());
			updateConfig();
			panel.refreshCurrentSetup();
		});
	}

	public void updateSlotFromContainer(final InventorySetupSlot slot)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			JOptionPane.showMessageDialog(panel,
					"You must be logged in to update from " + (slot.getSlotID().toString().toLowerCase() + "."),
					"Cannot Update Item",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		final ArrayList<InventorySetupItem> container = getContainerFromSlot(slot);

		// must be invoked on client thread to get the name
		clientThread.invokeLater(() ->
		{
			final ArrayList<InventorySetupItem> playerContainer = getNormalizedContainer(slot.getSlotID());
			final InventorySetupItem newItem = playerContainer.get(slot.getIndexInSlot());

			// update the rune pouch data
			if (!updateIfRunePouch(slot, container.get(slot.getIndexInSlot()), newItem))
			{
				return;
			}

			container.set(slot.getIndexInSlot(), newItem);
			updateConfig();
			panel.refreshCurrentSetup();
		});

	}

	public void updateSlotFromSearch(final InventorySetupSlot slot)
	{

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			JOptionPane.showMessageDialog(panel,
					"You must be logged in to search.",
					"Cannot Search for Item",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		final ArrayList<InventorySetupItem> container = getContainerFromSlot(slot);

		itemSearch
			.tooltipText("Set slot to")
			.onItemSelected((itemId) ->
			{
				clientThread.invokeLater(() ->
				{
					int finalId = itemManager.canonicalize(itemId);

					/*
					 NOTE: the itemSearch shows items from skill guides which can be selected
					 And it does not show equipment variants for worn items that reduce weight.
					 Variation mapping would fix this issue for the inventory,
					 but then it would cause rings, potions, etc to be the same when it may not be desired
					 If a worn item is selected for the equipment, it will not be the correct itemID since
					 only the inventory variant and the skill guide variants show up in the search
					 If there is a way to figure out if and item is a skill guide item, then the inventory
					 issue can be solved. For equipment, you would also need a way to get the equipment variant
					 of a worn item that has weight reduction from the inventory counterpart

					 For now, it's possible that the user will pick a skill guide item, and it will cause highlighting
					 This only occurs if variation differences are turned on. Weight reducing equipment
					 will also be highlighted if selected for equipment if variation differences are turned on.
					*/

					// if the item is stackable, ask for a quantity
					if (itemManager.getItemComposition(finalId).isStackable())
					{
						final int finalIdCopy = finalId;
						searchInput = chatboxPanelManager.openTextInput("Enter amount")
							.addCharValidator(arg -> arg >= 48 && arg <= 57) // only allow numbers (ASCII)
							.onDone((input) ->
							{
								clientThread.invokeLater(() ->
								{
									String inputParsed = input;
									if (inputParsed.length() > 10)
									{
										inputParsed = inputParsed.substring(0, 10);
									}

									// limit to max int value
									long quantityLong = Long.parseLong(inputParsed);
									int quantity = (int) Math.min(quantityLong, Integer.MAX_VALUE);
									quantity = Math.max(quantity, 1);

									final String itemName = itemManager.getItemComposition(finalIdCopy).getName();
									final InventorySetupItem newItem = new InventorySetupItem(finalIdCopy, itemName, quantity);

									// update the rune pouch data
									if (!updateIfRunePouch(slot, container.get(slot.getIndexInSlot()), newItem))
									{
										return;
									}

									container.set(slot.getIndexInSlot(), newItem);
									updateConfig();
									panel.refreshCurrentSetup();

								});
							}).build();
					}
					else
					{
						final String itemName = itemManager.getItemComposition(finalId).getName();
						final InventorySetupItem newItem = new InventorySetupItem(finalId, itemName, 1);

						// update the rune pouch data
						if (!updateIfRunePouch(slot, container.get(slot.getIndexInSlot()), newItem))
						{
							return;
						}

						container.set(slot.getIndexInSlot(), newItem);
						updateConfig();
						panel.refreshCurrentSetup();
					}

				});
			})
			.build();
	}

	public void updateSpellbookInSetup(int newSpellbook)
	{
		assert panel.getCurrentSelectedSetup() != null : "Setup is null";
		assert newSpellbook >= 0 && newSpellbook < 4 : "New spellbook out of range";

		clientThread.invokeLater(() ->
		{
			panel.getCurrentSelectedSetup().updateSpellbook(newSpellbook);
			updateConfig();
			panel.refreshCurrentSetup();
		});

	}

	public void updateNotesInSetup(final InventorySetup setup, final String text)
	{
		clientThread.invokeLater(() ->
		{
			setup.updateNotes(text);
			updateConfig();
		});
	}

	public void removeInventorySetup(final InventorySetup setup)
	{
		int confirm = JOptionPane.showConfirmDialog(panel,
				"Are you sure you want to permanently delete this inventory setup?",
				"Warning", JOptionPane.OK_CANCEL_OPTION);

		if (confirm != JOptionPane.YES_OPTION)
		{
			return;
		}

		inventorySetups.remove(setup);
		panel.rebuild(false);
		updateConfig();
	}

	public void updateConfig()
	{
		final Gson gson = new Gson();
		final String json = gson.toJson(inventorySetups);
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, json);
	}

	@Subscribe
	public void onSessionOpen(SessionOpen event)
	{
		final AccountSession session = sessionManager.getAccountSession();
		if (session != null && session.getUsername() != null)
		{
			// config will have changed to new account, load it up
			clientThread.invokeLater(() ->
			{
				loadConfig();
				SwingUtilities.invokeLater(() ->
				{
					panel.rebuild(true);
				});

				return true;
			});
		}
	}

	@Subscribe
	public void onSessionClose(SessionClose event)
	{
		// config will have changed to local file
		clientThread.invokeLater(() ->
		{
			loadConfig();
			SwingUtilities.invokeLater(() ->
			{
				panel.rebuild(true);
			});

			return true;
		});
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{

		// check to see that the container is the equipment or inventory
		ItemContainer container = event.getItemContainer();

		if (container == client.getItemContainer(InventoryID.INVENTORY))
		{
			panel.highlightInventory();
		}
		else if (container == client.getItemContainer(InventoryID.EQUIPMENT))
		{
			panel.highlightEquipment();
		}

	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		panel.highlightInventory();
		panel.highlightEquipment();
		panel.highlightSpellbook();
	}

	// Must be called on client thread!
	public int getCurrentSpellbook()
	{
		assert client.isClientThread() : "getCurrentSpellbook must be called on Client Thread";
		return client.getVarbitValue(SPELLBOOK_VARBIT);
	}

	public ArrayList<InventorySetupItem> getNormalizedContainer(final InventorySetupSlotID id)
	{
		switch (id)
		{
			case INVENTORY:
				return getNormalizedContainer(InventoryID.INVENTORY);
			case EQUIPMENT:
				return getNormalizedContainer(InventoryID.EQUIPMENT);
			case RUNE_POUCH:
				return getRunePouchData();
			default:
				assert false : "Wrong slot ID!";
				return null;
		}
	}

	public ArrayList<InventorySetupItem> getNormalizedContainer(final InventoryID id)
	{
		assert id == InventoryID.INVENTORY || id == InventoryID.EQUIPMENT : "invalid inventory ID";

		final ItemContainer container = client.getItemContainer(id);

		ArrayList<InventorySetupItem> newContainer = new ArrayList<>();

		Item[] items = null;
		if (container != null)
		{
			items = container.getItems();
		}

		int size = id == InventoryID.INVENTORY ? NUM_INVENTORY_ITEMS : NUM_EQUIPMENT_ITEMS;

		for (int i = 0; i < size; i++)
		{
			if (items == null || i >= items.length)
			{
				// add a "dummy" item to fill the normalized container to the right size
				// this will be useful to compare when no item is in a slot
				newContainer.add(new InventorySetupItem(-1, "", 0));
			}
			else
			{
				final Item item = items[i];
				String itemName = "";

				// only the client thread can retrieve the name. Therefore, do not use names to compare!
				if (client.isClientThread())
				{
					itemName = itemManager.getItemComposition(item.getId()).getName();
				}
				newContainer.add(new InventorySetupItem(item.getId(), itemName, item.getQuantity()));
			}
		}

		return newContainer;
	}

	public void exportSetup(final InventorySetup setup)
	{
		final Gson gson = new Gson();
		final String json = gson.toJson(setup);
		final StringSelection contents = new StringSelection(json);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);

		JOptionPane.showMessageDialog(panel,
				"Setup data was copied to clipboard.",
				"Export Setup Succeeded",
				JOptionPane.PLAIN_MESSAGE);
	}

	public void importSetup()
	{
		try
		{
			final String setup = JOptionPane.showInputDialog(panel,
					"Enter setup data",
					"Import New Setup",
					JOptionPane.PLAIN_MESSAGE);

			// cancel button was clicked
			if (setup == null)
			{
				return;
			}

			final Gson gson = new Gson();
			Type type = new TypeToken<InventorySetup>()
			{

			}.getType();

			final InventorySetup newSetup  = gson.fromJson(setup, type);
			clientThread.invokeLater(() ->
			{
				if (newSetup.getRune_pouch() == null && checkIfContainerContainsItem(ItemID.RUNE_POUCH, newSetup.getInventory(), false, true))
				{
					newSetup.updateRunePouch(getRunePouchData());
				}
				if (newSetup.getNotes() == null)
				{
					newSetup.updateNotes("");
				}
				addInventorySetupClientThread(newSetup);
			});
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(panel,
					"Invalid setup data.",
					"Import Setup Failed",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		bankSearch.reset(true);
	}

	public boolean isHighlightingAllowed()
	{
		return client.getGameState() == GameState.LOGGED_IN;
	}

	private ArrayList<InventorySetupItem> getContainerFromSlot(final InventorySetupSlot slot)
	{
		ArrayList<InventorySetupItem> container = slot.getParentSetup().getInventory();

		if (slot.getSlotID() == InventorySetupSlotID.EQUIPMENT)
		{
			container = slot.getParentSetup().getEquipment();
		}
		else if (slot.getSlotID() == InventorySetupSlotID.RUNE_POUCH)
		{
			container = slot.getParentSetup().getRune_pouch();
		}

		assert slot.getParentSetup() == panel.getCurrentSelectedSetup() : "Setup Mismatch";
		assert slot.getIndexInSlot() < container.size() : "Index is greater than container size";

		return container;
	}

	private void loadConfig()
	{
		// serialize the internal data structure from the json in the configuration
		final String json = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY);
		if (Strings.isNullOrEmpty(json))
		{
			inventorySetups = new ArrayList<>();
		}
		else
		{
			try
			{
				final Gson gson = new Gson();
				Type type = new TypeToken<ArrayList<InventorySetup>>()
				{

				}.getType();
				inventorySetups = gson.fromJson(json, type);
				clientThread.invokeLater(() ->
				{
					for (final InventorySetup setup : inventorySetups)
					{
						if (setup.getRune_pouch() == null && checkIfContainerContainsItem(ItemID.RUNE_POUCH, setup.getInventory(), false, true))
						{
							setup.updateRunePouch(getRunePouchData());
						}
						if (setup.getNotes() == null)
						{
							setup.updateNotes("");
						}
					}
				});
			}
			catch (Exception e)
			{
				inventorySetups = new ArrayList<>();
			}
		}
	}

	private void addInventorySetupClientThread(final InventorySetup newSetup)
	{
		SwingUtilities.invokeLater(() ->
		{
			inventorySetups.add(newSetup);
			panel.rebuild(true);

			updateConfig();
		});
	}

	private boolean setupContainsItem(final InventorySetup setup, int itemID)
	{

		// So place holders will show up in the bank.
		itemID = itemManager.canonicalize(itemID);

		// don't variation map unless it's been selected
		if (!setup.isVariationDifference())
		{
			itemID = ItemVariationMapping.map(itemID);
		}

		// check the rune pouch to see if it has the item (runes in this case)
		if (setup.getRune_pouch() != null)
		{
			if (checkIfContainerContainsItem(itemID, setup.getRune_pouch(), false, true))
			{
				return true;
			}
		}

		// canonicalize is needed for equipment to deal with worn items like graceful.
		return checkIfContainerContainsItem(itemID, setup.getInventory(), setup.isVariationDifference(), true) ||
				checkIfContainerContainsItem(itemID, setup.getEquipment(), setup.isVariationDifference(), true);
	}

	private boolean checkIfContainerContainsItem(int itemID, final ArrayList<InventorySetupItem> container, boolean isVariationDifference, boolean canonicalize)
	{
		for (final InventorySetupItem item : container)
		{
			if (itemID == getCorrectID(isVariationDifference, canonicalize, item.getId()))
			{
				return true;
			}
		}

		return false;
	}

	private int getCorrectID(boolean variationDifference, boolean canonicalize, int itemId)
	{

		if (canonicalize)
		{
			itemId = itemManager.canonicalize(itemId);
		}

		// if variation difference isn't selected, get the mapped id
		if (!variationDifference)
		{
			return ItemVariationMapping.map(itemId);
		}

		return itemId;
	}

	private boolean updateIfRunePouch(final InventorySetupSlot slot, final InventorySetupItem oldItem, final InventorySetupItem newItem)
	{

		if (ItemVariationMapping.map(newItem.getId()) == ItemID.RUNE_POUCH)
		{

			if (slot.getSlotID() != InventorySetupSlotID.INVENTORY)
			{

				SwingUtilities.invokeLater(() ->
				{
					JOptionPane.showMessageDialog(panel,
							"You can't have a Rune Pouch there.",
							"Invalid Item",
							JOptionPane.ERROR_MESSAGE);
				});

				return false;
			}

			// only display this message if we aren't replacing a rune pouch with a new rune pouch
			if (slot.getParentSetup().getRune_pouch() != null && ItemVariationMapping.map(oldItem.getId()) != ItemID.RUNE_POUCH)
			{
				SwingUtilities.invokeLater(() ->
				{
					JOptionPane.showMessageDialog(panel,
							"You can't have two Rune Pouches.",
							"Invalid Item",
							JOptionPane.ERROR_MESSAGE);
				});
				return false;
			}

			slot.getParentSetup().updateRunePouch(getRunePouchData());
		}
		else if (ItemVariationMapping.map(oldItem.getId()) == ItemID.RUNE_POUCH)
		{
			// if the old item is a rune pouch, need to update it to null 
			slot.getParentSetup().updateRunePouch(null);
		}

		return true;
	}

	private boolean bankIsFiltered()
	{
		if (panel.getCurrentSelectedSetup() != null)
		{
			Widget bankWidget = client.getWidget(WidgetInfo.BANK_TITLE_BAR);
			if (bankWidget == null || bankWidget.isHidden())
			{
				return false;
			}
			String bankTitle = bankWidget.getText();
			return bankTitle.equalsIgnoreCase("Showing items: <col=ff0000>" + INV_SEARCH + panel.getCurrentSelectedSetup().getName() + "</col>");
		}
		return false;
	}

}
