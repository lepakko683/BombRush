package okkapel.bombrush.state;

public interface IState {

	public void beforeSwitch();
	
	public void loop();
	
	public void deInit();
	
	public void switchFrom();
	
	public String getStateName();
	
}
