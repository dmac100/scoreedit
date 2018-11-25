package score;

import java.util.LinkedHashMap;
import java.util.Map;

public class MeasureAccidentals {
	enum Accidental {
		NONE, NATURAL, SHARP, FLAT, DOUBLESHARP, DOUBLEFLAT
	}
	
	private final Map<Pitch, Integer> sharps;
	
	public MeasureAccidentals(MeasureAccidentals measureAccidentals) {
		this.sharps = new LinkedHashMap<>(measureAccidentals.sharps);
	}
	
	public MeasureAccidentals(KeySig keySig) {
		this.sharps = new LinkedHashMap<>();
		
		for(int octave = 0; octave <= 10; octave++) {
			for(char name = 'A'; name <= 'G'; name++) {
				sharps.put(new Pitch(name, octave, 0), 0);
			}
		}
		
		for(Pitch pitch:keySig.getPitches()) {
			for(int octave = 0; octave <= 10; octave++) {
				sharps.put(new Pitch(pitch.getName(), octave, 0), (keySig.getFifths() > 0) ? 1 : -1);
			}
		}
	}

	public Accidental getAccidental(Pitch pitch) {
		Pitch plainPitch = new Pitch(pitch.getName(), pitch.getOctave(), 0);
		
		if(sharps.get(plainPitch) != pitch.getSharps()) {
			return getAccidental(pitch.getSharps());
		}
		
		return Accidental.NONE;
	}
	
	public void setAccidental(Pitch pitch) {
		Pitch plainPitch = new Pitch(pitch.getName(), pitch.getOctave(), 0);
		
		sharps.put(plainPitch, pitch.getSharps());
	}

	private Accidental getAccidental(int sharps) {
		switch(sharps) {
			case 0: return Accidental.NATURAL;
			case -1: return Accidental.FLAT;
			case -2: return Accidental.DOUBLEFLAT;
			case 1: return Accidental.SHARP;
			case 2: return Accidental.DOUBLESHARP;
			default: throw new IllegalArgumentException("Unknown sharps: " + sharps);
		}
	}
}