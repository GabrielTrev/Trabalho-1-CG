import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;

import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

// Classe contendo procedimentos para transformacoes de coordenadas em 2D e em 3D
public class AffineTransform {

    // Constantes para translacao
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    // Constantes para escalonamento
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    // Constantes para rotacao
    public static final int CLOCKWISE = 0;
    public static final int COUNTER_CLOCKWISE = 1;

    // Procedimento auxiliar para a realizacao da transformacao 2D
    // Parametros de entrada:
    //      coordList: lista de coordenadas a serem transformadas
    //      transformArray: matriz de transformacao 2D, em coordenadas homogeneas, a ser aplicada
    private static ArrayList<Vertex2D> affineTransform2D(ArrayList<Vertex2D> coordList, double[][] transformArray) {

        // Escrita da lista de coordenadas em um array
        double[][] coordArray = new double[3][coordList.size()];
        for (int i = 0; i < coordList.size(); i++) {
            coordArray[0][i] = coordList.get(i).x;
            coordArray[1][i] = coordList.get(i).y;
            coordArray[2][i] = 1;
        }

        // Escrita do array de coordenadas e de transformacao em RealMatrix
        RealMatrix coordMatrix = MatrixUtils.createRealMatrix(coordArray);
        RealMatrix transformMatrix = MatrixUtils.createRealMatrix(transformArray);

        // Multiplicacao da matriz de transformacao ao conjunto de pontos
        RealMatrix resultMatrix = transformMatrix.multiply(coordMatrix);

        ArrayList<Vertex2D> resultList = new ArrayList<>();
        // Escrita dos pontos encontrados na lista de coordenadas
        for (int i = 0; i < coordList.size(); i++) {
            Vertex2D v = new Vertex2D();
            v.x = resultMatrix.getEntry(0,i);
            v.y = resultMatrix.getEntry(1, i);
            resultList.add(v);
        }

        return resultList;

    }

    // Operacao de translacao do poligono
    // Parametros de entrada:
    //      verticesList: lista de vertices do poligono
    //      direction: direcao de translacao
    //          0: para cima
    //          1: para baixo
    //          2: para a esquerda
    //          3: para a direita
    //      value: numero de pixels que devem ser transladados
    public static ArrayList<Vertex2D> translate2D(ArrayList<Vertex2D> verticesList, int direction, double value) {

        ArrayList<Vertex2D> resultList = new ArrayList<>(verticesList);

        // Variaveis que armazenam o deslocamento
        double dx = 0;
        double dy = 0;

        // Definicao dos valores de deslocamento a partir da direcao escolhida
        // Valores pares -> sentido negativo
        if (direction == UP || direction == LEFT) { value = -value; }
        // Valores 0 e 1 -> eixo y
        dy = (direction == UP || direction == DOWN) ? value : 0;
        // Valores 2 e 3 -> eixo x
        dx = (direction == LEFT || direction == RIGHT) ? value : 0;

        /* Matriz de translacao
        *      | 1 0 dx |
        *  M = | 0 1 dy |
        *      | 0 0 1  |  */
        double[][] translationMatrix = {{1, 0, dx},
                {0, 1, dy},
                {0, 0, 1 }};

        // Aplicacao da matriz de translacao a lista de vertices
        resultList = affineTransform2D(resultList, translationMatrix);

        return resultList;
    }

    // Operacao de escalonamento do poligono
    // Parametros de entrada:
    //      verticesList: lista de vertices do poligono
    //      direction: direcao do escalonamento
    //          0: escalonamento em x
    //          1: escalonamento em y
    //      value: numero de pixels do escalonamento
    public static ArrayList<Vertex2D> scale2D(ArrayList<Vertex2D> verticesList, int direction, double value) {

        ArrayList<Vertex2D> resultList = new ArrayList<>(verticesList);

        // Variaveis que armazenam a escala
        double sx;
        double sy;

        // Determinacao do eixo de aplicacao da escala
        sx = (direction == HORIZONTAL) ? value : 1;
        sy = (direction == VERTICAL) ? value : 1;

        // Posicao do primeiro ponto do poligono
        double dx = verticesList.get(0).x;
        double dy = verticesList.get(0).y;

        // Translacao do primeiro ponto do polígono a origem
        resultList = translate2D(resultList, 0, dy);
        resultList = translate2D(resultList, 2, dx);

        /* Matriz de escalonamento:
         *     | sx 0  0 |
         * M = | 0  sy 0 |
         *     | 0  0  1 |  */
        double[][] scalingMatrix = new double[][]{{sx, 0 , 0},
                {0,  sy, 0,},
                {0 , 0 , 1}};

        // Aplicacao da matriz de translacao a lista de vertices
        resultList = affineTransform2D(resultList, scalingMatrix);

        // Translacao do primeiro ponto do poligono a sua posicao original
        resultList = translate2D(resultList, 1, dy);
        resultList = translate2D(resultList, 3, dx);

        return resultList;
    }

