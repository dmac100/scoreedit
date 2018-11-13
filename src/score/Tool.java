package score;

import org.eclipse.swt.graphics.GC;

interface Tool {
	public default void paint(GC gc) {}
	public default void mouseUp(int button, float x, float y) {}
	public default void mouseDown(int button, float x, float y) {}
	public default void mouseMove(float x, float y) {}
	public default void mouseScrolled(int count) {}
}