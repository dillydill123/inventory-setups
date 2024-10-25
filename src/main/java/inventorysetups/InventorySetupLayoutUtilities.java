package inventorysetups;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.banktags.TagManager;
import net.runelite.client.plugins.banktags.tabs.Layout;
import net.runelite.client.plugins.banktags.tabs.LayoutManager;
import net.runelite.client.util.Text;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static inventorysetups.InventorySetupsPlugin.CONFIG_GROUP;
import static inventorysetups.InventorySetupsPlugin.CONFIG_KEY_LAYOUT_DEFAULT;
import static inventorysetups.InventorySetupsPlugin.LAYOUT_PREFIX_MARKER;

public class InventorySetupLayoutUtilities
{
	private final ItemManager itemManager;

	private final TagManager tagManager;

	private final LayoutManager layoutManager;

	private final ConfigManager configManager;

	public InventorySetupLayoutUtilities(final ItemManager itemManager, final TagManager tagManager, final LayoutManager layoutManager, final ConfigManager configManager)
	{
		this.itemManager = itemManager;
		this.tagManager = tagManager;
		this.layoutManager = layoutManager;
		this.configManager = configManager;
	}

	public static String getTagNameForLayout(final String inventorySetupName)
	{
		String hashOfName = InventorySetupsPersistentDataManager.hashFunction.hashUnencodedChars(inventorySetupName).toString();
		return LAYOUT_PREFIX_MARKER + hashOfName;
	}

	public Layout getSetupLayout(final InventorySetup inventorySetupName)
	{
		final String tagName = getTagNameForLayout(inventorySetupName.getName());
		final Layout layout = layoutManager.loadLayout(tagName);
		assert layout != null : "Layout for " + inventorySetupName + " is null.";
		return layout;
	}

