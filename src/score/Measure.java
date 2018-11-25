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
		int timeSigWidth = getTimeSigWidth(previousMeasureOnLine, previousMeasure);
		int keySigWidth = getKeySigWidth(previousMeasureOnLine, previousMeasure);
		
		drawKeySig(gc, startX, startY, previousMeasureOnLine, previousMeasure);
		
		drawTimeSig(gc, startX + keySigWidth, startY, previousMeasure);
		
		new NoteLayout(voices, extraWidth).getVoiceItems().forEach((voice, items) -> {
			int x = startX + timeSigWidth + keySigWidth;
			for(CanvasItem item:items) {
				//item.getAlignmentBox().draw(gc, x, startY + voice.getClef().getOffset());
				
				item.draw(gc, x, startY + voice.getClef().getOffset());
				
				x += item.getAlignmentBox().getWidth();
			}
		});
	}
	
	private void drawTimeSig(GC gc, int startX, int startY, Measure previousMeasure) {
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
	}
	
	private void drawKeySig(GC gc, int startX, int startY, Measure previousMeasureOnLine, Measure previousMeasure) {
		for(Clef clef:Clef.values()) {
			int extraClefOffset = (clef == Clef.BASS) ? 2*8 : 0;
			
			int x = startX;
			if(previousMeasureOnLine == null || !previousMeasureOnLine.keySig.equals(keySig)) {
				String text = (keySig.getFifths() > 0) ? FetaFont.SHARP : FetaFont.FLAT;
				for(Pitch pitch:keySig.getPitches()) {
					gc.drawText(text, x, startY - (pitch.getScaleNumber() * 8) + 113 + clef.getOffset() + extraClefOffset, true);
					x += 20;
				}
			}
		}
	}

	public Rectangle getBoundingBox(GC gc, int startX, int startY, Measure previousMeasureOnLine, Measure previousMeasure) {
		return new Rectangle(startX, startY, getWidth(previousMeasureOnLine, previousMeasure), 8*8);
	}
	
	public int getWidth(Measure previousMeasureOnLine, Measure previousMeasure) {
		int maxWidth = 0;
		
		int timeSigWidth = getTimeSigWidth(previousMeasureOnLine, previousMeasure);
		int keySigWidth = getKeySigWidth(previousMeasureOnLine, previousMeasure);
		
		for(List<CanvasItem> items:new NoteLayout(voices, 0).getVoiceItems().values()) {
			int width = timeSigWidth + keySigWidth;
			for(CanvasItem item:items) {
				width += item.getAlignmentBox().getWidth();
			}
			maxWidth = Math.max(maxWidth, width);
		}
		
		return maxWidth;
	}
	
	public int getTimeSigWidth(Measure previousMeasureOnLine, Measure previousMeasure) {
		if(previousMeasure == null || !previousMeasure.timeSig.equals(timeSig)) {
			return 40;
		} else {
			return 0;
		}
	}
	
	public int getKeySigWidth(Measure previousMeasureOnLine, Measure previousMeasure) {
		if(previousMeasureOnLine == null || !previousMeasureOnLine.keySig.equals(keySig)) {
			return Math.abs(keySig.getFifths()) * 20 + 30;
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