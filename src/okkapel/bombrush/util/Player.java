package okkapel.bombrush.util;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class Player extends EntityMobile implements Renderable {
	
	public static Sprite player;
	
	private ByteBuffer renderData;
	private AnimSprite testa;
	
	public Player() {
		RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
		rbg.startCreatingBuffer();
//		rbg.addRect2D(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 0f, 1f, 0f, 1f, 0f, 0f, 1f, 1f);
		rbg.addRect2D(0f, 0f, coll.w, coll.h, 1f, 0f, 1f, 0f, 1f, 0f, 0f, 1f, 1f);
		rbg.addSprite(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 1f, 1f, 1f, 1f, Bomb.spark1);
		rbg.addSprite(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 1f, 1f, 1f, 1f, Bomb.spark2);
		renderData = rbg.createBuffer();
		testa = new AnimSprite(rbg.getSpriteOffsets(), 10);
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(coll.x, coll.y, 0f);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Render.renderVA(renderData, 0, 6, 0);
//		Render.renderVA(renderData, testa.updGetCurr(), 6, Bomb.spark1.texture);
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

}
