package score;

import org.eclipse.swt.graphics.GC;

public class TimeSig {
	private final int upperCount;
	private final int lowerCount;
	
	public TimeSig(int upperCount, int lowerCount) {
		this.upperCount = upperCount;
		this.lowerCount = lowerCount;
	}
	
	public int getUpperCount() {
		return upperCount;
	}
	
	public int getLowerCount() {
		return lowerCount;
	}

	public boolean isCommonTime() {
		return (upperCount == 4 && lowerCount == 4);
	}

	public boolean isCutCommonTime() {
		return (upperCount == 2 && lowerCount == 4);
	}
	
	public boolean equals(Object other) {
		return (other instanceof TimeSig) && equals((TimeSig) other);
	}
	
	private boolean equals(TimeSig other) {
		return (upperCount == other.upperCount) && (lowerCount == other.lowerCount);
	}
	
	public void draw(GC gc, int startX, int startY, Measure previousMeasure) {
		for(Clef clef:Clef.values()) {
			if(previousMeasure == null || !previousMeasure.getTimeSig().equals(this)) {
				if(isCommonTime()) {
					gc.drawText(FetaFont.COMMON, startX, startY - 122 + clef.getOffset(), true);
				} else if(isCutCommonTime()) {
					gc.drawText(FetaFont.CUTCOMMON, startX, startY - 122 + clef.getOffset(), true);
				} else {
					gc.drawText(getTimeSigText(upperCount), startX, startY - 135 + clef.getOffset(), true);
					gc.drawText(getTimeSigText(lowerCount), startX, startY - 102 + clef.getOffset(), true);
				}
			}
		}
	}
	
	public int getWidth(Measure previousMeasureOnLine, Measure previousMeasure) {
		if(previousMeasure == null || !previousMeasure.getTimeSig().equals(this)) {
			return 40;
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