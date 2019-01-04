package score;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import score.layout.AlignmentBox;
import view.ScoreCanvas;

/**
 * An item within a voice such as a chord.
 */
public interface VoiceItem {
	/**
	 * Sets the clef that this item uses.
	 */
	public default void setClef(Clef clef) {};
	
	/**
	 * Draws this item onto the canvas, reading or updating measureAccidentals as needed.
	 */
	public default void draw(ScoreCanvas canvas, int startX, int startY, MeasureAccidentals measureAccidentals) {};
	
	/**
	 * Returns the alignment of this item.
	 */
	public default AlignmentBox getAlignmentBox(MeasureAccidentals measureAccidentals) {
		return new AlignmentBox(0, 0);
	}
	
	/**
	 * Returns the duration of this item.
	 */
	public default int getDurationCount() {
		return 0;
	}
	
	/**
	 * Sets the accidentals that this item uses, or updates with accidental changes.
	 */
	public default void setAccidentals(MeasureAccidentals measureAccidentals) {};
	
	/**
	 * Returns the beam that this item belongs to.
	 */
	public default Beam getBeam() {
		return null;
	}
	
	/**
	 * Saves this element to an xml element, and writes any new beams to the beams list.
	 */
	public default void save(Element parent, List<Beam> beams) {};
	
	/**
	 * Returns all the notes, if any, within this item.
	 */
	public default List<Note> getNotes() {
		return new ArrayList<>();
	}

	/**
	 * Returns whether this item changes the layout and spacing of surrounding items.
	 */
	public default boolean includeInLayout() {
		return true;
	}
}