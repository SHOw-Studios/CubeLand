package io.show.graphics;

import io.show.graphics.internal.Window;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class Graphics {

    public static void main(String[] args) {

        init();

        Window window = new Window(300, 300, "Hello World");
        while (window.loopOnce()) ;
        window.destroy();

        terminate();
    }

    /**
     * Initializes GLFW and sets up the error callback to write to the system error stream
     */
    public static void init() {

        // Print out the currently used LWJGL version
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
    }

    /**
     * Terminates GLFW and clears the error callback
     */
    public static void terminate() {

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

}
