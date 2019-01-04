package score;

import static util.XmlUtil.addElement;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import score.Duration.DurationType;
import score.layout.AlignmentBox;
import view.FetaFont;
import view.ScoreCanvas;

public class Rest implements VoiceItem, Selectable {
	private Duration duration;
	
	private boolean selected;

	public Rest(Duration duration) {
		this.duration = duration;
	}

	public Rest(Element parent) {
		duration = new Duration(parent.getChild("duration"));
	}

	public void setClef(Clef clef) {
	}

	@Override
	public void draw(ScoreCanvas canvas, int startX, int startY, MeasureAccidentals measureAccidentals) {
		if(duration.getType() == DurationType.WHOLE) {
			canvas.drawText(FetaFont.getRest(duration), startX, startY - 134, selected);
		} else {
			canvas.drawText(FetaFont.getRest(duration), startX, startY - 119, selected);
		}
		
		canvas.setSelectableBounds(this, startX, startY, 20, 8*8);
		
		drawDots(canvas, startX, startY);
	}
	
	private void drawDots(ScoreCanvas canvas, int startX, int startY) {
		for(int x = 0; x < duration.getDots(); x++) {
			canvas.drawText(FetaFont.DOT, startX + 30 + (x * 10), startY - 119, selected);
		}
	}
	
	@Override
	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals) {
		return new AlignmentBox(25 + duration.getDots() * 10, 50, 0, 7);
	}

	@Override
	public int getDurationCount() {
		return duration.getDurationCount();
	}
	
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	@Override
	public void setAccidentals(MeasureAccidentals measureAccidentals) {
	}

	@Override
	public Beam getBeam() {
		return null;
	}
	
	public String toString() {
		return "[REST-" + duration + "]";
	}

	@Override
	public void save(Element parent, List<Beam> beams) {
		Element restElement = addElement(parent, "rest");
		duration.save(addElement(restElement, "duration"));
	}

	@Override
	public List<Note> getNotes() {
		return new ArrayList<>();
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}