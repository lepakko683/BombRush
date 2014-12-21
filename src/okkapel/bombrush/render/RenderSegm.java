package okkapel.bombrush.render;

import java.nio.ByteBuffer;

import okkapel.bombrush.util.Render;

public class RenderSegm {
	
	public int first, count, texture;
	
	public RenderSegm(int first, int count, int texture) {
		this.first = first;
		this.count = count;
		this.texture = texture;
	}

	public void render(ByteBuffer data) {
		Render.renderVA(data, first, count, texture);
	}
	
	
	
}
