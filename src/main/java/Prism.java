import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Prism {
    public static ArrayList<Vertex2D> to2D(ArrayList<Vertex3D> vertices3D, Vertex3D center) {
        if (vertices3D == null || vertices3D.size() == 0) { return new ArrayList<>(); }
        ArrayList<Vertex2D> polygon = new ArrayList<>();
        for (Vertex3D vertex3D : vertices3D) {
            polygon.add(vertex3D.to2D(center));
        }
        Polygon.closePolygon(polygon);

        return polygon;
    }

    public static ArrayList<Vertex3D> flatten(ArrayList<ArrayList<Vertex3D>> faces) {
        if (faces == null || faces.size() == 0) { return new ArrayList<>(); }
        ArrayList<Vertex3D> vertices = new ArrayList<>();
        for (ArrayList<Vertex3D> face : faces) {
            for (Vertex3D v : face) {
                vertices.add(v);
            }
        }

        return vertices;
    }

    public static void setPrism(ArrayList<ArrayList<Vertex3D>> prism1, ArrayList<ArrayList<Vertex3D>> prism2) {
        prism1.clear();
        for (ArrayList<Vertex3D> face : prism2) {
            prism1.add(face);
        }
    }

    // Calcula a distancia media de uma face em relacao ao ponto de projecao no eixo z
    private static double meanDistance(ArrayList<Vertex3D> vertices, Vertex3D center) {
        double sum = 0;
        if (vertices == null || vertices.size() == 0) { return 0; }
        for (Vertex3D v : vertices) {
            sum += v.computeDistance(center);
            //sum+= Math.abs(v.z - center.z);
        }
        return sum / vertices.size();
    }

    public static ArrayList<Map<ArrayList<Vertex2D>, ArrayList<Float>>> OrderedFaces(ArrayList<ArrayList<Vertex3D>> faces3D, Vertex3D center) {
        ArrayList<ArrayList<Vertex3D>> copy = new ArrayList<>(faces3D);

        ArrayList<Map<ArrayList<Vertex2D>, ArrayList<Float>>> ordered = new ArrayList<>();


        while (ordered.size() < faces3D.size()) {
            double meanZ = meanDistance(copy.get(0), center);
            int index = 0;
            for (int i = 0; i < copy.size(); i++) {
                double aux = meanDistance(copy.get(i), center);
                if (aux > meanZ) {
                    meanZ = aux;
                    index = i;
                }
            }
            Map<ArrayList<Vertex2D>, ArrayList<Float>> input = new HashMap<>();
            input.put(to2D(copy.get(index), center), Main.colors.get(faces3D.indexOf(copy.get(index))));
            ordered.add(input);
            copy.remove(index);
        }

        return ordered;
    }

    public static void drawPrism(ArrayList<ArrayList<Vertex3D>> faces3D, Vertex3D center, int width, int height,
                                 ArrayList<ArrayList<Float>> colors) {
        ArrayList<Map<ArrayList<Vertex2D>, ArrayList<Float>>> faces2D = OrderedFaces(faces3D, center);

        for (int i = 0; i < faces2D.size(); i++) {
            ArrayList<Vertex2D> face = faces2D.get(i).entrySet().iterator().next().getKey();
            ArrayList<Float> color = faces2D.get(i).entrySet().iterator().next().getValue();

            for (int j = 0; j < face.size() - 1; j++) {
                LineDraw.bresenham(face.get(j), face.get(j + 1), color.get(0), color.get(1), color.get(2));
            }

            Polygon.scanLine(face, width, height, color.get(0), color.get(1), color.get(2));
        }
    }
}
