package score.layout;

import score.VoiceItem;
import score.MeasureAccidentals;

public class Spacer implements VoiceItem {
	private final int spacing;
	
	public Spacer(int spacing) {
		this.spacing = spacing;
	}
	
	@Override
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals) {
		return new AlignmentBox(spacing, 0, 0, 32);
	}
}