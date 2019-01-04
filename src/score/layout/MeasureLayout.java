package score.layout;

import static view.ScoreCanvas.MEASURE_SPACING;

import java.util.ArrayList;
import java.util.List;

import score.Measure;

/**
 * Lays out measures across multiple lines.
 */
public class MeasureLayout {
	public static class Row {
		private List<Measure> measures = new ArrayList<>();
		private int extraWidth;
		
		/**
		 * Returns all the measures in this row.
		 */
		public List<Measure> getMeasures() {
			return measures;
		}
		
		/**
		 * Returns the extra width needed to be added in total to the measures in this row to reach the page width.
		 */
		public int getExtraWidth() {
			return extraWidth;
		}
	}
	
	private final List<Row> rows = new ArrayList<>();
	
	public MeasureLayout(int pageWidth, List<Measure> measures) {
		Row row = new Row();
		rows.add(row);
		int width = 0;
		
		Measure previousMeasure = null;
		
		Measure previousMeasureOnLine = null;
		
		// Add each measure to current row, or a new row.
		for(Measure measure:measures) {
			int measureWidth = measure.getWidth(previousMeasureOnLine, previousMeasure);
			
			// Create new row after page with is reached.
			if(width + measureWidth + MEASURE_SPACING > pageWidth) {
				if(!row.getMeasures().isEmpty()) {
					row = new Row();
					rows.add(row);
				}
				width = 0;
				previousMeasureOnLine = null;
			}
			
			measureWidth = measure.getWidth(previousMeasureOnLine, previousMeasure);
			
			width += measureWidth + MEASURE_SPACING;
			row.measures.add(measure);
			row.extraWidth = pageWidth - width;
			
			previousMeasure = measure;
			previousMeasureOnLine = measure;
		}
		
		if(row.measures.isEmpty()) {
			rows.remove(rows.size() - 1);
		}
	}
	
	public List<Row> getRows() {
		return rows;
	}
}