package score;

public class TimeSig {
	private final int upperCount;
	private final int lowerCount;
	
	public TimeSig(int upperCount, int lowerCount) {
		this.upperCount = upperCount;
		this.lowerCount = lowerCount;
	}
	
	public int getUpperCount() {
		return upperCount;
	}
	
	public int getLowerCount() {
		return lowerCount;
	}

	public boolean isCommonTime() {
		return (upperCount == 4 && lowerCount == 4);
	}

	public boolean isCutCommonTime() {
		return (upperCount == 2 && lowerCount == 4);
	}
	
	public boolean equals(Object other) {
		return (other instanceof TimeSig) && equals((TimeSig) other);
	}
	
	private boolean equals(TimeSig other) {
		return (upperCount == other.upperCount) && (lowerCount == other.lowerCount);
	}
}