package score;

import static util.XmlUtil.addElement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.jdom2.Element;

import score.layout.AlignmentBox;
import score.layout.ItemLayout;
import view.ScoreCanvas;

/**
 * A single measure containing the voices for each clef.
 */
public class Measure {
	private List<Voice> voices;
	private TimeSig timeSig;
	private KeySig keySig;

	public Measure() {
		this(new ArrayList<>(), new TimeSig(4, 4), new KeySig(0));
	}
	
	public Measure(List<Voice> voices, TimeSig timeSig, KeySig keySig) {
		this.voices = voices;
		this.timeSig = timeSig;
		this.keySig = keySig;
	}
	
	public Measure(Element parent) {
		voices = new ArrayList<>();
		keySig = new KeySig(parent.getChild("keySig"));
		timeSig = new TimeSig(parent.getChild("timeSig"));
		for(Element voiceElement:parent.getChildren("voice")) {
			voices.add(new Voice(voiceElement));
		}
	}
	
	public void drawMeasure(ScoreCanvas canvas, int startX, int startY, int extraWidth, Measure previousMeasureOnLine, Measure previousMeasure) {
		int timeSigWidth = timeSig.getWidth(previousMeasureOnLine, previousMeasure);
		int keySigWidth = keySig.getWidth(previousMeasureOnLine, previousMeasure);
		
		keySig.draw(canvas, startX, startY, previousMeasureOnLine, previousMeasure);
		
		timeSig.draw(canvas, startX + keySigWidth, startY, previousMeasure);
		
		// Find position of each item for each voice.
		new ItemLayout(keySig, voices, extraWidth).getVoiceItems().forEach((voice, items) -> {
			List<Beam> beams = getBeams(items);
			
			beams.forEach(beam -> beam.clearStems());
			
			// Draw item and advance to next item.
			MeasureAccidentals measureAccidentals = new MeasureAccidentals(keySig);
			int x = startX + timeSigWidth + keySigWidth;
			for(VoiceItem item:items) {
				item.draw(canvas, x, startY + voice.getClef().getOffset(), measureAccidentals);
				
				AlignmentBox alignmentBox = item.getAlignmentBox(measureAccidentals);
				
				canvas.setItemBounds(item, x + alignmentBox.getCenter(), startY + voice.getClef().getOffset(), alignmentBox.getWidth() - alignmentBox.getCenter(), 8*8);
				
				x += alignmentBox.getWidth();
				
				item.setAccidentals(measureAccidentals);
			}
			
			// Draw any beams that have been added.
			for(Beam beam:beams) {
				beam.draw(canvas);
			}
		});
	}
	
	/**
	 * Returns all the unique beams that are contained in items.
	 */
	private static List<Beam> getBeams(List<VoiceItem> items) {
		Set<Beam> beams = new LinkedHashSet<>();
		for(VoiceItem item:items) {
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
	
	/**
	 * Returns the width of this measure, given the previous measure, and the previous on the same line if any.
	 * Adds the width of all items, and any key signatures or time signatures that need to be added.
	 */
	public int getWidth(Measure previousMeasureOnLine, Measure previousMeasure) {
		int maxWidth = 0;
		
		int timeSigWidth = timeSig.getWidth(previousMeasureOnLine, previousMeasure);
		int keySigWidth = keySig.getWidth(previousMeasureOnLine, previousMeasure);
		
		for(List<VoiceItem> items:new ItemLayout(keySig, voices, 0).getVoiceItems().values()) {
			MeasureAccidentals measureAccidentals = new MeasureAccidentals(keySig);
			int width = timeSigWidth + keySigWidth;
			for(VoiceItem item:items) {
				width += item.getAlignmentBox(measureAccidentals).getWidth();
			}
			maxWidth = Math.max(maxWidth, width);
		}
		
		return maxWidth;
	}
	
	/**
	 * Returns all the items of every voice in this measure.
	 */
	public Set<VoiceItem> getVoiceItems() {
		Set<VoiceItem> items = new LinkedHashSet<>();
		for(Voice voice:voices) {
			items.addAll(voice.getItems());
		}
		return items;
	}
	
	public TimeSig getTimeSig() {
		return timeSig;
	}
	
	public void setTimeSig(TimeSig timeSig) {
		this.timeSig = timeSig;
	}
	
	public KeySig getKeySig() {
		return keySig;
	}
	
	public void setKeySig(KeySig keySig) {
		this.keySig = keySig;
	}
	
	/**
	 * Auto beams every voice in this measure.
	 */
	public void autoBeam() {
		for(Voice voice:voices) {
			voice.autoBeam(Duration.WHOLEDURATIONCOUNT / timeSig.getLowerCount());
		}
	}
	
	/**
	 * Returns the voice that contains the given item.
	 */
	public Voice getVoice(VoiceItem item) {
		for(Voice voice:voices) {
			for(VoiceItem voiceItem:voice.getItems()) {
				if(voiceItem == item) {
					return voice;
				}
			}
		}
		return null;
	}
	
	public List<Voice> getVoices() {
		return voices;
	}

	/**
	 * Returns all the voices with the given clef.
	 */
	public List<Voice> getVoices(Clef clef) {
		List<Voice> voices = new ArrayList<>();
		for(Voice voice:this.voices) {
			if(voice.getClef() == clef) {
				voices.add(voice);
			}
		}
		return voices;
	}

	/**
	 * Returns the start time of an the given item.
	 */
	public int getStartTime(VoiceItem item) {
		return getVoice(item).getStartTime(item);
	}

	public void save(Element parent) {
		keySig.save(addElement(parent, "keySig"));
		timeSig.save(addElement(parent, "timeSig"));
		for(Voice voice:voices) {
			voice.save(addElement(parent, "voice"));
		}
	}
}