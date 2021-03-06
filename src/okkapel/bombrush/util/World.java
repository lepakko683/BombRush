package okkapel.bombrush.util;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import okkapel.bombrush.BombRush;
import okkapel.bombrush.Data;
import okkapel.bombrush.entity.Entity;
import okkapel.bombrush.entity.Player;
import okkapel.bombrush.render.ParticleRender;
import okkapel.bombrush.render.TileRender;
import okkapel.bombrush.tile.Tile;

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
	private Entity playerRef;
	
	public short[] tiles;
	
	private int worldWidth;
	private int worldHeight;
	
	private int renderChangeOrigin = 0;
	private int renderChangeCap = -1;
	
	private ByteBuffer worldRenderData;
	private ByteBuffer backgroundData;
	
	private Sprite bgTex;
	
	private World() {
		worldWidth = DEFAULT_WORLD_WIDTH;
		worldHeight = DEFAULT_WORLD_WIDTH;
		setupWorld();
		entities = new LinkedList<Entity>();
		renderChangeCap = worldWidth*worldHeight;
	}
	
	protected void setBGTex(Sprite tex) {
		this.bgTex = tex;
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
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public void setupBackground() {
		if(backgroundData != null) {
			return;
		}
		bgTex = new Sprite(Data.D.getTileTexId(), 0, 256, 16);
		RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
		rbg.startCreatingBuffer();
		rbg.setAddSprOffs(false);
		for(int y=0;y<worldHeight;y++) {
			for(int x=0;x<worldWidth;x++) {
				rbg.addSprite(x*Tile.DEFAULT_TILE_WIDTH, y*Tile.DEFAULT_TILE_WIDTH, (x+1)*Tile.DEFAULT_TILE_WIDTH, (y+1)*Tile.DEFAULT_TILE_WIDTH, 1f, 1f, 1f, 1f, 1f, bgTex);	
			}
		}
		backgroundData = rbg.createBuffer();
	}
	
	public void render(TileRender tir) {
		Render.renderVA(backgroundData, 0, tiles.length*6, Data.D.getTileTexId());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		altRenderWorld(tir);
		
		renderEntities();
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private void renderEntities() {
		Iterator<Entity> iter = entities.iterator();
		Entity tr;
		while(iter.hasNext()) {
			tr = iter.next();
			tr.update();
			if(tr.isDead()) {
				iter.remove();
			} else {
				if(tr instanceof Renderable && tr != playerRef) {
					((Renderable)tr).render();
				}
			}
		}
	}
	
	public void setTile(int x, int y, int tile) {
		if(x < worldWidth && y < worldHeight) {
			tiles[y*worldWidth+x] = (short)tile;
			renderChangeOrigin = Math.min(y*worldWidth+x, renderChangeOrigin);
			renderChangeCap = Math.max(y*worldWidth+x, renderChangeCap)+1;
		}
	}
	
	public int getTile(int x, int y) {
		if(x < worldWidth && y < worldHeight) {
			return (int)tiles[y*worldWidth+x];
		}
		return -1;
	}
	
	public boolean isBombable(int x, int y) {
		int tile = getTile(x,y);
		return tile == -1 ? false : Tile.getTileFor(tile).isBombable();
	}
	
	public boolean isEmpty(int x, int y) {
		return tiles[y*worldWidth+x] == Tile.empty.id;
	}
	
	public void spawnEntity(Entity e) {
		if(e instanceof Player) {
			if(playerRef != null) {
				System.err.println("Can't add two players to a world!");
			} else {
				playerRef = e;
			}
		}
		e.setWorldRef(this);
		entities.add(e);
	}
	
	public void reset() {
		// TODO: implement
		System.err.println("Called an unimplemented method: World.reset()!");
	}
	
	public void altRenderWorld(TileRender tr) {
		if(worldRenderData == null) {
			worldRenderData = BufferUtils.createByteBuffer(tr.getLargestVC()*RenderBufferGenerator.DEFAULT_GL_STRIDE*tiles.length);
		}
		
		if(renderChangeOrigin < tiles.length) {
			RBE rbe = RBE.INSTANCE;
			rbe.attachBuffer(worldRenderData);
			int voffs = 0;
			for(int i=renderChangeOrigin;i<renderChangeCap;i++) {
				voffs = tr.getLargestVC() * i;
				tr.updTileRender(Tile.getTileFor(tiles[i]), i%worldWidth, (int)(i/worldWidth), i, voffs, rbe);
			}
			rbe.finishEditing();
			renderChangeOrigin=tiles.length;
			renderChangeCap = 0;
		}
		
		Render.renderVA(worldRenderData, 0, tr.getLargestVC()*tiles.length, Data.D.getTileTexId());
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
		
		for(int i=0;i<tiles.length;i++) {
			if(0 < rand.nextInt(10)) {
				tiles[i] = (short)Tile.stone.id;
			}
		}
		
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
