package score;

import static util.XmlUtil.addElement;
import static view.ScoreCanvas.ACCIDENTAL_SPACING;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.jdom2.Element;

import score.Duration.DurationType;
import score.MeasureAccidentals.Accidental;
import score.Stem.StemDirection;
import score.layout.AlignmentBox;
import view.FetaFont;
import view.ScoreCanvas;

/**
 * A chord containing one of more notes.
 */
public class Chord implements VoiceItem {
	private List<Note> notes;
	private Duration duration;
	private Clef clef = Clef.TREBLE;
	private Beam beam;

	public Chord(List<Note> notes, Duration duration) {
		this.notes = new ArrayList<>(notes);
		this.duration = duration;
	}
	
	public Chord(Clef clef, List<Note> notes, Duration duration) {
		this.clef = clef;
		this.notes = new ArrayList<>(notes);
		this.duration = duration;
	}

	public Chord(Element parent, List<Beam> beams) {
		duration = new Duration(parent.getChild("duration"));
		notes = new ArrayList<>();
		for(Element noteElement:parent.getChildren("note")) {
			notes.add(new Note(noteElement));
		}

		String beam = parent.getChildText("beam");
		
		if(beam != null) {
			int index = Integer.parseInt(beam);
			while(beams.size() <= index) {
				beams.add(new Beam());
			}
			this.beam = beams.get(index);
		}
	}

	public void setClef(Clef clef) {
		this.clef = clef;
	}

	public Beam getBeam() {
		return beam;
	}
	
	public void setBeam(Beam beam) {
		this.beam = beam;
	}
	
	public void removeNote(Note note) {
		notes.remove(note);
	}
	
	@Override
	public int getDurationCount() {
		return duration.getDurationCount();
	}
	
	public Duration getDuration() {
		return duration;
	}
	
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public void draw(ScoreCanvas canvas, int startX, int startY, MeasureAccidentals measureAccidentals) {
		List<List<Note>> accidentalLayout = getAccidentalLayout(measureAccidentals);
		
		Stem stem = getStem(startX + accidentalLayout.size() * ACCIDENTAL_SPACING, startY);
		
		stem.setDuration(duration);
		
		Set<Note> flippedNotes = getFlippedNotes(stem);
		
		boolean shiftStemRight = (!flippedNotes.isEmpty() && stem.getDirection() == StemDirection.DOWN);
		
		if(shiftStemRight) {
			stem.setStartX(stem.getStartX() + 19);
		}
		
		if(beam != null) {
			beam.addStem(stem);
		} else {
			drawStem(canvas, stem);
		}

		drawAccidentals(canvas, accidentalLayout, startX, startY);
		
		drawNotes(canvas, stem, flippedNotes, startX + accidentalLayout.size() * ACCIDENTAL_SPACING + (shiftStemRight ? 19 : 0), startY);
	}
	
	/**
	 * Draw accidentals based on the accidental layout.
	 */
	private void drawAccidentals(ScoreCanvas canvas, List<List<Note>> accidentalLayout, int startX, int startY) {
		int x = startX + accidentalLayout.size() * ACCIDENTAL_SPACING;
		for(List<Note> notes:accidentalLayout) {
			for(Note note:notes) {
				int scaleNumber = note.getScaleNumber() - clef.getLowScaleNumber();
				canvas.drawText(FetaFont.getAccidental(note.getSharps()), x - ACCIDENTAL_SPACING, startY - (scaleNumber * 8) - 71, false);
			}
			x -= ACCIDENTAL_SPACING;
		}
	}
	
