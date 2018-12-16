package score;

import static score.Duration.DurationType.EIGHTH;
import static score.Duration.DurationType.HALF;
import static score.Duration.DurationType.QUARTER;
import static score.Duration.DurationType.WHOLE;
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

class MeasureDataCache {
	private final Map<Object, Measure> itemMeasures = new HashMap<>();
	private final Map<Object, Voice> itemVoices = new HashMap<>();
	private final Map<Object, Integer> itemStartTimes = new HashMap<>();
	
	public MeasureDataCache(List<Measure> measures) {
		int measureTime = 0;
		
		for(Measure measure:measures) {
			int maxVoiceTime = 0;
			
			for(Voice voice:measure.getVoices()) {
				int voiceTime = 0;
				
				for(CanvasItem item:voice.getItems()) {
					itemMeasures.put(item, measure);
					itemVoices.put(item, voice);
					itemStartTimes.put(item, measureTime + voiceTime);
					if(item instanceof Chord) {
						for(Note note:((Chord) item).getNotes()) {
							itemMeasures.put(note, measure);
							itemVoices.put(note, voice);
							itemStartTimes.put(note, measureTime + voiceTime);
						}
					}
					
					voiceTime += item.getDuration();
				}
				
				maxVoiceTime = Math.max(maxVoiceTime, voiceTime);
			}
			
			measureTime += maxVoiceTime;
		}
	}
	
	public int getStartTime(Object item) {
		return itemStartTimes.get(item);
	}
	
	public Measure getMeasure(Object item) {
		return itemMeasures.get(item);
	}

	public Voice getVoice(Object item) {
		return itemVoices.get(item);
	}
}

public class Model {
	private final List<Measure> measures = new ArrayList<>();
	private final Set<Selectable> selectedItems = new HashSet<>();
	
	private DurationType selectedDurationType = QUARTER;
	
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
	}
	
	public List<Measure> getMeasures() {
		return measures;
	}
	
	public void setDurationType(DurationType durationType) {
		this.selectedDurationType = durationType;
	}
	
	public DurationType getDurationType() {
		return selectedDurationType;
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

	public void selectItems(List<Selectable> items, boolean shift, boolean control) {
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
		for(Measure measure:measures) {
			for(CanvasItem item:measure.getCanvasItems()) {
				if(item instanceof Selectable) {
					items.add((Selectable) item);
				}
				if(item instanceof Chord) {
					for(Note note:((Chord) item).getNotes()) {
						items.add(note);
					}
				}
			}
		}
		return items;
	}
}