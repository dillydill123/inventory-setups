package inventorysetups;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("inventorysetupplugin")
public interface InventorySetupConfig extends Config
{
	@ConfigItem(
		keyName = "highlightStackDiff",
		name = "Highlight Stack Differences",
		description = "Configures whether or not to highlight differences in stack quantities"
	)
	default boolean highlightStackDiff()
	{
		return false;
	}

	@ConfigItem(
		keyName = "highlightVarDiff",
		name = "Highlight Variation Differences",
		description = "Configures whether or not to highlight variations in similar equipment"
	)
	default boolean highlightVarDiff()
	{
		return false;
	}

	@ConfigItem(
		keyName = "highlight",
		name = "Highlight by default",
		description = "Configures whether or not to highlight differences in equipment"
	)
	default boolean highlight()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "highlightColor",
		name = "Color of highlights",
		description = "Configures the default highlighting color"
	)
	default Color highlightColor()
	{
		return Color.RED;
	}
}
