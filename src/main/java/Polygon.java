import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2i;

// Estrutura basica de um no da ET
// Contem o y_max, o x do y_min e o inverso do coeficiente angular da reta
class ETNode{
    private Double maxY;
    private Double xMinY;
    private Double inverseSlope;

    public ETNode() {}

    // Inicializacao do ETNode, dado um par de vertices
    public ETNode(Vertex2D v1, Vertex2D v2) {
        this.maxY = Math.max(v1.y, v2.y);
        this.xMinY = (v1.y < v2.y) ? v1.x : v2.x;
        this.inverseSlope = (v2.x - v1.x) / (v2.y - v1.y);
    }

    // Inicializacao do ETNode, de acordo com os valores de seus atributos
    public ETNode(Double maxY, Double xMinY, Double inverseSlope) {
        this.maxY = maxY;
        this.xMinY = xMinY;
        this.inverseSlope = inverseSlope;
    }

    public void update() {
        this.xMinY += this.inverseSlope;
    }
    public Double getMaxY () { return this.maxY; }
    public Double getxMinY () { return this.xMinY; }
    public Double getInverseSlope () { return this.inverseSlope; }
}

// Comparator utilizado para o ETNode para ordenacao dos nos na tabela
class ETNodeComparator implements Comparator<ETNode> {
    @Override
    public int compare(ETNode node1, ETNode node2) {
        return node1.getxMinY().compareTo(node2.getxMinY());
    }
}

public class Polygon {
    public static boolean closedPolygon(ArrayList<Vertex2D> vertices) {
        if (vertices == null || vertices.size() <= 1) { return false; }
        return vertices.get(vertices.size() - 1).equals(vertices.get(0));
    }

    public static void closePolygon(ArrayList<Vertex2D> vertices) {
        if (vertices == null || vertices.size() == 0) { return; }
        vertices.add(vertices.get(0));
    }

    public static boolean withinBounds(ArrayList<Vertex2D> vertices, int width, int height) {
        for (Vertex2D v : vertices) {
            if (v.x < 0 || v.y < 0 || v.x > width || v.y > height) { return false; }
        }
        return true;
    }

    public static void setPolygon(ArrayList<Vertex2D> src, ArrayList<Vertex2D> dst) {
        if (src.size() != dst.size()) { return; }

        for (int i = 0; i < src.size(); i++){
            src.get(i).x = dst.get(i).x;
            src.get(i).y = dst.get(i).y;
        }
    }

    // Implementacao do algoritmo de preenchimento de poligonos, dado o conjunto de vertices
    public static void scanLine(ArrayList<Vertex2D> vertices, int width, int height, float r, float g, float b) {

        if (!closedPolygon(vertices)) { return; }

        // Inicializacao da ET
        ArrayList<ArrayList<ETNode>> ET = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            ArrayList<ETNode> nodeList = new ArrayList<>();
            ET.add(nodeList);
        }

        // Preenchimento da ET
        for (int i = 0; i < vertices.size() - 1; i++) {
            // No criado de acordo com o proximo par de vertices
            ETNode node = new ETNode(vertices.get(i), vertices.get(i + 1));
            // Indice do no na tabela corresponde ao valor de y_min
            double indice = Math.min(vertices.get(i).y, vertices.get(i + 1).y);
            ET.get((int) indice).add(node);
        }

        // Inicializacao da AET
        ArrayList<ArrayList<ETNode>> AET = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            ArrayList<ETNode> nodeList = new ArrayList<>();
            AET.add(nodeList);
        }

        // Preenchimento da AET
        // Determinacao do primeiro indice nao vazio da ET
        int indice = 0;
        for (int i = 0; i < height; i++) {
            if (!ET.get(i).isEmpty()) {
                indice = i;
                break;
            }
        }

        // Atualizacao dos valores das linhas da AET e preenchimento das linhas
        for (int i = indice; i < height - 1; i++) {
            // Insercao dos nos presentes na linha da ET da mesma linha da AET
            for (ETNode node : ET.get(i)) {
                AET.get(i).add(node);
            }

            // Atualizacao dos valores do nivel anterior da AET
            for (ETNode node : AET.get(i)) {
                ETNode nodeAux = new ETNode(node.getMaxY(), node.getxMinY(), node.getInverseSlope());
                nodeAux.update();
                if (i + 1 < nodeAux.getMaxY().intValue()) { AET.get(i + 1).add(nodeAux); }
            }
        }

        // Ordenacao dos nos da linha da AET
        for (int i = 0; i < height; i++) {
            Collections.sort(AET.get(i), new ETNodeComparator());

            // Desenhos dos pontos, de acordo com os pares de valores de x do y_min
            for (int j = 0; j < AET.get(i).size() - 1; j += 2) {
                for (int k = (int) (double) AET.get(i).get(j).getxMinY(); k < (int) (double) AET.get(i).get(j + 1).getxMinY(); k++) {
                    glColor4f(r, g, b, 0.0f);
                    glPointSize(1.0f);
                    glBegin(GL_POINTS);
                    glVertex2i(k, i);
                    glEnd();
                }
            }
        }
    }
}
