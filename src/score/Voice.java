package score;

import java.util.List;

public class Voice {
	private Clef clef;
	private List<CanvasItem> items;

	public Voice(Clef clef, List<CanvasItem> items) {
		this.clef = clef;
		this.items = items;
	}

	public Clef getClef() {
		return clef;
	}
	
	public List<CanvasItem> getItems() {
		return items;
	}
	
	public String toString() {
		return items.toString();
	}
}