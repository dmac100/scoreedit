package score.layout;

public class Divider {
	private final int quotient;
	private final int remainder;
	private final int divisor;
	
	private int count = 0;

	public Divider(int dividend, int divisor) {
		this.divisor = divisor;
		this.quotient = dividend / divisor;
		this.remainder = dividend % divisor;
	}
	
	public int next() {
		count++;
		
		if(count > divisor) {
			throw new IllegalStateException("Too many calls to next");
		}
		
		if(count <= remainder) {
			return quotient + 1;
		} else {
			return quotient;
		}
	}
}