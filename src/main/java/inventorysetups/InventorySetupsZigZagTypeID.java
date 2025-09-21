package inventorysetups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum InventorySetupsZigZagTypeID
{
	// Use if you want to withdraw using a top to bottom zigzag pattern
	TOP_TO_BOTTOM("Top to Bottom", 0),

	// Use if you want to withdraw using a bottom to top zigzag pattern
	BOTTOM_TO_TOP("Bottom to Top", 1);

	private final int type;

	private static final List<InventorySetupsZigZagTypeID> VALUES;

	static
	{
		VALUES = new ArrayList<>();
		Collections.addAll(VALUES, InventorySetupsZigZagTypeID.values());
	}

	InventorySetupsZigZagTypeID(String s, int type)
	{
		this.type = type;
	}

	public static List<InventorySetupsZigZagTypeID> getValues()
	{
		return VALUES;
	}
}
