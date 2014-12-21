package okkapel.bombrush.util;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import celestibytes.lib.Tuple;
import okkapel.bombrush.BombRush;

public class RendStr implements Renderable {
	
	public static final float charWidth = 1f/32f;
	public static final Sprite whitespace = new Sprite(0, 20, 256, 8);
	
	private String string;
	private ByteBuffer renderData;
	private int charCount = 0;
	private int rendChrCount = 0;
	private float size;
	public float x, y;
	
	
	public RendStr(String str, float size, float x, float y) {
		RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
		rbg.startCreatingBuffer();
		string = str;
		this.size = size;
		this.x = x;
		this.y = y;
		
		char c;
		int xind=0, yind=0;
		
		float offset = 0f;
		for(int i=0;i<str.length();i++) {
			c = str.charAt(i);
			if(c < 128) {
				xind = ((int)c) % 32;
				yind = (int) ((int)c) / 32;
				rbg.addRect2D(offset, 0, offset+size, size, 0f, 1f, 1f, 1f, 1f, xind*charWidth, yind*charWidth, (xind+1)*charWidth, (yind+1)*charWidth);
				offset += size;
				offset += size/8f;
				charCount++;
				rendChrCount++;
			}
		}
		renderData = rbg.createBuffer();
	}
	
	/** Create empty string */
	public RendStr(int length, float size, float x, float y) {
		RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
		rbg.startCreatingBuffer();
		string = "";
		this.size = size;
		this.x = x;
		this.y = y;
		
		int xind = 0x20 % 32, yind = (int) (0x20 / 32);
		
		float offset = 0f;
		for(int i=0;i<length;i++) {
			rbg.addRect2D(offset, 0, offset+size, size, 0f, 1f, 1f, 1f, 1f, xind*charWidth, yind*charWidth, (xind+1)*charWidth, (yind+1)*charWidth);
			offset += size;
			offset += size/8f;
			charCount++;
			rendChrCount++;
		}
		renderData = rbg.createBuffer();
	}
	
