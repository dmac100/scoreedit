package score.layout;

import score.MeasureAccidentals;
import score.VoiceItem;

/**
 * An item to add extra horizontal spacing between items.
 */
public class Spacer implements VoiceItem {
	private final int spacing;
	
	public Spacer(int spacing) {
		this.spacing = spacing;
	}
	
	@Override
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals) {
		return new AlignmentBox(spacing, 0);
	}
}