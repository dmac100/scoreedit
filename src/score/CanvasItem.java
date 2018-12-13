package score;

import java.util.List;
import java.util.Set;

import org.jdom2.Element;

public interface CanvasItem {
	public void setClef(Clef clef);
	public void draw(ScoreCanvas canvas, int startX, int startY, MeasureAccidentals measureAccidentals);
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals);
	public int getDuration();
	public void setAccidentals(MeasureAccidentals measureAccidentals);
	public Beam getBeam();
	public void save(Element parent, List<Beam> beams);
}