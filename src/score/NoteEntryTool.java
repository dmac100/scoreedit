package score;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import score.Duration.DurationType;

public class NoteEntryTool implements Tool {
	private final Composite composite;
	private final Model model;
	private final ScoreCanvas scoreCanvas;
	
	private int mx;
	private int my;

	public NoteEntryTool(Composite composite, Model model, ScoreCanvas scoreCanvas) {
		this.composite = composite;
		this.model = model;
		this.scoreCanvas = scoreCanvas;
	}

	public void mouseUp(int button, float x, float y) {
	}
	
	public void mouseDown(int button, float x, float y) {
	}
	
	public void mouseMove(float x, float y) {
		this.mx = (int) x;
		this.my = (int) y;
		composite.redraw();
	}
	
	public void paint(GC gc) {
		Map<Measure, Rectangle> measureBounds = scoreCanvas.getMeasureBounds();
		Map<CanvasItem, Rectangle> itemBounds = scoreCanvas.getItemBounds();
		
		Measure measure = getClosestKey(measureBounds, mx, my);
		if(measure != null) {
			Rectangle measureRectangle = measureBounds.get(measure);
			Map<CanvasItem, Rectangle> measureItems = filterKeys(itemBounds, measure.getCanvasItems());
			
			CanvasItem item = getClosestKey(measureItems, mx, my);
			
			if(item != null) {
				Rectangle itemRectangle = itemBounds.get(item);
				
				Clef clef = (my < measureRectangle.y + 8*8 + 40) ? Clef.TREBLE : Clef.BASS;
				
				if(mx >= itemRectangle.x && mx <= itemRectangle.x + itemRectangle.width) {
					Pitch pitch = new Pitch(clef.getLowScaleNumber() + ((measureRectangle.y + clef.getOffset() + 80) - my) / 8);
					
					drawNote(gc, itemRectangle.x, measureRectangle.y + clef.getOffset(), pitch.getScaleNumber() - clef.getLowScaleNumber(), new Duration(DurationType.QUARTER));
				}
			}
		}
	}

	private void drawNote(GC gc, int startX, int startY, int scaleNumber, Duration duration) {
		drawNoteHead(gc, startX, startY, scaleNumber, duration);
	}
	
	private void drawNoteHead(GC gc, int startX, int startY, int scaleNumber, Duration duration) {
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
		
		if(duration.getType() == DurationType.WHOLE) {
			startX -= 5;
		}
		gc.drawText(FetaFont.getNoteHead(duration), startX, startY - (scaleNumber * 8) - 71, true);
	}

	private static <K, V> Map<K, V> filterKeys(Map<K, V> map, Set<K> keys) {
		Map<K, V> filteredMap = new HashMap<>();
		keys.forEach(key -> filteredMap.put(key, map.get(key)));
		return filteredMap;
	}

	private static <T> T getClosestKey(Map<T, Rectangle> bounds, int x, int y) {
		T closestKey = null;
		int closestDistance = Integer.MAX_VALUE;
		for(T key:bounds.keySet()) {
			Rectangle rectangle = bounds.get(key);
			
			int distance = (rectangle.contains(x, y)) ? 0 : Integer.MAX_VALUE;
			
			if(x >= rectangle.x && x <= rectangle.x + rectangle.width) {
				distance = Math.min(distance, Math.min(Math.abs(rectangle.y - y), Math.abs(rectangle.y + rectangle.height - y)));
			}
			
			if(distance < closestDistance) {
				closestDistance = distance;
				closestKey = key;
			}
		}
		return closestKey;
	}
}