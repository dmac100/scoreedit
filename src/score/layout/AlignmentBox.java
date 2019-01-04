package score.layout;

/**
 * The width of a item so that no other item can overlap it, and the center to align
 * items across voices.
 */
public class AlignmentBox {
	private final int width;
	private final int center;
	
	public AlignmentBox(int width, int center) {
		this.width = width;
		this.center = center;
	}

	public int getWidth() {
		return width;
	}
	
	public int getCenter() {
		return center;
	}
}