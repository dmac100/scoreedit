package score;

import static score.Duration.DurationType.EIGHTH;
import static score.Duration.DurationType.HALF;
import static score.Duration.DurationType.QUARTER;
import static score.Duration.DurationType.SIXTEENTH;
import static score.Duration.DurationType.THIRTYSECOND;
import static score.Duration.DurationType.WHOLE;
import static util.CollectionUtil.map;
import static util.CollectionUtil.sum;
import static util.XmlUtil.addElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jdom2.Element;

import score.Duration.DurationType;

public class Voice {
	private Clef clef;
	private List<CanvasItem> items;

	public Voice(Clef clef, List<CanvasItem> items) {
		this.clef = clef;
		this.items = new ArrayList<>(items);
	}

	public Voice(Element parent) {
		List<Beam> beams = new ArrayList<>();
		
		clef = Clef.valueOf(parent.getChildText("clef"));
		items = new ArrayList<>();
		for(Element child:parent.getChildren()) {
			if(child.getName().equals("rest")) {
				items.add(new Rest(child));
			} else if(child.getName().equals("chord")) {
				items.add(new Chord(child, beams));
			}
		}
	}
	
	public Clef getClef() {
		return clef;
	}
	
	public List<CanvasItem> getItems() {
		return new ArrayList<>(items);
	}
	
	public Pitch getPitchWithSharpsOrFlats(Pitch pitch, KeySig keySig, int startTime) {
		Pitch pitchWithSharpsOrFlats = null;
		
		int time = 0;
		for(CanvasItem item:items) {
			for(Note note:item.getNotes()) {
				if(new Pitch(note.getPitch().getScaleNumber()).equals(new Pitch(pitch.getScaleNumber()))) {
					pitchWithSharpsOrFlats = note.getPitch();
				}
			}
			
			time += item.getDuration();
			if(time > startTime) {
				break;
			}
		}
		
		if(pitchWithSharpsOrFlats != null) {
			return pitchWithSharpsOrFlats;
		}
		
		for(Pitch keySigPitch:keySig.getPitches()) {
			if(pitch.getName() == keySigPitch.getName()) {
				return new Pitch(pitch.getName(), pitch.getOctave(), (keySig.getFifths() > 1) ? 1 : -1);
			}
		}
		
		return pitch;
	}
	
	public int getStartTime(CanvasItem item) {
		int time = 0;
		for(CanvasItem voiceItem:items) {
			if(voiceItem == item) {
				return time;
			}
			time += voiceItem.getDuration();
		}
		throw new NoSuchElementException("Item not found: " + item);
	}

	public void removeItem(CanvasItem item) {
		items.remove(item);
	}
	
	public void replaceItem(CanvasItem oldItem, CanvasItem newItem) {
		int index = items.indexOf(oldItem);
		if(index >= 0) {
			items.remove(index);
			items.add(index, newItem);
		}
	}
	
	public void insertItemBefore(CanvasItem oldItem, CanvasItem newItem) {
		int index = items.indexOf(oldItem);
		if(index >= 0) {
			items.add(index, newItem);
		}
	}
	
	public void insertItem(CanvasItem newItem, int startTime) {
		Set<Beam> removedBeams = new HashSet<>();
		
		int i = 0;
		
		int time = 0;
		while(i < items.size()) {
			CanvasItem item = items.get(i++);
			time += item.getDuration();
			
			if(time > startTime) {
				i--;
				
				int removedTime = item.getDuration();
				time -= items.get(i).getDuration();
				removedBeams.add(items.get(i).getBeam());
				items.remove(i);
				
				while(removedTime < newItem.getDuration() && i < items.size()) {
					removedTime += items.get(i).getDuration();
					removedBeams.add(items.get(i).getBeam());
					items.remove(i);
				}
				
				if(startTime > time) {
					List<Rest> rests = createRests(startTime - time);
					items.addAll(i, rests);
					i += rests.size();
					
					removedTime -= (startTime - time);
				}
				
				items.add(i++, newItem);
				
				if(removedTime > newItem.getDuration()) {
					List<Rest> rests = createRests(removedTime - newItem.getDuration());
					items.addAll(i, rests);
					i += rests.size();
				}
				
				if(removedTime < newItem.getDuration()) {
					while(i < items.size() && items.get(i) instanceof Rest && removedTime < newItem.getDuration()) {
						removedTime += items.get(i).getDuration();
						removedBeams.add(items.get(i).getBeam());
						items.remove(i);
					}
					
					if(removedTime > newItem.getDuration()) {
						List<Rest> rests = createRests(removedTime - newItem.getDuration());
						items.addAll(i, rests);
						i += rests.size();
					}
				}
				
				removeBeams(removedBeams);
				
				return;
			}
		}
		
		if(startTime > time) {
			List<Rest> rests = createRests(startTime - time);
			items.addAll(i, rests);
			i += rests.size();
		}
		
		items.add(i++, newItem);
		
		removeBeams(removedBeams);
	}
	
