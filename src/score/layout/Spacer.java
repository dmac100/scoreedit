package score.layout;

import score.CanvasItem;
import score.MeasureAccidentals;

public class Spacer implements CanvasItem {
	private final int spacing;
	
	public Spacer(int spacing) {
		this.spacing = spacing;
	}
	
	@Override
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals) {
		return new AlignmentBox(spacing, 0, 0, 32);
	}
}