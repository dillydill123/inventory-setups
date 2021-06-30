package inventorysetups;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

// A section is a collection of inventory setups that show up when setups are sorted by section
public class InventorySetupsSection
{

	@Getter
	private String name;

	// The list of setup Ids that correspond to the setups contained in this section
	private ArrayList<Long> setupIds;

	// Whether or not the section is maximized (showing the collection of setups or not)
	@Getter
	@Setter
	private boolean isMaximized;
}
