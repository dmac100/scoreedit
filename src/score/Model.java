package score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import score.Duration.DurationType;

class Model {
	private final List<CanvasItem> items = new ArrayList<>();
	
	public Model() {
		items.add(
			new Chord(
				Arrays.asList(
					new Note(new Pitch("C4", 1), new Duration(DurationType.EIGHTH)),
					new Note(new Pitch("D4", 1), new Duration(DurationType.EIGHTH)),
					new Note(new Pitch("G4", 1), new Duration(DurationType.EIGHTH))
				),
				new Duration(DurationType.EIGHTH)
			)
		);
		items.add(
			new Chord(
				Arrays.asList(
					new Note(new Pitch("D5", -1), new Duration(DurationType.EIGHTH)),
					new Note(new Pitch("E5", -1), new Duration(DurationType.EIGHTH)),
					new Note(new Pitch("A5", -1), new Duration(DurationType.EIGHTH))
				),
				new Duration(DurationType.EIGHTH)
			)
		);
		items.add(
			new Chord(
				Arrays.asList(
					new Note(new Pitch("C3", 1), new Duration(DurationType.EIGHTH)),
					new Note(new Pitch("D3", -1), new Duration(DurationType.EIGHTH)),
					new Note(new Pitch("G2", 1), new Duration(DurationType.EIGHTH))
				),
				new Duration(DurationType.EIGHTH)
			)
		);
		items.add(
			new Chord(
				Arrays.asList(
					new Note(new Pitch("D6", -1), new Duration(DurationType.EIGHTH)),
					new Note(new Pitch("E6", 1), new Duration(DurationType.EIGHTH)),
					new Note(new Pitch("A6", -1), new Duration(DurationType.EIGHTH))
				),
				new Duration(DurationType.EIGHTH)
			)
		);
		items.add(
			new Chord(
				Arrays.asList(
					new Note(new Pitch("A4", 2), new Duration(DurationType.HALF)),
					new Note(new Pitch("C4", 2), new Duration(DurationType.HALF)),
					new Note(new Pitch("E5", 2), new Duration(DurationType.HALF))
				),
				new Duration(DurationType.HALF)
			)
		);
		items.add(
			new Chord(
				Arrays.asList(
					new Note(new Pitch("A4", -2), new Duration(DurationType.SIXTEENTH)),
					new Note(new Pitch("D5", -2), new Duration(DurationType.SIXTEENTH)),
					new Note(new Pitch("G5", -2), new Duration(DurationType.SIXTEENTH))
				),
				new Duration(DurationType.SIXTEENTH)
			)
		);
		items.add(
			new Chord(
				Arrays.asList(
					new Note(new Pitch("A4", 0), new Duration(DurationType.SIXTEENTH)),
					new Note(new Pitch("D5", 0), new Duration(DurationType.SIXTEENTH)),
					new Note(new Pitch("G5", 0), new Duration(DurationType.SIXTEENTH))
				),
				new Duration(DurationType.SIXTEENTH)
			)
		);
		items.add(
			new Chord(
				Arrays.asList(
					new Note(new Pitch("B4", 0), new Duration(DurationType.SIXTEENTH)),
					new Note(new Pitch("E5", 0), new Duration(DurationType.SIXTEENTH)),
					new Note(new Pitch("A5", 0), new Duration(DurationType.SIXTEENTH))
				),
				new Duration(DurationType.SIXTEENTH)
			)
		);
	}

	public List<CanvasItem> getItems() {
		return items;
	}

	public void selectBox(float x, float y, float width, float height) {
		//items.forEach(item -> item.selectBox(x, y, width, height));
	}
}