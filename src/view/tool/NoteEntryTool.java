package view.tool;

import static util.CollectionUtil.flatMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import score.CanvasItem;
import score.Chord;
import score.Clef;
import score.Duration;
import score.Duration.DurationType;
import score.KeySig;
import score.Measure;
import score.Model;
import score.Note;
import score.Pitch;
import score.Voice;
import view.FetaFont;
import view.ScoreCanvas;

public class NoteEntryTool implements Tool {
	private final Composite composite;
	private final Model model;
	private final ScoreCanvas scoreCanvas;
	
	private int mx;
	private int my;
	
	private Measure measure = null;
	private Pitch pitch = null;
	private CanvasItem item = null;
	private Clef clef = null;

	public NoteEntryTool(Composite composite, Model model, ScoreCanvas scoreCanvas) {
		this.composite = composite;
		this.model = model;
		this.scoreCanvas = scoreCanvas;
	}

	@Override
	public void mouseUp(int button, int stateMask, float x, float y) {
		if(button != 1) {
			return;
		}
		
		if(measure != null && pitch != null && item != null && clef != null) {
			Voice voice = measure.getVoices(clef).get(0);
			int startTime = measure.getStartTime(item);
			Duration duration = model.getDuration();
			
			Pitch pitchWithAccidentals = voice.getPitchWithSharpsOrFlats(pitch, measure.getKeySig(), startTime);
			
			Chord item = new Chord(clef, Arrays.asList(new Note(pitchWithAccidentals, duration)), duration);
			voice.insertItem(item, startTime);
			
			measure.autoBeam();
			
			model.selectItems(item.getNotes(), false, false);
			
			composite.redraw();
		}
	}
	
	@Override
	public void mouseDown(int button, int stateMask, float x, float y) {
	}
	
	@Override
	public void mouseMove(float x, float y) {
		this.mx = (int) x;
		this.my = (int) y;
		composite.redraw();
	}
	
	@Override
	public void paint(GC gc) {
		Map<Measure, Rectangle> measureBounds = scoreCanvas.getMeasureBounds();
		Map<CanvasItem, Rectangle> itemBounds = scoreCanvas.getItemBounds();
		
		this.measure = null;
		this.pitch = null;
		this.item = null;
		
		Measure measure = getClosestKey(measureBounds, mx, my);
		if(measure != null) {
			Rectangle measureRectangle = measureBounds.get(measure);
			Clef clef = (my < measureRectangle.y + 8*8 + ScoreCanvas.STAFF_SPACING / 2) ? Clef.TREBLE : Clef.BASS;
			Map<CanvasItem, Rectangle> measureItems = filterKeys(itemBounds, flatMap(measure.getVoices(clef), Voice::getItems));
			
			CanvasItem item = getClosestKey(measureItems, mx, my);
			
			if(item != null) {
				Rectangle itemRectangle = itemBounds.get(item);
				
				if(mx >= itemRectangle.x && mx <= itemRectangle.x + itemRectangle.width) {
					int scaleNumber = clef.getLowScaleNumber() + ((measureRectangle.y + clef.getOffset() + 80) - my) / 8;
					
					if(scaleNumber < new Pitch("A0").getScaleNumber() || scaleNumber > new Pitch("C9").getScaleNumber()) {
						return;
					}
					
					this.measure = measure;
					this.pitch = new Pitch(scaleNumber);
					this.item = item;
					this.clef = clef;
					
					drawNote(gc, itemRectangle.x, measureRectangle.y + clef.getOffset(), pitch.getScaleNumber() - clef.getLowScaleNumber(), new Duration(model.getDurationType()));
				}
			}
		}
	}

	private void drawNote(GC gc, int startX, int startY, int scaleNumber, Duration duration) {
		drawNoteHead(gc, startX, startY, scaleNumber, duration);
		
		int ledgersBelow = -(scaleNumber - 2) / 2;
		int ledgersAbove = (scaleNumber - 10) / 2;
		
		drawLedgers(gc, startX, startY, ledgersBelow, ledgersAbove);
	}
	
	private void drawNoteHead(GC gc, int startX, int startY, int scaleNumber, Duration duration) {
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
		
		if(duration.getType() == DurationType.WHOLE) {
			startX -= 5;
		}
		gc.drawText(FetaFont.getNoteHead(duration), startX, startY - (scaleNumber * 8) - 71, true);
	}
	
	private void drawLedgers(GC gc, int startX, int startY, int ledgersBelow, int ledgersAbove) {
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
		gc.setLineWidth(3);
		gc.setLineCap(SWT.CAP_SQUARE);
		
		for(int i = 0; i < ledgersAbove; i++) {
			gc.drawLine(
				startX - 7,
				startY - ((i + 1) * 16),
				startX + 27,
				startY - ((i + 1) * 16)
			);
		}
		
		for(int i = 0; i < ledgersBelow; i++) {
			gc.drawLine(
				startX - 7,
				startY + ((5 + i) * 16),
				startX + 27,
				startY + ((5 + i) * 16)
			);
		}
	}

	private static <K, V> Map<K, V> filterKeys(Map<K, V> map, Collection<K> keys) {
		Map<K, V> filteredMap = new HashMap<>();
		keys.forEach(key -> filteredMap.put(key, map.get(key)));
		return filteredMap;
	}

	private static <T> T getClosestKey(Map<T, Rectangle> bounds, int x, int y) {
		T closestKey = null;
		int closestDistance = Integer.MAX_VALUE;
		for(T key:bounds.keySet()) {
			Rectangle rectangle = bounds.get(key);
			
			int distance = (rectangle.contains(x, y)) ? 0 : Integer.MAX_VALUE;
			
			if(x >= rectangle.x && x <= rectangle.x + rectangle.width) {
				distance = Math.min(distance, Math.min(Math.abs(rectangle.y - y), Math.abs(rectangle.y + rectangle.height - y)));
			}
			
			if(distance < closestDistance) {
				closestDistance = distance;
				closestKey = key;
			}
		}
		return closestKey;
	}
}