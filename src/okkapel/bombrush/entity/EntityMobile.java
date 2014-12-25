package okkapel.bombrush.entity;

import okkapel.bombrush.BombRush;
import okkapel.bombrush.util.MathHelper;
import okkapel.bombrush.util.Rect;
import okkapel.bombrush.util.Wall;
import okkapel.bombrush.util.World;

public class EntityMobile extends Entity {
	protected float dx = 0f, dy = 0f;
	
	public EntityMobile() {
		
	}
	
	public void update() {
		move();
	}
	
	private float ndx = 0f, ndy = 0f;
	private void move() {
		for(Wall w : BombRush.currWorld.walls) {
			if(coll.xdistTo(w.coll) < Math.abs(dx)) {
				if(dy < 0f) {
					if(coll.y + dy < w.coll.y + w.coll.h && coll.y + coll.h > w.coll.y) {
						ndy =  w.coll.y + w.coll.h - coll.y;
//						coll.y = w.coll.y + w.coll.h;
					} else {
//						coll.y += dy;
						ndy = dy;
					}
				}
				if(dy > 0f) {
					if(coll.y + coll.h + dy > w.coll.y && coll.y < w.coll.y + w.coll.h) {
						ndy = coll.y + coll.h - w.coll.y;
//						coll.y = w.coll.y - coll.h;
					} else {
//						coll.y += dy;
						ndy = dy;
					}
				}
				dy = MathHelper.signlessMin(dy, ndy);
			}
			if(coll.ydistTo(w.coll) < Math.abs(dy)) {
				if(dx < 0f) {
					if(coll.x + dx < w.coll.x + w.coll.w && coll.x + coll.w > w.coll.x) {
						ndx = w.coll.x + w.coll.w - coll.x;
//						coll.x = w.coll.x + w.coll.w;
					} else {
//						coll.x += dx;
						ndx = dx;
					}
				}
				if(dx > 0f) {
					if(coll.x + coll.w + dx > w.coll.x && coll.x < w.coll.x + w.coll.w) {
						ndy = coll.x + coll.w - w.coll.x;
//						coll.x = w.coll.x - coll.w;
					} else {
//						coll.x += dx;
						ndx = dx;
					}
				}
				dx = MathHelper.signlessMin(dx, ndx);
			}
			ndx = 0f;
			ndy = 0f;
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
