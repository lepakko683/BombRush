package okkapel.bombrush.render;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import okkapel.bombrush.BombRush;
import okkapel.bombrush.util.RBE;
import okkapel.bombrush.util.Render;
import okkapel.bombrush.util.RenderBufferGenerator;
import okkapel.bombrush.util.Sprite;
import okkapel.bombrush.util.Tile;

public class TileRender {
	
	private ByteBuffer renderData;
	
	private int[] tileVertices;
	
	private int largestVC = 0;
	
	private int[] tileVOffs;
	
	public TileRender() {
		
	}
	
	public void init(int worldWidth, int worldHeight) {
		tileVertices = new int[worldWidth * worldHeight];
		tileVOffs = new int[worldWidth * worldHeight];
		for(int i=0;i<tileVertices.length;i++) {
			tileVertices[i] = -1;
		}
	}
	
	public void init() {
		RenderBufferGenerator rbg = RenderBufferGenerator.INSTANCE;
		Tiler tr;
		int first = 0;
		for(int i=0;i<Tile.tiles.length;i++) {
			if(Tile.tiles[i] != null) {
				rbg.startCreatingBuffer();
				
				tr = new Tiler(rbg, first);
				Tile.tiles[i].setupRender(tr);
				Tile.tiles[i].setTiler(tr);
				first = tr.first + tr.count;
				largestVC = Math.max(tr.count, largestVC);
				
				tr.tileRdata = rbg.createArray();
			}
		}
		
	}
	
	public ByteBuffer getRenderData() {
		return renderData;
	}
	
	public void updTileRender(Tile t, int x, int y, int tileIndex, int voffs, RBE rbe) {
		Tiler tt = t.getTiler();
		
		rbe.setVertexOffset(voffs);
		
		for(int i=0;i<largestVC;) {
			if(i < tt.count) {
				rbe.editVertexUAEFPos(
						RBE.getVXFromBytes(i, tt.tileRdata) + x*Tile.DEFAULT_TILE_WIDTH,
						RBE.getVYFromBytes(i, tt.tileRdata) + y*Tile.DEFAULT_TILE_WIDTH,
						1f,
						tt.tileRdata,
						i*RenderBufferGenerator.DEFAULT_GL_STRIDE); // God this line became long
				i++;
			} else {
				rbe.editRect2DXYZ(2048f, 0f, 2048f, 0f, 1f);
				i+=6;
			}
		}
	}
	
	public int getLargestVC() {
		return largestVC;
	}
	
	public void renderTile(Tile t, int x, int y) {
		GL11.glPushMatrix();
		GL11.glTranslatef(x * Tile.DEFAULT_TILE_WIDTH, y * Tile.DEFAULT_TILE_WIDTH, 0f);
		Render.renderVA(renderData, t.getTiler().first, t.getTiler().count, BombRush.getTileTexId());
		GL11.glPopMatrix();
	}
	
	public static class Tiler {
		private RenderBufferGenerator rbg;
		private int first, count;
		private byte[] tileRdata;
		
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
