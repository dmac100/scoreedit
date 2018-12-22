package score;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import score.layout.AlignmentBox;
import view.ScoreCanvas;

public interface CanvasItem {
	public default void setClef(Clef clef) {};
	
	public default void draw(ScoreCanvas canvas, int startX, int startY, MeasureAccidentals measureAccidentals) {};
	
	public default AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals) {
		return new AlignmentBox(0, 0, 0, 0);
	}
	
	public default int getDuration() {
		return 0;
	}
	
	public default void setAccidentals(MeasureAccidentals measureAccidentals) {};
	
	public default Beam getBeam() {
		return null;
	}
	
	public default void save(Element parent, List<Beam> beams) {};
	
	public default List<Note> getNotes() {
		return new ArrayList<>();
	}
}