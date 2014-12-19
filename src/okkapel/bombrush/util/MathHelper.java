package okkapel.bombrush.util;

public class MathHelper {
	
	public static float signlessMin(float a, float b) {
		return Math.abs(a) < Math.abs(b) ? a : b;
	}
	
	public static float signlessMax(float a, float b) {
		return Math.abs(a) > Math.abs(b) ? a : b;
	}
}
