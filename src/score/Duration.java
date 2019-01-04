package score;

import static org.apache.commons.lang3.StringUtils.repeat;
import static util.XmlUtil.addElement;

import org.jdom2.Element;

/**
 * The duration of a voice item such as a chord or rest, stored as a type and number of dots.
 */
public class Duration {
	public static final int WHOLEDURATIONCOUNT = 32;
	
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
	
	private final DurationType type;
	private final int dots;

	public Duration(DurationType type) {
		this(type, 0);
	}
	
	public Duration(DurationType type, int dots) {
		this.type = type;
		this.dots = dots;
	}
	
	public Duration(Element parent) {
		type = DurationType.valueOf(parent.getChildText("type"));
		dots = Integer.parseInt(parent.getChildText("dots"));
	}
	
	public Duration(int durationCount) {
		for(DurationType durationType:DurationType.values()) {
			for(int dots = 0; dots < 3; dots++) {
				Duration duration = new Duration(durationType, dots);
				if(durationCount == duration.getDurationCount()) {
					this.type = durationType;
					this.dots = dots;
					return;
				}
			}
		}
		
		throw new IllegalArgumentException("Unknown duration: " + durationCount);
	}

	public int getDurationCount() {
		int count = WHOLEDURATIONCOUNT / type.getDenominator();
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

	public void save(Element parent) {
		addElement(parent, "type", type.name());
		addElement(parent, "dots", dots);
	}
}