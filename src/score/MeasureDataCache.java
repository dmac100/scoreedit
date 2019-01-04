package score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads all measures a caches calculated values from them.
 */
public class MeasureDataCache {
	private final Map<Object, Measure> itemMeasures = new HashMap<>();
	private final Map<Object, Voice> itemVoices = new HashMap<>();
	private final Map<Object, Integer> itemStartTimes = new HashMap<>();
	private final Map<Object, Integer> measureStartTimes = new HashMap<>();
	private final Map<Note, Chord> noteChords = new HashMap<>();
	
	public MeasureDataCache(List<Measure> measures) {
		int measureTime = 0;
		
		for(Measure measure:measures) {
			int maxVoiceTime = 0;
			
			measureStartTimes.put(measure, measureTime);
			
			for(Voice voice:measure.getVoices()) {
				int voiceTime = 0;
				
				for(VoiceItem item:voice.getItems()) {
					itemMeasures.put(item, measure);
					itemVoices.put(item, voice);
					itemStartTimes.put(item, measureTime + voiceTime);
					if(item instanceof Chord) {
						Chord chord = ((Chord) item);
						for(Note note:chord.getNotes()) {
							itemMeasures.put(note, measure);
							itemVoices.put(note, voice);
							itemStartTimes.put(note, measureTime + voiceTime);
							noteChords.put(note, chord);
						}
					}
					
					voiceTime += item.getDurationCount();
				}
				
				maxVoiceTime = Math.max(maxVoiceTime, voiceTime);
			}
			
			measureTime += maxVoiceTime;
		}
	}
	
	/**
	 * Returns the start time of an item from the beginning of the score.
	 */
	public int getStartTime(Object item) {
		return itemStartTimes.get(item);
	}
	
	/**
	 * Returns the measure that contains an item.
	 */
	public Measure getMeasure(Object item) {
		return itemMeasures.get(item);
	}

	/**
	 * Returns the voice that contains an item.
	 */
	public Voice getVoice(Object item) {
		return itemVoices.get(item);
	}

	/**
	 * Returns the chord that contains a note.
	 */
	public Chord getChord(Note note) {
		return noteChords.get(note);
	}

	/**
	 * Returns the start time of a measure from the beginning of a score.
	 */
	public int getMeasureStartTime(Measure measure) {
		return measureStartTimes.get(measure);
	}
}