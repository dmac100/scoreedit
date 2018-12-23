package score;

import static org.apache.commons.lang3.StringUtils.repeat;
import static util.XmlUtil.addElement;

import java.util.Objects;

import org.jdom2.Element;

public class Pitch {
	private final char name;
	private final int octave;
	private final int sharps;

	public Pitch(String pitch) {
		this(pitch, 0);
	}
	
	public Pitch(String pitch, int sharps) {
		this(pitch.charAt(0), pitch.charAt(1) - '0', sharps);
	}
	
	public Pitch(char name, int octave, int sharps) {
		if(octave < 0 || octave > 10) throw new IllegalArgumentException("Invalid octave: " + octave);
		if(name < 'A' || name > 'G') throw new IllegalArgumentException("Invalid name: " + name);
		
		this.name = name;
		this.octave = octave;
		this.sharps = sharps;
	}
	
	public Pitch(int scaleNumber) {
		this(scaleNumber, 0);
	}
	
	public Pitch(int pitch, int sharps) {
		this.name = (char) ('A' + (pitch % 7));
		this.octave = (pitch + 5) / 7;
		this.sharps = sharps;
	}

	public Pitch(Element parent) {
		name = parent.getChildText("name").charAt(0);
		octave = Integer.parseInt(parent.getChildText("octave"));
		sharps = Integer.parseInt(parent.getChildText("sharps"));
	}
	
	public Pitch nextSemitone() {
		if(sharps < 0) {
			return new Pitch(name, octave, sharps + 1);
		} else if(sharps == 0) {
			if(name == 'B' || name == 'E') {
				return new Pitch(nextName(name), octave + ((name == 'B') ? 1 : 0), sharps);
			} else {
				return new Pitch(name, octave, sharps + 1);
			}
		} else {
			return new Pitch(nextName(name), octave, sharps - 1);
		}
	}
	
	public Pitch prevSemitone() {
		if(sharps > 0) {
			return new Pitch(name, octave, sharps - 1);
		} else if(sharps == 0) {
			if(name == 'C' || name == 'F') {
				return new Pitch(prevName(name), octave - ((name == 'C') ? 1 : 0), sharps);
			} else {
				return new Pitch(name, octave, sharps - 1);
			}
		} else {
			return new Pitch(prevName(name), octave, sharps + 1);
		}
	}

	private char nextName(char name) {
		return (name < 'G') ? (char) (name + 1) : 'A';
	}
	
	private char prevName(char name) {
		return (name > 'A') ? (char) (name - 1) : 'G';
	}

	public char getName() {
		return name;
	}

	public int getOctave() {
		return octave;
	}

	public int getSharps() {
		return sharps;
	}
	
	public int getScaleNumber() {
		return (name - 'A') + octave * 7 - ((name >= 'C') ? 7 : 0);
	}
	
	public String toString() {
		return String.valueOf(name) + octave + repeat("#", sharps) + repeat("b", -sharps);
	}
	
	public boolean equals(Object other) {
		return (other instanceof Pitch) && equals((Pitch) other);
	}
	
	public boolean equals(Pitch other) {
		return (name == other.name) && (octave == other.octave) && (sharps == other.sharps);
	}
	
	public int hashCode() {
		return Objects.hash(name, octave, sharps);
	}

	public void save(Element parent) {
		addElement(parent, "name", name);
		addElement(parent, "octave", octave);
		addElement(parent, "sharps", sharps);
	}
}