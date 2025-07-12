package inventorysetups;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.runelite.api.gameval.ItemID.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.runelite.client.game.ItemVariationMapping;

public class InventorySetupsVariationMapping
{
	private static final Map<Integer, Integer> mappings;

	private static final Multimap<Integer, Integer> invertedMappings;


	// Worn items with weight reducing property have a different worn and inventory ItemID
	// Copy of ItemManger::WORN_ITEMS. Use that instead if it becomes a publicly usable member.
	private static final ImmutableMap<Integer, Integer> WORN_ITEMS = ImmutableMap.<Integer, Integer>builder().
			put(IKOV_BOOTSOFLIGHTNESSWORN, IKOV_BOOTSOFLIGHTNESS).
			put(BARBASSAULT_PENANCE_GLOVES_WORN, BARBASSAULT_PENANCE_GLOVES).

			put(GRACEFUL_HOOD_WORN, GRACEFUL_HOOD).
			put(GRACEFUL_CAPE_WORN, GRACEFUL_CAPE).
			put(GRACEFUL_TOP_WORN, GRACEFUL_TOP).
			put(GRACEFUL_LEGS_WORN, GRACEFUL_LEGS).
			put(GRACEFUL_GLOVES_WORN, GRACEFUL_GLOVES).
			put(GRACEFUL_BOOTS_WORN, GRACEFUL_BOOTS).
			put(ZEAH_GRACEFUL_HOOD_ARCEUUS_WORN, ZEAH_GRACEFUL_HOOD_ARCEUUS).
			put(ZEAH_GRACEFUL_CAPE_ARCEUUS_WORN, ZEAH_GRACEFUL_CAPE_ARCEUUS).
			put(ZEAH_GRACEFUL_TOP_ARCEUUS_WORN, ZEAH_GRACEFUL_TOP_ARCEUUS).
			put(ZEAH_GRACEFUL_LEGS_ARCEUUS_WORN, ZEAH_GRACEFUL_LEGS_ARCEUUS).
			put(ZEAH_GRACEFUL_GLOVES_ARCEUUS_WORN, ZEAH_GRACEFUL_GLOVES_ARCEUUS).
			put(ZEAH_GRACEFUL_BOOTS_ARCEUUS_WORN, ZEAH_GRACEFUL_BOOTS_ARCEUUS).
			put(ZEAH_GRACEFUL_HOOD_PISCARILIUS_WORN, ZEAH_GRACEFUL_HOOD_PISCARILIUS).
			put(ZEAH_GRACEFUL_CAPE_PISCARILIUS_WORN, ZEAH_GRACEFUL_CAPE_PISCARILIUS).
			put(ZEAH_GRACEFUL_TOP_PISCARILIUS_WORN, ZEAH_GRACEFUL_TOP_PISCARILIUS).
			put(ZEAH_GRACEFUL_LEGS_PISCARILIUS_WORN, ZEAH_GRACEFUL_LEGS_PISCARILIUS).
			put(ZEAH_GRACEFUL_GLOVES_PISCARILIUS_WORN, ZEAH_GRACEFUL_GLOVES_PISCARILIUS).
			put(ZEAH_GRACEFUL_BOOTS_PISCARILIUS_WORN, ZEAH_GRACEFUL_BOOTS_PISCARILIUS).
			put(ZEAH_GRACEFUL_HOOD_LOVAKENGJ_WORN, ZEAH_GRACEFUL_HOOD_LOVAKENGJ).
			put(ZEAH_GRACEFUL_CAPE_LOVAKENGJ_WORN, ZEAH_GRACEFUL_CAPE_LOVAKENGJ).
			put(ZEAH_GRACEFUL_TOP_LOVAKENGJ_WORN, ZEAH_GRACEFUL_TOP_LOVAKENGJ).
			put(ZEAH_GRACEFUL_LEGS_LOVAKENGJ_WORN, ZEAH_GRACEFUL_LEGS_LOVAKENGJ).
			put(ZEAH_GRACEFUL_GLOVES_LOVAKENGJ_WORN, ZEAH_GRACEFUL_GLOVES_LOVAKENGJ).
			put(ZEAH_GRACEFUL_BOOTS_LOVAKENGJ_WORN, ZEAH_GRACEFUL_BOOTS_LOVAKENGJ).
			put(ZEAH_GRACEFUL_HOOD_SHAYZIEN_WORN, ZEAH_GRACEFUL_HOOD_SHAYZIEN).
			put(ZEAH_GRACEFUL_CAPE_SHAYZIEN_WORN, ZEAH_GRACEFUL_CAPE_SHAYZIEN).
			put(ZEAH_GRACEFUL_TOP_SHAYZIEN_WORN, ZEAH_GRACEFUL_TOP_SHAYZIEN).
			put(ZEAH_GRACEFUL_LEGS_SHAYZIEN_WORN, ZEAH_GRACEFUL_LEGS_SHAYZIEN).
			put(ZEAH_GRACEFUL_GLOVES_SHAYZIEN_WORN, ZEAH_GRACEFUL_GLOVES_SHAYZIEN).
			put(ZEAH_GRACEFUL_BOOTS_SHAYZIEN_WORN, ZEAH_GRACEFUL_BOOTS_SHAYZIEN).
			put(ZEAH_GRACEFUL_HOOD_HOSIDIUS_WORN, ZEAH_GRACEFUL_HOOD_HOSIDIUS).
			put(ZEAH_GRACEFUL_CAPE_HOSIDIUS_WORN, ZEAH_GRACEFUL_CAPE_HOSIDIUS).
			put(ZEAH_GRACEFUL_TOP_HOSIDIUS_WORN, ZEAH_GRACEFUL_TOP_HOSIDIUS).
			put(ZEAH_GRACEFUL_LEGS_HOSIDIUS_WORN, ZEAH_GRACEFUL_LEGS_HOSIDIUS).
			put(ZEAH_GRACEFUL_GLOVES_HOSIDIUS_WORN, ZEAH_GRACEFUL_GLOVES_HOSIDIUS).
			put(ZEAH_GRACEFUL_BOOTS_HOSIDIUS_WORN, ZEAH_GRACEFUL_BOOTS_HOSIDIUS).
			put(ZEAH_GRACEFUL_HOOD_KOUREND_WORN, ZEAH_GRACEFUL_HOOD_KOUREND).
			put(ZEAH_GRACEFUL_CAPE_KOUREND_WORN, ZEAH_GRACEFUL_CAPE_KOUREND).
			put(ZEAH_GRACEFUL_TOP_KOUREND_WORN, ZEAH_GRACEFUL_TOP_KOUREND).
			put(ZEAH_GRACEFUL_LEGS_KOUREND_WORN, ZEAH_GRACEFUL_LEGS_KOUREND).
			put(ZEAH_GRACEFUL_GLOVES_KOUREND_WORN, ZEAH_GRACEFUL_GLOVES_KOUREND).
			put(ZEAH_GRACEFUL_BOOTS_KOUREND_WORN, ZEAH_GRACEFUL_BOOTS_KOUREND).
			put(GRACEFUL_HOOD_SKILLCAPECOLOUR_WORN, GRACEFUL_HOOD_SKILLCAPECOLOUR).
			put(GRACEFUL_CAPE_SKILLCAPECOLOUR_WORN, GRACEFUL_CAPE_SKILLCAPECOLOUR).
			put(GRACEFUL_TOP_SKILLCAPECOLOUR_WORN, GRACEFUL_TOP_SKILLCAPECOLOUR).
			put(GRACEFUL_LEGS_SKILLCAPECOLOUR_WORN, GRACEFUL_LEGS_SKILLCAPECOLOUR).
			put(GRACEFUL_GLOVES_SKILLCAPECOLOUR_WORN, GRACEFUL_GLOVES_SKILLCAPECOLOUR).
			put(GRACEFUL_BOOTS_SKILLCAPECOLOUR_WORN, GRACEFUL_BOOTS_SKILLCAPECOLOUR).
			put(GRACEFUL_HOOD_HALLOWED_WORN, GRACEFUL_HOOD_HALLOWED).
			put(GRACEFUL_CAPE_HALLOWED_WORN, GRACEFUL_CAPE_HALLOWED).
			put(GRACEFUL_TOP_HALLOWED_WORN, GRACEFUL_TOP_HALLOWED).
			put(GRACEFUL_LEGS_HALLOWED_WORN, GRACEFUL_LEGS_HALLOWED).
			put(GRACEFUL_GLOVES_HALLOWED_WORN, GRACEFUL_GLOVES_HALLOWED).
			put(GRACEFUL_BOOTS_HALLOWED_WORN, GRACEFUL_BOOTS_HALLOWED).
			put(GRACEFUL_HOOD_TRAILBLAZER_WORN, GRACEFUL_HOOD_TRAILBLAZER).
			put(GRACEFUL_CAPE_TRAILBLAZER_WORN, GRACEFUL_CAPE_TRAILBLAZER).
			put(GRACEFUL_TOP_TRAILBLAZER_WORN, GRACEFUL_TOP_TRAILBLAZER).
			put(GRACEFUL_LEGS_TRAILBLAZER_WORN, GRACEFUL_LEGS_TRAILBLAZER).
			put(GRACEFUL_GLOVES_TRAILBLAZER_WORN, GRACEFUL_GLOVES_TRAILBLAZER).
			put(GRACEFUL_BOOTS_TRAILBLAZER_WORN, GRACEFUL_BOOTS_TRAILBLAZER).
			put(GRACEFUL_HOOD_ADVENTURER_WORN, GRACEFUL_HOOD_ADVENTURER).
			put(GRACEFUL_CAPE_ADVENTURER_WORN, GRACEFUL_CAPE_ADVENTURER).
			put(GRACEFUL_TOP_ADVENTURER_WORN, GRACEFUL_TOP_ADVENTURER).
			put(GRACEFUL_LEGS_ADVENTURER_WORN, GRACEFUL_LEGS_ADVENTURER).
			put(GRACEFUL_GLOVES_ADVENTURER_WORN, GRACEFUL_GLOVES_ADVENTURER).
			put(GRACEFUL_BOOTS_ADVENTURER_WORN, GRACEFUL_BOOTS_ADVENTURER).
			put(GRACEFUL_HOOD_WYRM_WORN, GRACEFUL_HOOD_WYRM).
			put(GRACEFUL_CAPE_WYRM_WORN, GRACEFUL_CAPE_WYRM).
			put(GRACEFUL_TOP_WYRM_WORN, GRACEFUL_TOP_WYRM).
			put(GRACEFUL_LEGS_WYRM_WORN, GRACEFUL_LEGS_WYRM).
			put(GRACEFUL_GLOVES_WYRM_WORN, GRACEFUL_GLOVES_WYRM).
			put(GRACEFUL_BOOTS_WYRM_WORN, GRACEFUL_BOOTS_WYRM).

