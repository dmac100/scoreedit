package score;

import static score.Duration.DurationType.EIGHTH;
import static score.Duration.DurationType.HALF;
import static score.Duration.DurationType.QUARTER;
import static score.Duration.DurationType.SIXTEENTH;
import static score.Duration.DurationType.THIRTYSECOND;
import static score.Duration.DurationType.WHOLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import score.Duration.DurationType;

public class Voice {
	private Clef clef;
	private List<CanvasItem> items;

	public Voice(Clef clef, List<CanvasItem> items) {
		this.clef = clef;
		this.items = new ArrayList<>(items);
	}

	public Clef getClef() {
		return clef;
	}
	
	public List<CanvasItem> getItems() {
		return items;
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
	
	public void insertItem(CanvasItem newItem, int startTime) {
		int i = 0;
		
		int time = 0;
		while(i < items.size()) {
			CanvasItem item = items.get(i++);
			time += item.getDuration();
			
			if(time > startTime) {
				i--;
				
				int removedTime = item.getDuration();
				items.remove(i);
				time -= item.getDuration();
				
				while(removedTime < newItem.getDuration() && i < items.size()) {
					items.remove(i);
					removedTime += item.getDuration();
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
				
				return;
			}
		}
		
		if(startTime > time) {
			List<Rest> rests = createRests(startTime - time);
			items.addAll(i, rests);
			i += rests.size();
		}
		
		items.add(i++, newItem);
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
	
	public String toString() {
		return items.toString();
	}
}