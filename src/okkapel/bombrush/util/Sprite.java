package okkapel.bombrush.util;


public class Sprite {
	
	public int texture;
	public float u1, v1, u2, v2;
	
	public Sprite(int texture, int index, int texWidth, int spriteWidth) {
		this.texture = texture;
		int xind = index % (texWidth / spriteWidth), yind = (int) (index / (texWidth / spriteWidth));
		float wInTex = 1f / (float)(texWidth/spriteWidth);
		
		u1 = xind * wInTex;
		v1 = yind * wInTex;
		u2 = (xind+1) * wInTex;
		v2 = (yind+1) * wInTex;
	}
	
	private Sprite() {}
	
	public Sprite dupFlipped(boolean vertical) {
		Sprite ret;
		if(vertical) {
			ret = new Sprite();
			ret.u1 = u1;
			ret.u2 = u2;
			ret.v1 = v2;
			ret.v2 = v1;
		} else {
			ret = new Sprite();
			ret.u1 = u2;
			ret.u2 = u1;
			ret.v1 = v1;
			ret.v2 = v2;
		}
		return ret;
	}
	
	public Sprite dup() {
		Sprite ret = new Sprite();
		ret.u1 = u1;
		ret.u2 = u2;
		ret.v1 = v1;
		ret.v2 = v2;
		return ret;
	}
}
