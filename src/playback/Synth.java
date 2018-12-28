package playback;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import score.Chord;
import score.Note;

public class Synth {
	private final int tempo = 10;
	
	private final Synthesizer synth;
	private final MidiChannel channel;

	public Synth() {
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
			
			channel = synth.getChannels()[0];
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void play(Chord chord) {
		chord.getNotes().forEach(this::play);
	}
	
	public void play(Note note) {
		int pitch = note.getPitch().getMidiNumber();
		int duration = note.getDuration().getDurationCount() * tempo;
		
		channel.noteOn(pitch, 128);
		
		new Thread(() -> {
			sleep(duration);
			channel.noteOff(pitch, 128);
		}).start();
	}

	private static void sleep(int duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}