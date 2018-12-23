package score;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PitchTest {
	@Test
	public void pitchRoundtrip() {
		for(int x = 0; x < 100; x++) {
			Pitch pitch = new Pitch(x);
			assertEquals(x, pitch.getScaleNumber());
		}
	}
	
	@Test
	public void noteNames() {
		assertEquals("A0", new Pitch(0).toString());
		assertEquals("B0", new Pitch(1).toString());
		assertEquals("C1", new Pitch(2).toString());
	}
	
	@Test
	public void nextSemitone() {
		Pitch pitch = new Pitch('C', 4, 0);
		
		String[] pitches = {
			"C4",
			"C4#",
			"D4",
			"D4#",
			"E4",
			"F4",
			"F4#",
			"G4",
			"G4#",
			"A4",
			"A4#",
			"B4",
			"C5",
		};
		
		for(String name:pitches) {
			assertEquals(name, pitch.toString());
			pitch = pitch.nextSemitone();
		}
	}
	
	@Test
	public void prevSemitone() {
		Pitch pitch = new Pitch('C', 5, 0);
		
		String[] pitches = {
			"C5",
			"B4",
			"B4b",
			"A4",
			"A4b",
			"G4",
			"G4b",
			"F4",
			"E4",
			"E4b",
			"D4",
			"D4b",
			"C4"
		};
		
		for(String name:pitches) {
			assertEquals(name, pitch.toString());
			pitch = pitch.prevSemitone();
		}
	}
}