package okkapel.bombrush.util;

public class IRCMsg {
	
	public final String source;
	public final String command;
	public final String[] data;
	
	/** Not sent by a client */
	public final boolean fromServer;
	public final boolean invalid;
	
	public IRCMsg(String m) {
		if(m != null) {
			String[] split = m.split(" ");
			
			if(split.length == 1) {
				invalid = false;
				fromServer = true;
				source = null;
				command = split[0];
				data = null;
			} else {
				if(m.startsWith(":")) {
					invalid = false;
					source = split[0].substring(1);
					fromServer = !source.contains("@");
					command = split[1];
					data = new String[split.length-2];
					System.arraycopy(split, 2, data, 0, data.length);
				} else {
					invalid = false;
					source = null;
					fromServer = true;
					command = split[0];
					data = new String[split.length-1];
					System.arraycopy(split, 1, data, 0, data.length);
				}
			}
		} else {
			System.err.println("Invalid message: \"" + m + "\"");
			invalid = true;
			fromServer = false;
			source = null;
			command = null;
			data = null;
		}
		
	}
	
	/** The data param must not have a colon */
	public IRCMsg(String source, String command, String data) {
		this.source = source;
		this.command = command;
		this.data = data.split(":"+data);
		fromServer = false;
		invalid = command == null;
	}
	
	public String getAsStringToSend() {
		StringBuilder sb = new StringBuilder();
		
		if(source != null) {
			sb.append(":" + source);
		}
		
		sb.append(" ");
		sb.append(command);
		
		for(int i=0;i<data.length;i++) {
			sb.append(" ");
			sb.append(data[i]);
		}
		
		return sb.toString();
	}
	
	public String getDataAsString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<data.length;i++) {
			if(i != 0) {
				sb.append(" ");
			}
			sb.append(data[i]);
		}
		
		return sb.toString();
	}
	
	public String getPMMsg() {
		if(data.length > 1) {
			StringBuilder sb = new StringBuilder();
			for(int i=1;i<data.length;i++) {
				if(i == 1) {
					sb.append(data[i].substring(1));
				} else {
					sb.append(" ");
					sb.append(data[i]);
				}
				
			}
			return sb.toString();
		}
		return "ERROR";
	}
	
	public String getUser() {
		if(source != null) {
			int end = source.indexOf("!");
			return end != -1 ? source.substring(0, end) : source;
		}
		return "ERROR";
	}
	
	public static IRCMsg privmsg(String target, String msg) {
		return new IRCMsg(null, "PRIVMSG", msg);
	}
	
	public static IRCMsg pong(String target, String msg) {
		return new IRCMsg(null, "PONG", msg.substring(1));
	}
}
