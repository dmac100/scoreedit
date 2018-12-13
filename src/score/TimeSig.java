package score;

import static util.XmlUtil.addElement;

import org.jdom2.Element;

public class TimeSig {
	private final int upperCount;
	private final int lowerCount;
	
	public TimeSig(int upperCount, int lowerCount) {
		this.upperCount = upperCount;
		this.lowerCount = lowerCount;
	}
	
	public TimeSig(Element parent) {
		upperCount = Integer.parseInt(parent.getChildText("upperCount"));
		lowerCount = Integer.parseInt(parent.getChildText("lowerCount"));
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
	
	public void draw(ScoreCanvas canvas, int startX, int startY, Measure previousMeasure) {
		for(Clef clef:Clef.values()) {
			if(previousMeasure == null || !previousMeasure.getTimeSig().equals(this)) {
				if(isCommonTime()) {
					canvas.drawText(FetaFont.COMMON, startX, startY - 122 + clef.getOffset());
				} else if(isCutCommonTime()) {
					canvas.drawText(FetaFont.CUTCOMMON, startX, startY - 122 + clef.getOffset());
				} else {
					canvas.drawText(FetaFont.getTimeSigText(upperCount), startX, startY - 135 + clef.getOffset());
					canvas.drawText(FetaFont.getTimeSigText(lowerCount), startX, startY - 102 + clef.getOffset());
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

	public void save(Element parent) {
		addElement(parent, "upperCount", upperCount);
		addElement(parent, "lowerCount", lowerCount);
	}
}