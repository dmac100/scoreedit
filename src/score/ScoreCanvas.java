package score;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class ScoreCanvas {
	private GC gc;

	public void reset(GC gc) {
		this.gc = gc;
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
}