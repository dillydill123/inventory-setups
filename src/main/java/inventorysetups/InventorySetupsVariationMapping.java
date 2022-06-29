package inventorysetups;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemVariationMapping;

public class InventorySetupsVariationMapping
{
	private static final Map<Integer, Integer> mappings;

	public InventorySetupsVariationMapping()
	{
	}

	public static int map(final Integer id)
	{
		int mappedId = ItemVariationMapping.map(id);

		// if the mapped ID is equal to the original id
		// this means there was no mapping for this id. Try the extra custom mappings
		if (mappedId == id)
		{
			mappedId = mappings.getOrDefault(id, id);
		}

		return mappedId;
	}

	static
	{
		mappings = new HashMap<>();

		// Granite Cannonball -> Cannonball
		mappings.put(ItemID.GRANITE_CANNONBALL, ItemID.CANNON_BALL);

		// Make god capes the same
		final int itemIDGodCape = 1000000001;
		mappings.put(ItemID.SARADOMIN_CAPE, itemIDGodCape);
		mappings.put(ItemID.GUTHIX_CAPE, itemIDGodCape);
		mappings.put(ItemID.ZAMORAK_CAPE, itemIDGodCape);
		final int itemIDImbuedGodCape = 1000000002;
		mappings.put(ItemID.IMBUED_SARADOMIN_CAPE, itemIDImbuedGodCape);
		mappings.put(ItemID.IMBUED_GUTHIX_CAPE, itemIDImbuedGodCape);
		mappings.put(ItemID.IMBUED_ZAMORAK_CAPE, itemIDImbuedGodCape);
		final int itemIDGodMaxCape = 1000000003;
		mappings.put(ItemID.SARADOMIN_MAX_CAPE, itemIDGodMaxCape);
		mappings.put(ItemID.GUTHIX_MAX_CAPE, itemIDGodMaxCape);
		mappings.put(ItemID.ZAMORAK_MAX_CAPE, itemIDGodMaxCape);
		final int itemIDImbuedGodMaxCape = 1000000004;
		mappings.put(ItemID.IMBUED_SARADOMIN_MAX_CAPE, itemIDImbuedGodMaxCape);
		mappings.put(ItemID.IMBUED_GUTHIX_MAX_CAPE, itemIDImbuedGodMaxCape);
		mappings.put(ItemID.IMBUED_ZAMORAK_MAX_CAPE, itemIDImbuedGodMaxCape);

		// Twisted Ancestral -> Regular Ancestral
		mappings.put(ItemID.TWISTED_ANCESTRAL_HAT, ItemID.ANCESTRAL_HAT);
		mappings.put(ItemID.TWISTED_ANCESTRAL_ROBE_BOTTOM, ItemID.ANCESTRAL_ROBE_BOTTOM);
		mappings.put(ItemID.TWISTED_ANCESTRAL_ROBE_TOP, ItemID.ANCESTRAL_ROBE_TOP);

		// Golden Prospectors -> Regular Prospectors
		mappings.put(ItemID.GOLDEN_PROSPECTOR_BOOTS, ItemID.PROSPECTOR_BOOTS);
		mappings.put(ItemID.GOLDEN_PROSPECTOR_HELMET, ItemID.PROSPECTOR_HELMET);
		mappings.put(ItemID.GOLDEN_PROSPECTOR_JACKET, ItemID.PROSPECTOR_JACKET);
		mappings.put(ItemID.GOLDEN_PROSPECTOR_LEGS, ItemID.PROSPECTOR_LEGS);

		// Spirit Anglers -> Regular Anglers
		mappings.put(ItemID.SPIRIT_ANGLER_BOOTS, ItemID.ANGLER_BOOTS);
		mappings.put(ItemID.SPIRIT_ANGLER_HEADBAND, ItemID.ANGLER_HAT);
		mappings.put(ItemID.SPIRIT_ANGLER_TOP, ItemID.ANGLER_TOP);
		mappings.put(ItemID.SPIRIT_ANGLER_WADERS, ItemID.ANGLER_WADERS);
		
		// ToB ornament kits -> base version
		mappings.put(ItemID.SANGUINE_SCYTHE_OF_VITUR, ItemID.SCYTHE_OF_VITUR);
		mappings.put(ItemID.HOLY_SCYTHE_OF_VITUR, ItemID.SCYTHE_OF_VITUR);
		mappings.put(ItemID.HOLY_SANGUINESTI_STAFF, ItemID.SANGUINESTI_STAFF);
		mappings.put(ItemID.HOLY_GHRAZI_RAPIER, ItemID.GHRAZI_RAPIER);
	}

}
