package score;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class Spacer implements CanvasItem {
	private final int spacing;
	
	public Spacer(int spacing) {
		this.spacing = spacing;
	}
	
	@Override
	public void setClef(Clef clef) {
	}

	@Override
	public void draw(GC gc, int startX, int startY) {
	}

	@Override
	public AlignmentBox getAlignmentBox() {
		return new AlignmentBox(spacing, 0, 0);
	}

	@Override
	public int getDuration() {
		return 0;
	}
}