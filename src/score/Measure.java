package score;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class Measure {
	private List<Voice> voices;
	private TimeSig timeSig;
	private KeySig keySig;

	public Measure(List<Voice> voices, TimeSig timeSig, KeySig keySig) {
		this.voices = voices;
		this.timeSig = timeSig;
		this.keySig = keySig;
	}
	
	public void drawMeasure(ScoreCanvas canvas, int startX, int startY, int extraWidth, Measure previousMeasureOnLine, Measure previousMeasure) {
		int timeSigWidth = timeSig.getWidth(previousMeasureOnLine, previousMeasure);
		int keySigWidth = keySig.getWidth(previousMeasureOnLine, previousMeasure);
		
		keySig.draw(canvas, startX, startY, previousMeasureOnLine, previousMeasure);
		
		timeSig.draw(canvas, startX + keySigWidth, startY, previousMeasure);
		
		new NoteLayout(keySig, voices, extraWidth).getVoiceItems().forEach((voice, items) -> {
			List<Beam> beams = getBeams(items);
			
			beams.forEach(beam -> beam.clearStems());
			
			MeasureAccidentals measureAccidentals = new MeasureAccidentals(keySig);
			int x = startX + timeSigWidth + keySigWidth;
			for(CanvasItem item:items) {
				item.draw(canvas, x, startY + voice.getClef().getOffset(), measureAccidentals);
				
				//item.getAlignmentBox(measureAccidentals).draw(gc, x, startY + voice.getClef().getOffset());
				
				AlignmentBox alignmentBox = item.getAlignmentBox(measureAccidentals);
				
				canvas.setItemBounds(item, x + alignmentBox.getCenter(), startY + voice.getClef().getOffset(), alignmentBox.getWidth() - alignmentBox.getCenter(), 8*8);
				
				x += alignmentBox.getWidth();
				
				item.setAccidentals(measureAccidentals);
			}
			
			for(Beam beam:beams) {
				beam.draw(canvas);
			}
		});
	}
	
	private static List<Beam> getBeams(List<CanvasItem> items) {
		Set<Beam> beams = new LinkedHashSet<>();
		for(CanvasItem item:items) {
			Beam beam = item.getBeam();
			if(beam != null) {
				beams.add(beam);
			}
		}
		return new ArrayList<>(beams);
	}

	public Rectangle getBoundingBox(GC gc, int startX, int startY, Measure previousMeasureOnLine, Measure previousMeasure) {
		return new Rectangle(startX, startY, getWidth(previousMeasureOnLine, previousMeasure), 8*8);
	}
	
	public int getWidth(Measure previousMeasureOnLine, Measure previousMeasure) {
		int maxWidth = 0;
		
		int timeSigWidth = timeSig.getWidth(previousMeasureOnLine, previousMeasure);
		int keySigWidth = keySig.getWidth(previousMeasureOnLine, previousMeasure);
		
		for(List<CanvasItem> items:new NoteLayout(keySig, voices, 0).getVoiceItems().values()) {
			MeasureAccidentals measureAccidentals = new MeasureAccidentals(keySig);
			int width = timeSigWidth + keySigWidth;
			for(CanvasItem item:items) {
				width += item.getAlignmentBox(measureAccidentals).getWidth();
			}
			maxWidth = Math.max(maxWidth, width);
		}
		
		return maxWidth;
	}
	
	public Set<CanvasItem> getCanvasItems() {
		Set<CanvasItem> items = new LinkedHashSet<>();
		for(Voice voice:voices) {
			items.addAll(voice.getItems());
		}
		return items;
	}
	
	public TimeSig getTimeSig() {
		return timeSig;
	}
	
	public KeySig getKeySig() {
		return keySig;
	}
	
	public Voice getVoice(CanvasItem item) {
		for(Voice voice:voices) {
			for(CanvasItem voiceItem:voice.getItems()) {
				if(voiceItem == item) {
					return voice;
				}
			}
		}
		return null;
	}
	
	public List<Voice> getVoices(Clef clef) {
		List<Voice> voices = new ArrayList<>();
		for(Voice voice:this.voices) {
			if(voice.getClef() == clef) {
				voices.add(voice);
			}
		}
		return voices;
	}

	public int getStartTime(CanvasItem item) {
		return getVoice(item).getStartTime(item);
	}
}