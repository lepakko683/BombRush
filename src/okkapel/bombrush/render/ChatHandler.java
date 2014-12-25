package okkapel.bombrush.render;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import celestibytes.lib.util.PileList;
import okkapel.bombrush.command.Command;
import okkapel.bombrush.util.RBE;
import okkapel.bombrush.util.RendStr;
import okkapel.bombrush.util.Render;
import okkapel.bombrush.util.RenderBufferGenerator;

public class ChatHandler {
	
	public static final int CHATLINE_MAX_LENGTH = 70;
	public static final int CHATLINE_MAX_COUNT = 10;
	
	private List<Command> cmdHandlers;
	
	private PileList<ChatLine> chatDisplay;
	
	private ChatLine inputLine;
	
	private boolean shouldChatHookRun = false;
	
	private ByteBuffer renderData;
	
	private float fontSize = 16f;
	
	public ChatHandler() {
		cmdHandlers = new ArrayList<Command>();
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
		if(!chatDisplay.isFull()) {
			chatDisplay.addItem(new ChatLine(firstv, CHATLINE_MAX_LENGTH));
			chatDisplay.getLast().setText(text, renderData);
			firstv += CHATLINE_MAX_LENGTH*6;
		} else {
			ChatLine cl = chatDisplay.popFirst();
			cl.setText(text, renderData);
			chatDisplay.addItem(cl);
//			System.out.println("cl text: " + chatDisplay.getLast().getText());
		}
	}
	
	private void setupFont() {
		RBE rbe = RBE.INSTANCE;
		rbe.attachBuffer(renderData);
		rbe.setVertexOffset(6);
		float offset;
		for(int q=0;q<CHATLINE_MAX_COUNT+1;q++) {
			offset = 0f;
			for(int i=0;i<CHATLINE_MAX_LENGTH;i++) {
	//			rbe.editRect2DXYZ(offset + fontSize*i, 0f, offset + (fontSize+1)*i, fontSize, 1f);
				rbe.editRect2D(offset, 0f, offset+fontSize, fontSize, 1f, 1f, 0f, 0f, 1f, RendStr.whitespace.u1, RendStr.whitespace.v1, RendStr.whitespace.u2, RendStr.whitespace.v2);
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
		
		if(slower < 1) {
			postLine("HELLO " + countr);
//			System.out.println(chatDisplay.getLast().getText());
			slower = 10;
			countr++;
		}
		slower--;
		
		if(chatDisplay.getLast() != null) {
			float yoffs = 720f-fontSize*2;
			Iterator<ChatLine> iter = chatDisplay.getItemIterator(true);
			do {
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
//		System.out.println("first: " + c.rinfo.first + " count: " + c.getVertCount());
		
		GL11.glPopMatrix();
	}
	
	public void onMessageReceive(String msgText) {
		if(!msgText.startsWith("&")) {
			return; // TODO: currently filter the in-game chat to commands only, might have an option for this in future
		}
		
	}
	
	private static class ChatHook extends Thread implements Runnable {
		
		@Override
		public void run() {
//			this.
		}
	}
	
}
