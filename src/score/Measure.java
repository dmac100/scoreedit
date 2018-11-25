package score;

import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class Measure {
	private Measure previousMeasure;
	private List<Voice> voices;
	private TimeSig timeSig;

	public Measure(List<Voice> voices, TimeSig timeSig) {
		this.voices = voices;
		this.timeSig = timeSig;
	}
	
	public void setPreviousMeasure(Measure previousMeasure) {
		this.previousMeasure = previousMeasure;
	}

	public void drawMeasure(GC gc, int startX, int startY, int extraWidth) {
		for(Clef clef:Clef.values()) {
			if(previousMeasure == null || !previousMeasure.timeSig.equals(timeSig)) {
				if(timeSig.isCommonTime()) {
					gc.drawText(FetaFont.COMMON, startX, startY - 122 + clef.getOffset(), true);
				} else if(timeSig.isCutCommonTime()) {
					gc.drawText(FetaFont.CUTCOMMON, startX, startY - 122 + clef.getOffset(), true);
				} else {
					gc.drawText(getTimeSigText(timeSig.getUpperCount()), startX, startY - 135 + clef.getOffset(), true);
					gc.drawText(getTimeSigText(timeSig.getLowerCount()), startX, startY - 102 + clef.getOffset(), true);
				}
			}
		}
		
		new NoteLayout(voices, extraWidth).getVoiceItems().forEach((voice, items) -> {
			int x = startX + getTimeSigWidth();
			for(CanvasItem item:items) {
				//item.getAlignmentBox(x, startY + voice.getClef().getOffset()).draw(gc);
				item.draw(gc, x, startY + voice.getClef().getOffset());
				
				x += item.getAlignmentBox().getWidth();
			}
		});
	}
	
	public Rectangle getBoundingBox(GC gc, int startX, int startY) {
		return new Rectangle(startX, startY, getWidth(), 8*8);
	}
	
	public int getWidth() {
		int maxWidth = 0;
		
		for(List<CanvasItem> items:new NoteLayout(voices, 0).getVoiceItems().values()) {
			int width = getTimeSigWidth();
			for(CanvasItem item:items) {
				width += item.getAlignmentBox().getWidth();
			}
			maxWidth = Math.max(maxWidth, width);
		}
		
		return maxWidth;
	}
	
	public int getTimeSigWidth() {
		if(previousMeasure == null || !previousMeasure.timeSig.equals(timeSig)) {
			return 50;
		} else {
			return 0;
		}
	}
	
	private static String getTimeSigText(int count) {
		switch(count) {
			case 0: return FetaFont.TIME0;
			case 1: return FetaFont.TIME1;
			case 2: return FetaFont.TIME2;
			case 3: return FetaFont.TIME3;
			case 4: return FetaFont.TIME4;
			case 5: return FetaFont.TIME5;
			case 6: return FetaFont.TIME6;
			case 7: return FetaFont.TIME7;
			case 8: return FetaFont.TIME8;
			case 9: return FetaFont.TIME9;
			default: throw new IllegalArgumentException("Unknown count: " + count);
		}
	}
}