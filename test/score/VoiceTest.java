package score;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static score.Duration.DurationType.EIGHTH;
import static score.Duration.DurationType.QUARTER;
import static score.Duration.DurationType.SIXTEENTH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import score.Duration.DurationType;

public class VoiceTest {
	@Test
	public void insertBeginning() {
		// [....|....|....|....]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)));
		
		voice.insertItem(Item(QUARTER), 0);
		
		// [####|....|....|....]
		assertItemsEquals(Arrays.asList(Item(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)), voice.getItems());
	}
	
	@Test
	public void insertEnd() {
		// [....|....|....|....]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)));
		
		voice.insertItem(Item(QUARTER), 32);
		
		// [....|....|....|....|####]
		assertItemsEquals(Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Item(QUARTER)), voice.getItems());
	}
	
	@Test
	public void insertAfterEnd() {
		// [....|....|....|....]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)));
		
		voice.insertItem(Item(QUARTER), 40);
		
		// [....|....|....|....|....|####]
		assertItemsEquals(Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Item(QUARTER)), voice.getItems());
	}
	
	@Test
	public void insertMiddle() {
		// [....|....|....|....]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)));
		
		voice.insertItem(Item(QUARTER), 8);
		
		// [....|####|....|....]
		assertItemsEquals(Arrays.asList(Rest(QUARTER), Item(QUARTER), Rest(QUARTER), Rest(QUARTER)), voice.getItems());
	}
	
	@Test
	public void insertMiddleOfItem() {
		// [....|....|....|....]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)));
		
		voice.insertItem(Item(DurationType.EIGHTH), 20);
		
		// [....|....|..##|....]
		assertItemsEquals(Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(EIGHTH), Item(EIGHTH), Rest(QUARTER)), voice.getItems());
	}
	
	@Test
	public void insertBeginningPartialOverwrite() {
		// [....|....|....|....]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)));
		
		voice.insertItem(Item(EIGHTH), 0);
		
		// [##..|....|....|....]
		assertItemsEquals(Arrays.asList(Item(EIGHTH), Rest(EIGHTH), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)), voice.getItems());
	}
	
	@Test
	public void insertBeginningPartialOverwriteMultipleItems() {
		// [....|....|....|....]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)));
		
		voice.insertItem(Item(DurationType.HALF, 2), 0);
		
		// [####|####|####|##..]
		assertItemsEquals(Arrays.asList(Item(DurationType.HALF, 2), Rest(EIGHTH)), voice.getItems());
	}
	
	@Test
	public void insertMiddlePartialOverwriteMultipleItems() {
		// [....|....|....|....]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Rest(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)));
		
		voice.insertItem(Item(DurationType.QUARTER, 2), 4);
		
		// [..##|####|#...|....]
		assertItemsEquals(Arrays.asList(Rest(EIGHTH), Item(DurationType.QUARTER, 2), Rest(EIGHTH), Rest(SIXTEENTH), Rest(QUARTER)), voice.getItems());
	}
	
	@Test
	public void insertStartEndBetweenRestsQuarter() {
		// [....|....]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Rest(QUARTER), Rest(QUARTER)));
		
		voice.insertItem(Item(DurationType.QUARTER, 0), 4);
		
		// [..##|##..]
		assertItemsEquals(Arrays.asList(Rest(EIGHTH), Item(DurationType.QUARTER), Rest(EIGHTH)), voice.getItems());
	}
	
	@Test
	public void insertStartEndBetweenRestsEighth() {
		// [....|....]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Rest(QUARTER), Rest(SIXTEENTH), Rest(SIXTEENTH), Rest(SIXTEENTH), Rest(SIXTEENTH)));
		
		voice.insertItem(Item(DurationType.QUARTER, 0), 4);
		
		// [..##|##..]
		assertItemsEquals(Arrays.asList(Rest(EIGHTH), Item(DurationType.QUARTER), Rest(SIXTEENTH), Rest(SIXTEENTH)), voice.getItems());
	}
	
	@Test
	public void insertEmpty() {
		// []
		Voice voice = new Voice(Clef.TREBLE, new ArrayList<>());
		
		voice.insertItem(Item(QUARTER), 4);
		
		assertItemsEquals(Arrays.asList(Rest(EIGHTH), Item(DurationType.QUARTER)), voice.getItems());
	}
	
	@Test
	public void getItemAt() {
		// [#.#]
		Voice voice = new Voice(Clef.TREBLE, Arrays.asList(Item(QUARTER), Rest(QUARTER), Item(QUARTER)));
		
		assertTrue(voice.getItemAt(0) instanceof Chord);
		assertTrue(voice.getItemAt(8) instanceof Rest);
		assertTrue(voice.getItemAt(16) instanceof Chord);
		assertTrue(voice.getItemAt(4) == null);
		assertTrue(voice.getItemAt(24) == null);
	}
	
	private static Chord Item(DurationType durationType) {
		return Item(durationType, 0);
	}
	
	private static Chord Item(DurationType durationType, int dots) {
		return new Chord(Arrays.asList(new Note(new Pitch("C4"), new Duration(durationType, dots))), new Duration(durationType, dots));
	}
	
	private static Rest Rest(DurationType durationType) {
		return Rest(durationType, 0);
	}
	
	private static Rest Rest(DurationType durationType, int dots) {
		return new Rest(new Duration(durationType, dots));
	}

	private static void assertItemsEquals(List<VoiceItem> expected, List<VoiceItem> actual) {
		if(expected.size() != actual.size()) {
			fail("Expected size: " + expected.size() + ", actual size: " + actual.size());
		}
		
		for(int i = 0; i < expected.size(); i++) {
			VoiceItem expectedItem = expected.get(i);
			VoiceItem actualItem = actual.get(i);
			
			assertEquals(expectedItem.getClass(), actualItem.getClass());
			assertEquals(expectedItem.getDurationCount(), actualItem.getDurationCount());
		}
	}
}