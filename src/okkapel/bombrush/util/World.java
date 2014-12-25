package okkapel.bombrush.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import okkapel.bombrush.entity.Entity;
import okkapel.bombrush.render.TileRender;

public abstract class World {
	
	/** In tiles */
	public static final int DEFAULT_WORLD_WIDTH = 16;
	
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
	
	private Rect wbounds;
	private List<Entity> entities;
	
	public Wall[] walls; // TODO: remove
	public short[] tiles;

	
	private World() {
		setupWorld();
		entities = new LinkedList<Entity>();
	}
	
	public Rect getWorldBounds() {
		return wbounds;
	}
	
	public void render(TileRender tir) {
		
		renderWorld(tir);
		
		Iterator<Entity> iter = entities.iterator();
		Entity tr;
		while(iter.hasNext()) {
			tr = iter.next();
			if(tr instanceof Renderable) {
				((Renderable)tr).render();
			}
		}
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
		wbounds = new Rect(0f,0f,DEFAULT_WORLD_WIDTH*Tile.DEFAULT_TILE_WIDTH,DEFAULT_WORLD_WIDTH*Tile.DEFAULT_TILE_WIDTH);
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
	
	protected abstract void setupWorld();
	
}
