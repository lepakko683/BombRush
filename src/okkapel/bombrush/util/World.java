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
	public static final int DEFAULT_WORLD_WIDTH = 31;
	
//	/** In tiles */
//	public static final int DEFAULT_WORLD_HEIGHT = 31; TODO
	
	public static final World debugWorld = new World() {
		protected void setupWorld() {
//			tiles = new short[1];
			generateRandomWorld(683, true);
		};
	};
	
	private Rect wbounds;
	private List<Entity> entities;
	
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
	}
	
	protected void generateRandomWorld(long seed, boolean useSeed) {
		worldWidth = DEFAULT_WORLD_WIDTH;
		worldHeight = DEFAULT_WORLD_WIDTH;
		
		tiles = new short[worldWidth * worldHeight];
		wbounds = new Rect(0f, 0f, worldWidth * Tile.DEFAULT_TILE_WIDTH, worldHeight * Tile.DEFAULT_TILE_WIDTH);
		Random rand = null;
		
		if(useSeed) {
			rand = new Random(32234512);
		} else {
			rand = new Random();
		}
		
//		int max = Tile.getTileCount();
		int max = 2;
		
		for(int y=0;y<worldHeight;y++) {
			for(int x=0;x<worldWidth;x++) {
				if(y != 0 && x != 0 && y != (worldHeight-1) && x != (worldWidth-1)) {
					if(x % 2 == 1 && y % 2 == 1) {
						tiles[y*worldWidth+x] = (short)Tile.undestrolite.id;
					}
				}
			}
		}
		
		clearPlayerSpawn(0);
		clearPlayerSpawn(1);
		clearPlayerSpawn(2);
		clearPlayerSpawn(3);
		
		
//		tiles[7*worldWidth+4] = (short)Tile.empty.id;
	}
	
	/** 0=top-left, 1=top-right, 2=bottom-right, 3=bottom-left */
	protected void clearPlayerSpawn(int corner) {
		switch(corner%4) {
		case 0:
			tiles[0]=(short)Tile.empty.id;
			tiles[1]=(short)Tile.empty.id;
			tiles[worldWidth]=(short)Tile.empty.id;
			break;
		case 1:
			tiles[worldWidth-2]=(short)Tile.empty.id;
			tiles[worldWidth-1]=(short)Tile.empty.id;
			tiles[worldWidth*2-1]=(short)Tile.empty.id;
			break;
		case 2:
			tiles[worldWidth*worldHeight-worldWidth-1]=(short)Tile.empty.id;
			tiles[worldWidth*worldHeight-2]=(short)Tile.empty.id;
			tiles[worldWidth*worldHeight-1]=(short)Tile.empty.id;
			break;
		case 3:
			tiles[worldWidth*worldHeight-worldWidth-worldWidth]=(short)Tile.empty.id;
			tiles[worldWidth*worldHeight-worldWidth]=(short)Tile.empty.id;
			tiles[worldWidth*worldHeight-worldWidth+1]=(short)Tile.empty.id;
			break;
		}
	}
	
	protected abstract void setupWorld();
	
}
