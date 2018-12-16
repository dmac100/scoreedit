package view.tool;

import org.eclipse.swt.graphics.GC;

public interface Tool {
	public default void paint(GC gc) {}
	public default void mouseUp(int button, int stateMask, float x, float y) {}
	public default void mouseDown(int button, int stateMask, float x, float y) {}
	public default void mouseMove(float x, float y) {}
	public default void mouseScrolled(int count) {}
}