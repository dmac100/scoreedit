package score;

public enum Clef {
	BASS("E2", 80 + 8*8), TREBLE("C4", 0);
	
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
}