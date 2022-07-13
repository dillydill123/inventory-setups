package inventorysetups;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

// Class to assist with speeding up operations by caching names when the config is loaded
public class InventorySetupsCache
{
	public InventorySetupsCache()
	{
		this.inventorySetupNames = new HashSet<>();
		this.sectionNames = new HashSet<>();
		this.setupsWithoutSections = new HashSet<>();
	}

	@Getter
	private final Set<String> inventorySetupNames;

	@Getter
	private final Set<String> sectionNames;

	@Getter
	private final Set<String> setupsWithoutSections;
}
