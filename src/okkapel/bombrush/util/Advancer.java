package okkapel.bombrush.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Advancer {

	private List<IIAdvancable> advcs;
	
	public Advancer() {
		advcs = new LinkedList<IIAdvancable>();
	}
	
	public void advanceAll(int delta) {
		Iterator<IIAdvancable> iter = advcs.iterator();
		
		while(iter.hasNext()) {
			iter.next().advance(delta);
		}
	}
}