	public void setString(String str, float size) {
		if(charCount < str.length()) {
			charCount = 0;
			RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
			rbg.startCreatingBuffer();
			string = str;
			this.size = size;
			char c;
			int xind=0, yind=0;
			float offset = 0f;
			for(int i=0;i<str.length();i++) {
				c = str.charAt(i);
				if(c < 128) {
					xind = ((int)c) % 32;
					yind = (int) ((int)c) / 32;
					rbg.addRect2D(offset, 0, offset+size, size, 0f, 1f, 1f, 1f, 1f, xind*charWidth, yind*charWidth, (xind+1)*charWidth, (yind+1)*charWidth);
					offset += size;
					offset += size/8f;
					charCount++;
					rendChrCount++;
				}
			}
			renderData = rbg.createBuffer();
		} else {
			rendChrCount = 0;
			if(size != this.size) {
				this.size = size;
				char c;
				int xind, yind;
				byte[] buffer = new byte[4];
				float offset = 0f;
				for(int i=0;i<str.length();i++) {
					c = str.charAt(i);
					if(c < 128) {
						xind = ((int)c) % 32;
						yind = (int) ((int)c) / 32;
						
						// Triangle A
							// Vertex 1
								// u1
								Render.floatToBytes(xind*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
								renderData.put(buffer);
								
								// v1
								Render.floatToBytes(yind*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
								renderData.put(buffer);
								
								// x1
								Render.floatToBytes(offset, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_X);
								renderData.put(buffer);
								
								// y1
								Render.floatToBytes(0f, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_Y);
								renderData.put(buffer);
								
							// Vertex 2
								// u1
								Render.floatToBytes(xind*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
								renderData.put(buffer);
								
								// v2
								Render.floatToBytes((yind+1)*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
								renderData.put(buffer);
								
								// x1
								Render.floatToBytes(offset, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_X);
								renderData.put(buffer);
								
								// y2
								Render.floatToBytes(size, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_Y);
								renderData.put(buffer);
							
							// Vertex 3
								// u2
								Render.floatToBytes((xind+1)*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 2*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
								renderData.put(buffer);
								
								// v2
								Render.floatToBytes((yind+1)*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 2*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
								renderData.put(buffer);
								
								// x2
								Render.floatToBytes(offset+size, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 2*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_X);
								renderData.put(buffer);
								
								// y2
								Render.floatToBytes(size, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 2*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_Y);
								renderData.put(buffer);
						
						// Triangle B
							// Vertex 1
								// u2
								Render.floatToBytes((xind+1)*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 3*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
								renderData.put(buffer);
								
								// v2
								Render.floatToBytes((yind+1)*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 3*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
								renderData.put(buffer);
								
								// x2
								Render.floatToBytes(offset+size, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 3*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_X);
								renderData.put(buffer);
								
								// y2
								Render.floatToBytes(size, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 3*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_Y);
								renderData.put(buffer);
								
							// Vertex 2
								// u2
								Render.floatToBytes((xind+1)*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 4*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
								renderData.put(buffer);
								
								// v1
								Render.floatToBytes(yind*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 4*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
								renderData.put(buffer);
								
								// x2
								Render.floatToBytes(offset+size, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 4*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_X);
								renderData.put(buffer);
								
								// y1
								Render.floatToBytes(0f, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 4*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_Y);
								renderData.put(buffer);
								
							// Vertex 3
								// u1
								Render.floatToBytes(xind*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 5*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
								renderData.put(buffer);
								
								// v1
								Render.floatToBytes(yind*charWidth, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 5*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
								renderData.put(buffer);
								
								// x1
								Render.floatToBytes(offset, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 5*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_X);
								renderData.put(buffer);
								
								// y1
								Render.floatToBytes(0f, buffer);
								renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 5*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_Y);
								renderData.put(buffer);
								
						offset += size;
						offset += size/8f;
						rendChrCount++;
					}
					
				}
			} else {
				char c;
				int xind, yind;
				byte[] buffer = new byte[4];
				for(int i=0;i<str.length();i++) {
					c = str.charAt(i);
					if(c < 128) {
						xind = ((int)c) % 32;
						yind = (int) ((int)c) / 32;
						
						// Triangle A
						// u1, v1
						Render.floatToBytes(xind*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
						renderData.put(buffer);
						
						Render.floatToBytes(yind*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
						renderData.put(buffer);
						
						
						// u1, v2
						Render.floatToBytes(xind*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
						renderData.put(buffer);
						
						Render.floatToBytes((yind+1)*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
						renderData.put(buffer);
						
						
						// u2, v2
						Render.floatToBytes((xind+1)*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 2*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
						renderData.put(buffer);
						
						Render.floatToBytes((yind+1)*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 2*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
						renderData.put(buffer);
						
						
						// Triangle B
						// u2, v2
						Render.floatToBytes((xind+1)*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 3*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
						renderData.put(buffer);
						
						Render.floatToBytes((yind+1)*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 3*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
						renderData.put(buffer);
						
						
						// u2, v1
						Render.floatToBytes((xind+1)*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 4*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
						renderData.put(buffer);
						
						Render.floatToBytes(yind*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 4*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
						renderData.put(buffer);
						
						
						// u1, v1
						Render.floatToBytes(xind*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 5*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_U);
						renderData.put(buffer);
						
						Render.floatToBytes(yind*charWidth, buffer);
						renderData.position(i*6*RenderBufferGenerator.DEFAULT_GL_STRIDE + 5*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_V);
						renderData.put(buffer);
						
						rendChrCount++;
					}
				}
			}
		}
		renderData.position(0);
	}
	
	public void setString(String str) {
		setString(str, size);
	}
	
	public String getString() {
		return string;
	}
	
	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPosX(float x) {
		this.x = x;
	}
	
	public void setPosY(float y) {
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		
		GL11.glTranslatef(x, y, 0f);
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Render.renderVA(renderData, 0, rendChrCount*6, BombRush.getFontTexId());
		
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glPopMatrix();
	}
}
