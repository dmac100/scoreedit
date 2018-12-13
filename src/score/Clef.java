package score;

import static score.ScoreCanvas.STAFF_SPACING;

import org.jdom2.Element;

public enum Clef {
	BASS("E2", STAFF_SPACING + 8*8), TREBLE("C4", 0);
	
	private int lowScaleNumber;
	private int offset;
	
	Clef(String pitch, int offset) {
		this.lowScaleNumber = new Pitch(pitch).getScaleNumber();
		this.offset = offset;
	}
	
	public int getLowScaleNumber() {
		return lowScaleNumber;
	}

	public int getOffset() {
		return offset;
	}

	public void save(Element parent) {
		parent.setText(this.name());
	}
}