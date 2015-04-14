package okkapel.bombrush.command;

public enum ParamType {
	INTEGER,
	DECIMAL,
	BOOLEAN;
	
	public boolean isOfParamType(String p) {
		switch(this) {
		case INTEGER:
			try {
				Integer.valueOf(p);
				return true;
			} catch(Throwable e){}
			return false;
		case DECIMAL:
			try {
				Float.valueOf(p);
				return true;
			} catch(Throwable e){}
			return false;
		case BOOLEAN:
			return "true".equalsIgnoreCase(p) || "false".equalsIgnoreCase(p);
		}
		return false;
	}
}
