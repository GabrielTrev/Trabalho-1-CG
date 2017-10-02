/*
    Algoritmo do Ponto Médio (Bresenham), feito por
        Felipe Khoji Myose (611026)
        Gabriel Silva Trevisan (554812)
        Vinicius Ito Nagura (558478)
 */

import com.sun.javafx.geom.Matrix3f;
import javafx.scene.transform.MatrixType;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MathUtil;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengles.GLES20.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


class Vertex {
    public double x;
    public double y;
}

class ETNode{
    private Double maxY;
    private Double xMinY;
    private Double inverseSlope;

    public ETNode() {}

    public ETNode(Vertex v1, Vertex v2) {
        this.maxY = Math.max(v1.y, v2.y);
        this.xMinY = (v1.y < v2.y) ? v1.x : v2.x;
        this.inverseSlope = (v2.x - v1.x) / (v2.y - v1.y);
    }

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

class ETNodeComparator implements Comparator<ETNode> {
    @Override
    public int compare(ETNode node1, ETNode node2) {
        return node1.getxMinY().compareTo(node2.getxMinY());
    }
}

// Class structure based on the LWJGL 3 Guide, presented in
// https://www.lwjgl.org/guide
public class Main {

    private ArrayList<Vertex> verticesList = new ArrayList<>();
    private boolean closedPolygon = false;

    // The window handle
    private long window;
    // Dimensions of Window
    private static int width = 800;
    private static int height = 600;

    public void scanLine(ArrayList<Vertex> vertices) {
        if (!closedPolygon) { return; }

        // Inicializacao da ET
        ArrayList<ArrayList<ETNode>> ET = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            ArrayList<ETNode> nodeList = new ArrayList<>();
            ET.add(nodeList);
        }

        // Preenchimento da ET
        for (int i = 0; i < verticesList.size() - 1; i++) {
            ETNode node = new ETNode(verticesList.get(i), verticesList.get(i + 1));
            double indice = Math.min(verticesList.get(i).y, verticesList.get(i + 1).y);
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

        for (int i = indice; i < height - 1; i++) {
            for (ETNode node : ET.get(i)) {
                AET.get(i).add(node);
            }

            for (ETNode node : AET.get(i)) {
                ETNode nodeAux = new ETNode(node.getMaxY(), node.getxMinY(), node.getInverseSlope());
                nodeAux.update();
                if (i + 1 < nodeAux.getMaxY()) { AET.get(i + 1).add(nodeAux); }
            }
        }

        for (int i = 0; i < height; i++) {
            Collections.sort(AET.get(i), new ETNodeComparator());

            for (int j = 0; j < AET.get(i).size() - 1; j += 2) {
                for (int k = (int) (double) AET.get(i).get(j).getxMinY(); k < (int) (double) AET.get(i).get(j + 1).getxMinY(); k++) {
                    glColor4f(1.0f, 0.0f, 0.0f, 0.0f);
                    glPointSize(1.0f);
                    glBegin(GL_POINTS);
                    glVertex2i(k, i);
                    glEnd();
                }
            }
        }
    }

