package okkapel.bombrush.util;

public class Rect {
	public float x, y, w, h;
	
	public Rect(float x, float y, float w, float h) {
		this.x=x;
		this.y=y;
		this.w=w;
		this.h=h;
	}
	
	public boolean collides(Rect o) {
		if(o.x+o.w >= x && o.x < x+w) {
			if(o.y+o.h >= y && o.y < y+h) {
				return true;
			}
		}
		return false;
	}
	
	public float xdistTo(Rect o) {
		if(x < o.x) {
			return o.x-(x+w);
		} else {
			return x-(o.x+o.w);
		}
	}
	
	public float ydistTo(Rect o) {
		if(y < o.y) {
			return o.y-(y+h);
		} else {
			return y-(o.y+o.h);
		}
	}
}
