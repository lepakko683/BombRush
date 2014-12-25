package okkapel.bombrush.render;

import java.nio.ByteBuffer;

import okkapel.bombrush.BombRush;
import okkapel.bombrush.util.RBE;
import okkapel.bombrush.util.Render;
import okkapel.bombrush.util.RenderBufferGenerator;
import okkapel.bombrush.util.Sprite;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;


public class ParticleRender {
	private ByteBuffer renderData;
	private Particle[] particles;
	
	public ParticleRender(int particleBufferSize) {
		renderData = BufferUtils.createByteBuffer(particleBufferSize * 6 * RenderBufferGenerator.DEFAULT_GL_STRIDE);
		particles = new Particle[particleBufferSize];
	}
	
	public void spawnParticle(Sprite spr, float x, float y, int life, float width) {
		int indx = -1;
		RBE rbe = RBE.INSTANCE;
		rbe.attachBuffer(renderData);
		for(int i=0;i<particles.length;i++) {
			if(particles[i] != null) {
				if(particles[i].dead) {
					particles[i].setData(spr, life, x, y, 0f, 0f, 0f);
					indx = i;
				}
			} else {
				particles[i] = new Particle(spr, life, x, y, 0f, 0f, 0f);
				indx = i;
			}
		}
		
		if(indx != -1) {
			rbe.setVertexOffset(6*indx);
			rbe.editRect2D(0f, 0f, width, width, 1f, 1f, 1f, 1f, 1f, spr.u1, spr.v1, spr.u2, spr.v2);
		}
		rbe.finishEditing();
	}
	
	public void spawnParticle(Sprite spr, float x, float y, int life, float dx, float dy, float fri, float width) {
		int indx = -1;
		RBE rbe = RBE.INSTANCE;
		rbe.attachBuffer(renderData);
		for(int i=0;i<particles.length;i++) {
			if(particles[i] != null) {
				if(particles[i].dead) {
					particles[i].setData(spr, life, x, y, dx, dy, fri);
					indx = i;
				}
			} else {
				particles[i] = new Particle(spr, life, x, y, dx, dy, fri);
				indx = i;
			}
		}
		
		if(indx != -1) {
			rbe.setVertexOffset(6*indx);
			rbe.editRect2D(0f, 0f, width, width, 1f, 1f, 1f, 1f, 1f, spr.u1, spr.v1, spr.u2, spr.v2);
		}
		rbe.finishEditing();
	}
	
	public void render() {
		for(int i=0;i<particles.length;i++) {
			Particle p = particles[i];
			if(p != null) {
				if(!p.dead) {
					GL11.glPushMatrix();
					Render.renderVA(renderData, i*6, 6, BombRush.getPartTexId());
					GL11.glPopMatrix();
					
					if(p.dx != 0f) {
						p.x += p.dx;
						
						if(p.dx < 0f) {
							p.dx += p.fri;
							if(p.dx > 0f) {
								p.dx = 0f;
							}
						} else {
							p.dx -= p.fri;
							if(p.dx < 0f) {
								p.dx = 0f;
							}
						}
					}
					
					if(p.dy != 0f) {
						p.y += p.dy;
					
						if(p.dy < 0f) {
							p.dy += p.fri;
							if(p.dy > 0f) {
								p.dy = 0f;
							}
						} else {
							p.dy -= p.fri;
							if(p.dy < 0f) {
								p.dy = 0f;
							}
						}
					}
				}
			}
		}
	}
	
}
