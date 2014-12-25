package okkapel.bombrush.command;

import okkapel.bombrush.entity.Player;
import okkapel.bombrush.util.World;

public abstract class Command {
	
	public static final String COMMAND_PREFIX = "&";
	
	public Command(String cmdName, ParamType[] params) {
		
	}
	
	public abstract void handleCommand(Player plr, World world);
	
}
