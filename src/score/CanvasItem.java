package score;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public interface CanvasItem {
	public void setClef(Clef clef);
	public void draw(GC gc, int startX, int startY);
	public Rectangle getBoundingBox(int startX, int startY);
	public Duration getDuration();
}