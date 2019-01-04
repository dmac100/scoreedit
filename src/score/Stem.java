package score;

/**
 * The stem location for a chord, storing both up and down directions in case it needs to be flipped.
 */
public class Stem {
	public enum StemDirection {
		UP, DOWN
	}
	
	private StemDirection direction;
	private Duration duration;
	private int startX;
	private int upStartY;
	private int upEndY;
	private int downStartY;
	private int downEndY;
	
	public StemDirection getDirection() {
		return direction;
	}
	
	public void setDirection(StemDirection direction) {
		this.direction = direction;
	}
	
	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public int getStartX() {
		return startX;
	}
	
	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getUpStartY() {
		return upStartY;
	}

	public void setUpStartY(int upStartY) {
		this.upStartY = upStartY;
	}

	public int getUpEndY() {
		return upEndY;
	}

	public void setUpEndY(int upEndY) {
		this.upEndY = upEndY;
	}

	public int getDownStartY() {
		return downStartY;
	}

	public void setDownStartY(int downStartY) {
		this.downStartY = downStartY;
	}

	public int getDownEndY() {
		return downEndY;
	}

	public void setDownEndY(int downEndY) {
		this.downEndY = downEndY;
	}

	public int getStartY() {
		return (direction == StemDirection.UP) ? upStartY : downStartY;
	}

	public int getEndY() {
		return (direction == StemDirection.UP) ? upEndY : downEndY;
	}
}