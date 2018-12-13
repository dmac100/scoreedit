package score.layout;

import static view.ScoreCanvas.MEASURE_SPACING;

import java.util.ArrayList;
import java.util.List;

import score.Measure;

public class MeasureLayout {
	public static class Row {
		private List<Measure> measures = new ArrayList<>();
		private int width;
		private int extraWidth;
		
		public List<Measure> getMeasures() {
			return measures;
		}
		
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
		
		for(Measure measure:measures) {
			int measureWidth = measure.getWidth(previousMeasureOnLine, previousMeasure);
			
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
			row.width = width;
			row.extraWidth = pageWidth - row.width;
			
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