package okkapel.bombrush.command;

import okkapel.bombrush.entity.Bomb;
import okkapel.bombrush.entity.Player;

public class CommandSpawnBomb extends Command {

	public CommandSpawnBomb() {
		super("bomb", new ParamType[] {ParamType.INTEGER, ParamType.INTEGER});
	}

	@Override
	public void handleCommand(Player plr, Object[] params) {
		int x = ((Integer)params[0]).intValue();
		int y = ((Integer)params[1]).intValue();
		Bomb spwn = new Bomb(60 * 5);
		if(plr.getWorldRef().isEmpty(x, y)) {
			spwn.setWorldGridPos(x, y);
			plr.getWorldRef().spawnEntity(spwn);
		}
	}

}
