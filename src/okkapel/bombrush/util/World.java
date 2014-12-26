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
	
	private int worldWidth;
	private int worldHeight;

	
	private World() {
		worldWidth = DEFAULT_WORLD_WIDTH;
		worldHeight = DEFAULT_WORLD_WIDTH;
		setupWorld();
		entities = new LinkedList<Entity>();
	}
	
	public int getWorldWidth() {
		return worldWidth;
	}
	
	public int getWorldHeight() {
		return worldHeight;
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
			tr.update();
			
			if(tr instanceof Renderable) {
				((Renderable)tr).render();
			}
		}
	}
	
	public void spawnEntity(Entity e) {
		e.setWorldRef(this);
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
		worldWidth = DEFAULT_WORLD_WIDTH;
		worldHeight = DEFAULT_WORLD_WIDTH;
		
		tiles = new short[worldWidth * worldHeight];
		wbounds = new Rect(0f,0f,worldWidth*Tile.DEFAULT_TILE_WIDTH,worldHeight*Tile.DEFAULT_TILE_WIDTH);
		Random rand = null;
		
		if(useSeed) {
			rand = new Random(32234512);
		} else {
			rand = new Random();
		}
		
//		int max = Tile.getTileCount();
		int max = 2;
		for(int i=0;i<tiles.length;i++) {
			tiles[i] = (short) rand.nextInt(max);
		}
		tiles[7*worldWidth+4] = (short)Tile.empty.id;
	}
	
	protected abstract void setupWorld();
	
}
