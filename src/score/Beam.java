package score;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;

import score.Duration.DurationType;
import score.Stem.StemDirection;
import view.ScoreCanvas;

public class Beam {
	private List<Stem> stems = new ArrayList<>();
	
	public void clearStems() {
		stems.clear();
	}
	
	public void addStem(Stem stem) {
		stems.add(stem);
	}

	public void draw(ScoreCanvas canvas) {
		int upDirections = 0;
		int topY = Integer.MAX_VALUE;
		int bottomY = -Integer.MIN_VALUE;
		
		for(Stem stem:stems) {
			upDirections += (stem.getDirection() == StemDirection.UP) ? 1 : -1;
			topY = Math.min(topY, stem.getEndY());
			bottomY = Math.max(bottomY, stem.getEndY());
		}
		
		StemDirection direction = (upDirections > 0) ? StemDirection.UP : StemDirection.DOWN;
		
		int beamY = (direction == StemDirection.UP) ? topY : bottomY;
		
		int[] flags = new int[stems.size()];
		for(int i = 0; i < stems.size(); i++) {
			flags[i] = getFlagCount(stems.get(i).getDuration().getType());
		}
		
		int[] stemStartX = new int[stems.size()];
		for(int i = 0; i < stems.size(); i++) {
			Stem stem = stems.get(i);
			stemStartX[i] = getStemStartX(stems.get(i), direction);
			canvas.drawLine(3, SWT.CAP_ROUND, stemStartX[i], stem.getStartY(), stemStartX[i], beamY);
		}

		int y = beamY;

		for(int i = 0; i < stems.size(); i++) {
			for(int j = 1; j <= 3; j++) {
				if(j <= flags[i]) {
					int d = (direction == StemDirection.UP) ? 1 : -1;
					if(i < stems.size() - 1 && j <= flags[i+1]) {
						canvas.drawLine(6, SWT.CAP_SQUARE, stemStartX[i] + 1, y + d*((j-1)*9), stemStartX[i+1] - 1, y + d*((j-1)*9));
					} else if(i > 0 && j <= flags[i-1]) {
					} else {
						if(i > 0) {
							canvas.drawLine(6, SWT.CAP_SQUARE, stemStartX[i] - 1, y + d*((j-1)*9), (stemStartX[i] + stemStartX[i-1]) / 2, y + d*((j-1)*9));	
						} else {
							canvas.drawLine(6, SWT.CAP_SQUARE, stemStartX[i] + 1, y + d*((j-1)*9), (stemStartX[i] + stemStartX[i+1]) / 2 - 1, y + d*((j-1)*9));
						}
					}
				}
			}
		}
	}

	private int getFlagCount(DurationType duration) {
		switch(duration) {
			case EIGHTH: return 1;
			case SIXTEENTH: return 2;
			case THIRTYSECOND: return 3;
			default: return 0;
		}
	}

	private int getStemStartX(Stem stem, StemDirection direction) {
		int offset = (stem.getDirection() != direction) ? -18 : 0;
		if(direction == StemDirection.UP) {
			offset = -offset;
		}
		return stem.getStartX() + offset;
	}
}