package inventorysetups;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("inventorysetupplugin")
public interface InventorySetupConfig extends Config
{
	@ConfigItem(
		keyName = "highlightStackDiff",
		name = "Highlight Stack Differences",
		description = "Configures whether or not to highlight differences in stack quantities",
		position = 1
	)
	default boolean highlightStackDiff()
	{
		return false;
	}

	@ConfigItem(
		keyName = "highlightVarDiff",
		name = "Highlight Variation Differences",
		description = "Configures whether or not to highlight variations in similar equipment",
		position = 2
	)
	default boolean highlightVarDiff()
	{
		return false;
	}

	@ConfigItem(
		keyName = "filter",
		name = "Filter bank",
		description = "Configures whether or not to filter your bank to show items in your setup",
		position = 3
	)
	default boolean filter()
	{
		return false;
	}

	@ConfigItem(
		keyName = "highlight",
		name = "Highlight by default",
		description = "Configures whether or not to highlight differences in equipment",
		position = 4
	)
	default boolean highlight()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "highlightColor",
		name = "Color of highlights",
		description = "Configures the default highlighting color",
		position = 5
	)
	default Color highlightColor()
	{
		return Color.RED;
	}
}
