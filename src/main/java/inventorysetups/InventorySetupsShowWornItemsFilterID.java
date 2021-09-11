package inventorysetups;

import java.util.ArrayList;
import java.util.Collections;

/*
* Enum that determines which setups show up when the "show worn items" button is right clicked
*/
public enum InventorySetupsShowWornItemsFilterID
{
	// Show all. This is the default
	All(0),

	// Show only the setups that are bank filtered
	BANK_FILTERED(1),

	// Show only the setups that favorited
	FAVORITED(2);

	private final int type;

	private static final ArrayList<InventorySetupsShowWornItemsFilterID> VALUES;

	static
	{
		VALUES = new ArrayList<>();
		Collections.addAll(VALUES, InventorySetupsShowWornItemsFilterID.values());
	}

	InventorySetupsShowWornItemsFilterID(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public static ArrayList<InventorySetupsShowWornItemsFilterID> getValues()
	{
		return VALUES;
	}
}
