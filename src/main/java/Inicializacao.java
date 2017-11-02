import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Inicializacao {
    public static long inicializarJanela(long window, int width, int height) {

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
        window = glfwCreateWindow(width, height, "Polygon Drawing Algorithm - 558478, 611026 and 554812", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

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
        return window;
    }

    public static void definirAcoesTeclado(ArrayList<Vertex> verticesList, long windowNum) {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowNum, (window, key, scancode, action, mods) -> {

            ArrayList<Vertex> resultList = new ArrayList<>(verticesList);
            if(action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    // Tecla esc: sair do programa
                    case GLFW_KEY_ESCAPE:
                        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                        break;

                    // Tecla para cima: transladar poligono para cima
                    case GLFW_KEY_UP:
                        // Segurar tecla shift para mover mais rapido
                        if (mods == GLFW_MOD_SHIFT) {
                            resultList = AffineTransform.translate(verticesList, 0, 3.0);
                        }
                        else { resultList = AffineTransform.translate(verticesList, 0, 1.0); }
                        break;

                    // Tecla para baixo: transladar poligono para baixo
                    case GLFW_KEY_DOWN:
                        // Segurar tecla shift para mover mais rapido
                        if (mods == GLFW_MOD_SHIFT) {
                            resultList = AffineTransform.translate(verticesList, 1, 3.0);
                        }
                        else { resultList = AffineTransform.translate(verticesList, 1, 1.0); }
                        break;

                    // Tecla para a esquerda: transladar poligono para a esquerda
                    case GLFW_KEY_LEFT:
                        // Segurar tecla shift para mover mais rapido
                        if (mods == GLFW_MOD_SHIFT) {
                            resultList = AffineTransform.translate(verticesList, 2, 3.0);
                        }
                        else { resultList = AffineTransform.translate(verticesList, 2, 1.0); }
                        break;

                    // Tecla para a direita: transladar poligono para a direita
                    case GLFW_KEY_RIGHT:
                        // Segurar tecla shift para mover mais rapido
                        if (mods == GLFW_MOD_SHIFT) {
                            resultList = AffineTransform.translate(verticesList, 3, 3.0);
                        }
                        else { resultList = AffineTransform.translate(verticesList, 3, 1.0); }
                        break;

                    // Teclas '{' e ']': expansao e compressao em x, respectivamente
                    // GLFW_KEY_LEFT_BRACKET esta deslocado de 1
                    case GLFW_KEY_LEFT_BRACKET + 1:
                        if (mods == GLFW_MOD_SHIFT) {
                            resultList = AffineTransform.scale(verticesList, 0, 1.1);
                        }
                        else { resultList = AffineTransform.scale(verticesList, 0, 0.9); }
                        break;

                    // Tecla '{' e '[': expansao e compressao em y, respectivamente
                    case GLFW_KEY_RIGHT_BRACKET:
                        if (mods == GLFW_MOD_SHIFT) {
                            resultList = AffineTransform.scale(verticesList, 1, 1.1);
                        }
                        else { resultList = AffineTransform.scale(verticesList, 1, 0.9); }
                        break;

                    // Tecla 'r': rotacao no sentido anti-horario
                    case GLFW_KEY_R:
                        resultList = AffineTransform.rotate(verticesList, 0, 0.02);
                        break;

                    // Tecla 't': rotacao no sentido horario
                    case GLFW_KEY_T:
                        resultList = AffineTransform.rotate(verticesList, 1, 0.02);
                        break;
                }

                // Determinacao das dimensoes da janela
                IntBuffer w = BufferUtils.createIntBuffer(1);
                IntBuffer h = BufferUtils.createIntBuffer(1);
                glfwGetWindowSize(window, w, h);
                int width = w.get(0);
                int height = h.get(0);

                // Escrita do poligono resultante
                if (Polygon.withinBounds(resultList, width, height)) {
                    Polygon.setPolygon(verticesList, resultList);
                }
            }

        });
    }

    public static void definirAcoesMouse(ArrayList<Vertex> verticesList, long windowNum) {
        // Setup a mouse callback. It will be called every time the left mouse button is pressed.
        // This is used to get the mouse location for drawing the line!
        glfwSetMouseButtonCallback(windowNum, (window, button, action, mods) -> {
            DoubleBuffer b1 = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer b2 = BufferUtils.createDoubleBuffer(1);

            // Botao esquerdo pressionado: adicionar vertice ao poligono
            if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                // mousePressed = true;
                glfwGetCursorPos(window, b1, b2);

                Vertex aux = new Vertex();
                aux.x = b1.get(0);
                aux.y = b2.get(0);

                // Caso o poligono tenha sido fechado, limpar lista de vertices do poligono
                if (Polygon.closedPolygon(verticesList)) {
                    verticesList.clear();
                    verticesList.add(aux);
                }

                // Caso contrario, apenas adicionar vertice
                else { verticesList.add(aux); }

            }
            // Botao direito do mouse: fechar poligono
            else if(button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS) {
                // mousePressed = false;
                glfwGetCursorPos(window, b1, b2);

                if (!Polygon.closedPolygon(verticesList)) {
                    if (!verticesList.isEmpty()) {
                        Polygon.closePolygon(verticesList);
                    }
                }
            }
        });
    }
}
