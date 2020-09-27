package inventorysetups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum InventorySetupStackCompare
{
	// Don't highlight at all
	None(0),

	// Only highlight if stacks are equal
	Standard(1),

	// Only highlight if stack is less than what is in the setup
	Less_Than(2),

	// Only highlight if stack is greater than what is in the setup
	Greater_Than(3);

	private final int type;

	private static final ArrayList<InventorySetupStackCompare> VALUES;

	static
	{
		VALUES = new ArrayList<>();
		Collections.addAll(VALUES, InventorySetupStackCompare.values());
	}

	InventorySetupStackCompare(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public static ArrayList<InventorySetupStackCompare> getValues()
	{
		return VALUES;
	}
}
