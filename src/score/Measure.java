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
		new NoteLayout(voices, extraWidth).getVoiceItems().forEach((voice, items) -> {
			int x = startX;
			for(CanvasItem item:items) {
				//item.getAlignmentBox(x, startY + voice.getClef().getOffset()).draw(gc);
				item.draw(gc, x, startY + voice.getClef().getOffset());
				
				x += item.getAlignmentBox().getWidth();
			}
		});
	}
	
	public Rectangle getBoundingBox(GC gc, int startX, int startY) {
		return new Rectangle(startX, startY, getWidth(), 8*8);
	}
	
	public int getWidth() {
		int maxWidth = 0;
		
		for(List<CanvasItem> items:new NoteLayout(voices, 0).getVoiceItems().values()) {
			int width = 0;
			for(CanvasItem item:items) {
				width += item.getAlignmentBox().getWidth();
			}
			maxWidth = Math.max(maxWidth, width);
		}
		
		return maxWidth;
	}
}