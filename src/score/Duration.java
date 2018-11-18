package score;

import static org.apache.commons.lang3.StringUtils.repeat;

public class Duration {
	public enum DurationType {
		WHOLE(1),
		HALF(2),
		QUARTER(4),
		EIGHTH(8),
		SIXTEENTH(16),
		THIRTYSECOND(32);
		
		int denominator;
		
		DurationType(int denominator) {
			this.denominator = denominator;
		}
		
		public int getDenominator() {
			return denominator;
		}
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
	
	public int getDurationCount() {
		int count = 32 / type.getDenominator();
		int dotValue = count;
		for(int dot = 0; dot < dots; dot++) {
			dotValue /= 2;
			count += dotValue;
		}
		return count;
	}
	
	public DurationType getType() {
		return type;
	}

	public int getDots() {
		return dots;
	}
	
	public String toString() {
		return type.toString() + repeat(".", dots);
	}
}