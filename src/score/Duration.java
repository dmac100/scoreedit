package score;

public class Duration {
	public enum DurationType {
		WHOLE(1),
		HALF(2),
		QUARTER(4),
		EIGHTH(8),
		SIXTEENTH(16),
		THIRTYSECOND(32);
		
		DurationType(int denominator) {
			this.denominator = denominator;
		}
		
		int denominator;
	}
	
	private final DurationType type;;
	private final int dots;

	public Duration(DurationType type) {
		this(type, 0);
	}
	
	public Duration(DurationType type, int dots) {
		this.type = type;
		this.dots = dots;
	}
	
	public DurationType getType() {
		return type;
	}

	public int getDots() {
		return dots;
	}
}