package score;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class ScoreCanvas {
	public static final int PAGE_WIDTH = 1950;
	public static final int SYSTEM_SPACING = 350;
	public static final int MEASURE_SPACING = 25;
	public static final int STAFF_SPACING = 80;
	public static final int ACCIDENTAL_SPACING = 25;
	
	private GC gc;
	
	private final Map<Measure, Rectangle> measureBounds = new HashMap<>();
	private final Map<CanvasItem, Rectangle> itemBounds = new HashMap<>();

	public void reset(GC gc) {
		this.gc = gc;
		itemBounds.clear();
		measureBounds.clear();
	}

	public void drawText(String text, int x, int y) {
		gc.drawText(text, x, y, true);
	}

	public void drawLine(int lineWidth, int capStyle, int x1, int y1, int x2, int y2) {
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		gc.setLineCap(capStyle);
		gc.setLineWidth(lineWidth);
		gc.drawLine(x1, y1, x2, y2);
	}

	public void fillRectangle(int x, int y, int width, int height) {
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		gc.fillRectangle(x, y, width, height);
		gc.drawRectangle(x, y, width, height);
	}

	public void setMeasureBounds(Measure measure, int x, int y, int width, int height) {
		measureBounds.put(measure, new Rectangle(x, y, width, height));
	}

	public void setItemBounds(CanvasItem item, int x, int y, int width, int height) {
		if(item instanceof Spacer) return;
		
		itemBounds.put(item, new Rectangle(x, y, width, height));
	}

	public Map<Measure, Rectangle> getMeasureBounds() {
		return measureBounds;
	}
	
	public Map<CanvasItem, Rectangle> getItemBounds() {
		return itemBounds;
	}
}