package drawapp;

import java.awt.Point;
import java.awt.Rectangle;

/** Aspect-preserving viewport shared by painting and mouse input. */
public final class GameViewport {
    private GameViewport() {
    }

    public static Rectangle bounds(int canvasWidth, int canvasHeight, int windowWidth, int windowHeight) {
        double scale = Math.min((double) windowWidth / canvasWidth, (double) windowHeight / canvasHeight);
        int width = Math.max(1, (int) Math.round(canvasWidth * scale));
        int height = Math.max(1, (int) Math.round(canvasHeight * scale));
        return new Rectangle((windowWidth - width) / 2, (windowHeight - height) / 2, width, height);
    }

    public static Point toGame(Point point, Rectangle viewport, int width, int height) {
        if (!viewport.contains(point)) {
            return new Point(-1, -1);
        }
        return new Point((int) ((long) (point.x - viewport.x) * width / viewport.width),
            (int) ((long) (point.y - viewport.y) * height / viewport.height));
    }
}
