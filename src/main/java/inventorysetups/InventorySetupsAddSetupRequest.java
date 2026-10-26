package inventorysetups;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// An add-setup PluginMessage payload, typed but not yet checked (InventorySetupsPlugin checks its rules).
// Null means the key was left out.
@Getter
@Builder
public class InventorySetupsAddSetupRequest
{
	private final String name;

	// EquipmentInventorySlot order, -1 for an empty slot
	private final List<Integer> equipmentIds;
	private final List<Integer> equipmentQuantities;

	// Inventory slot order, -1 for an empty slot
	private final List<Integer> inventoryIds;
	private final List<Integer> inventoryQuantities;

	// Additional filtered items, no positions
	private final List<Integer> additionalIds;
	private final List<Integer> additionalQuantities;

	// Special containers, used only when the setup holds the container
	private final List<Integer> runePouchIds;
	private final List<Integer> runePouchQuantities;
	private final List<Integer> boltPouchIds;
	private final List<Integer> boltPouchQuantities;
	private final Integer quiverItemId;
	private final Integer quiverQuantity;

	private final String notes;
	private final String section;
	private final String attackOption;

	// 0 = Standard, 1 = Ancient, 2 = Lunar, 3 = Arceuus, 4 = NONE (see InventorySetup)
	private final Integer spellbook;
	private final Integer iconId;
	// RGB
	private final Integer highlightColor;
	private final Integer displayColor;

	private final Boolean favorite;
	private final Boolean filterBank;
	private final Boolean highlightDifference;
	private final Boolean unorderedHighlight;
	private final Boolean fuzzy;
}
