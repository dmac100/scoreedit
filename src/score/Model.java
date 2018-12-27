package score;

import static score.Duration.DurationType.EIGHTH;
import static score.Duration.DurationType.HALF;
import static score.Duration.DurationType.QUARTER;
import static score.Duration.DurationType.WHOLE;
import static util.CollectionUtil.maxBy;
import static util.XmlUtil.addElement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import score.Duration.DurationType;

public class Model {
	private final List<Measure> measures = new ArrayList<>();
	private final Set<Selectable> selectedItems = new HashSet<>();
	
	private final List<Runnable> selectionChangedHandlers = new ArrayList<>();
	
	private Pitch lastPitch = new Pitch("C4");
	private DurationType selectedDurationType = QUARTER;
	private int dots = 0;
	
	public Model() {
		for(int x = 0; x < 32; x++) {
			Voice treble = new Voice(Clef.TREBLE, Arrays.asList(
				new Chord(Arrays.asList(new Note(new Pitch("C4"),  new Duration(WHOLE))), new Duration(WHOLE)),
				new Chord(Arrays.asList(new Note(new Pitch("D4"),  new Duration(HALF))), new Duration(HALF)),
				new Chord(Arrays.asList(new Note(new Pitch("E4"),  new Duration(QUARTER))), new Duration(QUARTER)),
				new Chord(Arrays.asList(new Note(new Pitch("F4"),  new Duration(EIGHTH))), new Duration(EIGHTH))
			));
			Voice bass = new Voice(Clef.BASS, Arrays.asList(
				new Rest(new Duration(WHOLE)),
				new Rest(new Duration(HALF)),
				new Rest(new Duration(QUARTER)),
				new Rest(new Duration(EIGHTH))
			));
			measures.add(new Measure(
				Arrays.asList(treble, bass),
				new TimeSig(4, 4),
				new KeySig(0)
			));
		}
		
		measures.forEach(measure -> measure.autoBeam());
		
		addSelectionChangedHandler(this::updateLastPitchFromSelection);
	}
	
	public Pitch getLastPitch() {
		return lastPitch;
	}
	
	public List<Measure> getMeasures() {
		return measures;
	}

	public DurationType getDurationType() {
		return selectedDurationType;
	}
	
	public void setDurationType(DurationType durationType) {
		this.selectedDurationType = durationType;
	}
	
	public int getDots() {
		return dots;
	}
	
	public void setDots(int dots) {
		this.dots = dots;
	}
	
	public Duration getDuration() {
		return new Duration(getDurationType(), getDots());
	}
	
	private void updateLastPitchFromSelection() {
		Set<Selectable> selectedItems = getSelectedItems();
		MeasureDataCache measureDataCache = new MeasureDataCache(getMeasures());
		
		Map<Integer, Note> notes = new HashMap<>();
		
		visitItems(new ItemVisitor() {
			public void visitNote(Note note) {
				if(selectedItems.contains(note)) {
					int startTime = measureDataCache.getStartTime(note);
					notes.put(startTime, note);
				}
			}
		});
		
		if(notes.size() == 1) {
			Note note = notes.values().iterator().next();
			lastPitch = note.getPitch();
		}
	}
	
	public void insertNote(char name) {
		Cursor cursor = findOrCreateCursor();
		
		Voice voice = cursor.getVoice();
		
		int startTime = voice.getStartTime(cursor);
		
		Pitch pitch = voice.getPitchWithSharpsOrFlats(getNewPitch(name), cursor.getMeasure().getKeySig(), startTime);
		Note note = new Note(pitch, getDuration());
		Chord chord = new Chord(voice.getClef(), Arrays.asList(note), getDuration());
		
		voice.insertItem(chord, startTime);
		voice.removeItem(cursor);
		
		voice.insertItem(cursor, startTime + chord.getDuration());
		
		deselectAll();
		chord.getNotes().forEach(this::selectItem);
		
		fireSelectionChangedHandlers();
	}
	
	private Pitch getNewPitch(char name) {
		Pitch newPitch = new Pitch(name, lastPitch.getOctave(), 0);
		for(int d = -1; d <= 1; d++) {
			int scaleNumber = new Pitch(name, lastPitch.getOctave() + d, 0).getScaleNumber();
			if(Math.abs(scaleNumber - lastPitch.getScaleNumber()) < Math.abs(newPitch.getScaleNumber() - lastPitch.getScaleNumber())) {
				newPitch = new Pitch(scaleNumber);
			}
		}
		return newPitch;
	}

