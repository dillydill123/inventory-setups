/*
 * Copyright (c) 2019, dillydill123 <https://github.com/dillydill123>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package inventorysetups;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import static inventorysetups.InventorySetupsPlugin.CONFIG_KEY_COMPACT_MODE;
import static inventorysetups.InventorySetupsPlugin.CONFIG_KEY_HIDE_BUTTON;
import static inventorysetups.InventorySetupsPlugin.CONFIG_KEY_SORTING_MODE;

@ConfigGroup(InventorySetupsPlugin.CONFIG_GROUP)
public interface InventorySetupConfig extends Config
{
	@ConfigItem(
			keyName = "bankFilter",
			name = "Default Filter Bank",
			description = "Configures the default setting for bank filtering in new setups",
			position = 1
	)
	default boolean bankFilter()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highlightStackDifference",
			name = "Default Highlight Stack Difference",
			description = "Configures the default setting for highlighting stack differences in new setups",
			position = 1,
			hidden = true
	)
	default boolean highlightStackDifferenceOld()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highlightStackDifferenceEnum",
			name = "Default Highlight Stack Difference",
			description = "Configures the default setting for highlighting stack differences in new setups",
			position = 1
	)
	default InventorySetupStackCompare highlightStackDifference()
	{
		return InventorySetupStackCompare.None;
	}

	@ConfigItem(
			keyName = "highlightVarianceDifference",
			name = "Default Highlight Variation Difference",
			description = "Configures the default setting for highlighting variations in new setups",
			position = 2
	)
	default boolean highlightVariationDifference()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highlightUnorderedDifference",
			name = "Default Highlight Unordered Difference",
			description = "Configures the default setting for unordered highlighting in new setups",
			position = 2
	)
	default boolean highlightUnorderedDifference()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highlightDifference",
			name = "Default Highlight",
			description = "Configures the default setting for highlighting differences in new setups",
			position = 4
	)
	default boolean highlightDifference()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
			keyName = "highlightColor",
			name = "Default Highlight Color",
			description = "Configures the default highlighting color in new setups",
			position = 5
	)
	default Color highlightColor()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "returnToSetupsHotkey",
			name = "Return To Setups Hotkey",
			description = "Configures the hotkey for returning to setups",
			position = 6
	)
	default Keybind returnToSetupsHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "filterBankHotkey",
			name = "Filter Bank Hotkey",
			description = "Configures the hotkey for filtering the bank",
			position = 7
	)
	default Keybind filterBankHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = CONFIG_KEY_COMPACT_MODE,
			name = "Compact Mode",
			description = "Configures the setup panels to be compact",
			position = 8
	)
	default boolean compactMode()
	{
		return false;
	}

	@ConfigItem(
			keyName = CONFIG_KEY_SORTING_MODE,
			name = "Sorting Mode",
			description = "Configures the sorting of setups",
			position = 9
	)
	default InventorySetupSorting sortingMode()
	{
		return InventorySetupSorting.DEFAULT;
	}

	@ConfigItem(
			keyName = CONFIG_KEY_HIDE_BUTTON,
			name = "Hide Help Button",
			description = "Hide the help button",
			position = 10
	)
	default boolean hideButton()
	{
		return false;
	}

	@ConfigItem(
			keyName = "disableBankTabBar",
			name = "Disable Bank Tab Bar",
			description = "Stops the thin bank tab bar from removing the bank filter when clicked",
			position = 11
	)
	default boolean disableBankTabBar()
	{
		return false;
	}
}
