package score;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class AlignmentBox {
	private final Rectangle boundingBox;
	private final int center;
	
	public AlignmentBox(Rectangle boundingBox, int center) {
		this.boundingBox = boundingBox;
		this.center = center;
	}

	public int getWidth() {
		return boundingBox.width;
	}
	
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	public int getCenter() {
		return center;
	}

	public void draw(GC gc) {
		gc.setLineWidth(4);
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		
		gc.drawRectangle(boundingBox);
		gc.drawLine(boundingBox.x + center, boundingBox.y, boundingBox.x + center, boundingBox.y + boundingBox.height);
		
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	}
}