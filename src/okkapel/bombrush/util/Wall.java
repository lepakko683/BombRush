package okkapel.bombrush.util;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class Wall extends Tile implements Renderable {

	public Rect coll;
	private ByteBuffer renderData;
	
	public Wall(float x, float y, float w, float h) {
		coll = new Rect(x, y, w, h);
		RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
		rbg.startCreatingBuffer();
		rbg.addRect2D(0f, 0f, w, h, 1f, .9f, .3f, .3f, 1f, 0f, 0f, 1f, 1f);
		renderData = rbg.createBuffer();
	}
	
	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(coll.x, coll.y, 0f);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Render.renderVA(renderData, 0, 6, 0);
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

}
