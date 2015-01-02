package okkapel.bombrush.entity;

import okkapel.bombrush.BombRush;
import okkapel.bombrush.util.MathHelper;
import okkapel.bombrush.util.Rect;
import okkapel.bombrush.util.Tile;
import okkapel.bombrush.util.Tile.Flag;
import okkapel.bombrush.util.Wall;
import okkapel.bombrush.util.World;

public class EntityMobile extends Entity {
	protected float dx = 0f, dy = 0f;
	
	public EntityMobile() {
		
	}
	
	@Override
	public void update() {
		if(dx != 0f || dy != 0f) {
			move();
		}
		
	}
	
	private float ndx = 0f, ndy = 0f;
	@SuppressWarnings("deprecation")
	private void move() { // TODO: fix bug with getting stuck while holding against a wall in x-direction and moving up
		Wall w = new Wall();
		w.coll.w = Tile.DEFAULT_TILE_WIDTH;
		w.coll.h = Tile.DEFAULT_TILE_WIDTH;
		short id = (short) -1;
		int worldWidth = BombRush.currWorld.getWorldWidth();
		int worldHeight = BombRush.currWorld.getWorldHeight();
		for(int i=0;i<BombRush.currWorld.tiles.length;i++) {
			id = BombRush.currWorld.tiles[i];
			if(Tile.tiles[id].getFlag(Flag.COLLIDABLE)) {
				ndx = 0f;
				ndy = 0f;
				w.coll.x = (i % worldWidth) * Tile.DEFAULT_TILE_WIDTH;
				w.coll.y = (int)(i / worldHeight) * Tile.DEFAULT_TILE_WIDTH;
				if(coll.xdistTo(w.coll) < Math.abs(dx)) {
					if(dy < 0f) {
						if(coll.y + dy < w.coll.y + w.coll.h && coll.y + coll.h > w.coll.y) {
							ndy =  w.coll.y + w.coll.h - coll.y;
						} else {
							ndy = dy;
						}
					}
					if(dy > 0f) {
						if(coll.y + coll.h + dy > w.coll.y && coll.y < w.coll.y + w.coll.h) {
							ndy = coll.y + coll.h - w.coll.y;
						} else {
							ndy = dy;
						}
					}
					dy = MathHelper.signlessMin(dy, ndy);
				}
				if(coll.ydistTo(w.coll) < Math.abs(dy)) {
					if(dx < 0f) {
						if(coll.x + dx < w.coll.x + w.coll.w && coll.x + coll.w > w.coll.x) {
							ndx = w.coll.x + w.coll.w - coll.x;
						} else {
							ndx = dx;
						}
					}
					if(dx > 0f) {
						if(coll.x + coll.w + dx > w.coll.x && coll.x < w.coll.x + w.coll.w) {
							ndy = coll.x + coll.w - w.coll.x;
						} else {
							ndx = dx;
						}
					}
					dx = MathHelper.signlessMin(dx, ndx);
				}
			}
		}
		
		Rect wbo = BombRush.currWorld.getWorldBounds();
		if(dx < 0f) {
			if(coll.x+dx < wbo.x && coll.x >= wbo.x) {
				dx = coll.x-wbo.x;
			}
		}
		if(dx > 0f) {
			if(coll.x+dx+coll.h > wbo.x+wbo.w && coll.x <= wbo.x+wbo.w) {
				dx = (wbo.x+wbo.w)-(coll.x+coll.h);
			}
		}
		
		if(dy < 0f) {
			if(coll.y+dy < wbo.y && coll.y >= wbo.y) {
				dy = coll.y-wbo.y;
			}
		}
		if(dy > 0f) {
			if(coll.y+dy+coll.h > wbo.y+wbo.h && coll.y <= wbo.y+wbo.h) {
				dy = (wbo.y+wbo.h)-(coll.y+coll.h);
			}
		}
		
		coll.x += dx;
		coll.y += dy;
		dx = 0;
		dy = 0;
	}
	
	public void setMovement(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void addMovement(float dx, float dy) {
		this.dx += dx;
		this.dy += dy;
	}
	
}