	/**
	 * Lays out accidentals from right to left, grouping accidentals that don't overlap vertically together.
	 */
	private List<List<Note>> getAccidentalLayout(MeasureAccidentals measureAccidentals) {
		List<List<Note>> layout = new ArrayList<>();
	
		measureAccidentals = new MeasureAccidentals(measureAccidentals);
		
		// Finds the notes needed given the current accidentals, and updates accidentals.
		List<Note> notesRemaining = new ArrayList<>();
		for(Note note:notes) {
			Accidental accidental = measureAccidentals.getAccidental(note.getPitch());
			if(accidental != Accidental.NONE) {
				notesRemaining.add(note);
			}
			measureAccidentals.setAccidental(note.getPitch());
		}
		
		// Try groups notes with same number of sharps and flats.
		Collections.sort(notesRemaining, Comparator.comparing(note -> note.getSharps()));
		
		// Orders accidentals on the same scale number on the order they appear in the measure.
		Collections.sort(notesRemaining, (a, b) -> {
			if(a.getPitch().getScaleNumber() == b.getPitch().getScaleNumber()) {
				return Integer.compare(notes.indexOf(b), notes.indexOf(a));
			}
			
			return 0;
		});
	
		// Add each note to the layout, moving to the next position when they overlap.
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

	public AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals) {
		int startX = 0;
		int startY = 0;
		
		Stem stem = getStem(startX, startY);
		
		List<List<Note>> accidentalLayout = getAccidentalLayout(measureAccidentals);
		
		Set<Note> flippedNotes = getFlippedNotes(stem);
		
		Rectangle box = notes.get(0).getBoundingBox(clef, startX, startY);
		for(Note note:notes) {
			if(flippedNotes.contains(note)) {
				box.add(note.getBoundingBox(clef, startX + 19 + accidentalLayout.size() * ACCIDENTAL_SPACING, startY));
			} else {
				box.add(note.getBoundingBox(clef, startX + accidentalLayout.size() * ACCIDENTAL_SPACING, startY));
			}
		}
		
		return new AlignmentBox(box.width, accidentalLayout.size() * ACCIDENTAL_SPACING);
	}
	
	/**
	 * Returns the stem for this chord, covering every note in the chord.
	 */
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
		stem.setDirection((downCount > 0) ? StemDirection.DOWN : StemDirection.UP);
		
		stem.setStartX((stem.getDirection() == StemDirection.DOWN) ? (startX + 1) : startX + 19);

		// Find lowest and highest notes in the chord.
		int minScaleNumber = Integer.MAX_VALUE;
		int maxScaleNumber = Integer.MIN_VALUE;
		for(Note note:notes) {
			minScaleNumber = Math.min(minScaleNumber, note.getScaleNumber());
			maxScaleNumber = Math.max(maxScaleNumber, note.getScaleNumber());
		}
		
		// Set down stem position.
		stem.setDownStartY(startY + -((maxScaleNumber - clef.getLowScaleNumber()) * 8) + 81);
		stem.setDownEndY(startY + -((minScaleNumber - clef.getLowScaleNumber()) * 8) + 80 + 60);
		stem.setDownEndY(Math.max(stem.getDownEndY(), startY + 33));
		
		// Set up stem position.
		stem.setUpStartY(startY + -((minScaleNumber - clef.getLowScaleNumber()) * 8) + 80);
		stem.setUpEndY(startY + -((maxScaleNumber - clef.getLowScaleNumber()) * 8) + 80 - 60);
		stem.setUpEndY(Math.min(stem.getUpEndY(), startY + 33));
		