    // Procedimento para a rotacao da figura desenhada em relacao ao ponto (0,0)
    // Parametros de entrada:
    //      originalList: lista de vertices do poligono
    //      direction: sentido de roatacao (0 para anti-horario e 1 para horario)
    //      angle: angulo de rotacao
    public static ArrayList<Vertex2D> rotate2D(ArrayList<Vertex2D> verticesList, int direction, double angle) {

        ArrayList<Vertex2D> resultList = new ArrayList<>(verticesList);

        // Verificacao de sentido anti-horario
        if (direction == COUNTER_CLOCKWISE) { angle = -angle; }

        // Posicao do primeiro ponto do poligono
        double dx = verticesList.get(0).x;
        double dy = verticesList.get(0).y;

        // Translacao do primeiro ponto do polígono a origem
        resultList = translate2D(resultList, 0, dy);
        resultList = translate2D(resultList, 2, dx);

        /* Matriz de rotacao:
         *     | cos(a) -sen(a) 0 |
         * M = | sen(a)  cos(a) 0 |
         *     |   0       0    1 |  */
        double[][] rotationMatrix = {{cos(angle), -sin(angle), 0},
                {sin(angle),  cos(angle), 0},
                {    0     ,      0     , 1}};

        // Aplicacao da matriz de rotacao a lista de vertices
        resultList = affineTransform2D(resultList, rotationMatrix);

        // Translacao do primeiro ponto do poligono a sua posicao original
        resultList = translate2D(resultList, 1, dy);
        resultList = translate2D(resultList, 3, dx);

        return resultList;
    }

    // Operacoes em 3D
    private static ArrayList<ArrayList<Vertex3D>> affineTransform3D(ArrayList<ArrayList<Vertex3D>> faceList, double[][] transformArray) {

        ArrayList<ArrayList<Vertex3D>> resultList = new ArrayList<>(faceList);

        for (int i = 0; i < faceList.size(); i++) {
            ArrayList<Vertex3D> face = new ArrayList<>(faceList.get(i));
            ArrayList<Vertex3D> result = new ArrayList<>();

            // Escrita da lista de coordenadas em um array
            double[][] coordArray = new double[4][face.size()];
            for (int j = 0; j < face.size(); j++) {
                coordArray[0][j] = face.get(j).x;
                coordArray[1][j] = face.get(j).y;
                coordArray[2][j] = face.get(j).z;
                coordArray[3][j] = 1;
            }

            // Escrita do array de coordenadas de transformacao em RealMatrix
            RealMatrix coordMatrix = MatrixUtils.createRealMatrix(coordArray);
            RealMatrix transformMatrix = MatrixUtils.createRealMatrix(transformArray);

            // Multiplicacao da matriz de transformacao ao conjunto de pontos
            RealMatrix resultMatrix = transformMatrix.multiply(coordMatrix);

            // Escrita dos pontos encontrados na lista de coordenadas
            for (int j = 0; j < face.size(); j++) {
                Vertex3D v = new Vertex3D();
                v.x = resultMatrix.getEntry(0, j);
                v.y = resultMatrix.getEntry(1, j);
                v.z = resultMatrix.getEntry(2, j);
                result.add(v);
            }

            resultList.set(i, result);
        }

        return resultList;

    }

