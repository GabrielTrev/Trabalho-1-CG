// Estrutura contendo vertice do poligono a ser desenhado
public class Vertex {
    public double x;
    public double y;

    public boolean equals(Vertex v) {
        if (v == null) { return false; }
        return v.x == this.x && v.y == this.y;
    }

    public double[] toArray() {
        return new double[]{this.x, this.y};
    }
}
