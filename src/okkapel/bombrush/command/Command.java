package okkapel.bombrush.command;

import okkapel.bombrush.entity.Player;
import okkapel.bombrush.util.World;

public abstract class Command {
	
	public static final String COMMAND_PREFIX = "&";
	public static final char COMMAND_PREFIXC = '&';
	
	public final String cmdName;
	public final ParamType[] ptypes;
	
	public Command(String cmdName, ParamType[] ptypes) {
		this.cmdName = cmdName;
		this.ptypes = ptypes;
	}
	
	public boolean isHandlerFor(String cmd) {
		return cmdName.equalsIgnoreCase(cmd);
	}
	
	public void attemptHandle(Player plr, String[] params) {
		if(params == null) {
			if(ptypes == null) {
				handleCommand(plr, null);
			}
			return;
		}
		
		if(ptypes == null) {
			return;
		}
		
		if(ptypes.length == params.length) {
			Object[] pars = new Object[params.length];
			for(int i=0;i<params.length;i++) {
				switch(ptypes[i]) {
				case INTEGER:
					if(ParamType.INTEGER.isOfParamType(params[i])) {
						pars[i] = new Integer(getIntegerParam(params[i]));
					} else {
						return;
					}
					break;
				case DECIMAL:
					if(ParamType.DECIMAL.isOfParamType(params[i])) {
						pars[i] = new Float(getDecimalParam(params[i]));
					} else {
						return;
					}
					break;
				case BOOLEAN:
					if(ParamType.BOOLEAN.isOfParamType(params[i])) {
						pars[i] = new Boolean(getBooleanParam(params[i]));
					} else {
						return;
					}
					break;
				}
			}
			
			handleCommand(plr, pars);
			System.out.println("Successfully handled command!");
		}
		
	}
	
	/** @param params an array containing the params in types Float, Integer and Boolean, null if there are no params */
	public abstract void handleCommand(Player plr, Object[] params);
	
	private static int getIntegerParam(String p) {
		try {
			return Integer.valueOf(p);
		}catch(Throwable e){}
		
		return 0;
	}
	
	private static float getDecimalParam(String p) {
		try {
			return Float.valueOf(p);
		}catch(Throwable e){}
		
		return 0f;
	}
	
	private static boolean getBooleanParam(String p) {
		if("true".equalsIgnoreCase(p)) {
			return true;
		}
		return false;
	}
	
}
