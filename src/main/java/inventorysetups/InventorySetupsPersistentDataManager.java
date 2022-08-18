package inventorysetups;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import inventorysetups.ui.InventorySetupsPluginPanel;
import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.swing.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static inventorysetups.InventorySetupsPlugin.CONFIG_GROUP;
import static inventorysetups.InventorySetupsPlugin.CONFIG_KEY_SECTIONS;
import static inventorysetups.InventorySetupsPlugin.CONFIG_KEY_SETUPS;

@Slf4j
public class InventorySetupsPersistentDataManager
{

	private final InventorySetupsPlugin plugin;
	private final InventorySetupsPluginPanel panel;
	private final ConfigManager configManager;
	private final InventorySetupsCache cache;

	private Gson gson;

	@Getter
	private List<InventorySetup> inventorySetups;
	@Getter
	private List<InventorySetupsSection> sections;

	@Inject
	public InventorySetupsPersistentDataManager(final InventorySetupsPlugin plugin,
												final InventorySetupsPluginPanel panel,
												final ConfigManager manager,
												final InventorySetupsCache cache,
												final Gson gson,
												List<InventorySetup> inventorySetups,
												List<InventorySetupsSection> sections)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.configManager = manager;
		this.cache = cache;
		this.gson = gson;

		this.gson = this.gson.newBuilder().registerTypeAdapter(long.class, new LongTypeAdapter()).create();
	}

	public void loadConfig()
	{
		inventorySetups = new ArrayList<>();
		sections = new ArrayList<>();
		cache.clearAll();

		Type setupType = new TypeToken<ArrayList<InventorySetup>>()
		{

		}.getType();
		Type sectionType = new TypeToken<ArrayList<InventorySetupsSection>>()
		{

		}.getType();

		inventorySetups = loadData(CONFIG_KEY_SETUPS, setupType);
		sections = loadData(CONFIG_KEY_SECTIONS, sectionType);
		// Fix names of setups from config if there are duplicate names
		for (final InventorySetupsSection section : sections)
		{
			final String newName = InventorySetupUtilities.findNewName(section.getName(), cache.getSectionNames().keySet());
			section.setName(newName);

			// Remove any duplicates that exist
			List<String> uniqueSetups = section.getSetups().stream().distinct().collect(Collectors.toList());
			section.setSetups(uniqueSetups);
		}

		processSetupsFromConfig();

		for (final InventorySetupsSection section : sections)
		{
			// Remove setups which don't exist in a section
			section.getSetups().removeIf(s -> !cache.getInventorySetupNames().containsKey(s));
			cache.addSection(section);
		}
	}

	public void updateConfig(boolean updateSetups, boolean updateSections)
	{
		if (updateSetups)
		{
			// update setups
			final String jsonSetups = gson.toJson(inventorySetups);
			configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_SETUPS, jsonSetups);
		}

		if (updateSections)
		{
			// update setups
			final String jsonSections = gson.toJson(sections);
			configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_SECTIONS, jsonSections);
		}

	}


	private <T> List<T> loadData(final String configKey, Type type)
	{
		final String storedData = configManager.getConfiguration(CONFIG_GROUP, configKey);
		if (Strings.isNullOrEmpty(storedData))
		{
			return new ArrayList<>();
		}
		else
		{
			try
			{
				// serialize the internal data structure from the json in the configuration
				return gson.fromJson(storedData, type);
			}
			catch (Exception e)
			{
				log.error("Exception occurred while loading data", e);
				return new ArrayList<>();
			}
		}
	}

	private void processSetupsFromConfig()
	{
		for (final InventorySetup setup : inventorySetups)
		{
			if (setup.getRune_pouch() == null && plugin.containerContainsRunePouch(setup.getInventory()))
			{
				setup.updateRunePouch(plugin.getRunePouchData());
			}
			if (setup.getBoltPouch() == null && plugin.containerContainsBoltPouch(setup.getInventory()))
			{
				setup.updateBoltPouch(plugin.getBoltPouchData());
			}
			if (setup.getNotes() == null)
			{
				setup.updateNotes("");
			}
			if (setup.getAdditionalFilteredItems() == null)
			{
				setup.updateAdditionalItems(new HashMap<>());
			}

			final String newName = InventorySetupUtilities.findNewName(setup.getName(), cache.getInventorySetupNames().keySet());
			setup.setName(newName);
			cache.addSetup(setup);
		}

	}

	private String fixOldJSONData(final String json)
	{
		final Gson gson = new Gson();
		JsonElement je = gson.fromJson(json, JsonElement.class);
		JsonArray ja = je.getAsJsonArray();
		for (JsonElement elem : ja)
		{
			JsonObject setup = elem.getAsJsonObject();

			// Example if needed in the future
//			if (setup.getAsJsonPrimitive("stackDifference").isBoolean())
//			{
//				int stackDiff = setup.get("stackDifference").getAsBoolean() ? 1 : 0;
//				setup.remove("stackDifference");
//				setup.addProperty("stackDifference", stackDiff);
//			}
		}
		return je.toString();
	}


}
