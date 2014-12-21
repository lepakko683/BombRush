package okkapel.bombrush.util;

import okkapel.bombrush.BombRush;
import okkapel.bombrush.render.ParticleRender;
import okkapel.bombrush.render.TileRender.Tiler;

public class Tile {
	
	public static final float DEFAULT_TILE_WIDTH = 48f;
	
	private static int indx = 0;
	public static Tile[] tiles = new Tile[128];
	
	// Tiles
	public static final Tile empty = new Tile();
	public static final Tile undestrolite = new Tile(1);
	public static final Tile hardstonite = new Tile();
	public static final Tile stone = new Tile();
	
	
	// Tile properties
	private long flags = 0L;
	private float speedModifier = 1f;
	private Rect bounds;
	private Tiler tire;
	private int spriteId = 0;
	
	public Tile() {
		setFlag(Flag.COLLIDABLE, true);
//		bounds = new Rect(0f,0f, );
		tiles[indx++] = this;
	}
	
	public Tile(int spriteId) {
		this();
		this.spriteId = spriteId;
	}

	private void setFlag(Flag flag, boolean value) {
		if(getFlag(flag)) {
			flags &= ~(1 << flag.offset);
		} else {
			flags |= (1 << flag.offset);
		}
	}
	
	public void renderParticles(ParticleRender fxr) {}
	
	public void setupRender(Tiler t) {
		t.addSprite(0, 0, DEFAULT_TILE_WIDTH, DEFAULT_TILE_WIDTH, 1f, 1f, 1f, 1f, 1f, new Sprite(BombRush.getTileTexId(), spriteId, 256, 16));
	}
	
	public boolean getFlag(Flag flag) {
		return (flags & (1 << flag.offset)) != 0;
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
		TEST(1);
		
		public final int offset;
		
		private Flag(int offset) {
			this.offset = offset;
		}
	}
}
