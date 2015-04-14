package okkapel.bombrush.util;

public class SpriteAnimation implements IIAdvancable {
	
	private int[] frames;
	private int speed;
	private int slower;
	private int cf;
	
	public SpriteAnimation(int[] frames, int speed) {
		this.frames = frames;
		this.speed = speed;
		slower = speed;
	}

	@Override
	public void advance(int delta) {
		if(slower < 1) {
			slower = speed;
			cf++;
			if(cf > frames.length-1) {
				cf = 0;
			}
		}
		slower -= delta;
	}
	
	public int getCurrentFrame() {
		return cf;
	}
	
}
