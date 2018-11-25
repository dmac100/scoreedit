package score;

import java.util.List;

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
	
	public void drawMeasure(GC gc, int startX, int startY, int extraWidth, Measure previousMeasureOnLine, Measure previousMeasure) {
		int timeSigWidth = timeSig.getWidth(previousMeasureOnLine, previousMeasure);
		int keySigWidth = keySig.getWidth(previousMeasureOnLine, previousMeasure);
		
		keySig.draw(gc, startX, startY, previousMeasureOnLine, previousMeasure);
		
		timeSig.draw(gc, startX + keySigWidth, startY, previousMeasure);
		
		new NoteLayout(voices, extraWidth).getVoiceItems().forEach((voice, items) -> {
			int x = startX + timeSigWidth + keySigWidth;
			for(CanvasItem item:items) {
				//item.getAlignmentBox().draw(gc, x, startY + voice.getClef().getOffset());
				
				item.draw(gc, x, startY + voice.getClef().getOffset());
				
				x += item.getAlignmentBox().getWidth();
			}
		});
	}
	
	public Rectangle getBoundingBox(GC gc, int startX, int startY, Measure previousMeasureOnLine, Measure previousMeasure) {
		return new Rectangle(startX, startY, getWidth(previousMeasureOnLine, previousMeasure), 8*8);
	}
	
	public int getWidth(Measure previousMeasureOnLine, Measure previousMeasure) {
		int maxWidth = 0;
		
		int timeSigWidth = timeSig.getWidth(previousMeasureOnLine, previousMeasure);
		int keySigWidth = keySig.getWidth(previousMeasureOnLine, previousMeasure);
		
		for(List<CanvasItem> items:new NoteLayout(voices, 0).getVoiceItems().values()) {
			int width = timeSigWidth + keySigWidth;
			for(CanvasItem item:items) {
				width += item.getAlignmentBox().getWidth();
			}
			maxWidth = Math.max(maxWidth, width);
		}
		
		return maxWidth;
	}
	
	public TimeSig getTimeSig() {
		return timeSig;
	}
	
	public KeySig getKeySig() {
		return keySig;
	}
}