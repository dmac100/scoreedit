package view;

import score.Duration;
import score.Stem.StemDirection;

/**
 * Stores the unicode characters that represent each musical item within the feta font.
 */
public class FetaFont {
	public static final String TREBLECLEF = "\uE050";
	public static final String BASSCLEF = "\uE062";
	
	public static final String SHARP = "\uE262";
	public static final String FLAT = "\uE260";
	public static final String NATURAL = "\uE261";
	public static final String DOUBLESHARP = "\uE263";
	public static final String DOUBLEFLAT = "\uE264";
	
	public static final String COMMON = "\uE08A";
	public static final String CUTCOMMON = "\uE08B";
	public static final String WHOLENOTEHEAD = "\uE0A2";
	public static final String HALFNOTEHEAD = "\uE0A3";
	public static final String QUARTERNOTEHEAD = "\uE0A4";
	
	public static final String WHOLEREST = "\uE4e3";
	public static final String HALFREST = "\uE4E4";
	public static final String QUARTERREST = "\uE4E5";
	public static final String EIGHTHREST = "\uE4E6";
	public static final String SIXTEENTHREST = "\uE4E7";
	public static final String THIRTYSECONDREST = "\uE4E8";
	
	public static final String EIGHTHUPFLAG = "\uE240";
	public static final String EIGHTHDOWNFLAG = "\uE241";
	public static final String SIXTEENTHUPFLAG = "\uE242";
	public static final String SIXTEENTHDOWNFLAG = "\uE243";
	public static final String THIRTYSECONDUPFLAG = "\uE244";
	public static final String THIRTYSECONDDOWNFLAG = "\uE245";
	
	public static final String DOT = "\uE044";
	
	public static final String TIME0 = "\uE080";
	public static final String TIME1 = "\uE081";
	public static final String TIME2 = "\uE082";
	public static final String TIME3 = "\uE083";
	public static final String TIME4 = "\uE084";
	public static final String TIME5 = "\uE085";
	public static final String TIME6 = "\uE086";
	public static final String TIME7 = "\uE087";
	public static final String TIME8 = "\uE088";
	public static final String TIME9 = "\uE089";
	
	public static String getFlags(Duration duration, StemDirection direction) {
		switch(duration.getType()) {
			case WHOLE:
			case HALF:
			case QUARTER:
				return "";
			case EIGHTH:
				return (direction == StemDirection.DOWN) ? FetaFont.EIGHTHDOWNFLAG : FetaFont.EIGHTHUPFLAG;
			case SIXTEENTH:
				return (direction == StemDirection.DOWN) ? FetaFont.SIXTEENTHDOWNFLAG : FetaFont.SIXTEENTHUPFLAG;
			case THIRTYSECOND:
				return (direction == StemDirection.DOWN) ? FetaFont.THIRTYSECONDDOWNFLAG : FetaFont.THIRTYSECONDUPFLAG;
			default:
				throw new IllegalStateException("Unknown duration: " + duration.getType());
		}
	}
	
	public static String getNoteHead(Duration duration) {
		switch(duration.getType()) {
			case WHOLE:
				return FetaFont.WHOLENOTEHEAD;
			case HALF:
				return FetaFont.HALFNOTEHEAD;
			case QUARTER:
			case EIGHTH:
			case SIXTEENTH:
			case THIRTYSECOND:
				return FetaFont.QUARTERNOTEHEAD;
			default:
				throw new IllegalStateException("Unknown duration: " + duration.getType());
		}
	}
	
	public static String getAccidental(int sharps) {
		switch(sharps) {
			case 0: return FetaFont.NATURAL;
			case 1: return FetaFont.SHARP;
			case 2: return FetaFont.DOUBLESHARP;
			case -1: return FetaFont.FLAT;
			case -2: return FetaFont.DOUBLEFLAT;
			default: throw new IllegalArgumentException("Unknown sharps: " + sharps);
		}
	}
	
	public static String getTimeSigText(int count) {
		switch(count) {
			case 0: return FetaFont.TIME0;
			case 1: return FetaFont.TIME1;
			case 2: return FetaFont.TIME2;
			case 3: return FetaFont.TIME3;
			case 4: return FetaFont.TIME4;
			case 5: return FetaFont.TIME5;
			case 6: return FetaFont.TIME6;
			case 7: return FetaFont.TIME7;
			case 8: return FetaFont.TIME8;
			case 9: return FetaFont.TIME9;
			default: throw new IllegalArgumentException("Unknown count: " + count);
		}
	}
	
	public static String getRest(Duration duration) {
		switch(duration.getType()) {
			case WHOLE: return FetaFont.WHOLEREST;
			case HALF: return FetaFont.HALFREST;
			case QUARTER: return FetaFont.QUARTERREST;
			case EIGHTH: return FetaFont.EIGHTHREST;
			case SIXTEENTH: return FetaFont.SIXTEENTHREST;
			case THIRTYSECOND: return FetaFont.THIRTYSECONDREST;
			default:
				throw new IllegalStateException("Unknown duration: " + duration.getType());
		}
	}
}