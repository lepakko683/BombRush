package okkapel.bombrush.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;

public class Render {
	public static final int OFFSET_U = 0;
	public static final int OFFSET_V = 4;
	
	public static final int OFFSET_RED = 2*4;
	public static final int OFFSET_GREEN = 2*4+1;
	public static final int OFFSET_BLUE = 2*4+2;
	public static final int OFFSET_ALPHA = 2*4+3;
	
	public static final int OFFSET_X = 2*4+4;
	public static final int OFFSET_Y = 3*4+4;
	public static final int OFFSET_Z = 4*4+4;
	
	public static void renderVA(ByteBuffer buf, int first, int count, int texture) {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		if(texture != 0) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		}
		
		GL11.glInterleavedArrays(GL11.GL_T2F_C4UB_V3F, 5*4+4, buf);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, first, count);
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		if(texture != 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}
	
	public static void floatToBytes(float f, byte[] arr) {
		if(arr == null || arr.length != 4) {
			return;
		}
		int flt = Float.floatToIntBits(f);
		
		if(ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
			arr[0] = (byte) (flt & 255);
			arr[1] = (byte) ((flt >> 8) & 255);
			arr[2] = (byte) ((flt >> 16) & 255);
			arr[3] = (byte) ((flt >> 24) & 255);
		} else {
			arr[3] = (byte) (flt & 255);
			arr[2] = (byte) ((flt >> 8) & 255);
			arr[1] = (byte) ((flt >> 16) & 255);
			arr[0] = (byte) ((flt >> 24) & 255);
		}
	}
}
