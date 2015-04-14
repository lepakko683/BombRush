package okkapel.bombrush.render;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import celestibytes.lib.util.PileList;
import okkapel.bombrush.BombRush;
import okkapel.bombrush.command.Command;
import okkapel.bombrush.command.CommandSpawnBomb;
import okkapel.bombrush.command.ParamType;
import okkapel.bombrush.util.IRCMsg;
import okkapel.bombrush.util.RBE;
import okkapel.bombrush.util.RendStr;
import okkapel.bombrush.util.Render;
import okkapel.bombrush.util.RenderBufferGenerator;

// https://twitter.com/rundavidrun/status/543480950739308544
public class ChatHandler {
	
	public static final int CHATLINE_MAX_LENGTH = 70;
	public static final int CHATLINE_MAX_COUNT = 10;
	
	private List<Command> cmdHandlers;
	private boolean chatHookRunning = false;
	private ChatHook chatHook;
	private int chRegl = 60;
	
	private PileList<ChatLine> chatDisplay;
	private ChatLine inputLine;
	private ByteBuffer renderData;
	private float fontSize = 16f;
	
	private static final boolean chatHookDisabled = true;
	
	public ChatHandler() {
		cmdHandlers = new ArrayList<Command>();
		cmdHandlers.add(new CommandSpawnBomb());
		
		// I don't want this to run pointlessy
		if(!chatHookDisabled) {
			chatHook = new ChatHook("#lepakko683");
			startChatHook();
		}
		
		renderData = BufferUtils.createByteBuffer(6 * RenderBufferGenerator.DEFAULT_GL_STRIDE + (CHATLINE_MAX_COUNT + 1) * CHATLINE_MAX_LENGTH * 6 * RenderBufferGenerator.DEFAULT_GL_STRIDE);
		renderData.position(0);
		renderData.limit(renderData.capacity());
		
		RBE rbe = RBE.INSTANCE;
		rbe.attachBuffer(renderData);
		rbe.setVertexOffset(0);
		rbe.editRect2D(0f, 720f/2f, 720f, 720f, 1f, .3f, .3f, .6f, .3f, 0f, 0f, 1f, 1f);
		rbe.finishEditing();
		
		setupFont();
		
		chatDisplay = new PileList<ChatLine>(CHATLINE_MAX_COUNT, false, false);
		
	}
	
	private int firstv = 6;
	public void postLine(String text) {
		System.out.println("post!");
		if(!chatDisplay.isFull()) {
			chatDisplay.addItem(new ChatLine(firstv, CHATLINE_MAX_LENGTH*6));
			chatDisplay.getLast().setText(text, renderData);
			firstv += CHATLINE_MAX_LENGTH*6;
		} else {
			ChatLine cl = chatDisplay.popFirst();
			cl.setText(text, renderData);
			chatDisplay.addItem(cl);
		}
	}
	
