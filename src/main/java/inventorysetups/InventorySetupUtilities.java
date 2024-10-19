package inventorysetups;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.banktags.TagManager;
import net.runelite.client.plugins.banktags.tabs.Layout;
import net.runelite.client.util.SwingUtil;

import static inventorysetups.InventorySetupsPlugin.MAX_SETUP_NAME_LENGTH;
import static net.runelite.api.ItemID.BLIGHTED_SUPER_RESTORE1;
import static net.runelite.api.ItemID.IMBUED_GUTHIX_MAX_CAPE_L;

public class InventorySetupUtilities
{
	private InventorySetupUtilities()
	{

	}

	static public int parseTextInputAmount(String input)
	{
		// only take the first 10 characters (max amount is 2.147B which is only 10 digits)
		if (input.length() > 10)
		{
			return Integer.MAX_VALUE;
		}
		input = input.toLowerCase();

		char finalChar = input.charAt(input.length() - 1);
		int factor = 1;
		if (Character.isLetter(finalChar))
		{
			input = input.substring(0, input.length() - 1);
			switch (finalChar)
			{
				case 'k':
					factor = 1000;
					break;
				case 'm':
					factor = 1000000;
					break;
				case 'b':
					factor = 1000000000;
					break;
			}
		}

		// limit to max int value
		long quantityLong = Long.parseLong(input) * factor;
		int quantity = (int) Math.min(quantityLong, Integer.MAX_VALUE);
		quantity = Math.max(quantity, 1);

		return quantity;
	}

	static public String findNewName(String originalName, final Set<String> objects)
	{
		// Do not allow names of more than MAX_SETUP_NAME_LENGTH chars
		if (originalName.length() > MAX_SETUP_NAME_LENGTH)
		{
			originalName = originalName.substring(0, MAX_SETUP_NAME_LENGTH);
		}

		// Fix duplicate name by adding an incrementing number to the duplicate
		String newName = originalName;
		int i = 1;
		while (objects.contains(newName) || newName.isEmpty())
		{
			String i_str = String.valueOf(i);
			if (originalName.length() + i_str.length() > MAX_SETUP_NAME_LENGTH)
			{
				int chars_to_cut_off = i_str.length() - (MAX_SETUP_NAME_LENGTH - originalName.length());
				newName = originalName.substring(0, MAX_SETUP_NAME_LENGTH - chars_to_cut_off) + i++;
			}
			else
			{
				newName = originalName + i++;
			}
		}
		return newName;
	}

	public static void fastRemoveAll(Container c)
	{
		fastRemoveAll(c, true);
	}

	private static void fastRemoveAll(Container c, boolean isMainParent)
	{
		// If we are not on the EDT this will deadlock, in addition to being totally unsafe
		assert SwingUtilities.isEventDispatchThread();

		// when a component is removed it has to be resized for some reason, but only if it's valid
		// so we make sure to invalidate everything before removing it
		c.invalidate();
		for (int i = 0; i < c.getComponentCount(); i++)
		{
			Component ic = c.getComponent(i);

			// removeAll and removeNotify are both recursive, so we have to recurse before them
			if (ic instanceof Container)
			{
				fastRemoveAll((Container) ic, false);
			}

			// each removeNotify needs to remove anything from the event queue that is for that widget
			// this however requires taking a lock, and is moderately slow, so we just execute all of
			// those events with a secondary event loop
			SwingUtil.pumpPendingEvents();

			// call removeNotify early; this is most of the work in removeAll, and generates events that
			// the next secondaryLoop will pickup
			ic.removeNotify();
		}

		if (isMainParent)
		{
			// Actually remove anything
			c.removeAll();
		}
	}

	public static Layout getZigZagLayout(final InventorySetup setup, final ItemManager itemManager, final TagManager tagManager)
	{
		final String tag = InventorySetupsPersistentDataManager.getTagNameForLayout(setup.getName());
		final Layout layout = new Layout(tag);

		return layout;
	}

	public static Layout getSetupLayout(String type, final InventorySetup setup, final ItemManager itemManager, final TagManager tagManager)
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

	public static Layout getPresetLayout(final InventorySetup setup, final ItemManager itemManager, final TagManager tagManager)
	{
		// This generates a "Preset" layout based on an InventorySetup
		// It will have the equipment on the left and inventory on the right
		// This must be called on the clientThread

		// Note: Core Layout API does not support custom variation mappings, so even if fuzzy is marked,
		// custom mappings will not work in the layout.

		final String tag = InventorySetupsPersistentDataManager.getTagNameForLayout(setup.getName());
		final Layout layout = new Layout(tag);

		// Determine size to help reduce amount of copy overs.
		// Since the inventory will be a 4x7 on the right side of the bank, it will end at position 55.
		int startOfAdditionalItems = 56;
		int startOfBoltPouch = 48;
		int startOfRunePouch = 40;

		int newSizeGuess = setup.getAdditionalFilteredItems().size() + startOfAdditionalItems;
		layout.resize(newSizeGuess);

		// Layout the equipment on left side
		// TODO: Add items to tag with fuzzy.
		final List<InventorySetupsItem> eqp = setup.getEquipment();
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.HEAD.getSlotIdx()).getId(), 1);
		if (setup.getQuiver() != null && !setup.getQuiver().isEmpty())
		{
			layout.setItemAtPos(setup.getQuiver().get(0).getId(), 2);
		}
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.CAPE.getSlotIdx()).getId(), 8);
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.AMULET.getSlotIdx()).getId(), 9);
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.WEAPON.getSlotIdx()).getId(), 16);
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.BODY.getSlotIdx()).getId(), 17);
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.SHIELD.getSlotIdx()).getId(), 18);
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.AMMO.getSlotIdx()).getId(), 10);
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.LEGS.getSlotIdx()).getId(), 25);
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.GLOVES.getSlotIdx()).getId(), 32);
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.BOOTS.getSlotIdx()).getId(), 33);
		layout.setItemAtPos(eqp.get(EquipmentInventorySlot.RING.getSlotIdx()).getId(), 34);

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
}