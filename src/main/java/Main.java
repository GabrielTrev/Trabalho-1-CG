/*
    Algoritmo do Ponto Médio (Bresenham), feito por
        Felipe Khoji Myose (611026)
        Gabriel Silva Trevisan (554812)
        Vinicius Ito Nagura (558478)
 */

import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengles.GLES20.GL_DEPTH_BUFFER_BIT;

// Class structure based on the LWJGL 3 Guide, presented in
// https://www.lwjgl.org/guide
public class Main {

    // Lista de vertices do poligono a ser desenhado
    private ArrayList<Vertex2D> verticesList = new ArrayList<>();

    // The window handle
    private long window;
    // Dimensions of Window
    private static int width = 800;
    private static int height = 600;

    public static Vertex3D verticeA = new Vertex3D(width/4, width/4, 100);
    public static Vertex3D verticeB = new Vertex3D(3*width/4, width/4, 100);
    public static Vertex3D verticeC = new Vertex3D(width/4, 3*width/4, 100);
    public static Vertex3D verticeD = new Vertex3D(3*width/4, 3*width/4, 100);
    public static Vertex3D verticeE = new Vertex3D(width/4, width/4, 200);
    public static Vertex3D verticeF = new Vertex3D(3*width/4, width/4, 200);
    public static Vertex3D verticeG = new Vertex3D(width/4, 3*width/4, 200);
    public static Vertex3D verticeH = new Vertex3D(3*width/4, 3*width/4, 200);

    public static ArrayList<Vertex3D> face1 = new ArrayList<>(Arrays.asList(verticeA, verticeB, verticeD, verticeC));
    public static ArrayList<Vertex3D> face2 = new ArrayList<>(Arrays.asList(verticeE, verticeF, verticeH, verticeG));
    public static ArrayList<Vertex3D> face3 = new ArrayList<>(Arrays.asList(verticeC, verticeD, verticeH, verticeG));
    public static ArrayList<Vertex3D> face4 = new ArrayList<>(Arrays.asList(verticeE, verticeF, verticeB, verticeA));
    public static ArrayList<Vertex3D> face5 = new ArrayList<>(Arrays.asList(verticeA, verticeC, verticeG, verticeE));
    public static ArrayList<Vertex3D> face6 = new ArrayList<>(Arrays.asList(verticeB, verticeF, verticeH, verticeD));

    public static ArrayList<ArrayList<Vertex3D>> cube = new ArrayList<>(Arrays.asList(face1, face2, face3, face4, face5, face6));

    public static boolean draw3D = false;

    public static Vertex3D center = new Vertex3D(width/2, height/2, -100);
    ArrayList<ArrayList<Float>> colors = new ArrayList<>();

    public void run() {

        System.out.println("Polygon Drawing Algorithm by Vinicius Ito, Felipe Myose and Gabriel Trevisan");

        init();

        for (int i = 0; i < 100; i++) {
            float r = (float) Math.random() * 0.375f + 0.5f;
            float g = (float) Math.random() * 0.125f;
            float b = (float) Math.random() * 0.125f;

            colors.add(new ArrayList<Float>(Arrays.asList(r,g,b)));
        }

        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    private void init() {

        // Inicializacao da janela
        window = Inicializacao.inicializarJanela(window, width, height);

        // Definicao da interface de mouse e teclado
        Inicializacao.definirAcoesTeclado(this.verticesList, this.center, this.window);
        Inicializacao.definirAcoesMouse((ArrayList<Vertex2D>) this.verticesList,  this.window);

        // Make the window visible
        glfwShowWindow(window);

    }

    private void draw(Main object) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        if (object.verticesList.size() > 1) {
            for (int i = 0; i < object.verticesList.size() - 1; i++) {
                LineDraw.bresenham(object.verticesList.get(i), object.verticesList.get(i + 1),
                                   1.0f, 0.0f, 0.0f);
            }
        }

        Polygon.scanLine(verticesList, this.width, this.height, 1.0f, 0.0f, 0.0f);

        glfwSwapBuffers(window); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }

    private void draw3D(Main object) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        Prism.drawPrism(cube, center, width, height, colors);

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
            if (draw3D)
                draw3D(this);
            else { draw(this); }
        }

    }

    public static void main(String[] args) {
        new Main().run();
    }

}
