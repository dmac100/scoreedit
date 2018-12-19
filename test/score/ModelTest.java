package score;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static score.Duration.DurationType.QUARTER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import score.Duration.DurationType;

public class ModelTest {
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
		Model model = new Model();
		
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		voice.getItems().forEach(item -> voice.removeItem(item));
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
		assertItemsEquals(Arrays.asList(Item(QUARTER), Rest(QUARTER), Rest(QUARTER), Item(QUARTER)), items);
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