	public void onGameExit() {
		if(chatHook == null) {
			return;
		}
		if(!chatHook.isAlive()) {
			return;
		}
		
		try {
			stopChatHook();
			
			chatHook.join();
			
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void enableIgnoreMode() {
		if(chatHook == null) {
			return;
		}
		
		chatHook.setIgnoreMode(true);
	}
	
	public void disableIgnoreMode() {
		if(chatHook == null) {
			return;
		}
		
		chatHook.setIgnoreMode(false);
	}
	
	private void setupFont() {
		RBE rbe = RBE.INSTANCE;
		rbe.attachBuffer(renderData);
		rbe.setVertexOffset(6);
		float offset;
		for(int q=0;q<CHATLINE_MAX_COUNT+1;q++) {
			offset = 0f;
			for(int i=0;i<CHATLINE_MAX_LENGTH;i++) {
				rbe.editRect2D(offset, 0f, offset+fontSize, fontSize, 1f, 1f, 1f, 1f, 1f, RendStr.whitespace.u1, RendStr.whitespace.v1, RendStr.whitespace.u2, RendStr.whitespace.v2);
				offset += fontSize * RendStr.charWidth + fontSize;
			}
		}
		rbe.finishEditing();
	}
	
	
	private int slower = 10;
	private int countr = 70;
	public void renderChat() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Render.renderVA(renderData, 0, 6, 0);
		
		if(chatHookRunning) {
			if(chRegl == 0) {
				readMsgLines();
				chRegl = 60;
			}
			chRegl--;
		}
		
		if(chatDisplay.getLast() != null) {
			float yoffs = 720f-fontSize*2;
			Iterator<ChatLine> iter = chatDisplay.getItemIterator(true);
			do { // TODO: fix PileList iterator!
				renderChatLine(0f, yoffs, iter.next());
				yoffs -= fontSize;
			} while(iter.hasNext());
		}
		
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private void renderChatLine(float x, float y, ChatLine c) {
		GL11.glPushMatrix();
		
		GL11.glTranslatef(x, y, 0f);
		Render.renderVA(renderData, c.rinfo.first, c.getVertCount(), c.rinfo.texture);
		
		GL11.glPopMatrix();
	}
	
	/** Handle the clean command string (without the '&' prefix) */
	public void onCommandReceive(String s) {
//		System.out.println("Cmd info(" + s + "):" + " Integer: " + ParamType.INTEGER.isOfParamType(s) + " Decimal: " + ParamType.DECIMAL.isOfParamType(s) + " Boolean: " + ParamType.BOOLEAN.isOfParamType(s));
		for(int i=0;i<cmdHandlers.size();i++) {
			String[] params = s.split(" ");
			if(cmdHandlers.get(i).isHandlerFor(params[0])) {
				
				if(params.length == 1) {
					cmdHandlers.get(i).attemptHandle(BombRush.getInGameState().thePlayer, null);					
				} else {
					String[] rpars = new String[params.length-1];
					System.arraycopy(params, 1, rpars, 0, rpars.length);
					cmdHandlers.get(i).attemptHandle(BombRush.getInGameState().thePlayer, rpars);
				}
				return;
			}
		}
		System.out.println("No handler found!");
	}
	
	private void readMsgLines() {
		if(chatHook != null) {
			chatHook.readMessages(this);
		}
	}
	
	private void startChatHook() {
		if(!chatHookRunning) {
			try {
				chatHook.start();
				chatHookRunning = true;
			} catch(Throwable e) {
				System.err.println("Chat hook couldn't be started");
				e.printStackTrace();
			}
		}
	}
	
	private void stopChatHook() {
		if(chatHookRunning) {
			chatHook.setStopping(true);
			try {
				chatHook.killConnection();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private static final class ChatHook extends Thread {
		
		private List<String> recvMsgs;
		private boolean canRead = true;
		
		private boolean ignoreMode = false;
		
		/** If set to true, all lines read after this should be discarded and the thread should stop asap */
		private boolean stopping = false;
		private String chan;
		private String user = null;
		private String pass = "~";
		private byte[] inputBuffer = new byte[1024];
		private int inbp = 0; // Input buffer pos
		private Socket conn;
		
		public ChatHook(String channel) {
			if(channel == null) {
				System.err.println("Invalid channel name, using #lepakko683 >:)");
				chan = "#lepakko683"; // :P
			} else {
				if(!channel.startsWith("#")) {
					System.err.println("Invalid channel name, using #lepakko683 >:)");
					chan = "#lepakko683"; // :P
				} else {
					chan = channel;
				}
			}
			
			setName("BombRush Chat Hook");
			recvMsgs = new LinkedList<String>();
		}
		
		public synchronized void killConnection() throws IOException {
			conn.close();
		}
		
		private void reset() {
			stopping = false;
			recvMsgs.clear();
		}
		
		@Override
		public void run() {
			Random rand = new Random();
			user = "justinfan"+(200000 + rand.nextInt(800000));
			
			InputStream is;
			OutputStream os;
			
			try {
				conn = new Socket("irc.twitch.tv", 6667);
				
				is = conn.getInputStream();
				os = conn.getOutputStream();
				
				sendString(os, "PASS " + pass);
				sendString(os, "NICK " + user);
				
				if(connSuccess(readString(is))) {
					sendString(os, "JOIN " + chan);
					
					String readLine;
					while(!stopping) {
						readLine = readString(is);
						if(readLine != null) {
							System.out.println("recv: " + readLine);
							handleMsg(readLine, os);
						}
					}
				}
				sendString(os, "QUIT quit");
				
				conn.close();
				
			} catch(Throwable e) {
				System.err.println("Chat Hook crashed!");
				e.printStackTrace();
			}
			
			reset();
		}
		
		private void handleMsg(String msg, OutputStream os) throws IOException {
			IRCMsg m = new IRCMsg(msg);
			if("PING".equals(m.command)) {
				sendString(os, IRCMsg.pong(chan, m.getDataAsString()).getAsStringToSend());
			} else if("PRIVMSG".equals(m.command)) {
				String toAdd = m.getDataAsString();
				if(toAdd != null && !ignoreMode) {
					if("jtv".equals(m.getUser())) {
						return;
					}
					canRead = false;
					recvMsgs.add(m.getUser() + ": " + m.getPMMsg());
					canRead = true;
				}
			}
			
			// Ignore everything else >:)
		}
		
		private String readString(InputStream is) throws IOException {
			int readc = -1;
			inbp = 0;
			boolean msgEnds = false;
			while(true) {
				readc = is.read();
				if(readc == -1) {
					System.err.println("Read error!");
					break;
				} else {
					if(msgEnds) {
						if(readc == '\n') {
							return new String(inputBuffer, 0, inbp);
						} else {
							if(!appendToInputBuffer((byte)'\r')) {
								return null;
							}
							if(!appendToInputBuffer((byte)readc)) {
								return null;
							}
						}
					}
					if(readc == '\r') {
						msgEnds = true;
					} else {
						if(!appendToInputBuffer((byte)readc)) {
							return null;
						}
					}
				}
			}
			return null;
			
		}
		
		public synchronized void readMessages(ChatHandler ch) {
			Iterator<String> iter = recvMsgs.iterator();
			while(iter.hasNext()) {
				String line = iter.next();
				System.out.println("Posted: " + line);
				if(line != null) {
					int cmds = line.indexOf(":")+2;
					if(line.charAt(cmds) == Command.COMMAND_PREFIXC) {
						ch.onCommandReceive(line.substring(cmds+1)); // Excluding the &
					}
					
					ch.postLine(line.toUpperCase());
				}
				iter.remove();
			}
		}
		
		public synchronized void setStopping(boolean v) {
			stopping = v;
		}
		
		public synchronized void setIgnoreMode(boolean v) {
			if(ignoreMode != v) {
				ignoreMode = v;
			}
		}
		
		private boolean appendToInputBuffer(byte b) {
			if(inbp < inputBuffer.length) {
				inputBuffer[inbp++] = b;
				return true;
			}
			return false;
		}
		
		private void sendString(OutputStream os, String str) throws IOException {
			os.write(str.getBytes());
			os.write("\r\n".getBytes());
			os.flush();
		}
		
		private boolean connSuccess(String message) {
			IRCMsg repl = new IRCMsg(message);
			if("001".equals(repl.command)) {
				return true;
			}
			return false;
		}
		
	}
	
}
