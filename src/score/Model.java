package score;

import static score.Duration.DurationType.EIGHTH;
import static score.Duration.DurationType.HALF;
import static score.Duration.DurationType.QUARTER;
import static score.Duration.DurationType.SIXTEENTH;
import static score.Duration.DurationType.THIRTYSECOND;
import static score.Duration.DurationType.WHOLE;
import static util.XmlUtil.addElement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import score.Duration.DurationType;
import util.CollectionUtil;

class Model {
	private final List<Measure> measures = new ArrayList<>();
	
	public Model() {
		for(int x = 0; x < 10; x++) {
			measures.add(measure(timeSig(3, 4), keySig(3),
				treble(
					rest(WHOLE),
					rest(HALF),
					rest(QUARTER),
					rest(EIGHTH),
					rest(SIXTEENTH),
					rest(THIRTYSECOND)
				),
				bass(
					beam(
						chord(QUARTER, note("C3", 1, QUARTER)),
						chord(QUARTER, note("D3", 1, QUARTER)),
						chord(QUARTER, note("E3", 1, QUARTER)),
						chord(QUARTER, note("F3", 1, QUARTER)),
						chord(QUARTER, note("C3", 1, QUARTER)),
						chord(QUARTER, note("D3", 1, QUARTER)),
						chord(QUARTER, note("E3", 1, QUARTER)),
						chord(QUARTER, note("F3", 1, QUARTER))
					)
				)
			));
			measures.add(measure(timeSig(3, 4), keySig(3),
				treble(
					chord(
						QUARTER,
						note("F4", 0, QUARTER),
						note("E4", 0, QUARTER),
						note("B4", 0, QUARTER)
					),
					chord(
						QUARTER,
						note("D5", 0, QUARTER),
						note("E5", 0, QUARTER),
						note("A5", 0, QUARTER)
					)
				),
				bass(
					beam(
						chord(EIGHTH, note("F3", 0, EIGHTH)),
						chord(EIGHTH, note("G3", 0, EIGHTH))
					),
					chord(QUARTER, note("A3", 0, QUARTER))
				)
			));
			measures.add(measure(timeSig(3, 4), keySig(3),
				treble(
					beam(
						chord(EIGHTH, note("C4", 0, EIGHTH)),
						chord(SIXTEENTH, note("C4", 0, SIXTEENTH)),
						chord(SIXTEENTH, note("C4", 1, SIXTEENTH)),
						chord(THIRTYSECOND, note("C4", 1, THIRTYSECOND))
					)
				),
				bass(
					beam(
						chord(EIGHTH, note("C3", 0, EIGHTH)),
						chord(EIGHTH, note("D3", 0, EIGHTH)),
						chord(EIGHTH, note("E3", 0, EIGHTH)),
						chord(EIGHTH, note("F3", 0, EIGHTH))
					)
				)
			));
		}
	}
	
	private Voice bass(CanvasItem[] beam, CanvasItem chord) {
		List<CanvasItem> items = (CollectionUtil.concat(Arrays.asList(beam), Arrays.asList(chord)));
		items.forEach(item -> item.setClef(Clef.BASS));
		return new Voice(Clef.BASS, items);
	}

	private KeySig keySig(int fifths) {
		return new KeySig(fifths);
	}
	
	private TimeSig timeSig(int upper, int lower) {
		return new TimeSig(upper, lower);
	}

	private static Measure measure(TimeSig timeSig, KeySig keySig, Voice... voices) {
		return new Measure(Arrays.asList(voices), timeSig, keySig);
	}
	
	private static Chord[] beam(Chord... chords) {
		Beam beam = new Beam();
		for(Chord chord:chords) {
			chord.setBeam(beam);
		}
		return chords;
	}
	
	private static Voice treble(CanvasItem... canvasItems) {
		Arrays.asList(canvasItems).forEach(item -> item.setClef(Clef.TREBLE));
		return new Voice(Clef.TREBLE, Arrays.asList(canvasItems));
	}
	
	private static Voice bass(CanvasItem... canvasItems) {
		Arrays.asList(canvasItems).forEach(item -> item.setClef(Clef.BASS));
		return new Voice(Clef.BASS, Arrays.asList(canvasItems));
	}
	
	private static Rest rest(DurationType durationType) {
		return new Rest(new Duration(durationType));
	}
	
	private static Chord chord(DurationType durationType, Note... notes) {
		return new Chord(Arrays.asList(notes), new Duration(durationType));
	}
	
	private static Note note(String pitch, int flats, DurationType durationType) {
		return new Note(new Pitch(pitch, flats), new Duration(durationType));
	}
	
	public List<Measure> getMeasures() {
		return measures;
	}

	public void selectBox(float x, float y, float width, float height) {
		//items.forEach(item -> item.selectBox(x, y, width, height));
	}

	public void save(OutputStream outputStream) throws IOException {
		Element root = new Element("Model");
		for(Measure measure:measures) {
			measure.save(addElement(root, "measure"));
		}
		new XMLOutputter().output(root, outputStream);
	}
	
	public void load(InputStream inputStream) throws IOException, JDOMException {
		Element root = new SAXBuilder().build(inputStream).getRootElement();
		measures.clear();
		for(Element measureElement:root.getChildren("measure")) {
			measures.add(new Measure(measureElement));
		}
	}
}