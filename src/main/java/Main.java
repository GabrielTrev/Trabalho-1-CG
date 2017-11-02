/*
    Algoritmo do Ponto Médio (Bresenham), feito por
        Felipe Khoji Myose (611026)
        Gabriel Silva Trevisan (554812)
        Vinicius Ito Nagura (558478)
 */

import org.lwjgl.opengl.GL;

import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengles.GLES20.GL_DEPTH_BUFFER_BIT;

// Class structure based on the LWJGL 3 Guide, presented in
// https://www.lwjgl.org/guide
public class Main {

    // Lista de vertices do poligono a ser desenhado
    private ArrayList<Vertex> verticesList = new ArrayList<>();

    // The window handle
    private long window;
    // Dimensions of Window
    private static int width = 800;
    private static int height = 600;

    public void run() {

        System.out.println("Polygon Drawing Algorithm by Vinicius Ito, Felipe Myose and Gabriel Trevisan");

        init();
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
        Inicializacao.definirAcoesTeclado(this.verticesList, this.window);
        Inicializacao.definirAcoesMouse(this.verticesList, this.window);

        // Make the window visible
        glfwShowWindow(window);

    }

    private void draw(Main object) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        if (object.verticesList.size() > 1) {
            for (int i = 0; i < object.verticesList.size() - 1; i++) {
                LineDraw.bresenham(object.verticesList.get(i), object.verticesList.get(i + 1));
            }
        }

        Polygon.scanLine(verticesList, this.width, this.height);

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
