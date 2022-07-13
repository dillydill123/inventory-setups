package inventorysetups;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Class to assist with speeding up operations by caching names when the config is loaded
public class InventorySetupsCache
{
	public InventorySetupsCache()
	{
		this.inventorySetupNames = new HashSet<>();
		this.sectionNames = new HashSet<>();
		this.setupsSectionCounter = new HashMap<>();
	}

	public void addSetup(final InventorySetup setup)
	{
		inventorySetupNames.add(setup.getName());
		setupsSectionCounter.put(setup.getName(), new SetupsSectionCounter(setup, 0));
	}

	public void addSection(final InventorySetupsSection section)
	{
		sectionNames.add(section.getName());
	}

	public void updateSetupName(final InventorySetup setup, final String newName)
	{
		inventorySetupNames.remove(setup.getName());
		inventorySetupNames.add(newName);

		// Update the key with the new name
		setupsSectionCounter.put(newName, setupsSectionCounter.remove(setup.getName()));
	}

	public void updateSectionName(final InventorySetupsSection section, final String newName)
	{
		sectionNames.remove(section.getName());
		sectionNames.add(newName);
	}

	public void removeSetup(final InventorySetup setup)
	{
		inventorySetupNames.remove(setup.getName());
		setupsSectionCounter.remove(setup.getName());
	}

	public void removeSection(final InventorySetupsSection section)
	{
		sectionNames.remove(section.getName());
		for (final String setupName : section.getSetups())
		{
			setupsSectionCounter.get(setupName).decreaseCount();
		}
	}

	public void addSetupToSection(final String setupName)
	{
		setupsSectionCounter.get(setupName).increaseCount();
	}

	public void removeSetupFromSection(final InventorySetup setup)
	{
		setupsSectionCounter.get(setup.getName()).decreaseCount();
	}


	@Getter
	private final Set<String> inventorySetupNames;

	@Getter
	private final Set<String> sectionNames;

	@AllArgsConstructor
	static public class SetupsSectionCounter
	{
		InventorySetup setup;
		@Getter
		Integer count;

		public void increaseCount()
		{
			count++;
		}
		public void decreaseCount()
		{
			count--;
		}
	};

	@Getter
	private final Map<String, SetupsSectionCounter> setupsSectionCounter;
}
