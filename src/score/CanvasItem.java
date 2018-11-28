package score;

import org.eclipse.swt.graphics.GC;

public interface CanvasItem {
	public void setClef(Clef clef);
	public void draw(GC gc, int startX, int startY, MeasureAccidentals measureAccidentals);
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals);
	public int getDuration();
	public void setAccidentals(MeasureAccidentals measureAccidentals);
	public Beam getBeam();
}