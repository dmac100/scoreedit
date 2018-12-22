package score;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static score.Duration.DurationType.HALF;
import static score.Duration.DurationType.QUARTER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import score.Duration.DurationType;
import util.CollectionUtil;

public class ModelTest {
	private Model model = new Model();
	
	@Before
	public void before() {
		for(Measure measure:model.getMeasures()) {
			for(Voice voice:measure.getVoices()) {
				voice.getItems().forEach(item -> voice.removeItem(item));
				
				voice.insertItem(Rest(QUARTER), 0);
				voice.insertItem(Rest(QUARTER), 8);
				voice.insertItem(Rest(QUARTER), 16);
				voice.insertItem(Rest(QUARTER), 24);
			}
		}
	}
	
	@Test
	public void saveLoadSaveRoundTrip() throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new Model().save(outputStream);
		String save1 = new String(outputStream.toByteArray(), "UTF-8");
		
		Model model = new Model();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		model.load(inputStream);
		
		ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
		model.save(outputStream2);
		String save2 = new String(outputStream2.toByteArray(), "UTF-8");
		
		assertEquals(save1, save2);
	}
	
	@Test
	public void deleteSelection() {
		// [####|####|####|####]
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		
		voice.insertItem(Item(QUARTER), 0);
		voice.insertItem(Item(QUARTER), 8);
		voice.insertItem(Item(QUARTER), 16);
		voice.insertItem(Item(QUARTER), 24);
		
		List<Note> notes = new ArrayList<>();
		notes.addAll(((Chord) voice.getItems().get(1)).getNotes());
		notes.addAll(((Chord) voice.getItems().get(2)).getNotes());
		
		model.selectItems(notes, false, false);
		model.deleteSelection(true);
		
		// [####|....|....|####]
		List<CanvasItem> items = model.getMeasures().get(0).getVoices().get(0).getItems();
		assertItemsEquals(Arrays.asList(Item(QUARTER), Rest(HALF), Item(QUARTER)), items);
	}
	
	@Test
	public void insertNote() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		
		model.insertNote('C');
		
		List<CanvasItem> items = voice.getItems();
		
		assertItemsEquals(Arrays.asList(Item(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)), items);
		assertEquals(new Pitch('C', 4, 0), ((Chord) voice.getItems().get(0)).getNotes().get(0).getPitch());
	}
	
	@Test
	public void insertNoteWithKeySig() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);

		model.getMeasures().get(0).setKeySig(new KeySig(3));
		
		model.insertNote('C');
		
		List<CanvasItem> items = voice.getItems();
		
		assertItemsEquals(Arrays.asList(Item(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)), items);
		assertEquals(new Pitch('C', 4, 1), ((Chord) voice.getItems().get(0)).getNotes().get(0).getPitch());
	}
	
	@Test
	public void insertSecondNote() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		
		model.insertNote('C');
		model.insertNote('D');
		model.insertNote('E');
		
		List<CanvasItem> items = model.getMeasures().get(0).getVoices().get(0).getItems();

		assertItemsEquals(Arrays.asList(Item(QUARTER), Item(QUARTER), Item(QUARTER), Rest(QUARTER)), items);
		assertEquals(new Pitch('C', 4, 0), ((Chord) voice.getItems().get(0)).getNotes().get(0).getPitch());
		assertEquals(new Pitch('D', 4, 0), ((Chord) voice.getItems().get(1)).getNotes().get(0).getPitch());
		assertEquals(new Pitch('E', 4, 0), ((Chord) voice.getItems().get(2)).getNotes().get(0).getPitch());
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

	private static void assertItemsEquals(List<CanvasItem> expected, List<CanvasItem> actual) {
		actual = CollectionUtil.filter(actual, item -> !(item instanceof Cursor));
		
		if(expected.size() != actual.size()) {
			fail("Expected size: " + expected.size() + ", actual size: " + actual.size());
		}
		
		for(int i = 0; i < expected.size(); i++) {
			CanvasItem expectedItem = expected.get(i);
			CanvasItem actualItem = actual.get(i);
			
			assertEquals(expectedItem.getClass(), actualItem.getClass());
			assertEquals(expectedItem.getDuration(), actualItem.getDuration());
		}
	}
}