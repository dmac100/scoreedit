package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import score.CanvasItem;
import score.Measure;
import score.Selectable;
import score.layout.Spacer;

public class ScoreCanvas {
	public static final int PAGE_WIDTH = 1950;
	public static final int SYSTEM_SPACING = 350;
	public static final int MEASURE_SPACING = 25;
	public static final int STAFF_SPACING = 80;
	public static final int ACCIDENTAL_SPACING = 25;
	
	private GC gc;
	
	private final Map<Measure, Rectangle> measureBounds = new HashMap<>();
	private final Map<CanvasItem, Rectangle> itemBounds = new HashMap<>();
	private final Map<Selectable, Rectangle> selectableBounds = new HashMap<>();

	public void reset(GC gc) {
		this.gc = gc;
		itemBounds.clear();
		measureBounds.clear();
		selectableBounds.clear();
	}

	public void drawText(String text, int x, int y, boolean selected) {
		if(selected) {
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		}
		gc.drawText(text, x, y, true);
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
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
	
	public void setSelectableBounds(Selectable selectable, int x, int y, int width, int height) {
		selectableBounds.put(selectable, new Rectangle(x, y, width, height));
	}

	public Map<Measure, Rectangle> getMeasureBounds() {
		return measureBounds;
	}
	
	public Map<CanvasItem, Rectangle> getItemBounds() {
		return itemBounds;
	}

	public List<Selectable> getItemsInRectangle(Rectangle rectangle) {
		List<Selectable> items = new ArrayList<>();
		selectableBounds.forEach((item, rect) -> {
			if(rectangle.contains(rect.x, rect.y) && rectangle.contains(rect.x + rect.width, rect.y + rect.height)) {
				items.add(item);
			}
		});
		return items;
	}

	public Selectable getItemAt(int x, int y) {
		for(Selectable item:selectableBounds.keySet()) {
			Rectangle rect = selectableBounds.get(item);
			if(rect.contains((int) x, (int) y)) {
				return item;
			}
		}
		return null;
	}
}