package score;

import static score.Duration.DurationType.EIGHTH;
import static score.Duration.DurationType.QUARTER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import score.Duration.DurationType;

class Model {
	private final List<Measure> measures = new ArrayList<>();
	
	public Model() {
		for(int x = 0; x < 10; x++) {
			measures.add(measure(timeSig(3, 4), keySig(3),
				treble(
					chord(
						QUARTER,
						note("C4", 1, QUARTER),
						note("D4", 1, QUARTER),
						note("G4", 1, QUARTER)
					),
					chord(
						QUARTER,
						note("D5", -1, QUARTER),
						note("E5", -1, QUARTER),
						note("A5", -1, QUARTER)
					)
				),
				bass(
					chord(EIGHTH, note("C3", 1, EIGHTH)),
					chord(EIGHTH, note("D3", 1, EIGHTH)),
					chord(EIGHTH, note("E3", 1, EIGHTH)),
					chord(EIGHTH, note("F3", 1, EIGHTH))
				)
			));
			measures.add(measure(timeSig(3, 4), keySig(3),
				treble(
					chord(
						QUARTER,
						note("F4", 0, QUARTER),
						note("E4", 0, QUARTER),
						note("B4", 0, QUARTER)
					),
					chord(
						QUARTER,
						note("D5", 0, QUARTER),
						note("E5", 0, QUARTER),
						note("A5", 0, QUARTER)
					)
				),
				bass(
					chord(EIGHTH, note("F3", 0, EIGHTH)),
					chord(EIGHTH, note("G3", 0, EIGHTH)),
					chord(QUARTER, note("A3", 0, QUARTER))
				)
			));
		}
		
		refreshPreviousMeasures();
	}
	
	private void refreshPreviousMeasures() {
		Measure previousMeasure = null;
		for(Measure measure:measures) {
			measure.setPreviousMeasure(previousMeasure);
			previousMeasure = measure;
		}
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
	
	private static Voice treble(CanvasItem... canvasItems) {
		Arrays.asList(canvasItems).forEach(item -> item.setClef(Clef.TREBLE));
		return new Voice(Clef.TREBLE, Arrays.asList(canvasItems));
	}
	
	private static Voice bass(CanvasItem... canvasItems) {
		Arrays.asList(canvasItems).forEach(item -> item.setClef(Clef.BASS));
		return new Voice(Clef.BASS, Arrays.asList(canvasItems));
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