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
package inventorysetups.ui;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupsItem;
import inventorysetups.InventorySetupsPlugin;
import inventorysetups.InventorySetupsRunePouchType;
import inventorysetups.InventorySetupsSlotID;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.game.ItemManager;

public class InventorySetupsRunePouchPanel extends InventorySetupsAmmunitionPanel
{
	// 23650 is what shows up when selecting a RunePouch from ChatBoxItemSearch, 27086 is likely lms
	public static final List<Integer> RUNE_POUCH_IDS = Arrays.asList(ItemID.BH_RUNE_POUCH, ItemID.BH_RUNE_POUCH_TROUVER, ItemID.BR_RUNE_REPLACEMENT, ItemID.PVPA_RUNE_REPLACEMENT);

	public static final Set<Integer> RUNE_POUCH_IDS_SET = new HashSet<>(RUNE_POUCH_IDS);

	public static final List<Integer> RUNE_POUCH_DIVINE_IDS = Arrays.asList(ItemID.DIVINE_RUNE_POUCH, ItemID.DIVINE_RUNE_POUCH_TROUVER);

	public static final Set<Integer> RUNE_POUCH_DIVINE_IDS_SET = new HashSet<>(RUNE_POUCH_DIVINE_IDS);

	public static final List<Integer> RUNE_POUCH_AMOUNT_VARBITS = Arrays.asList(VarbitID.RUNE_POUCH_QUANTITY_1, VarbitID.RUNE_POUCH_QUANTITY_2, VarbitID.RUNE_POUCH_QUANTITY_3, VarbitID.RUNE_POUCH_QUANTITY_4);

	public static final List<Integer> RUNE_POUCH_RUNE_VARBITS = Arrays.asList(VarbitID.RUNE_POUCH_TYPE_1, VarbitID.RUNE_POUCH_TYPE_2, VarbitID.RUNE_POUCH_TYPE_3, VarbitID.RUNE_POUCH_TYPE_4);

	InventorySetupsRunePouchPanel(ItemManager itemManager, InventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Rune Pouch");
	}

	@Override
	protected InventorySetupsSlotID getSlotId()
	{
		return InventorySetupsSlotID.RUNE_POUCH;
	}

	@Override
	protected int getSlotsCount()
	{
		return 4;
	}

	@Override
	protected List<InventorySetupsItem> getContainer(InventorySetup inventorySetup)
	{
		return inventorySetup.getRune_pouch();
	}

	public void handleRunePouchHighlighting(final InventorySetup inventorySetup, final InventorySetupsRunePouchType runePouchType)
	{
		if (!inventorySetup.isHighlightDifference() || !plugin.isHighlightingAllowed())
		{
			super.resetSlotColors();
			return;
		}

		// This must be run on the client thread!
		if (inventorySetup.getRune_pouch() != null)
		{
			// attempt to highlight if rune pouch is available
			if (runePouchType != InventorySetupsRunePouchType.NONE)
			{
				List<InventorySetupsItem> runePouchToCheck = plugin.getAmmoHandler().getRunePouchData(runePouchType);
				super.highlightSlots(runePouchToCheck, inventorySetup);
			}
			else // if the current inventory doesn't have a rune pouch but the setup does, highlight the RP pouch
			{
				super.highlightAllSlots(inventorySetup);
			}
		}
		else
		{
			super.resetSlotColors();
		}
	}
}
