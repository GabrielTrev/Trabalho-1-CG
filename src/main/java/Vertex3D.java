import java.util.ArrayList;

// Estrutura contendo vertice em 3D para projecao
public class Vertex3D implements Vertex {
    public double x;
    public double y;
    public double z;

    public Vertex3D() { super(); }

    public Vertex3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Vertex3D v) {
        if (v == null) { return false; }
        return v.x == this.x && v.y == this.y && v.z == this.z;
    }

    public double[] toArray() {
        return new double[]{this.x, this.y, this.z};
    }

    // Dados dois pontos, calcula a distancia entre eles
    public double computeDistance(Vertex3D v) {
        return Math.sqrt(Math.pow(this.x - v.x, 2) + Math.pow(this.y - v.y, 2) + Math.pow(this.z - v.z, 2));
    }

    // Projecao do vertice 3D em 2D
    // Parametros de entrada:
    //      center: ponto de projeção
    public Vertex2D to2D(Vertex3D center) {
        //double distCenter = this.computeDistance(center);
        double distCenter = Math.abs(this.z - center.z);
        double xAux = this.x / (this.z / distCenter + 1);
        double yAux = this.y / (this.z / distCenter + 1);

        // Ponto projetado no plano de projecao
        Vertex2D proj = new Vertex2D(xAux, yAux);
        return proj;
    }

    public static void setList(ArrayList<Vertex3D> v1, ArrayList<Vertex3D> v2) {
        v1.clear();
        for (Vertex3D item : v2) {
            v1.add(item);
        }
    }

}
