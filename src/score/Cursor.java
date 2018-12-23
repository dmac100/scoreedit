package score;

import score.layout.AlignmentBox;

public class Cursor implements CanvasItem {
	private Pitch lastPitch = new Pitch("C4");
	
	private final Measure measure;
	private final Voice voice;
	
	public Cursor(Measure measure, Voice voice) {
		this.measure = measure;
		this.voice = voice;
	}
	
	public Pitch getNewPitch(char name) {
		Pitch newPitch = new Pitch(name, lastPitch.getOctave(), 0);
		for(int d = -1; d <= 1; d++) {
			int scaleNumber = new Pitch(name, lastPitch.getOctave() + d, 0).getScaleNumber();
			if(Math.abs(scaleNumber - lastPitch.getScaleNumber()) < Math.abs(newPitch.getScaleNumber() - lastPitch.getScaleNumber())) {
				newPitch = new Pitch(scaleNumber);
			}
		}
		lastPitch = newPitch;
		return newPitch;
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