			put(SKILLCAPE_MAX_WORN, SKILLCAPE_MAX).

			put(HUNTING_LIGHT_CAPE_WORN, HUNTING_LIGHT_CAPE).
			put(HUNTING_LIGHTER_CAPE_WORN, HUNTING_LIGHTER_CAPE).

			put(SKILLCAPE_AGILITY_TRIMMED_WORN, SKILLCAPE_AGILITY_TRIMMED).
			put(SKILLCAPE_AGILITY_WORN, SKILLCAPE_AGILITY).

			put(HUNTING_CAMOFLAUGE_ROBE_WOOD_WORN, HUNTING_CAMOFLAUGE_ROBE_WOOD).
			put(HUNTING_TROUSERS_WOOD_WORN, HUNTING_TROUSERS_WOOD).
			put(HUNTING_CAMOFLAUGE_ROBE_JUNGLE_WORN, HUNTING_CAMOFLAUGE_ROBE_JUNGLE).
			put(HUNTING_TROUSERS_JUNGLE_WORN, HUNTING_TROUSERS_JUNGLE).
			put(HUNTING_CAMOFLAUGE_ROBE_DESERT_WORN, HUNTING_CAMOFLAUGE_ROBE_DESERT).
			put(HUNTING_TROUSERS_DESERT_WORN, HUNTING_TROUSERS_DESERT).
			put(HUNTING_CAMOFLAUGE_ROBE_POLAR_WORN, HUNTING_CAMOFLAUGE_ROBE_POLAR).
			put(HUNTING_TROUSERS_POLAR_WORN, HUNTING_TROUSERS_POLAR).
			build();

