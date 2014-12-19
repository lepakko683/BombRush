package okkapel.bombrush.util;

public class AnimSprite {
	
	private int speed, cind, tmr;
	private int[] sprOffs;
	
	/**
	 * @param speed higher = slower, amount of frames between animation updates
	 */
	public AnimSprite(int[] sprOffs, int speed) {
		this.speed = speed;
		this.cind = 0;
		this.tmr = 0;
		this.sprOffs = sprOffs;
	}
	
	public int getCurr() {
		return sprOffs[cind];
	}
	
	public int updGetCurr() {
		update();
		return sprOffs[cind];
	}
	
	public void update() {
		if(tmr >= speed) {
			tmr = 0;
			cind++;
		} else {
			tmr++;
			return;
		}
		
		if(cind >= sprOffs.length) {
			cind = 0;
		}
	}
	
}
