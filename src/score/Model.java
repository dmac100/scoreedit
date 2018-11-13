package score;

import java.util.ArrayList;
import java.util.List;

import score.Duration.DurationType;

class Model {
	private final List<CanvasItem> items = new ArrayList<>();
	
	public Model() {
		items.add(new Note(new Pitch("C3"), new Duration(DurationType.WHOLE)));
		items.add(new Note(new Pitch("D3"), new Duration(DurationType.HALF)));
		items.add(new Note(new Pitch("E3"), new Duration(DurationType.QUARTER)));
		items.add(new Note(new Pitch("F3"), new Duration(DurationType.EIGHTH, 1)));
		items.add(new Note(new Pitch("G3"), new Duration(DurationType.SIXTEENTH, 2)));
		items.add(new Note(new Pitch("A3"), new Duration(DurationType.THIRTYSECOND)));
		
		items.add(new Note(new Pitch("C4"), new Duration(DurationType.WHOLE)));
		items.add(new Note(new Pitch("D4"), new Duration(DurationType.HALF)));
		items.add(new Note(new Pitch("E4"), new Duration(DurationType.QUARTER)));
		items.add(new Note(new Pitch("F4"), new Duration(DurationType.EIGHTH)));
		items.add(new Note(new Pitch("G4"), new Duration(DurationType.SIXTEENTH)));
		items.add(new Note(new Pitch("A4"), new Duration(DurationType.THIRTYSECOND)));
		
		items.add(new Note(new Pitch("C5"), new Duration(DurationType.WHOLE)));
		items.add(new Note(new Pitch("D5"), new Duration(DurationType.HALF)));
		items.add(new Note(new Pitch("E5"), new Duration(DurationType.QUARTER)));
		items.add(new Note(new Pitch("F5"), new Duration(DurationType.EIGHTH)));
		items.add(new Note(new Pitch("G5"), new Duration(DurationType.SIXTEENTH)));
		items.add(new Note(new Pitch("A5"), new Duration(DurationType.THIRTYSECOND)));
		
		items.add(new Note(new Pitch("C6"), new Duration(DurationType.WHOLE)));
		items.add(new Note(new Pitch("D6"), new Duration(DurationType.HALF)));
		items.add(new Note(new Pitch("E6"), new Duration(DurationType.QUARTER)));
		items.add(new Note(new Pitch("F6"), new Duration(DurationType.EIGHTH)));
		items.add(new Note(new Pitch("G6"), new Duration(DurationType.SIXTEENTH)));
		items.add(new Note(new Pitch("A6"), new Duration(DurationType.THIRTYSECOND)));
	}

	public List<CanvasItem> getItems() {
		return items;
	}

	public void selectBox(float x, float y, float width, float height) {
		//items.forEach(item -> item.selectBox(x, y, width, height));
	}
}