	public static final Map<Integer, Integer> INVERTED_WORN_ITEMS;

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

	public static Collection<Integer> getVariations(int itemId)
	{
		Collection<Integer> baseMappings = ItemVariationMapping.getVariations(itemId);
		Collection<Integer> customMappings = invertedMappings.asMap().getOrDefault(itemId, Collections.singletonList(itemId));
		Collection<Integer> allMappings = new LinkedHashSet<>(baseMappings);
		allMappings.addAll(customMappings);
		return allMappings;
	}

	static
	{
		mappings = new HashMap<>();

		// Granite Cannonball -> Cannonball
		mappings.put(GRANITE_CANNONBALL, MCANNONBALL);

		// Smith Gloves (i) act as ice gloves
		mappings.put(SMITHING_UNIFORM_GLOVES_ICE, ICE_GLOVES);

		// Divine rune pouch -> Rune Pouch
		mappings.put(DIVINE_RUNE_POUCH, BH_RUNE_POUCH);

		// Make god capes the same
		final int itemIDGodCape = 1000000001;
		mappings.put(SARADOMIN_CAPE, itemIDGodCape);
		mappings.put(GUTHIX_CAPE, itemIDGodCape);
		mappings.put(ZAMORAK_CAPE, itemIDGodCape);
		final int itemIDImbuedGodCape = 1000000002;
		mappings.put(MA2_SARADOMIN_CAPE, itemIDImbuedGodCape);
		mappings.put(MA2_GUTHIX_CAPE, itemIDImbuedGodCape);
		mappings.put(MA2_ZAMORAK_CAPE, itemIDImbuedGodCape);
		mappings.put(MA2_SARADOMIN_CAPE_TROUVER, itemIDImbuedGodCape);
		mappings.put(MA2_GUTHIX_CAPE_TROUVER, itemIDImbuedGodCape);
		mappings.put(MA2_ZAMORAK_CAPE_TROUVER, itemIDImbuedGodCape);
		final int itemIDGodMaxCape = 1000000003;
		mappings.put(SKILLCAPE_MAX_SARADOMIN, itemIDGodMaxCape);
		mappings.put(SKILLCAPE_MAX_GUTHIX, itemIDGodMaxCape);
		mappings.put(SKILLCAPE_MAX_ZAMORAK, itemIDGodMaxCape);
		final int itemIDImbuedGodMaxCape = 1000000004;
		mappings.put(SKILLCAPE_MAX_SARADOMIN2, itemIDImbuedGodMaxCape);
		mappings.put(SKILLCAPE_MAX_GUTHIX2, itemIDImbuedGodMaxCape);
		mappings.put(SKILLCAPE_MAX_ZAMORAK2, itemIDImbuedGodMaxCape);
		mappings.put(SKILLCAPE_MAX_SARADOMIN2_TROUVER, itemIDImbuedGodMaxCape);
		mappings.put(SKILLCAPE_MAX_GUTHIX2_TROUVER, itemIDImbuedGodMaxCape);
		mappings.put(SKILLCAPE_MAX_ZAMORAK2_TROUVER, itemIDImbuedGodMaxCape);

		// Make god d'hides the same
		final int itemIDGodCoif = 1000000005;
		mappings.put(TRAIL_ANCIENT_COIF, itemIDGodCoif);
		mappings.put(TRAIL_ARMADYL_COIF, itemIDGodCoif);
		mappings.put(TRAIL_BANDOS_COIF, itemIDGodCoif);
		mappings.put(TRAIL_GUTHIX_COIF, itemIDGodCoif);
		mappings.put(TRAIL_SARADOMIN_COIF, itemIDGodCoif);
		mappings.put(TRAIL_ZAMORAK_COIF, itemIDGodCoif);

		final int itemIDGodDhideBody = 1000000006;
		mappings.put(TRAIL_ANCIENT_CHEST, itemIDGodDhideBody);
		mappings.put(TRAIL_ARMADYL_CHEST , itemIDGodDhideBody);
		mappings.put(TRAIL_BANDOS_CHEST, itemIDGodDhideBody);
		mappings.put(TRAIL_GUTHIX_CHEST, itemIDGodDhideBody);
		mappings.put(TRAIL_SARADOMIN_CHEST, itemIDGodDhideBody);
		mappings.put(TRAIL_ZAMORAK_CHEST, itemIDGodDhideBody);

		final int itemIDGodChaps = 1000000007;
		mappings.put(TRAIL_ANCIENT_CHAPS, itemIDGodChaps);
		mappings.put(TRAIL_ARMADYL_CHAPS, itemIDGodChaps);
		mappings.put(TRAIL_BANDOS_CHAPS, itemIDGodChaps);
		mappings.put(TRAIL_GUTHIX_CHAPS, itemIDGodChaps);
		mappings.put(TRAIL_SARADOMIN_CHAPS, itemIDGodChaps);
		mappings.put(TRAIL_ZAMORAK_CHAPS, itemIDGodChaps);

		final int itemIDGodBracers = 1000000008;
		mappings.put(TRAIL_ANCIENT_VAMBRACES, itemIDGodBracers);
		mappings.put(TRAIL_ARMADYL_VAMBRACES, itemIDGodBracers);
		mappings.put(TRAIL_BANDOS_VAMBRACES, itemIDGodBracers);
		mappings.put(TRAIL_GUTHIX_VAMBRACES, itemIDGodBracers);
		mappings.put(TRAIL_SARADOMIN_VAMBRACES, itemIDGodBracers);
		mappings.put(TRAIL_ZAMORAK_VAMBRACES, itemIDGodBracers);

		final int itemIDGodDhideBoots = 1000000009;
		mappings.put(BLESSED_BOOTS_ANCIENT, itemIDGodDhideBoots);
		mappings.put(BLESSED_BOOTS_ARMADYL, itemIDGodDhideBoots);
		mappings.put(BLESSED_BOOTS_BANDOS, itemIDGodDhideBoots);
		mappings.put(BLESSED_BOOTS_GUTHIX, itemIDGodDhideBoots);
		mappings.put(BLESSED_BOOTS_SARADOMIN, itemIDGodDhideBoots);
		mappings.put(BLESSED_BOOTS_ZAMORAK, itemIDGodDhideBoots);

		final int itemIDGodDhideShield = 1000000010;
		mappings.put(BLESSED_DHIDE_SHIELD_ANCIENT, itemIDGodDhideShield);
		mappings.put(BLESSED_DHIDE_SHIELD_ARMADYL, itemIDGodDhideShield);
		mappings.put(BLESSED_DHIDE_SHIELD_BANDOS, itemIDGodDhideShield);
		mappings.put(BLESSED_DHIDE_SHIELD_GUTHIX, itemIDGodDhideShield);
		mappings.put(BLESSED_DHIDE_SHIELD_SARADOMIN, itemIDGodDhideShield);
		mappings.put(BLESSED_DHIDE_SHIELD_ZAMORAK, itemIDGodDhideShield);

		// Twisted Ancestral -> Regular Ancestral
		mappings.put(ANCESTRAL_HAT_TWISTED, ANCESTRAL_HAT);
		mappings.put(ANCESTRAL_ROBE_BOTTOM_TWISTED, ANCESTRAL_ROBE_BOTTOM);
		mappings.put(ANCESTRAL_ROBE_TOP_TWISTED, ANCESTRAL_ROBE_TOP);

		// Golden Prospectors -> Regular Prospectors
		mappings.put(MOTHERLODE_REWARD_BOOTS_GOLD, MOTHERLODE_REWARD_BOOTS);
		mappings.put(MOTHERLODE_REWARD_HAT_GOLD, MOTHERLODE_REWARD_HAT);
		mappings.put(MOTHERLODE_REWARD_TOP_GOLD, MOTHERLODE_REWARD_TOP);
		mappings.put(MOTHERLODE_REWARD_LEGS_GOLD, MOTHERLODE_REWARD_LEGS);

		// Spirit Anglers -> Regular Anglers
		mappings.put(SPIRIT_ANGLER_BOOTS, TRAWLER_REWARD_BOOTS);
		mappings.put(SPIRIT_ANGLER_HAT, TRAWLER_REWARD_HAT);
		mappings.put(SPIRIT_ANGLER_TOP, TRAWLER_REWARD_TOP);
		mappings.put(SPIRIT_ANGLER_LEGS, TRAWLER_REWARD_LEGS);
		
		// ToB ornament kits -> base version
		mappings.put(SCYTHE_OF_VITUR_BL, SCYTHE_OF_VITUR);
		mappings.put(SCYTHE_OF_VITUR_OR, SCYTHE_OF_VITUR);
		mappings.put(SANGUINESTI_STAFF_OR, SANGUINESTI_STAFF);
		mappings.put(GHRAZI_RAPIER_OR, GHRAZI_RAPIER);

		mappings.put(INFERNAL_DEFENDER_GHOMMAL_5, INFERNAL_DEFENDER);
		mappings.put(INFERNAL_DEFENDER_GHOMMAL_5_TROUVER, INFERNAL_DEFENDER);
		mappings.put(INFERNAL_DEFENDER_GHOMMAL_6 , INFERNAL_DEFENDER);
		mappings.put(INFERNAL_DEFENDER_GHOMMAL_6_TROUVER, INFERNAL_DEFENDER);

		// Blazing blowpipe -> toxic blowpipe
		mappings.put(TOXIC_BLOWPIPE_ORNAMENT, TOXIC_BLOWPIPE);
		mappings.put(TOXIC_BLOWPIPE_LOADED_ORNAMENT, TOXIC_BLOWPIPE_LOADED);

		// Locked fire/infernal cape -> regular capes
		mappings.put(TZHAAR_CAPE_FIRE_TROUVER, TZHAAR_CAPE_FIRE);
		mappings.put(INFERNAL_CAPE_TROUVER, INFERNAL_CAPE);

		// Blood Torva -> Regular Torva
		mappings.put(TORVA_HELM_SANGUINE, TORVA_HELM);
		mappings.put(TORVA_CHEST_SANGUINE, TORVA_CHEST);
		mappings.put(TORVA_LEGS_SANGUINE, TORVA_LEGS);

		ImmutableMultimap.Builder<Integer, Integer> invertedBuilder = new ImmutableMultimap.Builder<>();
		Set<Integer> addedValues = new HashSet<>();
		for (Integer key : mappings.keySet())
		{
			Integer value = mappings.get(key);
			invertedBuilder.put(value, key);
			if (addedValues.add(value))
			{
				invertedBuilder.put(value, value);
			}
		}
		invertedMappings = invertedBuilder.build();

		// INVERTED_WORN_ITEMS mapping
		INVERTED_WORN_ITEMS = WORN_ITEMS.entrySet()
										.stream()
										.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

	}



}
