package score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class Measure {
	private List<Voice> voices;

	public Measure(List<Voice> voices) {
		this.voices = voices;
	}

	public void drawMeasure(GC gc, int startX, int startY, int extraWidth) {
		int noteSpacing = 60;
		
		List<List<Voice>> noteLayout = getNoteLayout();
		
		int x = startX;
		for(List<Voice> voices:noteLayout) {
			for(Voice voice:voices) {
				for(CanvasItem item:voice.getItems()) {
					item.draw(gc, x, startY + voice.getClef().getOffset());
				}
			}
			x += extraWidth / noteLayout.size();
			x += getWidth(voices) + noteSpacing;
		}
	}
	
	public Rectangle getBoundingBox(GC gc, int startX, int startY) {
		return new Rectangle(startX, startY, getWidth(), 8*8);
	}
	
	public int getWidth() {
		int noteSpacing = 60;
		
		int totalWidth = 0;
		for(List<Voice> voices:getNoteLayout()) {
			totalWidth += getWidth(voices) + noteSpacing;
		}
		return totalWidth;
	}
	
	private int getWidth(List<Voice> voices) {
		int width = 0;
		for(Voice voice:voices) {
			for(CanvasItem item:voice.getItems()) {
				width = Math.max(width, item.getBoundingBox(0, 0).width);
			}
		}
		return width;
	}
	
	private List<List<Voice>> getNoteLayout() {
		Map<Integer, List<Voice>> map = new TreeMap<>();
		
		for(Voice voice:voices) {
			int count = 0;
			for(CanvasItem item:voice.getItems()) {
				map.computeIfAbsent(count, ArrayList::new);
				map.get(count).add(new Voice(voice.getClef(), Arrays.asList(item)));
				
				count += item.getDuration().getDurationCount();
			}
		}
		
		return new ArrayList<>(map.values());
	}
}