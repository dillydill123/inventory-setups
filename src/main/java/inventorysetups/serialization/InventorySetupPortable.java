package inventorysetups.serialization;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupLayoutUtilities;
import lombok.Value;
import net.runelite.client.plugins.banktags.tabs.Layout;

import java.util.ArrayList;
import java.util.List;

// a class intended to be imported and exported.
// The reason for this is to include data that inventory setups may not handle, such as layouts.
@Value
public class InventorySetupPortable
{

	InventorySetupSerializable setup;

	// We don't store the layout tag info since it might change when importing. The name is not guaranteed.
	int[] layout;

	static public InventorySetupPortable convertFromInventorySetup(final InventorySetup setup, final InventorySetupLayoutUtilities layoutUtilities)
	{
		final Layout layout = layoutUtilities.getSetupLayout(setup);
		InventorySetupSerializable serializable = InventorySetupSerializable.convertFromInventorySetup(setup);
		return new InventorySetupPortable(serializable, layout.getLayout());
	}

	static public ArrayList<InventorySetupPortable> convertFromListOfSetups(final List<InventorySetup> setups, final InventorySetupLayoutUtilities layoutUtilities)
	{
		ArrayList<InventorySetupPortable> portables = new ArrayList<>();
		for (final InventorySetup setup : setups)
		{
			portables.add(convertFromInventorySetup(setup, layoutUtilities));
		}
		return portables;
	}

	public InventorySetup getSerializedSetup()
	{
		return InventorySetupSerializable.convertToInventorySetup(setup);
	}
}
