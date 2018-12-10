package score;

import org.eclipse.swt.graphics.Rectangle;

import score.Duration.DurationType;

public class Note {
	private Pitch pitch;
	private Duration duration;

	public Note(Pitch pitch, Duration duration) {
		this.pitch = pitch;
		this.duration = duration;
	}
	
	public void draw(ScoreCanvas layout, Clef clef, int startX, int startY) {
		int scaleNumber = pitch.getScaleNumber() - clef.getLowScaleNumber();
		
		drawNoteHead(layout, startX, startY, scaleNumber);
		
		drawDots(layout, startX, startY, scaleNumber);
	}
	
	private void drawNoteHead(ScoreCanvas canvas, int startX, int startY, int scaleNumber) {
		if(duration.getType() == DurationType.WHOLE) {
			startX -= 5;
		}
		canvas.drawText(FetaFont.getNoteHead(duration), startX, startY - (scaleNumber * 8) - 71);
	}
		
	private void drawDots(ScoreCanvas canvas, int startX, int startY, int scaleNumber) {
		if(duration.getType() == DurationType.WHOLE) {
			startX += 5;
		}
		
		for(int x = 0; x < duration.getDots(); x++) {
			canvas.drawText(
				FetaFont.DOT,
				startX + 25 + (x * 10),
				startY - ((scaleNumber + ((Math.abs(scaleNumber + 1)) % 2)) * 8) - 63
			);
		}
	}
	
	public Rectangle getBoundingBox(Clef clef, int startX, int startY) {
		int scaleNumber = pitch.getScaleNumber() - clef.getLowScaleNumber();
		
		return new Rectangle(
			startX - 2,
			startY - (scaleNumber * 8) + 2 * 80 - 92,
			24 + (duration.getDots() * 10),
			24
		);
	}

	public Pitch getPitch() {
		return pitch;
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
}