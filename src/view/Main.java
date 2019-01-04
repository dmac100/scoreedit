package view;

import static view.ScoreCanvas.MEASURE_SPACING;
import static view.ScoreCanvas.PAGE_WIDTH;
import static view.ScoreCanvas.STAFF_SPACING;
import static view.ScoreCanvas.SYSTEM_SPACING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import playback.Synth;
import score.Clef;
import score.Duration.DurationType;
import score.Measure;
import score.Model;
import score.layout.Divider;
import score.layout.MeasureLayout;
import score.layout.MeasureLayout.Row;
import view.common.CommandList;
import view.common.GridDataBuilder;
import view.common.GridLayoutBuilder;
import view.common.MenuBuilder;
import view.common.RunCommand;
import view.tool.NoteEntryTool;
import view.tool.SelectionTool;
import view.tool.Tool;

/**
 * Handles the main window, toolbar, menubar, and keyboard shortcuts.
 */
public class Main {
	private final Shell shell;
	private final Composite composite;
	
	private final CommandList commandList = new CommandList();

	private final PanAndZoomHandler panAndZoomHandler;
	private final SelectionTool selectionTool;
	private final NoteEntryTool noteEntryTool;
	
	private final ScoreCanvas canvas = new ScoreCanvas();
	private final Model model = new Model();
	private final Synth synth = new Synth();
	
	private Tool currentTool;
	
	public Main(Shell shell) {
		this.shell = shell;
		
		Display.getCurrent().loadFont(createFont().getAbsolutePath());
		
		shell.setLayout(new GridLayoutBuilder().numColumns(1).verticalSpacing(1).marginHeight(0).build());
		
		ToolBar toolbar = new ToolBar(shell, SWT.FLAT | SWT.BORDER);
		toolbar.setLayoutData(new GridDataBuilder().fillHorizontal().build());
		
		composite = new Composite(shell, SWT.DOUBLE_BUFFERED | SWT.BORDER);
		composite.setLayoutData(new GridDataBuilder().fillHorizontal().fillVertical().build());
		
		panAndZoomHandler = new PanAndZoomHandler(composite);
		
		selectionTool = new SelectionTool(composite, model, canvas);
		noteEntryTool = new NoteEntryTool(composite, model, canvas);
		
		currentTool = selectionTool;
		
		refreshToolbarItems(toolbar);
		model.addSelectionChangedHandler(() -> refreshToolbarItems(toolbar));
		
		model.addSelectionChangedHandler(() -> {
			model.getCurrentSelectedNotes().forEach(note -> synth.play(note));
		});
		
		composite.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				float[] position = transform(event.x, event.y);
				
				currentTool.mouseDown(event.button, event.stateMask, position[0], position[1]);
			}

