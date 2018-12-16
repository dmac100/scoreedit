package view.tool;

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import score.Model;
import score.Selectable;
import view.ScoreCanvas;

public class SelectionTool implements Tool {
	private boolean mouseDown = false;
	private float mouseDownX;
	private float mouseDownY;
	private float prevX;
	private float prevY;
	private float x;
	private float y;
	
	private final Composite composite;
	private final Model model;
	private final ScoreCanvas scoreCanvas;

	public SelectionTool(Composite composite, Model model, ScoreCanvas scoreCanvas) {
		this.composite = composite;
		this.model = model;
		this.scoreCanvas = scoreCanvas;
	}

	public void mouseUp(int button, float x, float y) {
		if(button == 1) {
			if(mouseDown) {
				float x1 = Math.min(mouseDownX, x);
				float y1 = Math.min(mouseDownY, y);
				float x2 = Math.max(mouseDownX, x);
				float y2 = Math.max(mouseDownY, y);
				
				List<Selectable> items = scoreCanvas.getItemsInRectangle(new Rectangle((int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1)));
				model.selectItems(items);
				
				mouseDown = false;
				composite.redraw();
			}
		}
	}
	
	public void mouseDown(int button, float x, float y) {
		if(button == 1) {
			mouseDown = true;
			prevX = x;
			prevY = y;
			mouseDownX = x;
			mouseDownY = y;
		}
	}
	
	public void mouseMove(float x, float y) {
		if(mouseDown) {
			prevX = x;
			prevY = y;
			
			this.x = x;
			this.y = y;
			
			composite.redraw();
		}
	}
	
	public void paint(GC gc) {
		Color selectionColor = new Color(Display.getDefault(), 35, 100, 240);
		
		if(mouseDown) {
			gc.setAlpha(25);
			gc.setBackground(selectionColor);
			gc.setForeground(selectionColor);
			gc.fillRectangle((int) x, (int) y, (int) (mouseDownX - x), (int) (mouseDownY - y));
			gc.setAlpha(70);
			gc.drawRectangle((int) x, (int) y, (int) (mouseDownX - x), (int) (mouseDownY - y));
		}
		
		selectionColor.dispose();
	}
}