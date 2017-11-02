import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2i;

public class LineDraw {
    // bresenham method implemented using LineDraw's line algorithm, which is based on the solution proposed in
    // https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java
    public static void bresenham(Vertex v1, Vertex v2) {
        // delta of exact value and rounded value of the dependant variable
        int x1 = (int)v1.x;
        int x2 = (int)v2.x;
        int y1 = (int)v1.y;
        int y2 = (int)v2.y;
        int d = 0;

        int dy = Math.abs(y2 - y1);
        int dx = Math.abs(x2 - x1);

        int dy2 = (dy << 1); // slope scaling factors to avoid floating
        int dx2 = (dx << 1); // point

        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;

        glColor4f(1.0f, 0.0f, 0.0f, 0.0f);
        glPointSize(3.0f);
        glBegin(GL_POINTS);

        if (dy <= dx) {
            for (;;) {
                glVertex2i(x1, y1);
                if (x1 == x2)
                    break;
                x1 += ix;
                d += dy2;
                if (d > dx) {
                    y1 += iy;
                    d -= dx2;
                }
            }
            glEnd();
        } else {
            for (;;) {
                glVertex2i(x1, y1);
                if (y1 == y2)
                    break;
                y1 += iy;
                d += dx2;
                if (d > dy) {
                    x1 += ix;
                    d -= dy2;
                }
            }
            glEnd();
        }
    }
}
