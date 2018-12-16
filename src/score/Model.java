package score;

import static score.Duration.DurationType.EIGHTH;
import static score.Duration.DurationType.HALF;
import static score.Duration.DurationType.QUARTER;
import static score.Duration.DurationType.WHOLE;
import static util.XmlUtil.addElement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import score.Duration.DurationType;

public class Model {
	private final List<Measure> measures = new ArrayList<>();
	
	private final Set<Selectable> selectedItems = new HashSet<>();
	
	private DurationType selectedDurationType = QUARTER;
	
	public Model() {
		for(int x = 0; x < 32; x++) {
			Voice treble = new Voice(Clef.TREBLE, Arrays.asList(
				new Chord(Arrays.asList(new Note(new Pitch("C4"),  new Duration(WHOLE))), new Duration(WHOLE)),
				new Chord(Arrays.asList(new Note(new Pitch("D4"),  new Duration(HALF))), new Duration(HALF)),
				new Chord(Arrays.asList(new Note(new Pitch("E4"),  new Duration(QUARTER))), new Duration(QUARTER)),
				new Chord(Arrays.asList(new Note(new Pitch("F4"),  new Duration(EIGHTH))), new Duration(EIGHTH))
			));
			Voice bass = new Voice(Clef.BASS, Arrays.asList(
				new Rest(new Duration(WHOLE)),
				new Rest(new Duration(HALF)),
				new Rest(new Duration(QUARTER)),
				new Rest(new Duration(EIGHTH))
			));
			measures.add(new Measure(
				Arrays.asList(treble, bass),
				new TimeSig(4, 4),
				new KeySig(0)
			));
		}
	}
	
	public List<Measure> getMeasures() {
		return measures;
	}
	
	public void setDurationType(DurationType durationType) {
		this.selectedDurationType = durationType;
	}
	
	public DurationType getDurationType() {
		return selectedDurationType;
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

	public void selectItems(List<Selectable> items) {
		selectedItems.forEach(item -> item.setSelected(false));
		items.forEach(item -> item.setSelected(true));
		selectedItems.clear();
		selectedItems.addAll(items);
	}
}