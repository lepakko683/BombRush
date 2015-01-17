package okkapel.bombrush.tile;

import okkapel.bombrush.Data;
import okkapel.bombrush.render.TileRender.Tiler;
import okkapel.bombrush.util.Sprite;

public class TileEmpty extends Tile {
	
	public TileEmpty(int sprId) {
		super(sprId);
	}
	
	@Override
	public void setupRender(Tiler t) {
		t.addSprite(0, 0, DEFAULT_TILE_WIDTH, DEFAULT_TILE_WIDTH, 1f, 1f, 1f, 1f, 0f, new Sprite(Data.D.getTileTexId(), getSpriteId(), 256, 16));
	}
}
