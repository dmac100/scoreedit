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
}