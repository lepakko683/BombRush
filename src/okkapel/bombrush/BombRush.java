package okkapel.bombrush;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

import okkapel.bombrush.entity.Bomb;
import okkapel.bombrush.entity.Entity;
import okkapel.bombrush.entity.EntityMobile;
import okkapel.bombrush.entity.Player;
import okkapel.bombrush.render.ChatHandler;
import okkapel.bombrush.render.Particle;
import okkapel.bombrush.render.ParticleRender;
import okkapel.bombrush.render.TileRender;
import okkapel.bombrush.state.IState;
import okkapel.bombrush.state.InGameState;
import okkapel.bombrush.state.MainMenuState;
import okkapel.bombrush.util.Advancer;
import okkapel.bombrush.util.RendStr;
import okkapel.bombrush.util.Sprite;
import okkapel.bombrush.util.Tile;
import okkapel.bombrush.util.World;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL11.*;

public class BombRush {

	private static int STATE_MENU = 0;
	private static int STATE_IN_GAME = 1;
	private static boolean QUITTING = false;
	
	// GAME STATE
	private static IState[] states;
	private static int state = -1;
	
	public static int currentState() {
		return state;
	}
	
	public static IState getCurrentState() {
		return states[state];
	}
	
	public static InGameState getInGameState() {
		return (InGameState)states[STATE_IN_GAME];
	}
	
	private static void updateState(int nw) {
		state = nw;
	}
	
	private static int texSheet = 0; // player, bomb 
	private static int texFont = 0;  // characters
	private static int texTiles = 0; // walls, tiles
	private static int texParts = 0; // particles
	
	private static long lastCyc = System.currentTimeMillis();
	private static int cycs = 0;
	
	
	public static void main(String[] args) {
		Random r4nd = new Random();
		try {
			Display.setDisplayMode(new DisplayMode(720, 720));
			if(r4nd.nextInt(1000) == 42) {
				Display.setTitle("BombRush - 42 < 20*"); // *No, I'm not crazy..
			} else {
				Display.setTitle("BombRush - Chat, drop bombs");
			}
			
			Display.create();
			
			glMatrixMode(GL_PROJECTION);
			glOrtho(0d, 720d, 720d, 0d, -2d, 2d);
			glMatrixMode(GL_MODELVIEW);
			
			glClearColor(.5f, .5f, .8f, 1f);
			
		} catch(Throwable e) {
			e.printStackTrace();
		}
		
		handleGameStart();
		
		int glerr = GL_NO_ERROR;
		
		while(!shouldClose()) {
			if(System.currentTimeMillis() - 1000 > lastCyc) {
				lastCyc = System.currentTimeMillis();
//				System.out.println("FPS: " + cycs);
				cycs = 0;
			} else {
				cycs++;
			}
			
			states[state].loop();
			
			glerr = glGetError();
			if(glerr != GL_NO_ERROR) {
				System.err.println("OpenGL ERROR: " + GLU.gluErrorString(glerr) + " | Current game state(" + state + "): " + (state < 0 || state >= states.length ? "INVALID" : states[state] == null ? "NULL" : states[state].getStateName()));
			}
		}
		
		handleGameExit();
	}
	
	private static void handleGameStart() {
		// Textures
		Data.D.setupTextures(
				loadTexture("res/textures/textureSheet.png"),
				loadTexture("res/textures/tiles.png"),
				loadTexture("res/textures/particles.png"),
				loadTexture("res/textures/font.png"));
		
		// Sprites
		Bomb.bomb0 = new Sprite(Data.D.getSpriteTexId(), 0, 128, 16);
		Bomb.fuse1 = new Sprite(Data.D.getSpriteTexId(), 1, 128, 16);
		Bomb.fuse2 = new Sprite(Data.D.getSpriteTexId(), 2, 128, 16);
		Bomb.spark1 = new Sprite(Data.D.getSpriteTexId(), 3, 128, 16);
		Bomb.spark2 = new Sprite(Data.D.getSpriteTexId(), 4, 128, 16);
		
		Particle.sprFireBall = new Sprite(Data.D.getPartTexId(), 0, 256, 16);
		
		// States
		states = new IState[2];
		states[0] = new MainMenuState();
		states[1] = new InGameState();
		
		switchState(1);
	}
	
	private static void switchState(int statepar) {
		if(statepar != state) {
			states[statepar].beforeSwitch();
			state = statepar;
		}
	}
	
	private static void handleGameExit() {
		states[state].deInit();
		
		delTex(texSheet);
		delTex(texFont);
		delTex(texTiles);
		delTex(texParts);
	}
	
	public static void setQuitting() {
		QUITTING = true;
	}
	
	private static boolean shouldClose() {
		return Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || QUITTING;
	}
	
	private static void delTex(int id) {
		if(id != 0) {
			glDeleteTextures(id);
		}
	}
	
	private static int loadTexture(String path) {
		try {
			BufferedImage img = ImageIO.read(new File(path));
			
			int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
			
			ByteBuffer buf = BufferUtils.createByteBuffer(4 * pixels.length);
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
			
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
			
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
