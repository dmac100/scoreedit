package score;

import static util.XmlUtil.addElement;

import org.eclipse.swt.graphics.Rectangle;
import org.jdom2.Element;

import score.Duration.DurationType;
import view.FetaFont;
import view.ScoreCanvas;

public class Note implements Selectable {
	private Pitch pitch;
	private Duration duration;
	private boolean selected;

	public Note(Pitch pitch, Duration duration) {
		this.pitch = pitch;
		this.duration = duration;
	}
	
	public Note(Element parent) {
		pitch = new Pitch(parent.getChild("pitch"));
		duration = new Duration(parent.getChild("duration"));
	}

	public void draw(ScoreCanvas canvas, Clef clef, int startX, int startY) {
		int scaleNumber = pitch.getScaleNumber() - clef.getLowScaleNumber();
		
		drawNoteHead(canvas, startX, startY, scaleNumber);
		
		drawDots(canvas, startX, startY, scaleNumber);
	}
	
	private void drawNoteHead(ScoreCanvas canvas, int startX, int startY, int scaleNumber) {
		if(duration.getType() == DurationType.WHOLE) {
			canvas.drawText(FetaFont.getNoteHead(duration), startX - 5, startY - (scaleNumber * 8) - 71, selected);
			canvas.setSelectableBounds(this, startX - 7, startY - (scaleNumber * 8) + 8*8+8, 34, 16);
		} else {
			canvas.drawText(FetaFont.getNoteHead(duration), startX, startY - (scaleNumber * 8) - 71, selected);
			canvas.setSelectableBounds(this, startX - 2, startY - (scaleNumber * 8) + 8*8+8, 24, 16);
		}
		
	}
		
	private void drawDots(ScoreCanvas canvas, int startX, int startY, int scaleNumber) {
		if(duration.getType() == DurationType.WHOLE) {
			startX += 5;
		}
		
		for(int x = 0; x < duration.getDots(); x++) {
			canvas.drawText(
				FetaFont.DOT,
				startX + 25 + (x * 10),
				startY - ((scaleNumber + ((Math.abs(scaleNumber + 1)) % 2)) * 8) - 63,
				selected
			);
		}
	}
	
	public Rectangle getBoundingBox(Clef clef, int startX, int startY) {
		int scaleNumber = pitch.getScaleNumber() - clef.getLowScaleNumber();
		
		return new Rectangle(
			startX - 2,
			startY - (scaleNumber * 8) + 8*8+8 - 4,
			24 + (duration.getDots() * 10),
			24
		);
	}

	public Pitch getPitch() {
		return pitch;
	}
	
	public void setPitch(Pitch pitch) {
		this.pitch = pitch;
	}
	
	public int getScaleNumber() {
		return pitch.getScaleNumber();
	}

	public int getSharps() {
		return pitch.getSharps();
	}
	
	public String toString() {
		return pitch.toString() + "-" + duration.toString();
	}

	public void save(Element parent) {
		pitch.save(addElement(parent, "pitch"));
		duration.save(addElement(parent, "duration"));
	}
	
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}