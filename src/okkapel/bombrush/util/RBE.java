package okkapel.bombrush.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** RenderBufferEditor */
public class RBE {
	public static final RBE INSTANCE = new RBE();
	
	public static final int WIDTH_RGBA_XYZ = 4+3*4;
	public static final int WIDTH_XYZ = 3*4;
	
	private boolean editing = false;
	private ByteBuffer ab;
	private byte[] buffer = new byte[4];
	
	private int oldPosition, oldLimit;
	
	private RBE() {}
	
	/** Attaches given buffer and sets it's position to 0 and limit to capacity. */
	public void attachBuffer(ByteBuffer buf) {
		if(editing) {
			System.err.println("RBE already editing!!! This may end very badly...");
		}
		
		editing = true;
		ab = buf;
		oldPosition = ab.position();
		oldLimit = ab.limit();
		ab.position(0);
		ab.limit(ab.capacity());
	}
	
	public void finishEditing() {
		editing = false;
		ab.position(oldPosition);
		ab.limit(oldLimit);
	}
	
	public void setOffset(int offset) {
		if(ab != null) {
			ab.position(offset);
		}
	}
	
	public void setVertexOffset(int offset) {
		if(ab != null) {
			ab.position(offset*RenderBufferGenerator.DEFAULT_GL_STRIDE);
		}
	}
	
	public void editRect2D(float x1, float y1, float x2, float y2, float z, float r, float g, float b, float a, float u1, float v1, float u2, float v2) {
		editVertex(x1, y1, z, r, g, b, a, u1, v1);
		editVertex(x1, y2, z, r, g, b, a, u1, v2);
		editVertex(x2, y2, z, r, g, b, a, u2, v2);
		
		editVertex(x2, y2, z, r, g, b, a, u2, v2);
		editVertex(x2, y1, z, r, g, b, a, u2, v1);
		editVertex(x1, y1, z, r, g, b, a, u1, v1);
	}
	
	public void editRect2DUV(float u1, float v1, float u2, float v2) {
		editUV(u1, v1);
		editUV(u1, v2);
		editUV(u2, v2);
		
		editUV(u2, v2);
		editUV(u2, v1);
		editUV(u1, v1);
	}
	
	public void editRect2DXYZ(float x1, float y1, float x2, float y2, float z) {
		editXYZ(x1, y1, z);
		editXYZ(x1, y2, z);
		editXYZ(x2, y2, z);
		
		editXYZ(x2, y2, z);
		editXYZ(x2, y1, z);
		editXYZ(x1, y1, z);
	}
	
	/** Very specific method :P "editVertexUsingArrayExceptForPos" */
	public void editVertexUAEFPos(float x, float y, float z, byte[] rgbauv, int offset) {
		ab.put(rgbauv, offset, 2*4+4);
		floatToBytes(x, buffer); ab.put(buffer, 0, 4);
		floatToBytes(y, buffer); ab.put(buffer, 0, 4);
		floatToBytes(z, buffer); ab.put(buffer, 0, 4);
	}
	
	/** It is expected that the buffer's current position is right before the vertex to be edited = before the uv */
	public void editUV(float u, float v) {
		floatToBytes(u, buffer); ab.put(buffer, 0, 4);
		floatToBytes(v, buffer); ab.put(buffer, 0, 4);
		ab.position(ab.position() + WIDTH_RGBA_XYZ);
	}
	
	/** It is expected that the buffer's current position is right before the vertex to be edited, not before the rgba */
	public void editRGBA(float r, float g, float b, float a) {
		ab.position(ab.position() + Render.OFFSET_RED);
		ab.put((byte)(r*255));
		ab.put((byte)(g*255));
		ab.put((byte)(b*255));
		ab.put((byte)(a*255));
		ab.position(ab.position() + WIDTH_XYZ);
	}
	
	/** It is expected that the buffer's current position is right before the vertex to be edited, not before the xyz */
	public void editXYZ(float x, float y, float z) {
		ab.position(ab.position() + Render.OFFSET_X);
		floatToBytes(x, buffer); ab.put(buffer, 0, 4);
		floatToBytes(y, buffer); ab.put(buffer, 0, 4);
		floatToBytes(z, buffer); ab.put(buffer, 0, 4);
	}
	
	public void editVertex(float x, float y, float z, float r, float g, float b, float a, float u, float v) {
		floatToBytes(u, buffer); ab.put(buffer, 0, 4);
		floatToBytes(v, buffer); ab.put(buffer, 0, 4);
		ab.put((byte)(r*255));
		ab.put((byte)(g*255));
		ab.put((byte)(b*255));
		ab.put((byte)(a*255));
		floatToBytes(x, buffer); ab.put(buffer, 0, 4);
		floatToBytes(y, buffer); ab.put(buffer, 0, 4);
		floatToBytes(z, buffer); ab.put(buffer, 0, 4);
	}
	
	
	private void floatToBytes(float f, byte[] arr) {
		if(arr == null || arr.length != 4) {
			return;
		}
		int flt = Float.floatToIntBits(f); // Not sure if needed
		
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
	
	private static int orderIntBytes(byte[] data, int first) {
		int ret = 0;
		
		if(ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
			ret |= data[first];
			ret |= (data[first+1] << 8);
			ret |= (data[first+2] << 16);
			ret |= (data[first+3] << 24);
		} else {
			ret |= (data[first] << 24);
			ret |= (data[first+1] << 16);
			ret |= (data[first+2] << 8);
			ret |= data[first+3];
		}
		
		return ret;
	}
	
	public static float getVXFromBytes(int voffs, byte[] data) {
		return Float.intBitsToFloat(orderIntBytes(data, voffs*RenderBufferGenerator.DEFAULT_GL_STRIDE+Render.OFFSET_X));
	}
	
	public static float getVYFromBytes(int voffs, byte[] data) {
		return Float.intBitsToFloat(orderIntBytes(data, voffs*RenderBufferGenerator.DEFAULT_GL_STRIDE+Render.OFFSET_Y));
	}
	
	public static float getVZFromBytes(int voffs, byte[] data) {
		return Float.intBitsToFloat(orderIntBytes(data, voffs*RenderBufferGenerator.DEFAULT_GL_STRIDE+Render.OFFSET_Z));
	}
	
}
