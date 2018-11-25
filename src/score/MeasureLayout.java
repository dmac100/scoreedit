package score;

import java.util.ArrayList;
import java.util.List;

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
		int measureSpacing = 30;
		
		Row row = new Row();
		rows.add(row);
		int width = 0;
		
		for(Measure measure:measures) {
			int measureWidth = measure.getWidth();
			
			if(width + measureWidth + measureSpacing > pageWidth) {
				row = new Row();
				rows.add(row);
				width = 0;
			}
			
			width += measureWidth + measureSpacing;
			row.measures.add(measure);
			row.width = width;
			row.extraWidth = pageWidth - row.width;
		}
		
		if(row.measures.isEmpty()) {
			rows.remove(rows.size() - 1);
		}
	}
	
	
	public List<Row> getRows() {
		return rows;
	}
}
