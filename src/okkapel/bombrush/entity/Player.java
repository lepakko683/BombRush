package okkapel.bombrush.entity;

import java.nio.ByteBuffer;

import okkapel.bombrush.util.AnimSprite;
import okkapel.bombrush.util.Render;
import okkapel.bombrush.util.RenderBufferGenerator;
import okkapel.bombrush.util.Renderable;
import okkapel.bombrush.util.Sprite;

import org.lwjgl.opengl.GL11;

public class Player extends EntityMobile implements Renderable {
	
	public static Sprite player;
	
	private ByteBuffer renderData;
	private AnimSprite testa;
	
	// properties
	private int health;
	private int invulTimer = 0;
	
	public Player() {
		RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
		rbg.startCreatingBuffer();
//		rbg.addRect2D(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 0f, 1f, 0f, 1f, 0f, 0f, 1f, 1f);
		rbg.addRect2D(0f, 0f, coll.w, coll.h, 1f, 0f, 1f, 0f, 1f, 0f, 0f, 1f, 1f);
		rbg.addSprite(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 1f, 1f, 1f, 1f, Bomb.spark1);
		rbg.addSprite(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 1f, 1f, 1f, 1f, Bomb.spark2);
		renderData = rbg.createBuffer();
		testa = new AnimSprite(rbg.getSpriteOffsets(), 10);
		respawn();
	}

	@Override
	public void render() {
		if(invulTimer > 0) {
			invulTimer--;
		}
		
		GL11.glPushMatrix();
//		GL11.glTranslatef(coll.x, coll.y, 0f);
		GL11.glTranslatef(720f/2f-coll.w/2f, 720f/2f-coll.h/2f, 0f);
		
		Render.renderVA(renderData, 0, 6, 0);
//		Render.renderVA(renderData, testa.updGetCurr(), 6, Bomb.spark1.texture);
		
		GL11.glPopMatrix();
	}
	
	public int getHealth() {
		return health;
	}
	
	/** Called after player death to reset properties for a new round. It's also called before starting a new game */
	public void respawn() {
		health = 5;
	}
	
	public void damage(int amt) {
		if(invulTimer <= 0) {
			health -= amt;
			invulTimer = 3*60;
		}
	}

}