    public void run() {

        System.out.println("Bresenham's Line Algorithm by Vinicius Ito and Felipe Myose");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    public void translation(ArrayList<Vertex> list, int direction, double value) {

        switch (direction) {
            // Up
            case 0:
                for(int i = 0; i < list.size(); i++) {
                    Vertex aux = new Vertex();
                    aux.x = list.get(i).x;
                    aux.y = list.get(i).y - value;
                    list.set(i, aux);
                }
                break;

            // Down
            case 1:
                for(int i = 0; i < list.size(); i++) {
                    Vertex aux = new Vertex();
                    aux.x = list.get(i).x;
                    aux.y = list.get(i).y + value;
                    list.set(i, aux);
                }
                break;

            // Left
            case 2:
                for(int i = 0; i < list.size(); i++) {
                    Vertex aux = new Vertex();
                    aux.x = list.get(i).x - value;
                    aux.y = list.get(i).y;
                    list.set(i, aux);
                }
                break;

            // Right
            case 3:
                for(int i = 0; i < list.size(); i++) {
                    Vertex aux = new Vertex();
                    aux.x = list.get(i).x + value;
                    aux.y = list.get(i).y;
                    list.set(i, aux);
                }
                break;

            default:
                break;
        }
    }

    public void scale(ArrayList<Vertex> originalList, int direction, double value) {

        ArrayList<Vertex> auxList = new ArrayList<>();
        auxList.addAll(originalList);

        translation(auxList, 0, auxList.get(0).y);
        translation(auxList, 2, auxList.get(0).x);

        switch(direction) {

            case 0:
                for(int i = 0; i < auxList.size(); i++) {

                    double[][] p = { {auxList.get(i).x}, {auxList.get(i).y} };
                    double[][] s = { {value, 0}, {0, 1} };

                    RealMatrix matrixP = MatrixUtils.createRealMatrix(p);
                    RealMatrix matrixS = MatrixUtils.createRealMatrix(s);
                    RealMatrix matrixPLine = matrixS.multiply(matrixP);

                    Vertex aux = new Vertex();
                    aux.x = matrixPLine.getEntry(0,0);
                    aux.y = matrixPLine.getEntry(1,0);
                    auxList.set(i, aux);
                }
                translation(auxList, 1, originalList.get(0).y);
                translation(auxList, 3, originalList.get(0).x * value);
                break;

            case 1:
                for(int i = 0; i < auxList.size(); i++) {
                    double[][] p = { {auxList.get(i).x}, {auxList.get(i).y} };
                    double[][] s = { {1, 0}, {0, value} };

                    RealMatrix matrixP = MatrixUtils.createRealMatrix(p);
                    RealMatrix matrixS = MatrixUtils.createRealMatrix(s);
                    RealMatrix matrixPLine = matrixS.multiply(matrixP);

                    Vertex aux = new Vertex();
                    aux.x = matrixPLine.getEntry(0,0);
                    aux.y = matrixPLine.getEntry(1,0);
                    auxList.set(i, aux);
                }
                translation(auxList, 1, originalList.get(0).y * value);
                translation(auxList, 3, originalList.get(0).x);
                break;

            default:
                break;
        }

        this.verticesList = auxList;

    }

    //procedimento para a rotacao da figura desenhada em relacao ao ponto (0,0)
    public void rotate(ArrayList<Vertex> originalList, int direction, double angle) {

        ArrayList<Vertex> auxList = new ArrayList<>();
        auxList.addAll(originalList);

        double x, y;

        switch(direction) {
            case 0: //anti-horario(R)
                System.out.println("rotacao anti-horario");
                for(int i = 0; i < auxList.size(); i++) {
                    x = auxList.get(i).x;
                    y = auxList.get(i).y;

                    double[][] p = { {x}, {y} };
                    double[][] r = { {cos(angle), -sin(angle)},
                                     {sin(angle),  cos(angle)} };
                    //p' = r*p
                    RealMatrix matrixP = MatrixUtils.createRealMatrix(p);
                    RealMatrix matrixR = MatrixUtils.createRealMatrix(r);
                    RealMatrix matrixPLine = matrixR.multiply((matrixP));

                    Vertex aux = new Vertex();
                    aux.x = matrixPLine.getEntry(0, 0); //row,column
                    aux.y = matrixPLine.getEntry(1, 0);
                    auxList.set(i, aux); //possui as coordenadas transformadas
                }
                break;

            case 1: //horario(T)
                System.out.println("rotacao horario");
                for(int i = 0; i < auxList.size(); i++) {
                    x = auxList.get(i).x;
                    y = auxList.get(i).y;

                    double[][] p = { {x}, {y} };
                    double[][] r = { {cos(angle), -sin(angle)},
                            {sin(angle),  cos(angle)} };
                    //p' = r*p
                    RealMatrix matrixP = MatrixUtils.createRealMatrix(p);
                    RealMatrix matrixR = MatrixUtils.createRealMatrix(r);
                    RealMatrix matrixPLine = matrixR.multiply((matrixP));

                    Vertex aux = new Vertex();
                    aux.x = matrixPLine.getEntry(0, 0); //row,column
                    aux.y = matrixPLine.getEntry(1, 0);
                    auxList.set(i, aux); //possui as coordenadas transformadas
                }
                break;

            default:
                break;
        }
        //atualizacao da lista de Vertex
        originalList.clear();
        originalList.addAll(auxList);

    }

    private void init() {

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Instructions for user
        System.out.println("\nPress, drag and then release left mouse button to draw a line");

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, height, "Bresenham's Line Algorithm - 558478 and 611026", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {

            if(action == GLFW_RELEASE) {
                switch (key) {

                    case GLFW_KEY_ESCAPE:
                        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                        break;

                    case GLFW_KEY_UP:
                        translation(this.verticesList, 0, 1.0);
                        break;

                    case GLFW_KEY_DOWN:
                        translation(this.verticesList, 1, 1.0);
                        break;

                    case GLFW_KEY_LEFT:
                        translation(this.verticesList, 2, 1.0);
                        break;

                    case GLFW_KEY_RIGHT:
                        translation(this.verticesList, 3, 1.0);
                  Vertex aux = new Vertex();
              break;

                    case GLFW_KEY_LEFT_BRACKET:
                        scale(this.verticesList, 0, 1.1);
                        break;

                    case GLFW_KEY_RIGHT_BRACKET:
                        scale(this.verticesList, 1, 1.1);
                        break;
                    case GLFW_KEY_R:
                        rotate(this.verticesList, 0, 0.02);
                        break;
                    case GLFW_KEY_T:
                        rotate(this.verticesList, 1, -0.02);
                        break;
                }
            }

        });

        // Setup a mouse callback. It will be called every time the left mouse button is pressed.
        // This is used to get the mouse location for drawing the line!
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            DoubleBuffer b1 = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer b2 = BufferUtils.createDoubleBuffer(1);
            // Button is pressed, so this is the start point of the line
            if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                // mousePressed = true;
                glfwGetCursorPos(window, b1, b2);

                Vertex aux = new Vertex();
                aux.x = b1.get(0);
                aux.y = b2.get(0);

                if (closedPolygon) {
                    verticesList.clear();
                    verticesList.add(aux);
                    closedPolygon = false;
                }
                else { verticesList.add(aux); }

            }
            // Button is released, so this is the end point of the line
            else if(button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS) {
                // mousePressed = false;
                glfwGetCursorPos(window, b1, b2);

                if (!closedPolygon) {
                    if (!verticesList.isEmpty()) {
                        verticesList.add(verticesList.get(0));
                        closedPolygon = true;
                    }
                }
            }
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

    }

    // drawLine method implemented using Bresenham's line algorithm, which is based on the solution proposed in
    // https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java
    private void drawLine(Vertex v1, Vertex v2) {
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

    private void draw(Main object) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        if (object.verticesList.size() > 1) {
            for (int i = 0; i < object.verticesList.size() - 1; i++) {
                drawLine(object.verticesList.get(i), object.verticesList.get(i + 1));
            }
        }

        this.scanLine(object.verticesList);

        glfwSwapBuffers(window); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }

    private void loop() {

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        glOrtho(0, width, height, 0, 0, 1);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            draw(this);
        }

    }

    public static void main(String[] args) {
        new Main().run();
    }

}
