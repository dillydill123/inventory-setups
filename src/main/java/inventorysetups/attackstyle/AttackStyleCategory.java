package inventorysetups.attackstyle;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class AttackStyleCategory
{

	// Indices of VarPlayerID.COM_MODE to map the attack option and tooltip.
	private ArrayList<Integer> combatModeIndices;

	// Chop, Slash, Pommel, Focus, Block, etc.
	private ArrayList<String> attackOptions;

	// The tooltip from hovering the style, e.g., (Aggressive)<br>(Slash)<br>(Strength XP)
	private ArrayList<String> tooltips;

	public String getCurrentAttackStyleOption(int currentAttackStyleVarbit)
	{
		int index = combatModeIndices.indexOf(currentAttackStyleVarbit);
		return attackOptions.get(index);
	}

}
