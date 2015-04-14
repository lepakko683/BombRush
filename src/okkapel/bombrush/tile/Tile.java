package okkapel.bombrush.tile;

import okkapel.bombrush.BombRush;
import okkapel.bombrush.Data;
import okkapel.bombrush.render.ParticleRender;
import okkapel.bombrush.render.TileRender.Tiler;
import okkapel.bombrush.util.Rect;
import okkapel.bombrush.util.Sprite;

public class Tile {
	
	public static final float DEFAULT_TILE_WIDTH = 48f;
	
	private static int indx = 0;
	public static Tile[] tiles = new Tile[128];
	
	// Tiles
	public static final Tile empty = new TileEmpty(0);
	public static final Tile undestrolite = new Tile(1) {
		public void init() {
			setFlag(Flag.COLLIDABLE, true);
			setFlag(Flag.INDESTRUCTIBLE, true);
		}
	};
	public static final Tile stone = new Tile(2) {
		public void init() {
			setFlag(Flag.COLLIDABLE, true);
			setFlag(Flag.MINEABLE, true);
		}
	};
	public static final Tile hardstonite = new Tile();
	
	
	// Tile properties
	public final int id;
	private long flags = 0L;
	private float speedModifier = 1f;
	private Rect bounds;
	private Tiler tire;
	private int spriteId = 0;
	
	public Tile() {
		id = indx;
		if(indx != 0) { 
			init();
		}
//		bounds = new Rect(0f,0f, );
		tiles[indx++] = this;
	}
	
	public Tile(int spriteId) {
		this();
		this.spriteId = spriteId;
	}
	
	public int getSpriteId() {
		return spriteId;
	}
	
	/** Meant for setting custom tile properties easily */
	public void init() {}

	protected void setFlag(Flag flag, boolean value) {
		if(getFlag(flag) && !value) {
			flags &= ~(1 << flag.offset);
		} else if(!getFlag(flag) && value) {
			flags |= (1 << flag.offset);
		}
	}
	
	public void renderParticles(ParticleRender fxr) {}
	
	public void setupRender(Tiler t) {
		t.addSprite(0, 0, DEFAULT_TILE_WIDTH, DEFAULT_TILE_WIDTH, 1f, 1f, 1f, 1f, 1f, new Sprite(Data.D.getTileTexId(), spriteId, 256, 16));
	}
	
	public boolean getFlag(Flag flag) {
		return (flags & (1 << flag.offset)) != 0;
	}
	
	public boolean isBombable() {
		return getFlag(Flag.INDESTRUCTIBLE) ? false : !getFlag(Flag.BOMBPROOF);
	}
	
	public final void setTiler(Tiler t) {
		this.tire = t;
	}
	
	public final Tiler getTiler() {
		return tire;
	}
	
	/** @return air if no such tile was found or index out of bounds*/
	public static Tile getTileFor(int i) {
		if(i-1 > tiles.length) {
			return tiles[0];
		}
		if(tiles[i] == null) {
			return tiles[0];
		}
		return tiles[i];
		
	}
	
	public static int getTileCount() {
		return indx;
	}
	
	public static enum Flag {
		COLLIDABLE(0),
		TEST(1),
		BOMBPROOF(2),
		INDESTRUCTIBLE(3),
		MINEABLE(4);
		
		public final int offset;
		
		private Flag(int offset) {
			this.offset = offset;
		}
	}
}
