package okkapel.bombrush.state;

import org.lwjgl.input.Mouse;

import okkapel.bombrush.util.Rect;

public class MainMenuState implements IState {
	
	private Rect[] buttons;
	private boolean mouseLeftDown = false;
	
	public MainMenuState() {
		buttons = new Rect[1];
		buttons[0] = new Rect(0f, 0f, 128f, 32f);
	}

	@Override
	public void loop() {
		
		if(mouseLeftDown && !Mouse.isButtonDown(0)) {
			mouseLeftDown = false;
			onMouseUp();
		} else if(!mouseLeftDown && Mouse.isButtonDown(0)) {
			mouseLeftDown = true;
		}
	}
	
	private void onMouseUp() {
		for(int i=0;i<buttons.length;i++) {
			if(buttons[i].wrapsAround(Mouse.getX(), Mouse.getY())) {
				doAction(i);
			}
		}
	}
	
	private void doAction(int action) {
		switch(action) {
		case 0:
			
			break;
		}
	}

	@Override
	public void deInit() {
		
	}

	@Override
	public void beforeSwitch() {
		
	}

	@Override
	public String getStateName() {
		return "Menu";
	}

}
