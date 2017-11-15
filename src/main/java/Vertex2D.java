import java.util.ArrayList;

// Estrutura contendo vertice do poligono a ser desenhado
public class Vertex2D implements Vertex {
    public double x;
    public double y;

    public Vertex2D() {super();}

    public Vertex2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Vertex3D v) {
        if (v == null) { return false; }
        return v.x == this.x && v.y == this.y;
    }

    public double[] toArray() {
        return new double[]{this.x, this.y};
    }

    public static void setList(ArrayList<Vertex2D> v1, ArrayList<Vertex2D> v2) {
        v1.clear();
        for (Vertex2D item : v2) {
            v1.add(item);
        }
    }
}
