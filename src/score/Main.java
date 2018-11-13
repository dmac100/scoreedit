package score;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {
	private final Shell shell;
	private final Composite composite;
	
	private final PanAndZoomHandler panAndZoomHandler;
	private final SelectionTool selectionTool;
	
	public Main(Shell shell) {
		this.shell = shell;
		composite = new Composite(shell, SWT.DOUBLE_BUFFERED);
		
		shell.setLayout(new FillLayout());
		
		Model model = new Model();
		
		panAndZoomHandler = new PanAndZoomHandler(composite);
		
		selectionTool = new SelectionTool(composite, model);
		
		Display.getCurrent().loadFont(createFont().getAbsolutePath());
		
		Tool currentTool = selectionTool;
		
		composite.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				float[] position = transform(event.x, event.y);
				
				currentTool.mouseDown(event.button, position[0], position[1]);
			}

			public void mouseUp(MouseEvent event) {
				float[] position = transform(event.x, event.y);
				
				currentTool.mouseUp(event.button, position[0], position[1]);
			}
		});
		
		composite.addMouseWheelListener(new MouseWheelListener() {
			public void mouseScrolled(MouseEvent event) {
				currentTool.mouseScrolled(event.count);
			}
		});
		
		composite.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent event) {
				float[] position = transform(event.x, event.y);
				
				currentTool.mouseMove(position[0], position[1]);
			}
		});
		
		composite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				GC gc = event.gc;
				
				Font mscore = new Font(Display.getCurrent(), "MScore", 46, SWT.NORMAL);
				gc.setFont(mscore);

				Transform transform = panAndZoomHandler.getTransform();
				
				gc.setTransform(transform);
				
				gc.setAlpha(255);
				gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
				gc.fillRectangle(0, 0, 2000, 3000);
				gc.drawRectangle(0, 0, 2000, 3000);
				
				drawStaff(gc, 50, 1950, 100);
				
				int spacing = 60;
				
				int x = 220;
				for(CanvasItem item:model.getItems()) {
					int startX = x + 50;
					int startY = 100;
					
					item.draw(gc, startX, startY);
					drawBoundingBox(gc, item.getBoundingBox(startX, startY));
					x += item.getBoundingBox(startX, startY).width;
					x += spacing;
				}
				
				gc.setAlpha(255);
				currentTool.paint(gc);
				
				transform.dispose();
			}

			private void drawBoundingBox(GC gc, Rectangle box) {
				gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
				//gc.drawRectangle(box);
				gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
			}

			private void drawStaff(GC gc, int startX, int endX, int startY) {
				for(int y = 0; y <= 64; y += 16) {
					gc.drawLine(startX, y + startY, endX, y + startY);
				}
				
				gc.drawText(FetaFont.TREBLECLEF, startX + 40, startY - 102, true);
				gc.drawText(FetaFont.SHARP, startX + 100, startY - 152, true);
				gc.drawText(FetaFont.CUTCOMMON, startX + 140, startY - 119, true);
			}
		});
		
		composite.setFocus();
	}
	
	private static File createFont() {
		try {
			File file = Files.createTempFile("mscore", ".ttf").toFile();
			file.deleteOnExit();
			try(InputStream inputStream = Main.class.getResourceAsStream("/resource/mscore.ttf")) {
				try(OutputStream outputStream = new FileOutputStream(file)) {
					IOUtils.copy(inputStream, outputStream);
					return file;
				}
			}
		} catch(IOException e) {
			throw new RuntimeException("Error loading font", e);
		}
	}

	private float[] transform(float x, float y) {
		Transform transform = panAndZoomHandler.getTransform();
		transform.invert();
		float[] position = { x, y };
		transform.transform(position);
		transform.dispose();
		return position;
	}

	public static void main(String[] args) {
		Display display = new Display();
		
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		
		new Main(shell);
		
		shell.setVisible(true);
		shell.setSize(1100, 800);
		shell.setText("Main");
		
		while(!shell.isDisposed()) {
			while(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();
	}
}