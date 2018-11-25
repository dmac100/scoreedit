package score;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import score.MeasureAccidentals.Accidental;

public class MeasureAccidentalsTest {
	private MeasureAccidentals measureAccidentals = new MeasureAccidentals(new KeySig(3));
	
	private Accidental getAndSetAccidental(Pitch pitch) {
		Accidental accidental = measureAccidentals.getAccidental(pitch);
		measureAccidentals.setAccidental(pitch);
		return accidental;
	}
	
	@Test
	public void noSharps() {
		assertEquals(Accidental.NONE, getAndSetAccidental(new Pitch("A4", 0)));
	}
	
	@Test
	public void sharpFromKeySig() {
		assertEquals(Accidental.NONE, getAndSetAccidental(new Pitch("F4", 1)));
	}
	
	@Test
	public void sharpNotInKeySig() {
		assertEquals(Accidental.SHARP, getAndSetAccidental(new Pitch("A4", 1)));
	}
	
	@Test
	public void addNatural() {
		assertEquals(Accidental.NATURAL, getAndSetAccidental(new Pitch("F4", 0)));
	}
	
	@Test
	public void chromatic() {
		assertEquals(Accidental.NONE, getAndSetAccidental(new Pitch("B4", 0)));
		assertEquals(Accidental.FLAT, getAndSetAccidental(new Pitch("C4", -1)));
		assertEquals(Accidental.NATURAL, getAndSetAccidental(new Pitch("C4", 0)));
		assertEquals(Accidental.SHARP, getAndSetAccidental(new Pitch("C4", 1)));
		assertEquals(Accidental.FLAT, getAndSetAccidental(new Pitch("D4", -1)));
	}
	
	@Test
	public void allAccidentals() {
		assertEquals(Accidental.FLAT, getAndSetAccidental(new Pitch("A4", -1)));
		assertEquals(Accidental.DOUBLEFLAT, getAndSetAccidental(new Pitch("A4", -2)));
		assertEquals(Accidental.SHARP, getAndSetAccidental(new Pitch("A4", 1)));
		assertEquals(Accidental.DOUBLESHARP, getAndSetAccidental(new Pitch("A4", 2)));
		assertEquals(Accidental.NATURAL, getAndSetAccidental(new Pitch("A4", 0)));
		assertEquals(Accidental.NONE, getAndSetAccidental(new Pitch("A4", 0)));
	}
	
	@Test
	public void differentOctave() {
		assertEquals(Accidental.SHARP, getAndSetAccidental(new Pitch("A4", 1)));
		assertEquals(Accidental.SHARP, getAndSetAccidental(new Pitch("A3", 1)));
		assertEquals(Accidental.NONE, getAndSetAccidental(new Pitch("A4", 1)));
		assertEquals(Accidental.NONE, getAndSetAccidental(new Pitch("A3", 1)));
	}
}