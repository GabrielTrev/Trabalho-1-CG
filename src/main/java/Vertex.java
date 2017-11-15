import java.util.ArrayList;

public interface Vertex {
    double[] toArray();

    static void setList(ArrayList<? extends Vertex> v1, ArrayList<? extends Vertex> v2) {}
}