			public void mouseUp(MouseEvent event) {
				float[] position = transform(event.x, event.y);
				
				currentTool.mouseUp(event.button, event.stateMask, position[0], position[1]);
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
				
				mscore.dispose();
			}
		});
		
		composite.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				onKeyPressed(event);
				
				composite.redraw();
				refreshToolbarItems(toolbar);
			}
		});
		
		createMenu();
	}
	
	private void onKeyPressed(KeyEvent event) {
		if(event.stateMask == 0) {
			if(event.keyCode == 'n') {
				currentTool = noteEntryTool;
			} else if(event.keyCode == SWT.ESC) {
				currentTool = selectionTool;
			} else if(event.keyCode == SWT.DEL) {
				model.deleteSelection(true);
			} else if(event.keyCode == '1') {
				model.setDurationType(DurationType.WHOLE);
			} else if(event.keyCode == '2') {
				model.setDurationType(DurationType.HALF);
			} else if(event.keyCode == '3') {
				model.setDurationType(DurationType.QUARTER);
			} else if(event.keyCode == '4') {
				model.setDurationType(DurationType.EIGHTH);
			} else if(event.keyCode == '5') {
				model.setDurationType(DurationType.SIXTEENTH);
			} else if(event.keyCode == '6') {
				model.setDurationType(DurationType.THIRTYSECOND);
			} else if(event.keyCode == '.') {
				model.setDots((model.getDots() == 1) ? 0 : 1);
			} else if(event.keyCode == 'r') {
				model.setRest(!model.getRest());
			} else if(event.keyCode >= 'a' && event.keyCode <= 'g') {
				model.insertNote(Character.toUpperCase((char) event.keyCode));
			} else if(event.keyCode == SWT.ARROW_UP) {
				model.shiftSelectionPitch(1);
			} else if(event.keyCode == SWT.ARROW_DOWN) {
				model.shiftSelectionPitch(-1);
			}
		} else if(event.stateMask == SWT.CONTROL) {
			if(event.keyCode == SWT.DEL) {
				model.deleteSelection(false);
			} else if(event.keyCode == SWT.ARROW_UP) {
				model.shiftSelectionOctave(1);
			} else if(event.keyCode == SWT.ARROW_DOWN) {
				model.shiftSelectionOctave(-1);
			}
		} else if(event.stateMask == SWT.SHIFT) {
			if(event.keyCode >= 'a' && event.keyCode <= 'g') {
				model.addNoteToSelectChords(Character.toUpperCase((char) event.keyCode));
			}
		}
		
		if(event.keyCode == SWT.ARROW_LEFT) {
			model.selectPrev((event.stateMask & (SWT.SHIFT)) > 0, (event.stateMask & (SWT.CONTROL)) > 0);
		} else if(event.keyCode == SWT.ARROW_RIGHT) {
			model.selectNext((event.stateMask & (SWT.SHIFT)) > 0, (event.stateMask & (SWT.CONTROL)) > 0);
		}
	}
	
	private void refreshToolbarItems(ToolBar toolbar) {
		Arrays.stream(toolbar.getItems()).forEach(ToolItem::dispose);
		
		ToolItem insertItem = new ToolItem(toolbar, SWT.CHECK);
		insertItem.setImage(ToolbarImages.getInsertImage());
		insertItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				currentTool = (insertItem.getSelection() ? noteEntryTool : selectionTool);
			}
		});
		
		insertItem.setSelection(currentTool == noteEntryTool);
		
		new ToolBar(toolbar, SWT.SEPARATOR);
		
		ToolItem wholeItem = addToolbarRadioItem(toolbar, ToolbarImages.createImage(DurationType.WHOLE), () -> {
			model.setDurationType(DurationType.WHOLE);
			composite.redraw();
		});
		
		ToolItem halfItem = addToolbarRadioItem(toolbar, ToolbarImages.createImage(DurationType.HALF), () -> {
			model.setDurationType(DurationType.HALF);
			composite.redraw();
		});
		
		ToolItem quarterItem = addToolbarRadioItem(toolbar, ToolbarImages.createImage(DurationType.QUARTER), () -> {
			model.setDurationType(DurationType.QUARTER);
			composite.redraw();
		});
		
		ToolItem eighthItem = addToolbarRadioItem(toolbar, ToolbarImages.createImage(DurationType.EIGHTH), () -> {
			model.setDurationType(DurationType.EIGHTH);
			composite.redraw();
		});
		
		ToolItem sixteenthItem = addToolbarRadioItem(toolbar, ToolbarImages.createImage(DurationType.SIXTEENTH), () -> {
			model.setDurationType(DurationType.SIXTEENTH);
			composite.redraw();
		});
		
		ToolItem thirtySecondItem = addToolbarRadioItem(toolbar, ToolbarImages.createImage(DurationType.THIRTYSECOND), () -> {
			model.setDurationType(DurationType.THIRTYSECOND);
			composite.redraw();
		});
		
		new ToolBar(toolbar, SWT.SEPARATOR);
		
		ToolItem dotItem = new ToolItem(toolbar, SWT.CHECK);
		dotItem.setImage(ToolbarImages.getDotImage());
		dotItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				model.setDots(dotItem.getSelection() ? 1 : 0);
				composite.redraw();
			}
		});
		
		new ToolBar(toolbar, SWT.SEPARATOR);
		
		ToolItem restItem = new ToolItem(toolbar, SWT.CHECK);
		restItem.setImage(ToolbarImages.getRestImage());
		restItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				model.setRest(restItem.getSelection());
				composite.redraw();
			}
		});
		
		wholeItem.setSelection(model.getDurationType() == DurationType.WHOLE);
		halfItem.setSelection(model.getDurationType() == DurationType.HALF);
		quarterItem.setSelection(model.getDurationType() == DurationType.QUARTER);
		eighthItem.setSelection(model.getDurationType() == DurationType.EIGHTH);
		sixteenthItem.setSelection(model.getDurationType() == DurationType.SIXTEENTH);
		thirtySecondItem.setSelection(model.getDurationType() == DurationType.THIRTYSECOND);
		
		dotItem.setSelection(model.getDots() == 1);
		restItem.setSelection(model.getRest());
	}
	
	private ToolItem addToolbarRadioItem(ToolBar toolbar, Image image, Runnable runnable) {
		ToolItem item = new ToolItem(toolbar, SWT.RADIO);
		item.setImage(image);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				runnable.run();
			}
		});
		return item;
	}

	private void createMenu() {
		MenuBuilder menuBuilder = new MenuBuilder(shell, commandList);
		
		menuBuilder.addMenu("File")
			.addItem("&New\tCtrl+N").addSelectionListener(() -> newFile())
			.setAccelerator(SWT.CONTROL |  'n')
			.addItem("&Open\tCtrl+O").addSelectionListener(() -> {
				String selected = selectOpenLocationWithDialog("*.pgn", "*.*");
				if(selected != null) {
					try {
						open(selected);
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.setAccelerator(SWT.CONTROL | 'o')
			.addSeparator()
			.addItem("&Save\tCtrl+S").addSelectionListener(() -> {
				try {
					save();
				} catch(Exception e) {
					displayException(e);
				}
			})
			.addItem("Save &As...\tShift+Ctrl+S").addSelectionListener(() -> {
				String selected = selectOpenLocationWithDialog("*.pgn", "*.*");
				if(selected != null) {
					try {
						saveAs(selected);
					} catch(Exception e) {
						displayException(e);
					}
				}
			})
			.setAccelerator(SWT.CONTROL | SWT.SHIFT | 's')
			.addSeparator()
			.addItem("Run Command...\tCtrl+3").addSelectionListener(() -> runCommand()).setAccelerator(SWT.CONTROL | '3')
			.addSeparator()
			.addItem("E&xit\tCtrl+Q").addSelectionListener(() -> shell.dispose())
			.setAccelerator(SWT.CONTROL |  'q');
		
		menuBuilder.build();
	}
	
	private void runCommand() {
		RunCommand runCommand = new RunCommand(shell);
		runCommand.setSearchFunction(findText -> commandList.findCommands(findText));
		String result = runCommand.open();
		if(result != null) {
			commandList.runCommand(result);
		}
	}

	private void newFile() {
	}
	
	private void open(String path) {
	}
	
	private void save() {
	}
	
	private void saveAs(String path) {
	}
	
	private String selectOpenLocationWithDialog(String... extensions) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Open");
		dialog.setFilterExtensions(extensions);
		
		return dialog.open();
	}
	
	private String selectSaveLocationWithDialog(String defaultName, String... extensions) {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Save");
		dialog.setFilterExtensions(extensions);
		dialog.setFileName(defaultName);
		
		return dialog.open();
	}
	
	private void displayException(Exception e) {
		MessageBox messageBox = new MessageBox(shell);
		messageBox.setText("Error");
		messageBox.setMessage(e.getMessage() == null ? e.toString() : e.getMessage());
		e.printStackTrace();
		
		messageBox.open();
	}

	private void drawScore(ScoreCanvas canvas) {
		List<Row> rows = new MeasureLayout(PAGE_WIDTH - 100, model.getMeasures()).getRows();
		
		int pageHeight = Math.max(3000, SYSTEM_SPACING * rows.size() + 100);
		
		drawPage(canvas, 0, 0, PAGE_WIDTH + 50, pageHeight);
		
		int startY = 150;
		
		Measure previousMeasure = null;
		
		for(Row row:rows) {
			Measure previousMeasureOnLine = null;
			
			drawSystem(canvas, 50, PAGE_WIDTH, startY);
			
			Divider measureSpacingDividor = new Divider(row.getExtraWidth(), row.getMeasures().size());
			
			int x = 100;
			for(Measure measure:row.getMeasures()) {
				int extraMeasureWidth = measureSpacingDividor.next();
				
				x += MEASURE_SPACING;
				measure.drawMeasure(canvas, x, startY, extraMeasureWidth, previousMeasureOnLine, previousMeasure);
				int measureWidth = measure.getWidth(previousMeasureOnLine, previousMeasure) + extraMeasureWidth;
				canvas.setMeasureBounds(measure, x - MEASURE_SPACING, startY, measureWidth + MEASURE_SPACING, 8*8 + Clef.BASS.getOffset());
				x += measureWidth;
				drawBarLine(canvas, x, startY);
				
				previousMeasure = measure;
				previousMeasureOnLine = measure;
			}
			
			startY += SYSTEM_SPACING;
		}
	}

	private void drawPage(ScoreCanvas canvas, int startX, int startY, int pageWidth, int pageHeight) {
		canvas.fillRectangle(startX, startY, pageWidth, pageHeight);
	}

	private void drawBarLine(ScoreCanvas canvas, int startX, int startY) {
		canvas.drawLine(2, SWT.CAP_SQUARE, startX, startY, startX, startY + 8*8 + 8*8 + STAFF_SPACING);
	}
	
	private void drawSystem(ScoreCanvas canvas, int startX, int endX, int startY) {
		canvas.drawLine(2, SWT.CAP_SQUARE, startX, startY, startX, startY + STAFF_SPACING + 8*8 + 8*8);
		drawStaff(canvas, Clef.TREBLE, startX, endX, startY);
		drawStaff(canvas, Clef.BASS, startX, endX, startY + STAFF_SPACING + 8*8);
	}

	private void drawStaff(ScoreCanvas canvas, Clef clef, int startX, int endX, int startY) {
		for(int y = 0; y <= 64; y += 16) {
			canvas.drawLine(2, SWT.CAP_SQUARE, startX, y + startY, endX, y + startY);
		}
		
		if(clef == Clef.TREBLE) {
			canvas.drawText(FetaFont.TREBLECLEF, startX + 20, startY - 102, false);
		} else if(clef == Clef.BASS) {
			canvas.drawText(FetaFont.BASSCLEF, startX + 20, startY - 135, false);
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
		
		Main main = new Main(shell);
		
		shell.setVisible(true);
		shell.setSize(1100, 800);
		shell.setText("ScoreEdit");
		
		main.composite.setFocus();
		
		while(!shell.isDisposed()) {
			while(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();
	}
}