package okkapel.bombrush.render;

import okkapel.bombrush.util.Sprite;

public class Particle {
	
	protected boolean dead = false;
	
	protected Sprite spr;
	protected int life;
	protected float x, y;
	protected float dx, dy, fri;
	
	public Particle(Sprite spr, int life, float x, float y, float dx, float dy, float fri) {
		this.spr = spr;
		this.life = life;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.fri = fri;
	}
	
	public void setData(Sprite spr, int life, float x, float y, float dx, float dy, float fri) {
		if(!dead) {
			return;
		}
		this.dead = false;
		this.spr = spr;
		this.life = life;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.fri = fri;
	}
	
	public void setDataForcefully(Sprite spr, int life, float x, float y, float dx, float dy, float fri) {
		this.dead = false;
		this.spr = spr;
		this.life = life;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.fri = fri;
	}
}
