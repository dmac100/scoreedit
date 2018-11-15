package score;

import java.util.List;

public class Measure {
	private List<CanvasItem> items;

	public Measure(List<CanvasItem> items) {
		this.items = items;
	}

	public List<CanvasItem> getItems() {
		return items;
	}
	
	public int getWidth() {
		int noteSpacing = 60;
		
		int width = 0;
		
		for(CanvasItem item:items) {
			width += item.getBoundingBox(0, 0).width + noteSpacing;
		}
		
		return width;
	}
}