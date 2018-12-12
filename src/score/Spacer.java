package score;

public class Spacer implements CanvasItem {
	private final int spacing;
	
	public Spacer(int spacing) {
		this.spacing = spacing;
	}
	
	public void setClef(Clef clef) {
	}

	@Override
	public void draw(ScoreCanvas layout, int startX, int startY, MeasureAccidentals measureAccidentals) {
	}

	@Override
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals) {
		return new AlignmentBox(spacing, 0, 0, 32);
	}

	@Override
	public int getDuration() {
		return 0;
	}

	@Override
	public void setAccidentals(MeasureAccidentals measureAccidentals) {
	}

	@Override
	public Beam getBeam() {
		return null;
	}
}