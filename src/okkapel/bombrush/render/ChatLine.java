package okkapel.bombrush.render;

import java.nio.ByteBuffer;

import okkapel.bombrush.BombRush;
import okkapel.bombrush.util.RBE;
import okkapel.bombrush.util.RendStr;

public class ChatLine {
	
	protected RenderSegm rinfo;
	private String txt;
	private int ccount = 0;
	
	protected ChatLine(int first, int count) {
		rinfo = new RenderSegm(first, count, BombRush.getFontTexId());
	}
	
	protected void setText(String txt, ByteBuffer data) {
		RBE rbe = RBE.INSTANCE;
		rbe.attachBuffer(data);
		rbe.setVertexOffset(rinfo.first);
		this.txt = txt;
		int xind, yind;
		char c = 0;
		for(int i=0;i<txt.length();i++) {
			c = txt.charAt(i);
			if(c < 128) {
				xind = ((int)c) % 32;
				yind = (int) ((int)c) / 32;
				rbe.editRect2DUV(xind*RendStr.charWidth, yind*RendStr.charWidth, (xind+1)*RendStr.charWidth, (yind+1)*RendStr.charWidth);
				
				ccount++;
			}
			
			if(ccount >= ChatHandler.CHATLINE_MAX_LENGTH) {
				break;
			}
		}
		rbe.finishEditing();
	}
	
	public String getText() {
		return txt;
	}
	
	public int getVertCount() {
		return Math.min(ccount*6, rinfo.count);
	}
}