	public Layout createSetupLayout(final InventorySetup setup)
	{
		InventorySetupLayoutType type = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_LAYOUT_DEFAULT, InventorySetupLayoutType.class);
		return createSetupLayout(setup, type, true);
	}

	public Layout createSetupLayout(final InventorySetup setup, final InventorySetupLayoutType type, final boolean addToTag)
	{
		if (type.equals(InventorySetupLayoutType.PRESET))
		{
			return getPresetLayout(setup, addToTag);
		}
		else
		{
			return getZigZagLayout(setup, addToTag);
		}
	}

	public Layout getZigZagLayout(final InventorySetup setup, final boolean addToTag)
	{
		final String tag = getTagNameForLayout(setup.getName());
		final Layout layout = new Layout(tag);

		int startOfEquipment = 0;
		int startOfInventory = 16;
		int startOfRunePouch = 48;
		int startOfBoltPouch = 52;
		int startOfAdditionalItems = 56;

		int newSizeGuess = setup.getAdditionalFilteredItems().size() + startOfAdditionalItems;
		layout.resize(newSizeGuess);

		int nextPos = layoutZigZagContainer(setup.getEquipment(), layout, tag, addToTag, startOfEquipment);
		if (setup.getQuiver() != null && !setup.getQuiver().isEmpty())
		{
			addItemToLayout(layout, tag, setup.getQuiver().get(0), nextPos, addToTag);
		}
		layoutZigZagContainer(setup.getInventory(), layout, tag, addToTag, startOfInventory);

		// Layout the rune pouch
		if (setup.getRune_pouch() != null)
		{
			for (int i = 0; i < setup.getRune_pouch().size(); i++)
			{
				addItemToLayout(layout, tag, setup.getRune_pouch().get(i), i + startOfRunePouch, addToTag);
			}
		}

		// Bolt pouch below the rune pouch
		if (setup.getBoltPouch() != null)
		{
			for (int i = 0; i < setup.getBoltPouch().size(); i++)
			{
				addItemToLayout(layout, tag, setup.getBoltPouch().get(i), i + startOfBoltPouch, addToTag);
			}
		}

		// Additional items
		int additionalItemsCounter = 0;
		Collection<InventorySetupsItem> additionalItems = setup.getAdditionalFilteredItems().values();
		for (final InventorySetupsItem item : additionalItems)
		{
			addItemToLayout(layout, tag, item, additionalItemsCounter + startOfAdditionalItems, addToTag);
			additionalItemsCounter++;
		}

		trimLayout(layout);

		return layout;
	}

	private int layoutZigZagContainer(final List<InventorySetupsItem> container, final Layout layout, final String tag, boolean addToTag, final int start)
	{
		// Note, this might not work if the start is not a multiple of the row size (8)...
		// But this is not needed, so I won't spend time over engineering this function.

		int doubleRowStart = 0;
		int nextPos = 0;
		final int rowSize = 8;

		for (final InventorySetupsItem item : container)
		{
			if (item.getId() != -1)
			{
				addItemToLayout(layout, tag, item, nextPos + start, addToTag);
				if (nextPos == (rowSize * 2) - 1)
				{
					// We hit the end of a double row, we need to start a new one.
					doubleRowStart += 2;
					nextPos = doubleRowStart * rowSize;
				}
				else if (nextPos < ((doubleRowStart * rowSize) + rowSize))
				{
					// We are in the top half of a double row. Go down directly one.
					nextPos += rowSize;
				}
				else
				{
					// We are in the bottom half of a double. Go back up and add one to move to the right.
					nextPos = (nextPos - rowSize) + 1;
				}
			}

		}

		return nextPos;
	}

	public Layout getPresetLayout(final InventorySetup setup, final boolean addToTag)
	{
		// This generates a "Preset" layout based on an InventorySetup
		// It will have the equipment on the left and inventory on the right
		// This must be called on the clientThread

		// Note: Core Layout API does not support custom variation mappings, so even if fuzzy is marked,
		// custom mappings will not work in the layout.

		final String tag = getTagNameForLayout(setup.getName());
		final Layout layout = new Layout(tag);

		// Determine size to help reduce amount of copy overs.
		// Since the inventory will be a 4x7 on the right side of the bank, it will end at position 55.
		int startOfAdditionalItems = 56;
		int startOfBoltPouch = 48;
		int startOfRunePouch = 40;

		int newSizeGuess = setup.getAdditionalFilteredItems().size() + startOfAdditionalItems;
		layout.resize(newSizeGuess);

		// Layout the equipment on left side
		final List<InventorySetupsItem> eqp = setup.getEquipment();
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.HEAD.getSlotIdx()), 1, addToTag);
		if (setup.getQuiver() != null && !setup.getQuiver().isEmpty())
		{
			addItemToLayout(layout, tag, setup.getQuiver().get(0), 2, addToTag);
		}
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.CAPE.getSlotIdx()), 8, addToTag);
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.AMULET.getSlotIdx()), 9, addToTag);
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.AMMO.getSlotIdx()), 10, addToTag);
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.WEAPON.getSlotIdx()), 16, addToTag);
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.BODY.getSlotIdx()), 17, addToTag);
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.SHIELD.getSlotIdx()), 18, addToTag);
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.LEGS.getSlotIdx()), 25, addToTag);
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.GLOVES.getSlotIdx()), 32, addToTag);
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.BOOTS.getSlotIdx()), 33, addToTag);
		addItemToLayout(layout, tag, eqp.get(EquipmentInventorySlot.RING.getSlotIdx()), 34, addToTag);

		// Layout the inventory on the right side
		int invRow = 0;
		int invCol = 4;
		int width = 8;
		for (final InventorySetupsItem item: setup.getInventory())
		{
			addItemToLayout(layout, tag, item, invCol + (invRow * width), addToTag);
			if (invCol == 7)
			{
				invCol = 4;
				invRow++;
			}
			else
			{
				invCol++;
			}
		}

		// Rune pouch below equipment
		if (setup.getRune_pouch() != null)
		{
			for (int i = 0; i < setup.getRune_pouch().size(); i++)
			{
				addItemToLayout(layout, tag, setup.getRune_pouch().get(i), i + startOfRunePouch, addToTag);
			}
		}

		// Bolt pouch below the rune pouch
		if (setup.getBoltPouch() != null)
		{
			for (int i = 0; i < setup.getBoltPouch().size(); i++)
			{
				addItemToLayout(layout, tag, setup.getBoltPouch().get(i), i + startOfBoltPouch, addToTag);
			}
		}

		// Additional items
		int additionalItemsCounter = 0;
		Collection<InventorySetupsItem> additionalItems = setup.getAdditionalFilteredItems().values();
		for (final InventorySetupsItem item : additionalItems)
		{
			addItemToLayout(layout, tag, item, additionalItemsCounter + startOfAdditionalItems, addToTag);
			additionalItemsCounter++;
		}

		trimLayout(layout);

		return layout;
	}

	private void addItemToLayout(final Layout layout, final String tagName, final InventorySetupsItem item, final int pos, final boolean addToTag)
	{
		int id = itemManager.canonicalize(item.getId());
		layout.setItemAtPos(id, pos);
		// We may not want to add to the tag if we just want to create a layout but not update tags.
		// Useful for displaying a temporary layout.
		if (addToTag)
		{
			tagManager.addTag(id, tagName, item.isFuzzy());
		}
	}

	public void recalculateLayout(final InventorySetup setup)
	{
		final List<InventorySetupsItem> itemsInSetup = InventorySetup.getSetupItems(setup);
		final String tagName = getTagNameForLayout(setup.getName());
		final Layout layout = layoutManager.loadLayout(tagName);
		assert layout != null : "Setup " + setup.getName() + " has no layout.";

		// Remove the tag entirely.
		tagManager.removeTag(tagName);

		// Re-add every item in the tag.
		HashSet<Integer> idsInSetup = new HashSet<>();
		HashSet<Integer> idsInSetupNoFuzzy = new HashSet<>();
		for (InventorySetupsItem item : itemsInSetup)
		{
			int processedId = itemManager.canonicalize(item.getId());

			// Fuzzy tags and non-fuzzy tags are actually stored separate, so a non-fuzzy item can't override
			// The same fuzzy item that came before it (even though this would be a rare case).
			// I don't know the effect of the fuzzy and non-fuzzy items being part of both tags.
			tagManager.addTag(processedId, tagName, item.isFuzzy());

			// Track all the IDs in the setup
			idsInSetup.add(processedId);
			idsInSetupNoFuzzy.add(processedId);
			if (item.isFuzzy())
			{
				idsInSetup.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(processedId)));
			}
		}

		// Remove any items that do not belong in the layout.
		// This won't potentially remove an item if there are multiple copies of it in the setup.
		// It's possible that some users would want one of the instances of the item to be replaced
		// But for now, we avoid that. If this is needed, it's probably better to do it in the caller of this function
		// Since it knows if an item was removed or not, while this is catch all recalculation of the layout.
		HashSet<Integer> idsInLayout = new HashSet<>();
		for (int i = 0; i < layout.size(); i++)
		{
			int layoutId = layout.getItemAtPos(i);
			if (layoutId == -1)
			{
				continue;
			}

			if (!idsInSetup.contains(layoutId))
			{
				layout.removeItemAtPos(i);
				continue;
			}

			idsInLayout.add(layoutId);
		}

		// Add any items that should belong in the layout based on the setup.
		// Do not include fuzzy items in the setup because the layoutManager will automatically
		// Add those if they exist in the bank, otherwise we would add every possible variation which is not ideal.
		for (final Integer idInSetup : idsInSetupNoFuzzy)
		{
			if (!idsInLayout.contains(idInSetup))
			{
				layout.addItem(idInSetup);
			}
		}

		trimLayout(layout);

		layoutManager.saveLayout(layout);
	}

	public void exportSetupToBankTagTab(final InventorySetup setup, final Component component)
	{
		final List<String> data = new ArrayList<>();
		final Layout layout = getSetupLayout(setup);
		data.add("banktags");
		data.add("1");

		// Instead of the super long marked name, just use the standardized name for the setup.
		data.add(Text.standardize(setup.getName()));
		int icon = setup.getIconID();
		if (icon <= 0)
		{
			icon = ItemID.SPADE;
		}
		data.add(String.valueOf(icon));

		for (Integer item : tagManager.getItemsForTag(layout.getTag()))
		{
			if (layout.count(item) == 0)
			{
				data.add(String.valueOf(item));
			}
		}

		data.add("layout");
		int[] l = layout.getLayout();
		for (int idx = 0; idx < l.length; ++idx)
		{
			if (l[idx] != -1)
			{
				data.add(String.valueOf(idx));
				data.add(String.valueOf(l[idx]));
			}
		}

		final StringSelection stringSelection = new StringSelection(Text.toCSV(data));
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

		SwingUtilities.invokeLater(() ->
		{
			JOptionPane.showMessageDialog(component,
					"Bank tag tab data was copied to clipboard.",
					"Export Setup To Bank Tag Tab Succeeded",
					JOptionPane.PLAIN_MESSAGE);
		});
	}

	public Layout convertHubBankTagLayoutToCoreBankTagLayout(final String hubBankTagLayoutData, final String tag)
	{
		try
		{
			final Layout layout = new Layout(tag);
			final String[] pairs = hubBankTagLayoutData.split(",");
			for (final String pair : pairs)
			{
				String[] numbers = pair.split(":");
				int id = Integer.parseInt(numbers[0]);
				int pos = Integer.parseInt(numbers[1]);
				layout.setItemAtPos(id, pos);
			}
			return layout;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private void trimLayout(final Layout layout)
	{
		// Remove all trailing `-1`s in a layout.
		final int[] layoutArr = layout.getLayout();
		if (layoutArr[layoutArr.length - 1] != -1)
		{
			return;
		}

		int indexToTrimTo = layoutArr.length - 2;
		while (indexToTrimTo >= 0 && layoutArr[indexToTrimTo] == -1)
		{
			indexToTrimTo--;
		}
		layout.resize(indexToTrimTo + 1);
	}

}