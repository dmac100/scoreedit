package score;

import static score.Duration.DurationType.QUARTER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import score.Duration.DurationType;
import util.CollectionUtil;

class Model {
	private final List<Measure> measures = new ArrayList<>();
	
	public Model() {
		for(int x = 0; x < 32; x++) {
			measures.add(measure(timeSig(4, 4), keySig(0),
				treble(
					rest(QUARTER),
					rest(QUARTER),
					rest(QUARTER),
					rest(QUARTER)
				),
				bass(
					rest(QUARTER),
					rest(QUARTER),
					rest(QUARTER),
					rest(QUARTER)
				)
			));
		}
	}
	
	private Voice bass(CanvasItem[] beam, CanvasItem chord) {
		List<CanvasItem> items = (CollectionUtil.concat(Arrays.asList(beam), Arrays.asList(chord)));
		items.forEach(item -> item.setClef(Clef.BASS));
		return new Voice(Clef.BASS, items);
	}

	private KeySig keySig(int fifths) {
		return new KeySig(fifths);
	}
	
	private TimeSig timeSig(int upper, int lower) {
		return new TimeSig(upper, lower);
	}

	private static Measure measure(TimeSig timeSig, KeySig keySig, Voice... voices) {
		return new Measure(Arrays.asList(voices), timeSig, keySig);
	}
	
	private static Chord[] beam(Chord... chords) {
		Beam beam = new Beam();
		for(Chord chord:chords) {
			chord.setBeam(beam);
		}
		return chords;
	}
	
	private static Voice treble(CanvasItem... canvasItems) {
		Arrays.asList(canvasItems).forEach(item -> item.setClef(Clef.TREBLE));
		return new Voice(Clef.TREBLE, Arrays.asList(canvasItems));
	}
	
	private static Voice bass(CanvasItem... canvasItems) {
		Arrays.asList(canvasItems).forEach(item -> item.setClef(Clef.BASS));
		return new Voice(Clef.BASS, Arrays.asList(canvasItems));
	}
	
	private static Rest rest(DurationType durationType) {
		return new Rest(new Duration(durationType));
	}
	
	private static Chord chord(DurationType durationType, Note... notes) {
		return new Chord(Arrays.asList(notes), new Duration(durationType));
	}
	
	private static Note note(String pitch, int flats, DurationType durationType) {
		return new Note(new Pitch(pitch, flats), new Duration(durationType));
	}
	
	public List<Measure> getMeasures() {
		return measures;
	}

	public void selectBox(float x, float y, float width, float height) {
		//items.forEach(item -> item.selectBox(x, y, width, height));
	}
}