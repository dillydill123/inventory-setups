package inventorysetups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum InventorySetupsGroundItemMenuSwapPriority
{
	// Items not in the setup will be moved to the top
	OUT(0),

	// Items in the setup will be moved to the top
	IN(1);

	private final int type;

	private static final List<InventorySetupsGroundItemMenuSwapPriority> VALUES;

	static
	{
		VALUES = new ArrayList<>();
		Collections.addAll(VALUES, InventorySetupsGroundItemMenuSwapPriority.values());
	}

	InventorySetupsGroundItemMenuSwapPriority(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public static List<InventorySetupsGroundItemMenuSwapPriority> getValues()
	{
		return VALUES;
	}
}
