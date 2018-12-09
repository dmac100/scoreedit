package score;

public interface CanvasItem {
	public void setClef(Clef clef);
	public void draw(ScoreCanvas canvas, int startX, int startY, MeasureAccidentals measureAccidentals);
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals);
	public int getDuration();
	public void setAccidentals(MeasureAccidentals measureAccidentals);
	public Beam getBeam();
}