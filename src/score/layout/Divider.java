package score.layout;

/**
 * Divides a quotient by a divisor and distributes the remainder across each divisor, giving
 * integer results.
 */
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
	
	/**
	 * Returns the next divisor, with a possible partial remainder added.
	 */
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