    // Operacao de translacao do poligono nos eixos x e y
    // Parametros de entrada:
    //      verticesList: lista de vertices do poligono
    //      direction: direcao de translacao
    //          0: para cima
    //          1: para baixo
    //          2: para a esquerda
    //          3: para a direita
    //      value: numero de pixels que devem ser transladados
    public static ArrayList<ArrayList<Vertex3D>> translate3D(ArrayList<ArrayList<Vertex3D>> verticesList, int direction, double value) {

        ArrayList<ArrayList<Vertex3D>> resultList;

        // Variaveis que armazenam o deslocamento
        double dx = 0;
        double dy = 0;

        // Definicao dos valores de deslocamento a partir da direcao escolhida
        // Valores pares -> sentido negativo
        if (direction == UP || direction == LEFT) { value = -value; }
        // Valores 0 e 1 -> eixo y
        dy = (direction == UP || direction == DOWN) ? value : 0;
        // Valores 2 e 3 -> eixo x
        dx = (direction == LEFT || direction == RIGHT) ? value : 0;

        /* Matriz de translacao
        *      | 1 0 0 dx |
        *  M = | 0 1 0 dy |
        *      | 0 0 1 0  |
        *      | 0 0 0 1  |  */
        double[][] translationMatrix = {{1, 0, 0, dx},
                                        {0, 1, 0, dy},
                                        {0, 0, 1, 0 },
                                        {0, 0, 0, 1 }};

        // Aplicacao da matriz de translacao a lista de vertices
        resultList = affineTransform3D(verticesList, translationMatrix);

        return resultList;
    }

    // Operacao de escalonamento do poligono
    // Parametros de entrada:
    //      verticesList: lista de vertices do poligono
    //      direction: direcao do escalonamento
    //          0: escalonamento em x
    //          1: escalonamento em y
    //      value: numero de pixels do escalonamento
    public static ArrayList<ArrayList<Vertex3D>> scale3D(ArrayList<ArrayList<Vertex3D>> verticesList, int direction, double value) {

        ArrayList<ArrayList<Vertex3D>> resultList = new ArrayList<>(verticesList);

        // Variaveis que armazenam a escala
        double sx;
        double sy;

        // Determinacao do eixo de aplicacao da escala
        sx = (direction == HORIZONTAL) ? value : 1;
        sy = (direction == VERTICAL) ? value : 1;

        // Posicao do primeiro ponto do poligono
        double dx = verticesList.get(0).get(0).x;
        double dy = verticesList.get(0).get(0).y;

        // Translacao do primeiro ponto do polígono a origem
        resultList = translate3D(resultList, 0, dy);
        resultList = translate3D(resultList, 2, dx);

        /* Matriz de escalonamento:
         *     | sx 0  0 0 |
         * M = | 0  sy 0 0 |
         *     | 0  0  1 0 |
         *     | 0  0  0 1 |  */
        double[][] scalingMatrix = new double[][]{{sx, 0,  0, 0},
                                                  {0,  sy, 0, 0},
                                                  {0,  0,  1, 0},
                                                  {0 , 0 , 0, 1}};

        // Aplicacao da matriz de translacao a lista de vertices
        resultList = affineTransform3D(resultList, scalingMatrix);

        // Translacao do primeiro ponto do poligono a sua posicao original
        resultList = translate3D(resultList, 1, dy);
        resultList = translate3D(resultList, 3, dx);

        return resultList;
    }

    // Procedimento para a rotacao da figura desenhada em relacao ao ponto (0,0)
    // Parametros de entrada:
    //      originalList: lista de vertices do poligono
    //      direction: sentido de roatacao (0 para anti-horario e 1 para horario)
    //      angle: angulo de rotacao
    public static ArrayList<ArrayList<Vertex3D>> rotate3D(ArrayList<ArrayList<Vertex3D>> verticesList, int direction, double angle) {

        ArrayList<ArrayList<Vertex3D>> resultList = new ArrayList<>(verticesList);

        // Verificacao de sentido anti-horario
        if (direction == COUNTER_CLOCKWISE) { angle = -angle; }

        // Posicao do primeiro ponto do poligono
        double dx = verticesList.get(0).get(0).x;
        double dy = verticesList.get(0).get(0).y;

        // Translacao do primeiro ponto do polígono a origem
        resultList = translate3D(resultList, 0, dy);
        resultList = translate3D(resultList, 2, dx);

        /* Matriz de rotacao:
         *     | cos(a) -sen(a) 0 0 |
         * M = | sen(a)  cos(a) 0 0 |
         *     |   0       0    1 0 |
         *     |   0       0    0 1 |  */
        double[][] rotationMatrix = {{cos(angle), -sin(angle), 0, 0},
                                     {sin(angle),  cos(angle), 0, 0},
                                     {    0     ,      0     , 1, 0},
                                     {    0     ,      0     , 0, 1}};

        // Aplicacao da matriz de rotacao a lista de vertices
        resultList = affineTransform3D(resultList, rotationMatrix);

        // Translacao do primeiro ponto do poligono a sua posicao original
        resultList = translate3D(resultList, 1, dy);
        resultList = translate3D(resultList, 3, dx);

        return resultList;
    }

}
