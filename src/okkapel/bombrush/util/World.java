package okkapel.bombrush.util;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import okkapel.bombrush.render.TileRender;

public abstract class World {
	
	/** In tiles */
	public static final int DEFAULT_WORLD_WIDTH = 8;
	
	public static final World debugWorld = new World() {
		protected void setupWorld() {
			walls = new Wall[2];
//			walls = new Wall[1];
			walls[0] = new Wall(64f, 64f, 32f, 32f);
			walls[1] = new Wall(64f+32f+48f, 64f, 32f, 32f);
			
//			tiles = new short[1];
			generateRandomWorld(683, true);
		};
	};
	
	public Wall[] walls;
	public short[] tiles;
	
	private World() {
		setupWorld();
	}
	
	public void renderWorld(TileRender tr) {
		for(int i=0;i<tiles.length;i++) {
			tr.renderTile(Tile.getTileFor(tiles[i]), i%DEFAULT_WORLD_WIDTH, (int)(i/DEFAULT_WORLD_WIDTH));
		}
		
		for(int i=0;i<walls.length;i++) {
			walls[i].render();
		}
	}
	
	protected void generateRandomWorld(long seed, boolean useSeed) {
		tiles = new short[DEFAULT_WORLD_WIDTH * DEFAULT_WORLD_WIDTH];
		Random rand = null;
		
		if(useSeed) {
			rand = new Random(seed);
		} else {
			rand = new Random();
		}
		int max = Tile.getTileCount();
		for(int i=0;i<tiles.length;i++) {
			tiles[i] = (short) rand.nextInt(max);
		}
	}
	
	public void handleEntityMovement(EntityMobile e, float dx, float dy) {
		
	}
	
	protected abstract void setupWorld();
	
}
