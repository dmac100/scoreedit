package score;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import score.Duration.DurationType;

public class Note implements CanvasItem {
	private Pitch pitch;
	private Duration duration;

	public Note(Pitch pitch, Duration duration) {
		this.pitch = pitch;
		this.duration = duration;
	}
	
	public void draw(GC gc, int startX, int startY) {
		int scaleNumber = pitch.getScaleNumber() - 23;
		
		drawNoteHead(gc, startX, startY, scaleNumber);
		
		drawDots(gc, startX, startY, scaleNumber);
		
		drawStem(gc, startX, startY, scaleNumber);
		
		int ledgersBelow = (scaleNumber <= 0) ? -((scaleNumber - 2) / 2) : 0;
		int ledgersAbove = (scaleNumber > 10) ? ((scaleNumber - 10) / 2) : 0;
		
		drawLedgers(gc, startX, startY, ledgersBelow, ledgersAbove);
	}
	
	private String getNoteHead() {
		switch(duration.getType()) {
			case WHOLE:
				return FetaFont.WHOLENOTEHEAD;
			case HALF:
				return FetaFont.HALFNOTEHEAD;
			case QUARTER:
			case EIGHTH:
			case SIXTEENTH:
			case THIRTYSECOND:
				return FetaFont.QUARTERNOTEHEAD;
			default:
				throw new IllegalStateException("Unknown duration: " + duration.getType());
		}
	}
	
	private String getFlags(boolean down) {
		switch(duration.getType()) {
			case WHOLE:
			case HALF:
			case QUARTER:
				return "";
			case EIGHTH:
				return down ? FetaFont.EIGHTHDOWNFLAG : FetaFont.EIGHTHUPFLAG;
			case SIXTEENTH:
				return down ? FetaFont.SIXTEENTHDOWNFLAG : FetaFont.SIXTEENTHUPFLAG;
			case THIRTYSECOND:
				return down ? FetaFont.THIRTYSECONDDOWNFLAG : FetaFont.THIRTYSECONDUPFLAG;
			default:
				throw new IllegalStateException("Unknown duration: " + duration.getType());
		}
	}

	private void drawNoteHead(GC gc, int startX, int startY, int scaleNumber) {
		if(duration.getType() == DurationType.WHOLE) {
			startX -= 5;
		}
		gc.drawText(getNoteHead(), startX, startY - (scaleNumber * 8) - 71, true);
	}
		
	private void drawDots(GC gc, int startX, int startY, int scaleNumber) {
		if(duration.getType() == DurationType.WHOLE) {
			startX += 5;
		}
		
		for(int x = 0; x < duration.getDots(); x++) {
			gc.drawText(FetaFont.DOT,
				startX + 25 + (x * 10),
				startY - ((scaleNumber + ((Math.abs(scaleNumber + 1)) % 2)) * 8) - 63,
				true
			);
		}
	}
	
	public Rectangle getBoundingBox(int startX, int startY) {
		int scaleNumber = pitch.getScaleNumber() - 23;
		
		return new Rectangle(
			startX - 2,
			startY - (scaleNumber * 8) + 2 * 80 - 92,
			24 + (duration.getDots() * 10),
			24
		);
	}
	
	private void drawStem(GC gc, int startX, int startY, int scaleNumber) {
		if(duration.getType() == DurationType.WHOLE) {
			return;
		}
		
		gc.setLineWidth(3);
		gc.setLineCap(SWT.CAP_ROUND);
		
		if(scaleNumber > 5) {
			gc.drawText(
				getFlags(true),
				startX,
				startY + Math.max(32, -(scaleNumber * 8) + 60 + 80) - 151,
				true
			);
			
			gc.drawLine(
				startX + 1,
				startY - (scaleNumber * 8) + 2 + 80,
				startX + 1,
				startY + Math.max(32, -(scaleNumber * 8) + 60 + 80)
			);
		} else {
			gc.drawText(
				getFlags(false),
				startX + 19,
				startY + Math.min(32, -(scaleNumber * 8) - 60 + 80) - 151,
				true
			);
			
			gc.drawLine(
				startX + 19,
				startY - (scaleNumber * 8) - 2 + 80,
				startX + 19,
				startY + Math.min(32, -(scaleNumber * 8) - 60 + 80)
			);
		}
	}

	private void drawLedgers(GC gc, int startX, int startY, int ledgersBelow, int ledgersAbove) {
		gc.setLineWidth(3);
		gc.setLineCap(SWT.CAP_SQUARE);
		
		for(int i = 0; i < ledgersAbove; i++) {
			gc.drawLine(
				startX - 7,
				startY - ((i + 1) * 16),
				startX + 27,
				startY - ((i + 1) * 16)
			);
		}
		
		for(int i = 0; i < ledgersBelow; i++) {
			gc.drawLine(
				startX - 7,
				startY + ((5 + i) * 16),
				startX + 27,
				startY + ((5 + i) * 16)
			);
		}
	}
}