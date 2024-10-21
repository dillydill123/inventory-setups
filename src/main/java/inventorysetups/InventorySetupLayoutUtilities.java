package inventorysetups;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.banktags.TagManager;
import net.runelite.client.plugins.banktags.tabs.Layout;
import net.runelite.client.plugins.banktags.tabs.LayoutManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static inventorysetups.InventorySetupsPlugin.LAYOUT_PREFIX_MARKER;

public class InventorySetupLayoutUtilities
{
	private InventorySetupLayoutUtilities()
	{

	}

	public static String getTagNameForLayout(final String inventorySetupName)
	{
		String hashOfName = InventorySetupsPersistentDataManager.hashFunction.hashUnencodedChars(inventorySetupName).toString();
		return LAYOUT_PREFIX_MARKER + hashOfName;
	}

	public static Layout getLayoutForSetup(final String inventorySetupName, final LayoutManager layoutManager)
	{
		final String tagName = getTagNameForLayout(inventorySetupName);
		final Layout layout = layoutManager.loadLayout(tagName);
		assert layout != null : "Layout for " + inventorySetupName + " is null.";
		return layout;
	}

	public static Layout createSetupLayout(String type, final InventorySetup setup, final ItemManager itemManager, final TagManager tagManager)
	{
		// TODO: Use actual config option
		if (type.equals("preset"))
		{
			return getPresetLayout(setup, itemManager, tagManager);
		}
		else
		{
			return getZigZagLayout(setup, itemManager, tagManager);
		}
	}


	public static Layout getZigZagLayout(final InventorySetup setup, final ItemManager itemManager, final TagManager tagManager)
	{
		// TODO: Add this
		final String tag = InventorySetupLayoutUtilities.getTagNameForLayout(setup.getName());
		final Layout layout = new Layout(tag);

		return layout;
	}