	private void removeBeams(Set<Beam> beams) {
		for(CanvasItem item:items) {
			if(item instanceof Chord && item.getBeam() != null) {
				if(beams.contains(item.getBeam())) {
					((Chord) item).setBeam(null);
				}
			}
		}
	}
	
	public void autoBeam(int beamDuration) {
		List<Chord> beamedChords = new ArrayList<>();
		
		int time = 0;
		
		for(CanvasItem item:getItems()) {
			if(item instanceof Chord) {
				Chord chord = (Chord) item;
				
				chord.setBeam(null);
				
				if(item.getDuration() < 8) {
					beamedChords.add(chord);
					
					if(time / beamDuration != (time + item.getDuration()) / beamDuration) {
						addBeam(beamedChords);
						beamedChords.clear();
					}
					
					time += item.getDuration();
				} else {
					addBeam(beamedChords);
					beamedChords.clear();	
				}
			} else {
				addBeam(beamedChords);
				beamedChords.clear();
			}
		}
			
		addBeam(beamedChords);
	}
	
	private static void addBeam(List<Chord> beamedChords) {
		if(beamedChords.size() > 1) {
			Beam beam = new Beam();
			beamedChords.forEach(c -> c.setBeam(beam));
		}
	}

	private static List<Rest> createRests(int duration) {
		int remaining = duration;
		List<Rest> rests = new ArrayList<>();
		while(remaining > 0) {
			for(DurationType durationType:Arrays.asList(WHOLE, HALF, QUARTER, EIGHTH, SIXTEENTH, THIRTYSECOND)) {
				Duration d = new Duration(durationType);
				if(d.getDurationCount() <= remaining) {
					remaining -= d.getDurationCount();
					rests.add(new Rest(new Duration(durationType)));
					break;
				}
			}
		}
		return rests;
	}
	
	/**
	 * Collapse consecutive rests in this voice that are in the given set with rests of an equal duration.
	 */
	public void collapseRests(Set<Rest> rests) {
		replaceSpans(items, item -> rests.contains(item), replacementList -> {
			int totalDuration = sum(map(replacementList, item -> item.getDuration()));
			return createRests(totalDuration);
		});
	}
	
	/**
	 * Replaces spans of elements in list matching predicate with the replacement function.
	 */
	private static <T> void replaceSpans(List<T> list, Predicate<T> predicate, Function<List<T>, List<? extends T>> replacementFunction) {
		int startIndex = -1;
		for(int i = 0; i < list.size(); i++) {
			if(predicate.test(list.get(i))) {
				if(startIndex == -1) {
					startIndex = i;
				}
			} else {
				if(startIndex != -1) {
					int endIndex = i;
					
					List<T> subList = list.subList(startIndex, endIndex);
					List<? extends T> replacementList = replacementFunction.apply(subList);
					
					i -= subList.size();
					subList.clear();
					
					i += replacementList.size();
					subList.addAll(replacementList);
					
					startIndex = -1;
				}
			}
		}
		
		if(startIndex != -1) {
			List<T> subList = list.subList(startIndex, list.size());
			List<? extends T> replacementList = replacementFunction.apply(subList);
			
			subList.clear();
			subList.addAll(replacementList);
			
			startIndex = -1;
		}
	}
	
	public String toString() {
		return items.toString();
	}

	public void save(Element parent) {
		List<Beam> beams = new ArrayList<>();
		clef.save(addElement(parent, "clef"));
		for(CanvasItem canvasItem:items) {
			canvasItem.save(parent, beams);
		}
	}
}