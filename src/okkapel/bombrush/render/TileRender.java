package okkapel.bombrush.render;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import okkapel.bombrush.BombRush;
import okkapel.bombrush.util.Render;
import okkapel.bombrush.util.RenderBufferGenerator;
import okkapel.bombrush.util.Sprite;
import okkapel.bombrush.util.Tile;

public class TileRender {
	
	private ByteBuffer renderData;
	
	public TileRender() {
		
	}
	
	public void init() {
		RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
		rbg.startCreatingBuffer();
		Tiler tr;
		int lcount = 0;
		for(int i=0;i<Tile.tiles.length;i++) {
			if(Tile.tiles[i] != null) {
				tr = new Tiler(rbg, lcount);
				Tile.tiles[i].setupRender(tr);
				Tile.tiles[i].setTiler(tr);
				lcount = tr.first + tr.count;
			}
		}
		renderData = rbg.createBuffer();
	}
	
	public void renderTile(Tile t, int x, int y) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x*Tile.DEFAULT_TILE_WIDTH, y*Tile.DEFAULT_TILE_WIDTH, 0f);
		Render.renderVA(renderData, t.getTiler().first, t.getTiler().count, BombRush.getTileTexId());
		GL11.glPopMatrix();
	}
	
	public static class Tiler {
		private RenderBufferGenerator rbg;
		private int first, count;
		
		private Tiler(RenderBufferGenerator rbg, int first) {
			this.rbg = rbg;
			this.first = first;
		}
		
		public void addSprite(float x1, float y1, float x2, float y2, float z, float r, float g, float b, float a, Sprite spr) {
			rbg.addSprite(x1, y1, x2, y2, z, r, g, b, a, spr);
			count += 6;
		}
		
	}
}
