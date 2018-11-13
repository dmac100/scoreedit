package score;

public class Pitch {
	private final char name;
	private final int octave;
	private final int sharps;

	public Pitch(String pitch) {
		this(pitch.charAt(0), pitch.charAt(1) - '0', 0);
	}
	
	public Pitch(char name, int octave, int sharps) {
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
		return String.valueOf(name) + octave;
	}
}