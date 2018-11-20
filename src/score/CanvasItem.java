package score;

import org.eclipse.swt.graphics.GC;

public interface CanvasItem {
	public void setClef(Clef clef);
	public void draw(GC gc, int startX, int startY);
	public AlignmentBox getAlignmentBox(int startX, int startY);
	public int getDuration();
}