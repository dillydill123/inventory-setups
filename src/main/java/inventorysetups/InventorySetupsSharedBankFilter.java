package inventorysetups;

import inventorysetups.ui.InventorySetupsPluginPanel;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;

// Handles filtering the Group Ironman shared storage ("shared bank") to match a selected inventory setup.
// The normal bank filtering is powered by RuneLite's core Bank Tags plugin, which only works on the normal
// bank (InterfaceID.BANKMAIN) and has no awareness of the shared storage. This is therefore a custom,
// classic-style filter: it simply hides the item widgets that are not part of the selected setup. Kept items
// are left exactly where the game placed them (no repositioning), which guarantees that withdrawing always
// targets the correct storage slot.
public class InventorySetupsSharedBankFilter
{
	private final InventorySetupsPlugin plugin;
	private final Client client;
	private final InventorySetupsPluginPanel panel;
	private final InventorySetupsConfig config;

	public InventorySetupsSharedBankFilter(InventorySetupsPlugin plugin, Client client,
											InventorySetupsPluginPanel panel, InventorySetupsConfig config)
	{
		this.plugin = plugin;
		this.client = client;
		this.panel = panel;
		this.config = config;
	}

	// True if the Group Ironman shared storage interface is currently open.
	public boolean isSharedBankOpen()
	{
		final Widget itemContainer = client.getWidget(InterfaceID.SharedBank.ITEMS);
		return itemContainer != null && !itemContainer.isHidden();
	}

	// Hides every item in the shared storage that is not part of the currently selected setup.
	// Must be called on the client thread. This is idempotent: a freshly built storage has all items
	// visible, so re-running it (e.g. after every deposit/withdraw rebuild) reproduces the same result.
	public void filterSharedBank()
	{
		assert client.isClientThread() : "filterSharedBank must be called on the client thread";

		final InventorySetup setup = panel.getCurrentSelectedSetup();

		// Nothing to filter. A freshly built storage is already unfiltered, so just leave it as is.
		if (setup == null || !setup.isFilterBank() || !config.filterSharedBank() || !plugin.isFilteringAllowed())
		{
			return;
		}

		final Widget itemContainer = client.getWidget(InterfaceID.SharedBank.ITEMS);
		if (itemContainer == null || itemContainer.isHidden())
		{
			return;
		}

		final Widget[] items = itemContainer.getChildren();
		if (items == null)
		{
			return;
		}

		for (final Widget item : items)
		{
			final int itemId = item.getItemId();

			// Skip non-item children and empty slots (the build script uses BLANKOBJECT for empty slots).
			if (itemId <= -1 || itemId == ItemID.BLANKOBJECT)
			{
				continue;
			}

			// Storage items are in inventory form, so canonicalize the same way the ground item swapping does.
			final boolean canonicalize = InventorySetupsVariationMapping.INVERTED_WORN_ITEMS.containsKey(itemId);
			final boolean keep = plugin.setupContainsItem(setup, itemId, true, canonicalize);

			// Set the hidden state both ways. The storage reuses these widgets across rebuilds, so an item
			// that should now be shown must be explicitly un-hidden, not just left as it was.
			item.setHidden(!keep);
		}

		// NOTE: Only the main shared storage view is filtered. The compact side view
		// (InterfaceID.SharedBankSide.ITEMS) is intentionally left untouched for now.
	}

	// Restores the shared storage to its native, unfiltered state by un-hiding every item slot we may have
	// hidden. Kept items were never moved, so simply un-hiding them puts the storage back as it was.
	// Must be called on the client thread.
	public void resetSharedBank()
	{
		assert client.isClientThread() : "resetSharedBank must be called on the client thread";

		final Widget itemContainer = client.getWidget(InterfaceID.SharedBank.ITEMS);
		if (itemContainer == null)
		{
			return;
		}

		final Widget[] items = itemContainer.getChildren();
		if (items == null)
		{
			return;
		}

		// Only item slots are ever hidden by the filter. Leave empty/non-item children untouched.
		for (final Widget item : items)
		{
			final int itemId = item.getItemId();
			if (itemId <= -1 || itemId == ItemID.BLANKOBJECT)
			{
				continue;
			}
			item.setHidden(false);
		}
	}
}
