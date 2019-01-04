package score;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static score.Duration.DurationType.EIGHTH;
import static score.Duration.DurationType.HALF;
import static score.Duration.DurationType.QUARTER;
import static util.CollectionUtil.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import score.Duration.DurationType;

public class ModelTest {
	private Model model = new Model();
	
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
		List<VoiceItem> items = getFirstMeasureItems();
		assertItemsEquals(Arrays.asList(Item(QUARTER), Rest(HALF), Item(QUARTER)), items);
	}
	
	@Test
	public void insertNote() {
		model.insertNote('C');
		
		List<VoiceItem> items = getFirstMeasureItems();
		
		assertItemsEquals(Arrays.asList(Item(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)), items);
		assertEquals(new Pitch('C', 4, 0), ((Chord) items.get(0)).getNotes().get(0).getPitch());
	}
	
	@Test
	public void insertNoteWithKeySig() {
		model.getMeasures().get(0).setKeySig(new KeySig(3));
		
		model.insertNote('C');
		
		List<VoiceItem> items = getFirstMeasureItems();
		
		assertItemsEquals(Arrays.asList(Item(QUARTER), Rest(QUARTER), Rest(QUARTER), Rest(QUARTER)), items);
		assertEquals(new Pitch('C', 4, 1), ((Chord) items.get(0)).getNotes().get(0).getPitch());
	}
	
	@Test
	public void insertSecondNote() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		
		model.insertNote('C');
		model.insertNote('D');
		model.insertNote('E');
		
		List<VoiceItem> items = getFirstMeasureItems();

		assertItemsEquals(Arrays.asList(Item(QUARTER), Item(QUARTER), Item(QUARTER), Rest(QUARTER)), items);
		assertEquals(new Pitch('C', 4, 0), ((Chord) items.get(0)).getNotes().get(0).getPitch());
		assertEquals(new Pitch('D', 4, 0), ((Chord) items.get(1)).getNotes().get(0).getPitch());
		assertEquals(new Pitch('E', 4, 0), ((Chord) items.get(2)).getNotes().get(0).getPitch());
	}
	
	@Test
	public void insertNoteAfterSelection() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		
		model.selectItems(Arrays.asList((Rest) voice.getItems().get(2)), false, false);
		
		model.insertNote('C');
		
		List<VoiceItem> items = getFirstMeasureItems();
		
		assertItemsEquals(Arrays.asList(Rest(QUARTER), Rest(QUARTER), Item(QUARTER), Rest(QUARTER)), items);
		assertEquals(new Pitch('C', 4, 0), ((Chord) items.get(2)).getNotes().get(0).getPitch());
	}
	
	@Test
	public void insertNoteBeforeNote() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		
		model.insertNote('C');
		model.insertNote('D');
		
		model.selectItems(Arrays.asList((Note) voice.getItems().get(0).getNotes().get(0)), false, false);
		
		model.insertNote('E');
		
		List<VoiceItem> items = getFirstMeasureItems();
		
		assertItemsEquals(Arrays.asList(Item(QUARTER), Item(QUARTER), Rest(QUARTER), Rest(QUARTER)), items);
		assertEquals(new Pitch('E', 4, 0), ((Chord) items.get(0)).getNotes().get(0).getPitch());
		assertEquals(new Pitch('D', 4, 0), ((Chord) items.get(1)).getNotes().get(0).getPitch());
	}
	
	@Test
	public void shiftSelectedPitchUp() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		
		model.insertNote('C');
		model.selectItems(Arrays.asList((Note) voice.getItems().get(0).getNotes().get(0)), false, false);
		
		model.shiftSelectionPitch(1);
		assertEquals(new Pitch('C', 4, 1), ((Chord) getFirstMeasureItems().get(0)).getNotes().get(0).getPitch());
		
		model.shiftSelectionPitch(1);
		assertEquals(new Pitch('D', 4, 0), ((Chord) getFirstMeasureItems().get(0)).getNotes().get(0).getPitch());
	}
	
	@Test
	public void shiftSelectedPitchDown() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		
		model.insertNote('D');
		
		model.selectItems(Arrays.asList((Note) voice.getItems().get(0).getNotes().get(0)), false, false);
		
		model.shiftSelectionPitch(-1);
		assertEquals(new Pitch('D', 4, -1), ((Chord) getFirstMeasureItems().get(0)).getNotes().get(0).getPitch());
		
		model.shiftSelectionPitch(-1);
		assertEquals(new Pitch('C', 4, 0), ((Chord) getFirstMeasureItems().get(0)).getNotes().get(0).getPitch());
	}
	
	@Test
	public void shiftSelectedOctave() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		
		model.insertNote('D');
		
		model.selectItems(Arrays.asList((Note) voice.getItems().get(0).getNotes().get(0)), false, false);
		
		model.shiftSelectionOctave(1);
		assertEquals(new Pitch('D', 5, 0), ((Chord) getFirstMeasureItems().get(0)).getNotes().get(0).getPitch());
		
		model.shiftSelectionOctave(-2);
		assertEquals(new Pitch('D', 3, 0), ((Chord) getFirstMeasureItems().get(0)).getNotes().get(0).getPitch());
	}
	
	@Test
	public void selectPrevNext() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		model.insertNote('C');
		model.selectItems(Arrays.asList((Note) voice.getItems().get(0).getNotes().get(0)), false, false);
		
		for(int x = 0; x < 100; x++) {
			model.selectNext(false, false);
		}
		
		assertTrue(new ArrayList<>(model.getSelectedItems()).get(0) instanceof Rest);
		
		for(int x = 0; x < 100; x++) {
			model.selectPrev(false, false);
		}
		
		assertTrue(new ArrayList<>(model.getSelectedItems()).get(0) instanceof Note);
	}
	
	@Test
	public void autoBeam() {
		Voice voice = model.getMeasures().get(0).getVoices().get(0);
		
		voice.insertItem(Item(EIGHTH), 0);
		voice.insertItem(Item(EIGHTH), 4);
		voice.insertItem(Item(EIGHTH), 8);
		voice.insertItem(Item(EIGHTH), 16);
		
		model.getMeasures().get(0).autoBeam();

		List<Chord> chords = new ArrayList<>();
		
		for(VoiceItem item:voice.getItems()) {
			if(item instanceof Chord) {
				chords.add((Chord) item);
			}
		}
		
		assertEquals(4, chords.size());
		assertEquals(chords.get(0).getBeam(), chords.get(1).getBeam());
		assertEquals(chords.get(2).getBeam(), chords.get(3).getBeam());
		assertNotEquals(chords.get(1).getBeam(), chords.get(2).getBeam());
	}
	
	public List<VoiceItem> getFirstMeasureItems() {
		return filter(model.getMeasures().get(0).getVoices().get(0).getItems(), item -> !(item instanceof Cursor));
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