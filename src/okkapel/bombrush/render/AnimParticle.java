package okkapel.bombrush.render;

import java.nio.ByteBuffer;

import okkapel.bombrush.util.RBE;
import okkapel.bombrush.util.Sprite;

public class AnimParticle extends Particle {

	private Sprite[] frames;
	private int slower;
	private int speed;
	private int cind;
	
	public AnimParticle(Sprite[] spr, int life, float x, float y, float dx, float dy, float fri, int speed) {
		super(spr[0], life, x, y, dx, dy, fri);
		this.speed = speed;
		slower = speed;
	}
	
	@Override
	public void update(ByteBuffer renderData, int offset) {
		super.update(renderData, offset);
		if(slower < 1) {
			slower = speed + 1;
			cind++;
			if(cind >= frames.length) {
				cind = 0;
			}
			RBE rbe = RBE.INSTANCE;
			rbe.attachBuffer(renderData);
			rbe.setVertexOffset(offset);
			rbe.editRect2DUV(frames[cind].u1, frames[cind].v1, frames[cind].u2, frames[cind].v2);
			rbe.finishEditing();
		}
		slower--;
	}

}
