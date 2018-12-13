package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class PanAndZoomHandler {
	private Control control;
	
	private float scale = 0.5f;
	private float tx = 100;
	private float ty = 100;
	
	public PanAndZoomHandler(Control control) {
		this.control = control;
	
		control.addMouseWheelListener(new MouseWheelListener() {
			public void mouseScrolled(MouseEvent event) {
				if((event.stateMask & SWT.CTRL) > 0) {
					int x = event.x;
					int y = event.y;
					
					float newScale = (float) (scale * Math.pow(1.1f, event.count));
		
					tx = (scale * tx - x) / scale + x / newScale;
					ty = (scale * ty - y) / scale + y / newScale;
					
					scale = newScale;
				} else if((event.stateMask & SWT.SHIFT) > 0) {
					tx += (20 * event.count) / scale;
				} else {
					ty += (20 * event.count) / scale;
				}
				
				control.redraw();
			}
		});
		
		class Handler implements MouseListener, MouseMoveListener {
			private boolean mouseDown = false;
			private int mouseDownX;
			private int mouseDownY;
			private int prevX;
			private int prevY;
			
			public void mouseMove(MouseEvent event) {
				if(mouseDown) {
					tx += (event.x - prevX) / scale;
					ty += (event.y - prevY) / scale;
					
					prevX = event.x;
					prevY = event.y;
					
					control.redraw();
				}
			}

			public void mouseDoubleClick(MouseEvent event) {
			}

			public void mouseDown(MouseEvent event) {
				if(event.button == 2) {
					mouseDown = true;
					prevX = event.x;
					prevY = event.y;
					mouseDownX = event.x;
					mouseDownY = event.y;
				}
			}

			public void mouseUp(MouseEvent event) {
				if(event.button == 2) {
					mouseDown = false;
				}
			}
		}
		
		Handler handler = new Handler();
		control.addMouseListener(handler);
		control.addMouseMoveListener(handler);
	}
	
	public Transform getTransform() {
		Transform transform = new Transform(Display.getDefault());
		transform.scale(scale, scale);
		transform.translate(tx, ty);
		return transform;
	}
}