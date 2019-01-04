package score;

import score.layout.AlignmentBox;

public class Cursor implements VoiceItem {
	private final Measure measure;
	private final Voice voice;
	
	public Cursor(Measure measure, Voice voice) {
		this.measure = measure;
		this.voice = voice;
	}
	
	public Measure getMeasure() {
		return measure;
	}
	
	public Voice getVoice() {
		return voice;
	}

	@Override
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals) {
		return new AlignmentBox(0, 0, 0, 0);
	}

	@Override
	public String toString() {
		return "[CURSOR]";
	}
	
	@Override
	public boolean includeInLayout() {
		return false;
	}
}