package score;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

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
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import score.MeasureLayout.Row;
import util.CollectionUtil;

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
				
				List<Row> rows = new MeasureLayout(1950 - 200, model.getMeasures()).getRows();
				
				int pageHeight = Math.max(3000, 200 * rows.size() + 100);
				
				drawPage(gc, 0, 0, 2000, pageHeight);
				
				int measureSpacing = 60;

				int startY = 150;
				
				for(Row row:rows) {
					drawSystem(gc, 50, 1950, startY);
					
					int extraMeasureWidth = row.getExtraWidth() / row.getMeasures().size();
					
					int x = 200;
					for(Measure measure:row.getMeasures()) {
						x += measureSpacing;
						measure.drawMeasure(gc, x, startY, extraMeasureWidth);
						x += measure.getWidth() + extraMeasureWidth;
						if(measure == CollectionUtil.getLast(row.getMeasures())) {
							x = 1950;
						}
						drawBarLine(gc, x, startY);
					}
					
					startY += 350;
				}
				
				gc.setAlpha(255);
				currentTool.paint(gc);
				
				transform.dispose();
			}

			private void drawPage(GC gc, int startX, int startY, int pageWidth, int pageHeight) {
				gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
				gc.fillRectangle(startX, startY, pageWidth, pageHeight);
				gc.drawRectangle(startX, startY, pageWidth, pageHeight);
			}

			private void drawBarLine(GC gc, int startX, int startY) {
				int staffSpacing = 80;
				gc.setLineCap(SWT.CAP_SQUARE);
				gc.setLineWidth(2);
				gc.drawLine(startX, startY, startX, startY + 8*8 + 8*8 + staffSpacing);
			}
			
			private void drawSystem(GC gc, int startX, int endX, int startY) {
				int staffSpacing = 80;
				gc.setLineWidth(2);
				gc.setLineCap(SWT.CAP_SQUARE);
				gc.drawLine(startX, startY, startX, startY + staffSpacing + 8*8 + 8*8);
				drawStaff(gc, Clef.TREBLE, startX, endX, startY);
				drawStaff(gc, Clef.BASS, startX, endX, startY + staffSpacing + 8*8);
			}

			private void drawStaff(GC gc, Clef clef, int startX, int endX, int startY) {
				gc.setLineWidth(2);
				
				for(int y = 0; y <= 64; y += 16) {
					gc.drawLine(startX, y + startY, endX, y + startY);
				}
				
				if(clef == Clef.TREBLE) {
					gc.drawText(FetaFont.TREBLECLEF, startX + 40, startY - 102, true);
				} else if(clef == Clef.BASS) {
					gc.drawText(FetaFont.BASSCLEF, startX + 40, startY - 135, true);
				}
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