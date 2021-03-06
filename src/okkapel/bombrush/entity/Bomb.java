package okkapel.bombrush.entity;

import java.nio.ByteBuffer;

import okkapel.bombrush.BombRush;
import okkapel.bombrush.render.Particle;
import okkapel.bombrush.tile.Tile;
import okkapel.bombrush.util.Rect;
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
	private int power = 4;
	
	// TODO: explosion animation
	public Bomb(int fuse) {
		this.maxFuse = fuse;
		this.fuse = fuse;
		RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
		rbg.startCreatingBuffer();
		
		// Base bomb
		rbg.addRect2D(0f, 0f, coll.w, coll.h, 1f, 0f, 0f, 0f, 1f, bomb0.u1, bomb0.v1, bomb0.u2, bomb0.v2);
		// fuse full
		rbg.addRect2D(0f, 0f, coll.w, coll.h, 1f, 1f, 1f, 1f, 1f, fuse1.u1, fuse1.v1, fuse1.u2, fuse1.v2);
		// fuse half
		rbg.addRect2D(0f, 0f, coll.w, coll.h, 1f, 1f, 1f, 1f, 1f, fuse2.u1, fuse2.v1, fuse2.u2, fuse2.v2);
		// spark 1
		rbg.addRect2D(0f, 0f, coll.w, coll.h, 1f, 1f, 1f, 1f, 1f, spark1.u1, spark1.v1, spark1.u2, spark1.v2);
		// spark 2
		rbg.addRect2D(0f, 0f, coll.w, coll.h, 1f, 1f, 1f, 1f, 1f, spark2.u1, spark2.v1, spark2.u2, spark2.v2);
		
		renderData = rbg.createBuffer();
	}

	@Override
	public void render() {
		if(dead) {
			return;
		}
		
		fuse--;
		if(fuse < 1 && !dead) {
			dead = true;
			bombGoBoom();
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
		
		GL11.glPopMatrix();
	}
	
	public void setPower(int newPower) {
		power = newPower;
	}
	
	//BombRush.getFxRender().spawnParticle(Particle.sprFireBall, coll.x, coll.y, 60, Tile.DEFAULT_TILE_WIDTH);
	private void bombGoBoom() { // TODO: damage on collision with explosion
		int ox = (int)(coll.x/Tile.DEFAULT_TILE_WIDTH);
		int oy = (int)(coll.y/Tile.DEFAULT_TILE_WIDTH);
		
		int xmin=ox, xmax=ox+1, ymin=oy, ymax=oy+1;
		
		for(int x=1;x<power+1;x++) {
			if(ox+x < world.getWorldWidth()) {
				if(world.isEmpty(ox+x, oy)) {
					BombRush.getInGameState().fxRender.spawnParticle(Particle.sprFireBall, coll.x + x * Tile.DEFAULT_TILE_WIDTH, coll.y, 60, Tile.DEFAULT_TILE_WIDTH);
					xmax = x+ox;
				} else {
					if(world.isBombable(ox+x,  oy)) {
						world.setTile(ox+x, oy, Tile.empty.id);
						BombRush.getInGameState().fxRender.spawnParticle(Particle.sprFireBall, coll.x + x * Tile.DEFAULT_TILE_WIDTH, coll.y, 60, Tile.DEFAULT_TILE_WIDTH);
						xmax = x+ox;
					}
					break;
				}
			}
		}
		for(int x=-1;x>-power-1;x--) {
			if(ox+x > -1) {
				if(world.isEmpty(ox+x, oy)) {
					BombRush.getInGameState().fxRender.spawnParticle(Particle.sprFireBall, coll.x + x * Tile.DEFAULT_TILE_WIDTH, coll.y, 60, Tile.DEFAULT_TILE_WIDTH);
					xmin = x+ox;
				} else {
					if(world.isBombable(ox+x,  oy)) {
						world.setTile(ox+x, oy, Tile.empty.id);
						BombRush.getInGameState().fxRender.spawnParticle(Particle.sprFireBall, coll.x + x * Tile.DEFAULT_TILE_WIDTH, coll.y, 60, Tile.DEFAULT_TILE_WIDTH);
						xmax = x+ox;
					}
					break;
				}
			}
		}
		for(int y=1;y<power+1;y++) {
			if(oy+y < world.getWorldHeight()) {
				if(world.isEmpty(ox, oy+y)) {
					BombRush.getInGameState().fxRender.spawnParticle(Particle.sprFireBall, coll.x, coll.y + y * Tile.DEFAULT_TILE_WIDTH, 60, Tile.DEFAULT_TILE_WIDTH);
					ymax = y+oy;
				} else {
					if(world.isBombable(ox,  oy+y)) {
						world.setTile(ox, oy+y, Tile.empty.id);
						BombRush.getInGameState().fxRender.spawnParticle(Particle.sprFireBall, coll.x, coll.y + y * Tile.DEFAULT_TILE_WIDTH, 60, Tile.DEFAULT_TILE_WIDTH);
						ymax = y+oy;
					}
					break;
				}
			}
		}
		for(int y=0;y>-power-1;y--) {
			if(oy+y > -1) {
				if(world.isEmpty(ox, oy+y)) {
					BombRush.getInGameState().fxRender.spawnParticle(Particle.sprFireBall, coll.x, coll.y + y * Tile.DEFAULT_TILE_WIDTH, 60, Tile.DEFAULT_TILE_WIDTH);
					ymin = y+oy;
				} else {
					if(world.isBombable(ox,  oy+y)) {
						world.setTile(ox, oy+y, Tile.empty.id);
						BombRush.getInGameState().fxRender.spawnParticle(Particle.sprFireBall, coll.x, coll.y + y * Tile.DEFAULT_TILE_WIDTH, 60, Tile.DEFAULT_TILE_WIDTH);
						ymin = y+oy;
					}
					break;
				}
			}
		}
		
		// Horizontal
		if(Rect.collides(BombRush.getInGameState().thePlayer.coll,
				xmin*Tile.DEFAULT_TILE_WIDTH,
				oy*Tile.DEFAULT_TILE_WIDTH,
				xmax*Tile.DEFAULT_TILE_WIDTH,
				(oy+1)*Tile.DEFAULT_TILE_WIDTH)) {
			BombRush.getInGameState().thePlayer.damage(1);
		}
		
		// Vertical
		if(Rect.collides(BombRush.getInGameState().thePlayer.coll,
				ox*Tile.DEFAULT_TILE_WIDTH,
				ymin*Tile.DEFAULT_TILE_WIDTH,
				(ox+1)*Tile.DEFAULT_TILE_WIDTH,
				ymax*Tile.DEFAULT_TILE_WIDTH)) {
			BombRush.getInGameState().thePlayer.damage(1);
		}
		
	}
	
	private void updateColor() {
		for(int i=0;i<6;i++) {
			renderData.position(i*RenderBufferGenerator.DEFAULT_GL_STRIDE + Render.OFFSET_RED);
			renderData.put((byte)(flicker & 0xFF));
		}
		renderData.position(0);
	}
	
}
