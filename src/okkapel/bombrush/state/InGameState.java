package okkapel.bombrush.state;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

import okkapel.bombrush.entity.Bomb;
import okkapel.bombrush.entity.EntityMobile;
import okkapel.bombrush.entity.Player;
import okkapel.bombrush.render.ChatHandler;
import okkapel.bombrush.render.ParticleRender;
import okkapel.bombrush.render.TileRender;
import okkapel.bombrush.util.RendStr;
import okkapel.bombrush.util.Tile;
import okkapel.bombrush.util.World;

public class InGameState implements IState {
	
	public ParticleRender fxRender;
	private TileRender tr;
	private ChatHandler ch;
	public Player thePlayer;
	
	public World currWorld = null;
	
	// Temporary
	private RendStr healthDisp;
	
	private boolean spaceDown = false;
	
	private int plrOldHealth = 0;
	
	public InGameState() {
		currWorld = World.debugWorld;
		
		thePlayer = new Player();
		
		healthDisp = new RendStr("HEALTH: ", 32f, 0f, 0f);
		fxRender = new ParticleRender(128);
		ch = new ChatHandler();
		tr = new TileRender();
		tr.init();
	}

	@Override
	public void loop() {
		glClear(GL_COLOR_BUFFER_BIT);
		glLoadIdentity();
		
		playerMove(thePlayer);
		
		if(!spaceDown && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			spaceDown = true;
			Bomb nbomb = new Bomb(5*60);
			nbomb.setWorldGridPos((int)((thePlayer.getX() + thePlayer.getColl().w/2f) / Tile.DEFAULT_TILE_WIDTH), (int)((thePlayer.getY() + thePlayer.getColl().h/2f) / Tile.DEFAULT_TILE_WIDTH));
			currWorld.spawnEntity(nbomb);
		}
		if(spaceDown && !Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			spaceDown = false;
		}
		
		glPushMatrix();
		glTranslatef(720f/2f-thePlayer.getColl().w/2f-thePlayer.getX(), 720f/2f-thePlayer.getColl().h/2f-thePlayer.getY(), 0f);
		glPushMatrix();
		
		currWorld.render(tr);
		
		glLoadIdentity();
		thePlayer.render();
		
		glPopMatrix();
		fxRender.render();
		
		glPopMatrix();
		
		if(thePlayer.getHealth() != plrOldHealth) {
			plrOldHealth = thePlayer.getHealth();
			healthDisp.setString("HEALTH: " + plrOldHealth);
		}
		healthDisp.render();
		
		ch.renderChat();
		
		Display.update();
		Display.sync(60);
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

	@Override
	public void deInit() {
		ch.onGameExit();
	}

	@Override
	public void beforeSwitch() {
		if(healthDisp == null) {
			healthDisp = new RendStr("HEALTH: ", 32f, 0f, 0f);
		}
		
		currWorld.reset();
		currWorld.spawnEntity(thePlayer);
		
		ch.disableIgnoreMode();
	}

	@Override
	public String getStateName() {
		return "In-Game";
	}

	@Override
	public void switchFrom() {
		ch.enableIgnoreMode();
	}

}
