package inventorysetups.ui;

import inventorysetups.InventorySetup;
import inventorysetups.InventorySetupItem;
import inventorysetups.InventorySetupSlotID;
import inventorysetups.InventorySetupsPlugin;
import net.runelite.api.SpriteID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class InventorySetupSpellbookPanel extends InventorySetupContainerPanel
{

	private InventorySetupSlot spellbookSlot;
	private ArrayList<BufferedImage> spellbookImages;

	InventorySetupSpellbookPanel(ItemManager itemManager, InventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Spellbook");
		spellbookImages = new ArrayList<>();

		plugin.getClientThread().invokeLater(() ->
		{

			BufferedImage standardSpellbook = plugin.getSpriteManager().getSprite(SpriteID.TAB_MAGIC, 0);
			BufferedImage ancientsSpellbook = plugin.getSpriteManager().getSprite(SpriteID.TAB_MAGIC_SPELLBOOK_ANCIENT_MAGICKS, 0);
			BufferedImage lunarSpellbook = plugin.getSpriteManager().getSprite(SpriteID.TAB_MAGIC_SPELLBOOK_LUNAR, 0);
			BufferedImage arceuusSpellbook = plugin.getSpriteManager().getSprite(SpriteID.TAB_MAGIC_SPELLBOOK_ARCEUUS, 0);
			BufferedImage noneSpellbook = null;

			// might be null depending on game state
			if (standardSpellbook == null || ancientsSpellbook == null || lunarSpellbook == null || arceuusSpellbook == null)
			{
				return false;
			}

			spellbookImages.add(standardSpellbook);
			spellbookImages.add(ancientsSpellbook);
			spellbookImages.add(lunarSpellbook);
			spellbookImages.add(arceuusSpellbook);
			spellbookImages.add(noneSpellbook);

			return true;
		});

	}

	@Override
	public void setupContainerPanel(JPanel containerSlotsPanel)
	{
		final GridLayout gridLayout = new GridLayout(1, 2, 3, 1);
		containerSlotsPanel.setLayout(gridLayout);

		spellbookSlot = new InventorySetupSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupSlotID.SPELL_BOOK, 0);

		// add options to easily change spellbook without having to do it manually in game
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem updateToStandard = new JMenuItem("Update Slot to Standard");
		JMenuItem updateToAncient = new JMenuItem("Update Slot to Ancient");
		JMenuItem updateToLunar = new JMenuItem("Update Slot to Lunar");
		JMenuItem updateToArceuus = new JMenuItem("Update Slot to Arceuus");
		JMenuItem updateToNone = new JMenuItem("Update Slot to None");

		popupMenu.add(updateToStandard);
		popupMenu.add(updateToAncient);
		popupMenu.add(updateToLunar);
		popupMenu.add(updateToArceuus);
		popupMenu.add(updateToNone);

		for (int i = 0; i < 5; i++)
		{
			JMenuItem item = (JMenuItem)popupMenu.getComponent(i);
			final int newSpellbook = i;
			item.addActionListener(e ->
			{
				plugin.updateSpellbookInSetup(newSpellbook);
			});
		}

		spellbookSlot.setComponentPopupMenu(popupMenu);
		spellbookSlot.getImageLabel().setComponentPopupMenu(popupMenu);
		containerSlotsPanel.add(spellbookSlot);
	}

	@Override
	public void highlightSlotDifferences(ArrayList<InventorySetupItem> currContainer, InventorySetup inventorySetup)
	{
		plugin.getClientThread().invokeLater(() ->
		{
			if (inventorySetup.getSpellBook() != 4 && inventorySetup.getSpellBook() != plugin.getCurrentSpellbook())
			{
				spellbookSlot.setBackground(inventorySetup.getHighlightColor());
			}
			else
			{
				resetSlotColors();
			}
		});
	}

	@Override
	public void setSlots(InventorySetup setup)
	{
		/* 0 = Standard
		   1 = Ancient
		   2 = Lunar
		   3 = Arceuus */
		String spellbookStr = "";
		switch (setup.getSpellBook())
		{
			case 0:
				spellbookStr = "Standard";
				break;
			case 1:
				spellbookStr = "Ancient";
				break;
			case 2:
				spellbookStr = "Lunar";
				break;
			case 3:
				spellbookStr = "Arceuus";
				break;
			case 4:
				spellbookStr = "None";
				break;
			default:
				spellbookStr = "Incorrect";
				break;
		}

		spellbookSlot.setImageLabel(spellbookStr + " Spellbook", spellbookImages.get(setup.getSpellBook()));

		validate();
		repaint();
	}

	@Override
	public void resetSlotColors()
	{
		spellbookSlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
	}

}