	public static Layout getPresetLayout(final InventorySetup setup, final ItemManager itemManager, final TagManager tagManager)
	{
		// This generates a "Preset" layout based on an InventorySetup
		// It will have the equipment on the left and inventory on the right
		// This must be called on the clientThread

		// Note: Core Layout API does not support custom variation mappings, so even if fuzzy is marked,
		// custom mappings will not work in the layout.

		final String tag = InventorySetupLayoutUtilities.getTagNameForLayout(setup.getName());
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
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.HEAD.getSlotIdx()), 1, itemManager, tagManager);
		if (setup.getQuiver() != null && !setup.getQuiver().isEmpty())
		{
			addEquipmentSlotToLayout(layout, tag, setup.getQuiver().get(0), 2, itemManager, tagManager);
		}
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.CAPE.getSlotIdx()), 8, itemManager, tagManager);
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.AMULET.getSlotIdx()), 9, itemManager, tagManager);
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.AMMO.getSlotIdx()), 10, itemManager, tagManager);
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.WEAPON.getSlotIdx()), 16, itemManager, tagManager);
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.BODY.getSlotIdx()), 17, itemManager, tagManager);
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.SHIELD.getSlotIdx()), 18, itemManager, tagManager);
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.LEGS.getSlotIdx()), 25, itemManager, tagManager);
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.GLOVES.getSlotIdx()), 32, itemManager, tagManager);
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.BOOTS.getSlotIdx()), 33, itemManager, tagManager);
		addEquipmentSlotToLayout(layout, tag, eqp.get(EquipmentInventorySlot.RING.getSlotIdx()), 34, itemManager, tagManager);

		// Layout the inventory on the right side
		int invRow = 0;
		int invCol = 4;
		int width = 8;
		for (final InventorySetupsItem item: setup.getInventory())
		{
			int id = itemManager.canonicalize(item.getId());
			layout.setItemAtPos(id, invCol + (invRow * width));
			tagManager.addTag(id, tag, item.isFuzzy());
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
				final InventorySetupsItem item = setup.getRune_pouch().get(i);
				int id = itemManager.canonicalize(item.getId());
				tagManager.addTag(id, tag, item.isFuzzy());
				layout.setItemAtPos(id, i + startOfRunePouch);
			}
		}

		// Bolt pouch below the rune pouch
		if (setup.getBoltPouch() != null)
		{
			for (int i = 0; i < setup.getBoltPouch().size(); i++)
			{
				final InventorySetupsItem item = setup.getBoltPouch().get(i);
				int id = itemManager.canonicalize(item.getId());
				tagManager.addTag(id, tag, item.isFuzzy());
				layout.setItemAtPos(id, i + startOfBoltPouch);
			}
		}

		// Additional items
		int c = 0;
		Collection<InventorySetupsItem> additionalItems = setup.getAdditionalFilteredItems().values();
		for (final InventorySetupsItem item : additionalItems)
		{
			int id = itemManager.canonicalize(item.getId());
			tagManager.addTag(id, tag, item.isFuzzy());
			layout.setItemAtPos(id, startOfAdditionalItems + c);
			c++;
		}

		return layout;
	}

	private static void addEquipmentSlotToLayout(final Layout layout, final String tagName, final InventorySetupsItem item, final int pos, final ItemManager itemManager, final TagManager tagManager)
	{
		int id = itemManager.canonicalize(item.getId());
		layout.setItemAtPos(id, pos);
		tagManager.addTag(id, tagName, item.isFuzzy());
	}

	public static HashMap<Integer, Integer> getSetupItemCounts(final InventorySetup setup, final ItemManager itemManager)
	{
		HashMap<Integer, Integer> itemsInSetup = new HashMap<>();
		setup.getInventory().forEach(item ->
				itemsInSetup.put(itemManager.canonicalize(item.getId()), itemsInSetup.getOrDefault(itemManager.canonicalize(item.getId()), 0) + 1)
		);
		setup.getEquipment().forEach(item ->
				itemsInSetup.put(itemManager.canonicalize(item.getId()), itemsInSetup.getOrDefault(itemManager.canonicalize(item.getId()), 0) + 1)
		);
		setup.getAdditionalFilteredItems().values().forEach(item ->
				itemsInSetup.put(itemManager.canonicalize(item.getId()), itemsInSetup.getOrDefault(itemManager.canonicalize(item.getId()), 0) + 1)
		);
		if (setup.getRune_pouch() != null)
		{
			setup.getRune_pouch().forEach(item ->
					itemsInSetup.put(itemManager.canonicalize(item.getId()), itemsInSetup.getOrDefault(itemManager.canonicalize(item.getId()), 0) + 1)
			);
		}
		if (setup.getBoltPouch() != null)
		{
			setup.getBoltPouch().forEach(item ->
					itemsInSetup.put(itemManager.canonicalize(item.getId()), itemsInSetup.getOrDefault(itemManager.canonicalize(item.getId()), 0) + 1)
			);
		}
		if (setup.getQuiver() != null)
		{
			setup.getQuiver().forEach(item ->
					itemsInSetup.put(itemManager.canonicalize(item.getId()), itemsInSetup.getOrDefault(itemManager.canonicalize(item.getId()), 0) + 1)
			);
		}
		// Remove empty if it's there.
		itemsInSetup.remove(-1);
		return itemsInSetup;
	}

	public static void recalculateLayout(final InventorySetup setup, final ItemManager itemManager, TagManager tagManager, final LayoutManager layoutManager)
	{
		final List<InventorySetupsItem> itemsInSetup = InventorySetup.getSetupItems(setup);
		final String tagName = getTagNameForLayout(setup.getName());
		final Layout layout = layoutManager.loadLayout(tagName);
		assert layout != null : "Setup " + setup.getName() + " has no layout.";

		tagManager.removeTag(tagName);
		HashSet<Integer> idsInSetup = new HashSet<>();
		for (InventorySetupsItem item : itemsInSetup)
		{
			int processedId = itemManager.canonicalize(item.getId());
			tagManager.addTag(processedId, tagName, item.isFuzzy());
			idsInSetup.add(processedId);
			if (item.isFuzzy())
			{
				Collection<Integer> variations = ItemVariationMapping.getVariations(ItemVariationMapping.map(processedId));
				idsInSetup.addAll(variations);
			}
		}

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
			}
		}

		layoutManager.saveLayout(layout);
	}

	public static void removeItemsFromLayoutNotInSetup(final InventorySetup setup, final List<InventorySetupsItem> itemsToRemove, final ItemManager itemManager, final TagManager tagManager, final LayoutManager layoutManager)
	{
		HashMap<Integer, Integer> itemsInSetup = getSetupItemCounts(setup, itemManager);
		HashSet<Integer> itemsToRemoveSet = new HashSet<>();

		itemsToRemove.forEach(item -> itemsToRemoveSet.add(item.getId()));

		final String tagName = getTagNameForLayout(setup.getName());
		final Layout layout = getLayoutForSetup(setup.getName(), layoutManager);
		for (final InventorySetupsItem item : itemsToRemove)
		{
			int itemIdToRemove = item.getId();
			if (itemsInSetup.getOrDefault(itemIdToRemove, 0) != 0)
			{
				continue;
			}
			for (int i = 0; i < layout.size(); i++)
			{
				int layoutId = layout.getItemAtPos(i);
				if (itemsToRemoveSet.contains(layoutId))
				{
					layout.removeItemAtPos(i);
					tagManager.removeTag(layoutId, tagName);
				}
			}
		}
	}



	public static void removeItemFromLayout(final InventorySetup setup, final InventorySetupsItem item, final Layout layout, final ItemManager itemManager, final TagManager tagManager, final LayoutManager layoutManager)
	{
		if (item.getId() == -1)
		{
			return;
		}

		int processedId = itemManager.canonicalize(item.getId());
		final String tagName = InventorySetupLayoutUtilities.getTagNameForLayout(setup.getName());
		final HashMap<Integer, Integer> itemsInSetup = getSetupItemCounts(setup, itemManager);
		boolean removeAll = false;

		if (itemsInSetup.getOrDefault(processedId, 0) < 1)
		{
			tagManager.removeTag(processedId, tagName);
			removeAll = true;  // Remove all instances if the setup does not contain the item.
		}

		// Because we aren't checking if there is the same item that is fuzzy elsewhere in the setup.
		// This will cause the variation mapped items to be moved to the first positions
		// This is probably fine, since it's not common someone would have the same item variation mapped
		// multiple times.
		if (item.isFuzzy())
		{
			InventorySetupLayoutUtilities.removeVariationMappedItemsFromLayout(setup, processedId, layout, itemManager, tagManager, layoutManager);
		}

		for (int i = 0; i < layout.size(); i++)
		{
			if (layout.getItemAtPos(i) == processedId)
			{
				layout.removeItemAtPos(i);
				if (!removeAll)
				{
					break;
				}
			}
		}
	}

	public static void removeVariationMappedItemsFromLayout(final InventorySetup setup, final int itemId,  final Layout layout, ItemManager itemManager, TagManager tagManager, final LayoutManager layoutManager)
	{
		final String tagName = getTagNameForLayout(setup.getName());

		int processedId = itemManager.canonicalize(itemId);
		int baseId = ItemVariationMapping.map(processedId);
		HashSet<Integer> variations = new HashSet<>(ItemVariationMapping.getVariations(baseId));

		for (int i = 0; i < layout.size(); i++)
		{
			int layoutId = layout.getItemAtPos(i);
			// We need to check the tag to ensure that we do not delete the item that is being marked fuzzy
			// We also need to check that it is in the list of variations, so we do not delete
			// other items that are variation mapped.

			// Don't worry about the case that the same item might be fuzzy in another part of the setup, and that we should keep it in the layout. This is highly unlikely.
			if (layoutId != -1 && variations.contains(layoutId) && !tagManager.findTag(layoutId, tagName))
			{
				layout.removeItemAtPos(i);
			}
		}
	}
}