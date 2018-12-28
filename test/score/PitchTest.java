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
	
	@Test
	public void getMidiNumber() {
		assertEquals(21, new Pitch("A0").getMidiNumber());
		assertEquals(23, new Pitch("B0").getMidiNumber());
		assertEquals(24, new Pitch("C1").getMidiNumber());
		assertEquals(26, new Pitch("D1").getMidiNumber());
		assertEquals(28, new Pitch("E1").getMidiNumber());
		assertEquals(29, new Pitch("F1").getMidiNumber());
		assertEquals(31, new Pitch("G1").getMidiNumber());
		assertEquals(33, new Pitch("A1").getMidiNumber());
		assertEquals(35, new Pitch("B1").getMidiNumber());
		assertEquals(36, new Pitch("C2").getMidiNumber());
		assertEquals(38, new Pitch("D2").getMidiNumber());
		
		assertEquals(60, new Pitch("C4").getMidiNumber());
		assertEquals(69, new Pitch("A4").getMidiNumber());
		assertEquals(70, new Pitch("A4", 1).getMidiNumber());
		assertEquals(59, new Pitch("C4", -1).getMidiNumber());
	}
}