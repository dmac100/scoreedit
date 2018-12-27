package score;

public interface ItemVisitor {
	public default void visitMeasure(Measure measure) {}
	public default void visitVoice(Voice voice) {}
	public default void visitChord(Chord chord) {}
	public default void visitNote(Note note) {}
	public default void visitRest(Rest item) {}
}