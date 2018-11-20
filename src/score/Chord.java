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
	
	private final int ACCIDENTALSPACING = 25;
	
	private List<Note> notes;
	private Duration duration;
	private Clef clef = Clef.TREBLE;

	public Chord(List<Note> notes, Duration duration) {
		this.notes = notes;
		this.duration = duration;
	}
	
	public void setClef(Clef clef) {
		this.clef = clef;
	}
	
	@Override
	public int getDuration() {
		return duration.getDurationCount();
	}

	public void draw(GC gc, int startX, int startY) {
		List<List<Note>> accidentalLayout = getAccidentalLayout();
		
		Stem stem = getStem(startX + accidentalLayout.size() * ACCIDENTALSPACING, startY);
		
		Set<Note> flippedNotes = getFlippedNotes(stem);
		
		boolean shiftStemRight = (!flippedNotes.isEmpty() && stem.direction == StemDirection.DOWN);
		
		drawAccidentals(gc, accidentalLayout, startX, startY);
		
		drawNotes(gc, stem, flippedNotes, startX + accidentalLayout.size() * ACCIDENTALSPACING + (shiftStemRight ? 19 : 0), startY);
		
		if(shiftStemRight) {
			stem.startX += 19;
		}
		
		drawStem(gc, stem);
	}
	
	private void drawAccidentals(GC gc, List<List<Note>> accidentalLayout, int startX, int startY) {
		int x = startX + accidentalLayout.size() * ACCIDENTALSPACING;
		for(List<Note> notes:accidentalLayout) {
			for(Note note:notes) {
				int sharps = note.getSharps();
				int scaleNumber = note.getScaleNumber() - clef.getLowScaleNumber();
				if(sharps != 0) {
					gc.drawText(getAccidental(note.getSharps()), x - ACCIDENTALSPACING, startY - (scaleNumber * 8) - 71, true);
				}
			}
			x -= ACCIDENTALSPACING;
		}
	}
	
	private List<List<Note>> getAccidentalLayout() {
		List<List<Note>> layout = new ArrayList<>();
		
		List<Note> notesRemaining = new ArrayList<>();
		for(Note note:notes) {
			if(note.getSharps() != 0) {
				notesRemaining.add(note);
			}
		}
		
		Collections.sort(notesRemaining, Comparator.comparing(note -> note.getSharps()));
		
		while(!notesRemaining.isEmpty()) {
			Set<Integer> usedScaleNumbers = new HashSet<>();
			List<Note> nextNotes = new ArrayList<>();
			for(Note note:notesRemaining) {
				int scaleNumber = note.getScaleNumber();
				
				boolean occupied = false;
				for(int i = -3; i <= 3; i++) {
					if(usedScaleNumbers.contains(scaleNumber + i)) {
						occupied = true;
					}
				}
				
				if(!occupied) {
					usedScaleNumbers.add(scaleNumber);
					nextNotes.add(note);
				}
			}
			notesRemaining.removeAll(nextNotes);
			layout.add(nextNotes);
		}
		
		return layout;
	}

	private static String getAccidental(int sharps) {
		switch(sharps) {
			case 1: return FetaFont.SHARP;
			case 2: return FetaFont.DOUBLESHARP;
			case -1: return FetaFont.FLAT;
			case -2: return FetaFont.DOUBLEFLAT;
			default: throw new IllegalArgumentException("Unknown sharps: " + sharps);
		}
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

	public AlignmentBox getAlignmentBox() {
		int startX = 0;
		int startY = 0;
		
		Stem stem = getStem(startX, startY);
		
		List<List<Note>> accidentalLayout = getAccidentalLayout();
		
		Set<Note> flippedNotes = getFlippedNotes(stem);
		
		Rectangle box = notes.get(0).getBoundingBox(clef, startX, startY);
		for(Note note:notes) {
			if(flippedNotes.contains(note)) {
				box.add(note.getBoundingBox(clef, startX + 19 + accidentalLayout.size() * ACCIDENTALSPACING, startY));
			} else {
				box.add(note.getBoundingBox(clef, startX + accidentalLayout.size() * ACCIDENTALSPACING, startY));
			}
		}
		return new AlignmentBox(box.width, box.height, accidentalLayout.size() * ACCIDENTALSPACING);
	}
	
	private Stem getStem(int startX, int startY) {
		Stem stem = new Stem();
		int downCount = 0;
		for(Note note:notes) {
			if(note.getScaleNumber() - clef.getLowScaleNumber() > 5) {
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
			stem.startY = startY + -((maxScaleNumber - clef.getLowScaleNumber()) * 8) + 81;
			stem.endY = startY + -((minScaleNumber - clef.getLowScaleNumber()) * 8) + 80 + 60;
			stem.endY = Math.max(stem.endY, startY + 33);
		} else {
			stem.startY = startY + -((minScaleNumber - clef.getLowScaleNumber()) * 8) + 80;
			stem.endY = startY + -((maxScaleNumber - clef.getLowScaleNumber()) * 8) + 80 - 60;
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

	private void drawNotes(GC gc, Stem stem, Set<Note> flippedNotes, int startX, int startY) {
		int ledgersAbove = 0;
		int ledgersBelow = 0;
		int fatLedgersAbove = 0;
		int fatLedgersBelow = 0;
		
		for(Note note:notes) {
			ledgersBelow = Math.max(ledgersBelow, -((note.getScaleNumber() - clef.getLowScaleNumber()) - 2) / 2);
			ledgersAbove = Math.max(ledgersAbove, ((note.getScaleNumber() - clef.getLowScaleNumber()) - 10) / 2);
			
			if(flippedNotes.contains(note)) {
				note.draw(gc, clef, (stem.direction == StemDirection.UP) ? startX + 19 : startX - 19, startY);
				
				fatLedgersBelow = Math.max(fatLedgersBelow, -((note.getScaleNumber() - clef.getLowScaleNumber()) - 2) / 2);
				fatLedgersAbove = Math.max(fatLedgersAbove, ((note.getScaleNumber() - clef.getLowScaleNumber()) - 10) / 2);
			} else {
				note.draw(gc, clef, startX, startY);
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
		boolean prevFlipped = false;
		
		Set<Note> flippedNotes = new HashSet<>();
		
		for(Note note:sortedNotes) {
			if(Math.abs(note.getScaleNumber() - prevScaleNumber) <= 1 && !prevFlipped) {
				flippedNotes.add(note);
				prevFlipped = true;
			} else {
				prevFlipped = false;
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
	
	public String toString() {
		return notes.toString();
	}
}