	private Cursor findOrCreateCursor() {
		Cursor cursor = findCursor();
		
		if(cursor != null) {
			return cursor;
		}
		
		Measure measure = getMeasures().get(0);
		Voice voice = measure.getVoices().get(0);
	
		cursor = new Cursor(measure, voice);
		voice.insertItem(cursor, 0);
		
		return cursor;
	}
	
	private Cursor findCursor() {
		for(Measure measure:getMeasures()) {
			for(Voice voice:measure.getVoices()) {
				for(CanvasItem item:voice.getItems()) {
					if(item instanceof Cursor) {
						return (Cursor) item;
					}
				}
			}
		}
		return null;
	}

	public void save(OutputStream outputStream) throws IOException {
		Element root = new Element("Model");
		for(Measure measure:measures) {
			measure.save(addElement(root, "measure"));
		}
		new XMLOutputter().output(root, outputStream);
	}
	
	public void load(InputStream inputStream) throws IOException, JDOMException {
		Element root = new SAXBuilder().build(inputStream).getRootElement();
		measures.clear();
		for(Element measureElement:root.getChildren("measure")) {
			measures.add(new Measure(measureElement));
		}
	}
	
	public void deleteSelection(boolean replaceWithRests) {
		Set<Rest> collapsableRests = new HashSet<>();
		
		visitItems(new ItemVisitor() {
			private Voice voice;
			private Chord chord;

			public void visitVoice(Voice voice) {
				this.voice = voice;
			}

			public void visitChord(Chord chord) {
				this.chord = chord;
			}
			
			public void visitRest(Rest rest) {
				if(selectedItems.contains(rest)) {
					if(replaceWithRests) {
						collapsableRests.add(rest);
					} else {
						voice.removeItem(rest);
					}
					deselectItem(rest);
				}
			}

			public void visitNote(Note note) {
				if(selectedItems.contains(note)) {
					chord.removeNote(note);
					if(chord.getNotes().isEmpty()) {
						if(replaceWithRests) {
							Rest rest = new Rest(new Duration(chord.getDuration()));
							voice.replaceItem(chord, rest);
							collapsableRests.add(rest);
						} else {
							voice.removeItem(chord);
						}
					}
					deselectItem(note);
				}
			}
		});
		
		for(Measure measure:measures) {
			for(Voice voice:measure.getVoices()) {
				voice.collapseRests(collapsableRests);
			}
		}
		
		measures.forEach(measure -> measure.autoBeam());
	}
	
	public void visitItems(ItemVisitor itemVisitor) {
		for(Measure measure:measures) {
			itemVisitor.visitMeasure(measure);
			for(Voice voice:measure.getVoices()) {
				itemVisitor.visitVoice(voice);
				for(CanvasItem item:voice.getItems()) {
					if(item instanceof Chord) {
						Chord chord = (Chord) item;
						itemVisitor.visitChord(chord);
						for(Note note:chord.getNotes()) {
							itemVisitor.visitNote((Note) note);
						}
					} else if(item instanceof Rest) {
						itemVisitor.visitRest((Rest) item);
					}
				}
			}
		}
	}
	
	public void addSelectionChangedHandler(Runnable handler) {
		selectionChangedHandlers.add(handler);
	}
	
	private void fireSelectionChangedHandlers() {
		selectionChangedHandlers.forEach(Runnable::run);
	}
	
	public void shiftSelectionPitch(int shiftCount) {
		visitItems(new ItemVisitor() {
			public void visitNote(Note note) {
				if(selectedItems.contains(note)) {
					if(shiftCount > 0) {
						for(int x = 0; x < shiftCount; x++) {
							note.setPitch(note.getPitch().nextSemitone());
						}
					} else {
						for(int x = 0; x < -shiftCount; x++) {
							note.setPitch(note.getPitch().prevSemitone());
						}
					}
				}
			}
		});
		
		fireSelectionChangedHandlers();
	}
	
	public void shiftSelectionOctave(int shiftCount) {
		visitItems(new ItemVisitor() {
			public void visitNote(Note note) {
				if(selectedItems.contains(note)) {
					Pitch pitch = note.getPitch();
					note.setPitch(new Pitch(pitch.getName(), pitch.getOctave() + shiftCount, pitch.getSharps()));
				}
			}
		});
		
		fireSelectionChangedHandlers();
	}

