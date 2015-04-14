package okkapel.bombrush;

public final class Data {
	private Data() {}
	public static final Data D = new Data();
	
	// TEXTURES
	
	private int TEX_SPRITES = 0;
	private int TEX_TILES = 0;
	private int TEX_PARTICLES = 0;
	private int TEX_FONT = 0;
	
	protected void setupTextures(int sprites, int tiles, int particles, int font) {
		TEX_SPRITES = sprites;
		TEX_TILES = tiles;
		TEX_PARTICLES = particles;
		TEX_FONT = font;
	}
	
	public int getFontTexId() {
		return TEX_FONT;
	}
	
	public int getSpriteTexId() {
		return TEX_SPRITES;
	}
	
	public int getTileTexId() {
		return TEX_TILES;
	}
	
	public int getPartTexId() {
		return TEX_PARTICLES;
	}
	
}
