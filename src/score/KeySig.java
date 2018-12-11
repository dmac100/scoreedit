package score;

import java.util.Arrays;
import java.util.List;

public class KeySig {
	private final List<Pitch> sharps = Arrays.asList(new Pitch("F5"), new Pitch("C5"), new Pitch("G5"), new Pitch("D5"), new Pitch("A4"), new Pitch("E5"), new Pitch("B4"));
	private final List<Pitch> flats = Arrays.asList(new Pitch("B4"), new Pitch("E5"), new Pitch("A4"), new Pitch("D5"), new Pitch("G4"), new Pitch("C5"), new Pitch("F4"));
	
	private final int fifths;
	
	public KeySig(int fifths) {
		if(Math.abs(fifths) > 7) {
			throw new IllegalArgumentException("Invalid fifths: " + fifths);
		}
		
		this.fifths = fifths;
	}
	
	public int getFifths() {
		return fifths;
	}
		
	public boolean equals(Object other) {
		return (other instanceof KeySig) && equals((KeySig) other);
	}
	
	private boolean equals(KeySig other) {
		return (fifths == other.fifths);
	}

	public List<Pitch> getPitches() {
		if(fifths > 0) {
			return sharps.subList(0, fifths);
		} else {
			return flats.subList(0, -fifths);
		}
	}
	
	public int getWidth(Measure previousMeasureOnLine, Measure previousMeasure) {
		if(previousMeasureOnLine == null || !previousMeasureOnLine.getKeySig().equals(this)) {
			if(fifths > 0) {
				return Math.abs(fifths) * 20 + 30;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public void draw(ScoreCanvas canvas, int startX, int startY, Measure previousMeasureOnLine, Measure previousMeasure) {
		for(Clef clef:Clef.values()) {
			int extraClefOffset = (clef == Clef.BASS) ? 2*8 : 0;
			
			int x = startX;
			if(previousMeasureOnLine == null || !previousMeasureOnLine.getKeySig().equals(this)) {
				String text = (fifths > 0) ? FetaFont.SHARP : FetaFont.FLAT;
				for(Pitch pitch:this.getPitches()) {
					canvas.drawText(text, x, startY - (pitch.getScaleNumber() * 8) + 113 + clef.getOffset() + extraClefOffset);
					x += 20;
				}
			}
		}
	}
}