package score;

import static view.ScoreCanvas.STAFF_SPACING;

import org.jdom2.Element;

/**
 * A treble or bass clef and its spacing within the grand staff.
 */
public enum Clef {
	BASS("E2", STAFF_SPACING + 8*8), TREBLE("C4", 0);
	
	private int lowScaleNumber;
	private int offset;
	
	Clef(String pitch, int offset) {
		this.lowScaleNumber = new Pitch(pitch).getScaleNumber();
		this.offset = offset;
	}
	
	/**
	 * Returns the scale number of note where middle C would be in the treble clef.
	 */
	public int getLowScaleNumber() {
		return lowScaleNumber;
	}

	/**
	 * Returns the vertical distance to add from the top of the grand staff for this clef.
	 */
	public int getOffset() {
		return offset;
	}

	public void save(Element parent) {
		parent.setText(this.name());
	}
}