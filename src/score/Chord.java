package score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import score.Duration.DurationType;

public class Chord implements CanvasItem {
	private enum StemDirection {
		UP, DOWN
	}

	private static class Stem {
		StemDirection direction;
		int startX;
		int startY;
		int endY;
	}
	
	private final int CSCALENUMBER = new Pitch("C4").getScaleNumber();
	
	private List<Note> notes;
	private Duration duration;

	public Chord(List<Note> notes, Duration duration) {
		this.notes = notes;
		this.duration = duration;
	}

	public void draw(GC gc, int startX, int startY) {
		Stem stem = getStem(startX, startY);
		
		drawNotes(gc, stem, startX, startY);
		
		drawStem(gc, stem);
	}
	
	private String getFlags(StemDirection direction) {
		switch(duration.getType()) {
			case WHOLE:
			case HALF:
			case QUARTER:
				return "";
			case EIGHTH:
				return (direction == StemDirection.DOWN) ? FetaFont.EIGHTHDOWNFLAG : FetaFont.EIGHTHUPFLAG;
			case SIXTEENTH:
				return (direction == StemDirection.DOWN) ? FetaFont.SIXTEENTHDOWNFLAG : FetaFont.SIXTEENTHUPFLAG;
			case THIRTYSECOND:
				return (direction == StemDirection.DOWN) ? FetaFont.THIRTYSECONDDOWNFLAG : FetaFont.THIRTYSECONDUPFLAG;
			default:
				throw new IllegalStateException("Unknown duration: " + duration.getType());
		}
	}

	public Rectangle getBoundingBox(int startX, int startY) {
		Stem stem = getStem(startX, startY);
		
		Set<Note> flippedNotes = getFlippedNotes(stem);
		
		Rectangle box = notes.get(0).getBoundingBox(startX, startY);
		for(Note note:notes) {
			if(flippedNotes.contains(note)) {
				box.add(note.getBoundingBox((stem.direction == StemDirection.UP) ? startX + 19 : startX - 19, startY));
			} else {
				box.add(note.getBoundingBox(startX, startY));
			}
		}
		return box;
	}
	
	private Stem getStem(int startX, int startY) {
		Stem stem = new Stem();
		int downCount = 0;
		for(Note note:notes) {
			if(note.getScaleNumber() - CSCALENUMBER > 5) {
				downCount++;
			} else {
				downCount--;
			}
		}
		stem.direction = (downCount > 0) ? StemDirection.DOWN : StemDirection.UP;
		
		stem.startX = (stem.direction == StemDirection.DOWN) ? (startX + 1) : startX + 19;

		int minScaleNumber = Integer.MAX_VALUE;
		int maxScaleNumber = Integer.MIN_VALUE;
		for(Note note:notes) {
			minScaleNumber = Math.min(minScaleNumber, note.getScaleNumber());
			maxScaleNumber = Math.max(maxScaleNumber, note.getScaleNumber());
		}
		
		if(stem.direction == StemDirection.DOWN) {
			stem.startY = startY + -((maxScaleNumber - CSCALENUMBER) * 8) + 81;
			stem.endY = startY + -((minScaleNumber - CSCALENUMBER) * 8) + 80 + 60;
			stem.endY = Math.max(stem.endY, startY + 33);
		} else {
			stem.startY = startY + -((minScaleNumber - CSCALENUMBER) * 8) + 80;
			stem.endY = startY + -((maxScaleNumber - CSCALENUMBER) * 8) + 80 - 60;
			stem.endY = Math.min(stem.endY, startY + 33);
		}
		
		return stem;
	}
	
	private void drawStem(GC gc, Stem stem) {
		if(duration.getType() == DurationType.WHOLE) {
			return;
		}
		
		gc.setLineWidth(3);
		gc.setLineCap(SWT.CAP_ROUND);
		gc.drawLine(stem.startX, stem.startY, stem.startX, stem.endY);
		gc.drawText(getFlags(stem.direction), stem.startX, stem.endY - 150, true);
	}

	private void drawNotes(GC gc, Stem stem, int startX, int startY) {
		int ledgersAbove = 0;
		int ledgersBelow = 0;
		int fatLedgersAbove = 0;
		int fatLedgersBelow = 0;
		
		Set<Note> flippedNotes = getFlippedNotes(stem);
		
		for(Note note:notes) {
			ledgersBelow = Math.max(ledgersBelow, -((note.getScaleNumber() - CSCALENUMBER) - 2) / 2);
			ledgersAbove = Math.max(ledgersAbove, ((note.getScaleNumber() - CSCALENUMBER) - 10) / 2);
			
			if(flippedNotes.contains(note)) {
				note.draw(gc, (stem.direction == StemDirection.UP) ? startX + 19 : startX - 19, startY);
				
				fatLedgersBelow = Math.max(fatLedgersBelow, -((note.getScaleNumber() - CSCALENUMBER) - 2) / 2);
				fatLedgersAbove = Math.max(fatLedgersAbove, ((note.getScaleNumber() - CSCALENUMBER) - 10) / 2);
			} else {
				note.draw(gc, startX, startY);
			}
		}
		
		drawLedgers(gc, stem, startX, startY, ledgersBelow, ledgersAbove, fatLedgersAbove, fatLedgersBelow);
	}
	
	private Set<Note> getFlippedNotes(Stem stem) {
		List<Note> sortedNotes = new ArrayList<>(notes);
		
		Collections.sort(sortedNotes, Comparator.comparingInt(note -> note.getScaleNumber()));
		if((stem.direction == StemDirection.DOWN)) {
			Collections.reverse(sortedNotes);
		}
		
		int prevScaleNumber = Integer.MIN_VALUE;
		
		Set<Note> flippedNotes = new HashSet<>();
		
		for(Note note:sortedNotes) {
			if(Math.abs(note.getScaleNumber() - prevScaleNumber) <= 1) {
				flippedNotes.add(note);
			}
			
			prevScaleNumber = note.getScaleNumber();
		}
		
		return flippedNotes;
	}
	
	private void drawLedgers(GC gc, Stem stem, int startX, int startY, int ledgersBelow, int ledgersAbove, int fatLedgersAbove, int fatLedgersBelow) {
		gc.setLineWidth(3);
		gc.setLineCap(SWT.CAP_SQUARE);
		
		for(int i = 0; i < ledgersAbove; i++) {
			gc.drawLine(
				startX - ((i < fatLedgersAbove && stem.direction == StemDirection.DOWN) ? 27 : 7),
				startY - ((i + 1) * 16),
				startX + ((i < fatLedgersAbove && stem.direction == StemDirection.UP) ? 47 : 27),
				startY - ((i + 1) * 16)
			);
		}
		
		for(int i = 0; i < ledgersBelow; i++) {
			gc.drawLine(
				startX - ((i < fatLedgersBelow && stem.direction == StemDirection.DOWN) ? 27 : 7),
				startY + ((5 + i) * 16),
				startX + ((i < fatLedgersBelow && stem.direction == StemDirection.UP) ? 47 : 27),
				startY + ((5 + i) * 16)
			);
		}
	}
}