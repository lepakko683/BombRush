package okkapel.bombrush;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import okkapel.bombrush.render.ParticleRender;
import okkapel.bombrush.util.Bomb;
import okkapel.bombrush.util.Entity;
import okkapel.bombrush.util.EntityMobile;
import okkapel.bombrush.util.Player;
import okkapel.bombrush.util.RendStr;
import okkapel.bombrush.util.Sprite;
import okkapel.bombrush.util.Tile;
import okkapel.bombrush.util.World;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL11.*;

public class BombRush {
	
	private static int texSheet = 0; // player, bomb, wall 
	private static int texFont = 0;
	
	private static long lastCyc = System.currentTimeMillis();
	private static int cycs = 0;
	
	private static ParticleRender fxRender;
	
	public static World currWorld = null;
	
	public static int getFontTexId() {
		return texFont;
	}
	
	public static int getSpriteTexId() {
		return texSheet;
	}
	
	public static void main(String[] args) {
		try {
			Display.setDisplayMode(new DisplayMode(720, 720));
			Display.setTitle("BombRush - Chat, drop bombs");
			Display.create();
			
			glMatrixMode(GL_PROJECTION);
			glOrtho(0d, 720d, 720d, 0d, -2d, 2d);
			glMatrixMode(GL_MODELVIEW);
			
			glClearColor(.5f, .5f, .8f, 1f);
			
			load();
			
		} catch(Throwable e) {
			e.printStackTrace();
		}
		
		Bomb bt = new Bomb(60*5);
		RendStr hello = new RendStr("TICK: ", 64f, 0f, 0f);
		int ticks = 0;
		Player plr = new Player();
		currWorld = World.debugWorld;
		
		int glerr = GL_NO_ERROR;
		while(!shouldClose()) {
			glClear(GL_COLOR_BUFFER_BIT);
			glLoadIdentity();
			
			if(System.currentTimeMillis() - 1000 > lastCyc) {
				lastCyc = System.currentTimeMillis();
//				System.out.println("FPS: " + cycs);
				cycs = 0;
			} else {
				cycs++;
			}
			
//			playerMove(bt);
			playerMove(plr);
			
			currWorld.renderWorld();
			
			bt.render();
//			hello.render();
			plr.render();
			
//			System.out.println("xdist: " + plr.getColl().xdistTo(currWorld.walls[0].coll) + " ydist: " + plr.getColl().ydistTo(currWorld.walls[0].coll));
			
			
			glerr = glGetError();
			if(glerr != GL_NO_ERROR) {
				System.out.println(GLU.gluErrorString(glerr));
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				ticks++;
				hello.setString("TICK: " + Integer.toString(ticks));
			}
			Display.update();
			Display.sync(60);
		}
		
		delTex(texSheet);
		delTex(texFont);
	}
	
	private static boolean shouldClose() {
		return Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
	}
	
	private static void delTex(int id) {
		if(id != 0) {
			glDeleteTextures(id);
		}
	}
	
	private static void load() {
		texSheet = loadTexture("res/textures/textureSheet.png");
		texFont = loadTexture("res/textures/font.png");
		
		Bomb.bomb0 = new Sprite(texSheet, 0, 128, 16);
		Bomb.fuse1 = new Sprite(texSheet, 1, 128, 16);
		Bomb.fuse2 = new Sprite(texSheet, 2, 128, 16);
		Bomb.spark1 = new Sprite(texSheet, 3, 128, 16);
		Bomb.spark2 = new Sprite(texSheet, 4, 128, 16);
	}
	
	private static void playerMove(EntityMobile plr) {
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			plr.addMovement(-4f, 0f);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			plr.addMovement(4f, 0f);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			plr.addMovement(0f, -4f);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			plr.addMovement(0f, 4f);
		}
		plr.update();
	}
	
	private static int loadTexture(String path) {
		try {
			BufferedImage img = ImageIO.read(new File(path));
			
			int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
			
			ByteBuffer buf = BufferUtils.createByteBuffer(4*pixels.length);
			int pix = 0;
			
			for(int i=0;i<pixels.length;i++) {
				pix = pixels[i];
				
				buf.put((byte)((pix >> 16) & 0xFF));
				buf.put((byte)((pix >> 8 ) & 0xFF));
				buf.put((byte)((pix      ) & 0xFF));
				buf.put((byte)((pix >> 24) & 0xFF));
			}
			buf.flip();
			
			int tex = glGenTextures();
			
			if(tex == 0) {
				throw new Exception();
			}
			
			glBindTexture(GL_TEXTURE_2D, tex);
			
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			
			glBindTexture(GL_TEXTURE_2D, 0);
			
			return tex;
		
		} catch(Throwable e) {
			System.err.println("Missing texture: " + path);
			e.printStackTrace();
		}
		return 0;
	}
}
