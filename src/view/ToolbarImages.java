package view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

import score.Duration;
import score.Duration.DurationType;
import score.Stem.StemDirection;

public class ToolbarImages {
	private static final Map<DurationType, Image> durationTypeImages = new HashMap<>();
	
	public static Image createImage(DurationType durationType) {
		return durationTypeImages.computeIfAbsent(durationType, key -> {
			return createImage(gc -> {
				gc.drawText(FetaFont.getNoteHead(new Duration(durationType)), 5, -50, true);
				gc.drawText(FetaFont.getFlags(new Duration(durationType), StemDirection.UP), 14, -80, true);
				
				if(durationType != DurationType.WHOLE) {
					gc.setLineWidth(2);
					gc.setLineCap(SWT.CAP_ROUND);
					gc.drawLine(15, 0, 15, 25);
				}			
			});			
		});
	}

	public static Image getDotImage() {
		return createImage(gc -> {
			gc.drawText(FetaFont.DOT, 5, -45, true);	
		});
	}
	
	public static Image getRestImage() {
		return createImage(gc -> {
			gc.drawText(FetaFont.QUARTERREST, 10, -60, true);	
		});
	}
	
	public static Image getInsertImage() {
		return createImage(gc -> {
			gc.drawText("N", 0, 0, true);	
		});
	}
	
	private static Image createImage(Consumer<GC> callback) {
		ImageData imageData = new ImageData(25, 30, 24, new PaletteData(0xff0000, 0x00ff00, 0x0000ff));
		imageData.setAlpha(0, 0, 0);
		Arrays.fill(imageData.alphaData, (byte) 0);
		
		Image image = new Image(null, imageData);
		
		GC gc = new GC(image);
		Font mscore = new Font(Display.getCurrent(), "MScore", 23, SWT.NORMAL);
		gc.setFont(mscore);
		
		gc.setAntialias(SWT.ON);
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		
		callback.accept(gc);
		
		gc.dispose();
		
		mscore.dispose();
		
		return image;
	}
}