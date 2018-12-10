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

public class Main {
	private static final int pageWidth = 1950;
	private static final int systemSpacing = 350;
	private static final int measureSpacing = 30;
	
	private final Shell shell;
	private final Composite composite;
	
	private final PanAndZoomHandler panAndZoomHandler;
	private final SelectionTool selectionTool;
	private final NoteEntryTool noteEntryTool;
	
	private final ScoreCanvas canvas = new ScoreCanvas();
	private final Model model = new Model();
	
	private int mouseX = 0;
	private int mouseY = 0;
	
	public Main(Shell shell) {
		this.shell = shell;
		composite = new Composite(shell, SWT.DOUBLE_BUFFERED);
		
		shell.setLayout(new FillLayout());
		
		panAndZoomHandler = new PanAndZoomHandler(composite);
		
		selectionTool = new SelectionTool(composite, model);
		noteEntryTool = new NoteEntryTool(composite, model, canvas);
		
		Display.getCurrent().loadFont(createFont().getAbsolutePath());
		
		Tool currentTool = noteEntryTool;
		
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
				
				canvas.reset(gc);
				
				Font mscore = new Font(Display.getCurrent(), "MScore", 46, SWT.NORMAL);
				gc.setFont(mscore);
				
				Transform transform = panAndZoomHandler.getTransform();
				
				gc.setTransform(transform);
				
				gc.setAlpha(255);
				
				drawScore(canvas);
				
				gc.setAlpha(255);
				currentTool.paint(gc);
				
				transform.dispose();
			}
		});
		
		composite.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent event) {
				float[] m = transform(event.x, event.y);
				mouseX = (int) m[0];
				mouseY = (int) m[1];
				composite.redraw();
			}
		});
		
		composite.setFocus();
	}
	
	private void drawScore(ScoreCanvas canvas) {
		List<Row> rows = new MeasureLayout(pageWidth - 100, model.getMeasures()).getRows();
		
		int pageHeight = Math.max(3000, systemSpacing * rows.size() + 100);
		
		drawPage(canvas, 0, 0, pageWidth + 50, pageHeight);
		
		int startY = 150;
		
		Measure previousMeasure = null;
		
		for(Row row:rows) {
			Measure previousMeasureOnLine = null;
			
			drawSystem(canvas, 50, pageWidth, startY);
			
			Divider measureSpacingDividor = new Divider(row.getExtraWidth(), row.getMeasures().size());
			
			int x = 100;
			for(Measure measure:row.getMeasures()) {
				int extraMeasureWidth = measureSpacingDividor.next();
				
				x += measureSpacing;
				measure.drawMeasure(canvas, x, startY, extraMeasureWidth, previousMeasureOnLine, previousMeasure);
				int measureWidth = measure.getWidth(previousMeasureOnLine, previousMeasure) + extraMeasureWidth;
				canvas.setMeasureBounds(measure, x - measureSpacing, startY, measureWidth + measureSpacing, 8*8 + Clef.BASS.getOffset());
				x += measureWidth;
				drawBarLine(canvas, x, startY);
				
				previousMeasure = measure;
				previousMeasureOnLine = measure;
			}
			
			startY += systemSpacing;
		}
	}

	private void drawPage(ScoreCanvas canvas, int startX, int startY, int pageWidth, int pageHeight) {
		canvas.fillRectangle(startX, startY, pageWidth, pageHeight);
	}

	private void drawBarLine(ScoreCanvas canvas, int startX, int startY) {
		int staffSpacing = 80;
		canvas.drawLine(2, SWT.CAP_SQUARE, startX, startY, startX, startY + 8*8 + 8*8 + staffSpacing);
	}
	
	private void drawSystem(ScoreCanvas canvas, int startX, int endX, int startY) {
		int staffSpacing = 80;
		canvas.drawLine(2, SWT.CAP_SQUARE, startX, startY, startX, startY + staffSpacing + 8*8 + 8*8);
		drawStaff(canvas, Clef.TREBLE, startX, endX, startY);
		drawStaff(canvas, Clef.BASS, startX, endX, startY + staffSpacing + 8*8);
	}

	private void drawStaff(ScoreCanvas canvas, Clef clef, int startX, int endX, int startY) {
		for(int y = 0; y <= 64; y += 16) {
			canvas.drawLine(2, SWT.CAP_SQUARE, startX, y + startY, endX, y + startY);
		}
		
		if(clef == Clef.TREBLE) {
			canvas.drawText(FetaFont.TREBLECLEF, startX + 20, startY - 102);
		} else if(clef == Clef.BASS) {
			canvas.drawText(FetaFont.BASSCLEF, startX + 20, startY - 135);
		}
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