package score;

import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class Measure {
	private List<Voice> voices;

	public Measure(List<Voice> voices) {
		this.voices = voices;
	}

	public void drawMeasure(GC gc, int startX, int startY, int extraWidth) {
		int noteSpacing = 60;

		for(Voice voice:voices) {
			int x = startX;
			for(CanvasItem item:voice.getItems()) {
				item.draw(gc, x, startY + voice.getClef().getOffset());
				
				x += item.getBoundingBox(startX, startY).width + noteSpacing;
				
				x += extraWidth / voice.getItems().size();
			}
		}
	}
	
	public Rectangle getBoundingBox(GC gc, int startX, int startY) {
		return new Rectangle(startX, startY, getWidth(), 8*8);
	}
	
	public int getWidth() {
		int maxWidth = 0;
		
		for(Voice voice:voices) {
			int noteSpacing = 60;
			
			int width = 0;
			
			for(CanvasItem item:voice.getItems()) {
				width += item.getBoundingBox(0, 0).width + noteSpacing;
			}
			
			maxWidth = Math.max(maxWidth, width);
		}
		
		return maxWidth;
	}
}