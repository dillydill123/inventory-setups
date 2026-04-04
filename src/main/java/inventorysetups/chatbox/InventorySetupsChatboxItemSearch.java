/*
 * Copyright (c) 2019, Ron Young <https://github.com/raiyni>
 * All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
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

package inventorysetups.chatbox;

import com.google.common.primitives.Shorts;
import com.google.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import static net.runelite.api.ScriptID.GE_ITEM_SEARCH;

import net.runelite.api.events.GrandExchangeSearched;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.client.input.KeyListener;

// NOTE: Copy of https://github.com/raiyni/runelite/blob/00d5f3667075d43d2bab3d088efbffb93f8b14ea/runelite-client/src/main/java/net/runelite/client/game/chatbox/ChatboxItemSearch.java
// Some changes have been made, marked by `// NOTE:` comments.
// Part of RuneLite https://github.com/runelite/runelite/pull/18410. Even if this merges, we still need this due to the custom filtering logic.


@Singleton
@Slf4j
public class InventorySetupsChatboxItemSearch extends InventorySetupsChatboxInput implements KeyListener
{
	private static final int MAX_RESULTS = 250;

	//@ScriptArguments(integer = 3, string = 1)
	public static final int GE_OPEN_ITEM_SEARCH = 750;

	//@ScriptArguments(integer = 2)
	public static final int GE_ITEM_SEARCH_SELECTED = 754;

	public InventorySetupsChatboxItemSearchFilter searchFilter;

	private final InventorySetupsChatboxPanelManager chatboxPanelManager;
	private final ItemManager itemManager;
	private final Client client;

	private String tooltipText;

	@Getter
	private Consumer<Integer> onItemSelected;

	@Inject
	private InventorySetupsChatboxItemSearch(InventorySetupsChatboxPanelManager chatboxPanelManager, ItemManager itemManager, Client client)
	{
		this.chatboxPanelManager = chatboxPanelManager;
		this.itemManager = itemManager;
		this.client = client;
	}

	public InventorySetupsChatboxItemSearch build()
	{
		chatboxPanelManager.openInput(this);

		return this;
	}

	@Override
	protected void open()
	{
		log.debug("opening item search");
		this.client.runScript(GE_OPEN_ITEM_SEARCH, "Item Search", 1, -1, 0);
	}

	@Override
	protected void close()
	{
		log.debug("closing item search");
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (event.getScriptId() != GE_ITEM_SEARCH_SELECTED)
		{
			return;
		}

		Object[] args = event.getScriptEvent().getArguments();
		if ((int) args[2] != 84)
		{
			// items are only selected if 84 is passed as a parameter
			return;
		}

		final int resultId = (int) args[1];

		log.debug("selecting item {}", resultId);
		onItemSelected.accept(resultId);
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() != GE_ITEM_SEARCH)
		{
			return;
		}

		Widget results = client.getWidget(InterfaceID.Chatbox.MES_LAYER_SCROLLCONTENTS);
		if (results == null || results.isSelfHidden())
		{
			return;
		}

		for (Widget w : results.getDynamicChildren())
		{
			// ops are defined on the highlight widgets surrounding the results
			if (w.getType() == WidgetType.RECTANGLE)
			{
				w.setAction(0, tooltipText);
			}
		}
	}

	@Subscribe(priority = 1)
	public void onGrandExchangeSearched(GrandExchangeSearched event)
	{
		// NOTE: added `.toLowerCase()`
		final String input = client.getVarcStrValue(VarClientID.MESLAYERINPUT).toLowerCase();
		client.setGeSearchResultIndex(0);

		List<Integer> ids = IntStream.range(0, client.getItemCount())
				.map(itemManager::canonicalize)
				.distinct()
				// NOTE: Added the extra search filter to remove fake items after canonicalization.
				.filter(id -> searchFilter.shouldKeep(id))
				.mapToObj(itemManager::getItemComposition)
				.filter(item -> item.getName().toLowerCase().contains(input))
				.limit(MAX_RESULTS + 1)
				.map(ItemComposition::getId)
				.collect(Collectors.toList());

		if (ids.size() > MAX_RESULTS)
		{
			client.setGeSearchResultCount(-1);
			client.setGeSearchResultIds(null);
		}
		else
		{
			client.setGeSearchResultCount(ids.size());
			client.setGeSearchResultIds(Shorts.toArray(ids));
		}

		event.consume();
	}

	public InventorySetupsChatboxItemSearch onItemSelected(Consumer<Integer> onItemSelected)
	{
		this.onItemSelected = onItemSelected;
		return this;
	}

	public InventorySetupsChatboxItemSearch tooltipText(final String text)
	{
		tooltipText = text;
		return this;
	}

	// NOTE: Added the extra search filter to remove lms and estimated quest items
	public InventorySetupsChatboxItemSearch searchFilter(final InventorySetupsChatboxItemSearchFilter filter)
	{
		searchFilter = filter;
		return this;
	}

	@Override
	public void keyTyped(KeyEvent ev)
	{
	}

	@Override
	public void keyPressed(KeyEvent ev)
	{
		if (!chatboxPanelManager.shouldTakeInput())
		{
			return;
		}

		int code = ev.getKeyCode();
		if (code == KeyEvent.VK_ESCAPE)
		{
			ev.consume();
			chatboxPanelManager.close();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}
}