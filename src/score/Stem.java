package score;

public class Stem {
	public enum StemDirection {
		UP, DOWN
	}
	
	private StemDirection direction;
	private Duration duration;
	private int startX;
	private int startY;
	private int endY;
	
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
	
	public int getStartY() {
		return startY;
	}
	
	public void setStartY(int startY) {
		this.startY = startY;
	}
	
	public int getEndY() {
		return endY;
	}
	
	public void setEndY(int endY) {
		this.endY = endY;
	}
}