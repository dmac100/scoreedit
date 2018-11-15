package score;

import static score.Duration.DurationType.EIGHTH;
import static score.Duration.DurationType.HALF;
import static score.Duration.DurationType.QUARTER;
import static score.Duration.DurationType.WHOLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import score.Duration.DurationType;

class Model {
	private final List<Measure> measures = new ArrayList<>();
	
	public Model() {
		for(int x = 0; x < 20; x++) {
			measures.add(measure(
				chord(
					EIGHTH,
					note("C4", 1, EIGHTH),
					note("D4", 1, EIGHTH),
					note("G4", 1, EIGHTH)
				),
				chord(
					EIGHTH,
					note("D5", -1, EIGHTH),
					note("E5", -1, EIGHTH),
					note("A5", -1, EIGHTH)
				)
			));
			
			measures.add(measure(
				chord(
					EIGHTH,
					note("D4", -1, EIGHTH),
					note("E4", 1, EIGHTH),
					note("F4", 1, EIGHTH)
				),
				chord(
					EIGHTH,
					note("D5", -1, EIGHTH),
					note("A5", 0, EIGHTH),
					note("G5", 1, EIGHTH)
				)
			));
			
			measures.add(measure(
				chord(EIGHTH, note("B5", 0, EIGHTH)),
				chord(QUARTER, note("B5", 0, QUARTER))
			));
			
			measures.add(measure(chord(EIGHTH, note("A4", 1, EIGHTH))));
			measures.add(measure(chord(WHOLE, note("B4", -1, WHOLE))));
			measures.add(measure(chord(HALF, note("E5", 0, HALF))));
			measures.add(measure(chord(EIGHTH, note("D5", 1, EIGHTH))));
			measures.add(measure(chord(EIGHTH, note("E5", -1, EIGHTH))));
			
			measures.add(measure(
				chord(EIGHTH, note("A4", 0, EIGHTH)),
				chord(QUARTER, note("C4", 0, QUARTER)),
				chord(QUARTER, note("D4", 0, QUARTER))
			));
			
			measures.add(measure(
				chord(EIGHTH, note("B3", 1, EIGHTH)),
				chord(QUARTER, note("D3", 1, QUARTER)),
				chord(QUARTER, note("F3", 1, QUARTER))
			));
		}
	}
	
	private static Measure measure(CanvasItem... canvasItems) {
		return new Measure(Arrays.asList(canvasItems));
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