package score;

import org.eclipse.swt.graphics.GC;

import score.Duration.DurationType;

public class Rest implements CanvasItem {
	private final Duration duration;

	public Rest(Duration duration) {
		this.duration = duration;
	}

	@Override
	public void setClef(Clef clef) {
	}

	@Override
	public void draw(GC gc, int startX, int startY, MeasureAccidentals measureAccidentals) {
		if(duration.getType() == DurationType.WHOLE) {
			gc.drawText(getRest(), startX, startY - 134, true);
		} else {
			gc.drawText(getRest(), startX, startY - 119, true);
		}
	}
	
	private String getRest() {
		switch(duration.getType()) {
			case WHOLE: return FetaFont.WHOLEREST;
			case HALF: return FetaFont.HALFREST;
			case QUARTER: return FetaFont.QUARTERREST;
			case EIGHTH: return FetaFont.EIGHTHREST;
			case SIXTEENTH: return FetaFont.SIXTEENTHREST;
			case THIRTYSECOND: return FetaFont.THIRTYSECONDREST;
			default:
				throw new IllegalStateException("Unknown duration: " + duration.getType());
		}
	}

	@Override
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals) {
		return new AlignmentBox(25, 50, 0, 7);
	}

	@Override
	public int getDuration() {
		return duration.getDurationCount();
	}

	@Override
	public void setAccidentals(MeasureAccidentals measureAccidentals) {
	}

	@Override
	public Beam getBeam() {
		return null;
	}
}