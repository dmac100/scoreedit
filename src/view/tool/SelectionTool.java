package view.tool;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
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

	@Override
	public void mouseUp(int button, int stateMask, float x, float y) {
		boolean shift = (stateMask & SWT.SHIFT) > 0;
		boolean control = (stateMask & SWT.CTRL) > 0;
		
		if(button == 1) {
			if(mouseDown) {
				if(x == mouseDownX && y == mouseDownY) {
					Selectable item = scoreCanvas.getItemAt((int) mouseDownX, (int) mouseDownY);
					if(item != null) {
						model.selectItems(Arrays.asList(item), shift, control);
					} else {
						model.selectItems(Arrays.asList(), shift, control);
					}
				} else {
					float x1 = Math.min(mouseDownX, x);
					float y1 = Math.min(mouseDownY, y);
					float x2 = Math.max(mouseDownX, x);
					float y2 = Math.max(mouseDownY, y);
					
					List<Selectable> items = scoreCanvas.getItemsInRectangle(new Rectangle((int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1)));
					model.deselectAll();
					model.selectItems(items, shift, control);
				}
				mouseDown = false;
				composite.redraw();
			}
		}
	}
	
	@Override
	public void mouseDown(int button, int stateMask, float x, float y) {
		if(button == 1) {
			mouseDown = true;
			prevX = x;
			prevY = y;
			mouseDownX = x;
			mouseDownY = y;
		}
	}
	
	@Override
	public void mouseMove(float x, float y) {
		if(mouseDown) {
			prevX = x;
			prevY = y;
			
			this.x = x;
			this.y = y;
			
			composite.redraw();
		}
	}
	
	@Override
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