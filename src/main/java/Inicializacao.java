import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.lang.reflect.Array;
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

    public static void definirAcoesTeclado(ArrayList<Vertex2D> verticesList, Vertex3D center, long windowNum) {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowNum, (window, key, scancode, action, mods) -> {

            ArrayList<Vertex2D> resultList = new ArrayList<>(verticesList);
            ArrayList<ArrayList<Vertex3D>> resultList3D = new ArrayList<>(Main.cube);

            double step;

            if(action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    // Tecla esc: sair do programa
                    case GLFW_KEY_ESCAPE:
                        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                        break;

                    // Tecla para cima: transladar poligono para cima
                    case GLFW_KEY_UP:
                        step = 1.0;
                        // Segurar tecla shift para mover mais rapido
                        if (mods == GLFW_MOD_SHIFT) {
                            step = 3.0;
                        }
                        if (Main.draw3D) {
                            resultList3D = AffineTransform.translate3D(Main.cube, 0, step);
                        }
                        else {
                            resultList = AffineTransform.translate2D(verticesList, 0, step);
                        }
                        break;

                    // Tecla para baixo: transladar poligono para baixo
                    case GLFW_KEY_DOWN:
                        step = 1.0;
                        // Segurar tecla shift para mover mais rapido
                        if (mods == GLFW_MOD_SHIFT) {
                            step = 3.0;
                        }
                        if (Main.draw3D) {
                            resultList3D = AffineTransform.translate3D(Main.cube, 1, step);
                        }
                        else {
                            resultList = AffineTransform.translate2D(verticesList, 1, step);
                        }
                        break;

                    // Tecla para a esquerda: transladar poligono para a esquerda
                    case GLFW_KEY_LEFT:
                        step = 1.0;
                        // Segurar tecla shift para mover mais rapido
                        if (mods == GLFW_MOD_SHIFT) {
                            step = 3.0;
                        }
                        if (Main.draw3D) {
                            resultList3D = AffineTransform.translate3D(Main.cube, 2, step);
                        }
                        else {
                            resultList = AffineTransform.translate2D(verticesList, 2, step);
                        }
                        break;

                    // Tecla para a direita: transladar poligono para a direita
                    case GLFW_KEY_RIGHT:
                        step = 1.0;
                        // Segurar tecla shift para mover mais rapido
                        if (mods == GLFW_MOD_SHIFT) {
                            step = 3.0;
                        }
                        if (Main.draw3D) {
                            resultList3D = AffineTransform.translate3D(Main.cube, 3, step);
                        }
                        else {
                            resultList = AffineTransform.translate2D(verticesList, 3, step);
                        }
                        break;

                    // Teclas '{' e ']': expansao e compressao em x, respectivamente
                    // GLFW_KEY_LEFT_BRACKET esta deslocado de 1
                    case GLFW_KEY_LEFT_BRACKET + 1:
                        step = 0.9;
                        if (mods == GLFW_MOD_SHIFT) {
                            step = 1.1;
                        }
                        if (Main.draw3D) {
                            resultList3D = AffineTransform.scale3D(Main.cube, 0, step);
                        }
                        else { resultList = AffineTransform.scale2D(verticesList, 0, step); }
                        break;

                    // Tecla '{' e '[': expansao e compressao em y, respectivamente
                    case GLFW_KEY_RIGHT_BRACKET:
                        step = 0.9;
                        if (mods == GLFW_MOD_SHIFT) {
                            step = 1.1;
                        }
                        if (Main.draw3D) {
                            resultList3D = AffineTransform.scale3D(Main.cube, 1, step);
                        }
                        else { resultList = AffineTransform.scale2D(verticesList, 1, step); }
                        break;

                    // Tecla 'r': rotacao no sentido anti-horario
                    case GLFW_KEY_R:
                        if (Main.draw3D) {
                            resultList3D = AffineTransform.rotate3D(Main.cube, 0, 0.02);
                        }
                        else { resultList = AffineTransform.rotate2D(verticesList, 0, 0.02); }
                        break;

                    // Tecla 't': rotacao no sentido horario
                    case GLFW_KEY_T:
                        if (Main.draw3D) {
                            resultList3D = AffineTransform.rotate3D(Main.cube, 1, 0.02);
                        }
                        else { resultList = AffineTransform.rotate2D(verticesList, 1, 0.02); }
                        break;

                    // Tecla '3': exibicao da projecao 3D do poligono extrudado
                    case GLFW_KEY_3:
                        Main.draw3D = true;
                        break;

                    // Tecla 'o': zoom out no plano de projecao
                    case GLFW_KEY_O:
                        center.z = center.z- 1;
                        break;

                    // Tecla 'i': zoom in no plano de projecao
                    case GLFW_KEY_I:
                        center.z = center.z + 1;
                        break;

                    // Tecla 'o': zoom out no plano de projecao
                    case GLFW_KEY_N:
                        center.x = center.x- 1;
                        break;

                    // Tecla 'i': zoom in no plano de projecao
                    case GLFW_KEY_M:
                        center.x = center.x + 1;
                        break;
                }

                // Determinacao das dimensoes da janela
                IntBuffer w = BufferUtils.createIntBuffer(1);
                IntBuffer h = BufferUtils.createIntBuffer(1);
                glfwGetWindowSize(window, w, h);
                int width = w.get(0);
                int height = h.get(0);

                if (Main.draw3D) {
                    if (Polygon.withinBounds(Prism.to2D(Prism.flatten(resultList3D), Main.center), width-1, height-1)) {
                        Prism.setPrism(Main.cube, resultList3D);
                    }
                }
                // Escrita do poligono resultante
                if (Polygon.withinBounds(resultList, width, height)) {
                    Polygon.setPolygon(verticesList, resultList);
                }
            }

        });
    }

    public static void definirAcoesMouse(ArrayList<Vertex2D> verticesList, long windowNum) {
        // Setup a mouse callback. It will be called every time the left mouse button is pressed.
        // This is used to get the mouse location for drawing the line!
        glfwSetMouseButtonCallback(windowNum, (window, button, action, mods) -> {
            DoubleBuffer b1 = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer b2 = BufferUtils.createDoubleBuffer(1);

            // Botao esquerdo pressionado: adicionar vertice ao poligono
            if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                // mousePressed = true;
                glfwGetCursorPos(window, b1, b2);

                Main.draw3D = false;

                Vertex2D aux = new Vertex2D();
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