	public void selectItems(List<? extends Selectable> items, boolean shift, boolean control) {
		MeasureDataCache measureDataCache = new MeasureDataCache(measures);
		
		if(!control && !shift) {
			deselectAll();
		}
		
		if(control) {
			items.forEach(item -> {
				if(selectedItems.contains(item)) {
					deselectItem(item);
				} else {
					selectItem(item);
				}
			});
			
			fireSelectionChangedHandlers();
			updateCursorToSelection();
			
			return;
		}
		
		items.forEach(item -> selectItem(item));
		
		if(shift) {
			if(!selectedItems.isEmpty()) {
				int minTime = Integer.MAX_VALUE;
				int maxTime = Integer.MIN_VALUE;
				Set<Clef> clefs = new HashSet<>();
				for(Selectable item:selectedItems) {
					clefs.add(measureDataCache.getVoice(item).getClef());
					int startTime = measureDataCache.getStartTime(item);
					minTime = Math.min(minTime, startTime);
					maxTime = Math.max(maxTime, startTime);
				}
				
				for(Selectable item:getAllSelectableItems()) {
					int startTime = measureDataCache.getStartTime(item);
					Clef clef = measureDataCache.getVoice(item).getClef();
					if(clefs.contains(clef) && startTime >= minTime && startTime <= maxTime) {
						selectItem(item);
					}
				}
			}
		}
		
		fireSelectionChangedHandlers();
		updateCursorToSelection();
	}

	private void updateCursorToSelection() {
		visitItems(new ItemVisitor() {
			private Cursor cursor = findCursor();
			
			private Measure measure;
			private Voice voice;
			private Chord chord;
			
			private boolean done = false;
			
			public void visitMeasure(Measure measure) {
				this.measure = measure;
			}
			
			public void visitVoice(Voice voice) {
				this.voice = voice;
			}
			
			public void visitRest(Rest rest) {
				if(selectedItems.contains(rest)) {
					visitItem(rest);
				}
			}
			
			public void visitChord(Chord chord) {
				this.chord = chord;
			}
			
			public void visitNote(Note note) {
				if(selectedItems.contains(note)) {
					visitItem(chord);
				}
			}
			
			private void visitItem(CanvasItem item) {
				if(!done) {
					if(cursor != null) {
						cursor.getVoice().removeItem(cursor);
					}
				
					cursor = new Cursor(measure, voice);
					voice.insertItemBefore(item, cursor);
					done = true;
				}
			}
		});
	}

	public void deselectAll() {
		new HashSet<>(selectedItems).forEach(item -> deselectItem(item));
	}
	
	private void selectItem(Selectable item) {
		selectedItems.add(item);
		item.setSelected(true);
	}
	
	private void deselectItem(Selectable item) {
		selectedItems.remove(item);
		item.setSelected(false);
	}
	
	private Set<Selectable> getAllSelectableItems() {
		Set<Selectable> items = new HashSet<>();
		visitItems(new ItemVisitor() {
			public void visitRest(Rest rest) {
				items.add(rest);
			}
			
			public void visitNote(Note note) {
				items.add(note);
			}
		});
		return items;
	}

	public void selectPrev(boolean shift, boolean control) {
		selectPrevNext(-1, shift, control);
	}
	
	public void selectNext(boolean shift, boolean control) {
		selectPrevNext(1, shift, control);
	}
	
	private void selectPrevNext(int d, boolean shift, boolean control) {
		MeasureDataCache measureDataCache = new MeasureDataCache(measures);
		
		CanvasItem selectedItem = null;
		
		Selectable startSelectable = maxBy(selectedItems, item -> measureDataCache.getStartTime(item) * d);
		
		if(startSelectable instanceof Note) {
			selectedItem = measureDataCache.getChord((Note) startSelectable);
		} else if(startSelectable instanceof Rest) {
			selectedItem = (Rest) startSelectable;
		}
		
		if(selectedItem != null) {
			Measure measure = measureDataCache.getMeasure(selectedItem);
			Voice voice = measureDataCache.getVoice(selectedItem);
			
			int measureIndex = measures.indexOf(measure);
			int voiceIndex = measure.getVoices().indexOf(voice);
			int itemIndex = voice.getItems().indexOf(selectedItem);
			
			while(measureIndex >= 0 && measureIndex < measures.size()) {
				List<CanvasItem> items = voice.getItems();
				
				while(itemIndex + d >= 0 && itemIndex + d < items.size()) {
					itemIndex += d;
					if(items.get(itemIndex) instanceof Chord) {
						selectItems(((Chord) items.get(itemIndex)).getNotes(), shift, control);
						return;
					} else if(items.get(itemIndex) instanceof Rest) {
						selectItems(Arrays.asList((Rest) items.get(itemIndex)), shift, control);
						return;
					}
				}
				
				measureIndex += d;
				
				if(measureIndex >= 0 && measureIndex < measures.size()) {
					measure = measures.get(measureIndex);
					voice = measure.getVoices().get(voiceIndex);
					itemIndex = (d < 0) ? voice.getItems().size() : -1;
				}
			}
		}
	}
	
	public Set<Selectable> getSelectedItems() {
		return new HashSet<>(selectedItems);
	}
}