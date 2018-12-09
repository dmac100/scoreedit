package score;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class AlignmentBox {
	private final int width;
	private final int height;
	private final int center;
	private int top;
	
	public AlignmentBox(int width, int height, int center, int top) {
		this.width = width;
		this.height = height;
		this.center = center;
		this.top = top;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getCenter() {
		return center;
	}
	
	public int getTop() {
		return top;
	}

	public void draw(GC gc, int startX, int startY) {
		gc.setLineWidth(4);
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		
		gc.drawRectangle(new Rectangle(startX, startY + top, width, height));
		gc.drawLine(startX + center, startY + top, startX + center, startY + top + height);
		
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	}
}