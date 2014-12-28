package okkapel.bombrush.entity;

import java.nio.ByteBuffer;

import okkapel.bombrush.util.Render;
import okkapel.bombrush.util.RenderBufferGenerator;
import okkapel.bombrush.util.Renderable;
import okkapel.bombrush.util.Sprite;

import org.lwjgl.opengl.GL11;

public class Bomb extends EntityMobile implements Renderable {
	
	public static Sprite bombE2;
	public static Sprite bombE1;
	public static Sprite bombE0;
	public static Sprite bomb0;
	public static Sprite fuse1;
	public static Sprite fuse2;
	public static Sprite spark1;
	public static Sprite spark2;
	
	private ByteBuffer renderData;
	private short flicker = 0;
	private boolean dir = true;
	private int maxFuse, fuse;
	private int sparkAnim = 0;
	
	// TODO: explosion animation
	public Bomb(int fuse) {
		this.maxFuse = fuse;
		this.fuse = fuse;
		RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
		rbg.startCreatingBuffer();
		
		// Base bomb
		rbg.addRect2D(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 0f, 0f, 0f, 1f, bomb0.u1, bomb0.v1, bomb0.u2, bomb0.v2);
		// fuse full
		rbg.addRect2D(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 1f, 1f, 1f, 1f, fuse1.u1, fuse1.v1, fuse1.u2, fuse1.v2);
		// fuse half
		rbg.addRect2D(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 1f, 1f, 1f, 1f, fuse2.u1, fuse2.v1, fuse2.u2, fuse2.v2);
		// spark 1
		rbg.addRect2D(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 1f, 1f, 1f, 1f, spark1.u1, spark1.v1, spark1.u2, spark1.v2);
		// spark 2
		rbg.addRect2D(coll.w/-2, coll.h/-2, coll.w/2, coll.h/2, 1f, 1f, 1f, 1f, 1f, spark2.u1, spark2.v1, spark2.u2, spark2.v2);
		
		renderData = rbg.createBuffer();
	}

	@Override
	public void render() {
		if(dead) {
			return;
		}
		
		fuse--;
		if(fuse < 59) {
			dead = true;
		}
		
		float fuseLeft = (float)(fuse) / (float)(maxFuse);
		
		if(dir) {
			flicker += (12 + ((1-fuseLeft) * 32));
		}else {
			flicker -= (12 + ((1-fuseLeft) * 32));
		}
		
		if(flicker >= 180) {
			dir = false;
			flicker = 180;
		}
		
		if(flicker <= 0) {
			dir = true;
			flicker = 0;
		}
		
		updateColor();
		
		GL11.glPushMatrix();
		GL11.glTranslatef(coll.x, coll.y, 0f);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		renderVA(renderData, 0, 6, bomb0.texture);
		
		if(fuseLeft > 0.66f) {
			renderVA(renderData, 6, 6, fuse1.texture);
			GL11.glTranslatef(coll.w*0.2f, -coll.h*0.45f, 0f);
		} else if(fuseLeft > 0.33f) {
			renderVA(renderData, 6*2, 6, fuse2.texture);
			GL11.glTranslatef(0f, -coll.h*0.40f, 0f);
		} else {
			GL11.glTranslatef(0f, -coll.h*0.25f, 0f);
		}
		
		if(sparkAnim > 9) {
			sparkAnim = 0;
		}
		
		if(sparkAnim < 5) {
			renderVA(renderData, 6*3, 6, spark1.texture);
		} else {
			renderVA(renderData, 6*4, 6, spark2.texture);
		}
		
		sparkAnim++;
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
	
	private void updateColor() {
		for(int i=0;i<6;i++) {
			renderData.position(i*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_RED);
			renderData.put((byte)(flicker & 0xFF));
		}
		renderData.position(0);
	}
	
}
