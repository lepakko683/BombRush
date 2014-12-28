package okkapel.bombrush.entity;

import java.nio.ByteBuffer;

import okkapel.bombrush.util.Pos;
import okkapel.bombrush.util.Rect;
import okkapel.bombrush.util.Tile;
import okkapel.bombrush.util.World;

import org.lwjgl.opengl.GL11;

public class Entity implements Pos {
	
	protected World world;
	
	protected Rect coll;
	protected boolean dead = false;
	
	public Entity() {
		coll = new Rect(0,0,32,32);
	}
	
	public Rect getColl() {
		return coll;
	}
	
	public void setWorldGridPos(int x, int y) {
		coll.x = Tile.DEFAULT_TILE_WIDTH * x;
		coll.y = Tile.DEFAULT_TILE_WIDTH * y;
	}

	@Override
	public float getX() {
		return coll.x;
	}

	@Override
	public float getY() {
		return coll.y;
	}
	
	public void setWorldRef(World w) {
		world = w;
	}
	
	public World getWorldRef() {
		return world;
	}
	
	public void update() {}
	
	public boolean isDead() {
		return dead;
	}
	
	
	@Deprecated
	public void renderVA(ByteBuffer data, int first, int count, int texture) {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		if(texture != 0) {
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		}
		
		GL11.glInterleavedArrays(GL11.GL_T2F_C4UB_V3F, 5*4+4, data);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, first, count);
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		if(texture != 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}
		
	}

}
