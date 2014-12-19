package okkapel.bombrush.util;

public abstract class World {
	
	public static final World debugWorld = new World() {
		protected void setupWorld() {
			walls = new Wall[2];
//			walls = new Wall[1];
			walls[0] = new Wall(64f, 64f, 32f, 32f);
			walls[1] = new Wall(64f+32f+48f, 64f, 32f, 32f);
			
			tiles = new short[1];
		};
	};
	
	public Wall[] walls;
	public short[] tiles;
	
	private World() {
		setupWorld();
	}
	
	public void renderWorld() {
		for(int i=0;i<walls.length;i++) {
			walls[i].render();
		}
	}
	
	public void handleEntityMovement(EntityMobile e, float dx, float dy) {
		
	}
	
	protected abstract void setupWorld();
	
}
