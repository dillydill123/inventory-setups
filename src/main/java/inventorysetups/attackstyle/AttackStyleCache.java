package inventorysetups.attackstyle;

import net.runelite.api.Client;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


// Convenience helper for the DBTable 78 which has weapon category mappings.
// https://abextm.github.io/cache2/#/viewer/dbtable/78
public class AttackStyleCache
{

	private static final int ATTACK_STYLES_DB_TABLE_ID = 78;

	private final Client client;

	public static final String CASTING_OPTION_NAME = "Cast";

	public static final String DEF_CASTING_OPTION_NAME = "D. Cast";

	// Mapping from weapon category to attack style category
	private final HashMap<Integer, AttackStyleCategory> attackStyleCategories;

	public AttackStyleCache(final Client client)
	{
		this.attackStyleCategories = new HashMap<>();
		this.client = client;

		List<Integer> attackStyleRows = client.getDBTableRows(ATTACK_STYLES_DB_TABLE_ID);
		for (Integer row: attackStyleRows)
		{
			Integer weaponCategoryFromRow = (Integer) client.getDBTableField(row, 0, 0)[0];
			ArrayList<Integer> combatModeIndices = Arrays.stream(client.getDBTableField(row, 1, 0))
														.map(o -> (Integer) o)
														.collect(Collectors.toCollection(ArrayList::new));
			ArrayList<String> attackOptions = Arrays.stream(client.getDBTableField(row, 1, 1))
														.map(o -> (String) o)
														.collect(Collectors.toCollection(ArrayList::new));
			ArrayList<String> tooltips = Arrays.stream(client.getDBTableField(row, 1, 2))
														.map(o -> (String) o)
														.collect(Collectors.toCollection(ArrayList::new));
			AttackStyleCategory category = new AttackStyleCategory(combatModeIndices, attackOptions, tooltips);
			attackStyleCategories.put(weaponCategoryFromRow, category);
		}
	}

	public AttackStyleCategory getAttackStyleCategory(int weaponCategory)
	{
		return attackStyleCategories.get(weaponCategory);
	}

	public AttackStyleCategory getCurrentAttackStyleCategory()
	{
		final int currentEquippedWeaponTypeVarbit = client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY);
		return getAttackStyleCategory(currentEquippedWeaponTypeVarbit);
	}

	public String getCurrentAttackOption()
	{
		final int currentAttackStyleVarbit = client.getVarpValue(VarPlayerID.COM_MODE);
		final int currentCastingModeVarbit = client.getVarbitValue(VarbitID.AUTOCAST_DEFMODE);
		// Have to add the casting mode varbit (0 or 1) to correctly move from casting -> defensive casting
		// currentAttackStyleVarbit will be [0,4]. 4 If we are auto casting.
		final int combinedAttackStyle = currentAttackStyleVarbit + currentCastingModeVarbit;
		if (combinedAttackStyle == 4)
		{
			return CASTING_OPTION_NAME;
		}
		else if (combinedAttackStyle == 5)
		{
			return DEF_CASTING_OPTION_NAME;
		}
		return getCurrentAttackStyleCategory().getCurrentAttackStyleOption(currentAttackStyleVarbit);
	}
}
