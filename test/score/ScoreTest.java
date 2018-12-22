package score;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class ScoreTest {
	@Test
	public void getNewPitch() {
		Cursor cursor = new Cursor(new Measure(), new Voice(Clef.TREBLE, new ArrayList<>()));
		
		assertEquals(new Pitch("C4", 0), cursor.getNewPitch('C'));
		assertEquals(new Pitch("E4", 0), cursor.getNewPitch('E'));
		assertEquals(new Pitch("G4", 0), cursor.getNewPitch('G'));
		assertEquals(new Pitch("C5", 0), cursor.getNewPitch('C'));
		assertEquals(new Pitch("D5", 0), cursor.getNewPitch('D'));
		assertEquals(new Pitch("B4", 0), cursor.getNewPitch('B'));
	}
}