		return stem;
	}
	
	/**
	 * Draw the stem of the chord.
	 */
	private void drawStem(ScoreCanvas canvas, Stem stem) {
		if(duration.getType() == DurationType.WHOLE) {
			return;
		}
		
		canvas.drawLine(3, SWT.CAP_ROUND, stem.getStartX(), stem.getStartY(), stem.getStartX(), stem.getEndY());
		canvas.drawText(FetaFont.getFlags(duration, stem.getDirection()), stem.getStartX(), stem.getEndY() - 150, false);
	}

	/**
	 * Draw every note in the chord including note heads and ledger lines, where flipped notes are facing the opposite
	 * way on the stem than normal. 
	 */
	private void drawNotes(ScoreCanvas canvas, Stem stem, Set<Note> flippedNotes, int startX, int startY) {
		int ledgersAbove = 0;
		int ledgersBelow = 0;
		int fatLedgersAbove = 0;
		int fatLedgersBelow = 0;
		
		for(Note note:notes) {
			ledgersBelow = Math.max(ledgersBelow, -((note.getScaleNumber() - clef.getLowScaleNumber()) - 2) / 2);
			ledgersAbove = Math.max(ledgersAbove, ((note.getScaleNumber() - clef.getLowScaleNumber()) - 10) / 2);
			
			if(flippedNotes.contains(note)) {
				note.draw(canvas, clef, (stem.getDirection() == StemDirection.UP) ? startX + 19 : startX - 19, startY);
				
				fatLedgersBelow = Math.max(fatLedgersBelow, -((note.getScaleNumber() - clef.getLowScaleNumber()) - 2) / 2);
				fatLedgersAbove = Math.max(fatLedgersAbove, ((note.getScaleNumber() - clef.getLowScaleNumber()) - 10) / 2);
			} else {
				note.draw(canvas, clef, startX, startY);
			}
		}
		
		drawLedgers(canvas, stem, startX, startY, ledgersBelow, ledgersAbove, fatLedgersAbove, fatLedgersBelow);
	}
	
	/**
	 * Returns which notes on the stem to flip so that there are no notes within a semitone that are on the same
	 * side of the stem.
	 */
	private Set<Note> getFlippedNotes(Stem stem) {
		List<Note> sortedNotes = new ArrayList<>(notes);
		
		Collections.sort(sortedNotes, Comparator.comparingInt(note -> note.getScaleNumber()));
		if((stem.getDirection() == StemDirection.DOWN)) {
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
	
	/**
	 * Draw a number of ledger lines above or below the staff. Either single ledger lines, or fat ledger lines
	 * that can contain notes on both sides of the stem.
	 */
	private void drawLedgers(ScoreCanvas canvas, Stem stem, int startX, int startY, int ledgersBelow, int ledgersAbove, int fatLedgersAbove, int fatLedgersBelow) {
		for(int i = 0; i < ledgersAbove; i++) {
			canvas.drawLine(
				3,
				SWT.CAP_SQUARE,
				startX - ((i < fatLedgersAbove && stem.getDirection() == StemDirection.DOWN) ? 27 : 7),
				startY - ((i + 1) * 16),
				startX + ((i < fatLedgersAbove && stem.getDirection() == StemDirection.UP) ? 47 : 27),
				startY - ((i + 1) * 16)
			);
		}
		
		for(int i = 0; i < ledgersBelow; i++) {
			canvas.drawLine(
				3,
				SWT.CAP_SQUARE,
				startX - ((i < fatLedgersBelow && stem.getDirection() == StemDirection.DOWN) ? 27 : 7),
				startY + ((5 + i) * 16),
				startX + ((i < fatLedgersBelow && stem.getDirection() == StemDirection.UP) ? 47 : 27),
				startY + ((5 + i) * 16)
			);
		}
	}
	
	public String toString() {
		return notes.toString();
	}

	@Override
	public void setAccidentals(MeasureAccidentals measureAccidentals) {
		for(Note note:notes) {
			measureAccidentals.setAccidental(note.getPitch());
		}
	}

	@Override
	public void save(Element parent, List<Beam> beams) {
		Element chordElement = addElement(parent, "chord");
		duration.save(addElement(chordElement, "duration"));
		for(Note note:notes) {
			note.save(addElement(chordElement, "note"));
		}
		
		if(beam != null) {
			if(!beams.contains(beam)) {
				beams.add(beam);
			}
			addElement(chordElement, "beam", beams.indexOf(beam));
		}
	}

	@Override
	public List<Note> getNotes() {
		return new ArrayList<>(notes);
	}
	
	public void addNote(Note note) {
		notes.add(note);
	}
}