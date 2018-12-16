package score;

import java.util.List;

import org.jdom2.Element;

import score.layout.AlignmentBox;
import view.ScoreCanvas;

public interface CanvasItem {
	public void setClef(Clef clef);
	public void draw(ScoreCanvas canvas, int startX, int startY, MeasureAccidentals measureAccidentals);
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals);
	public int getDuration();
	public void setAccidentals(MeasureAccidentals measureAccidentals);
	public Beam getBeam();
	public void save(Element parent, List<Beam> beams);
	public List<Note> getNotes();
}