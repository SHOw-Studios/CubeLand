package io.show.graphics;

import io.show.graphics.internal.Window;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class Graphics {

    public static void main(String[] args) {

        registerBitmap("air", new Bitmap(16, 16));
        registerBitmap("dirt", new Bitmap(16, 16));
        registerBitmap("stone", new Bitmap(16, 16));

        init();

        Window window = new Window(300, 300, "Hello World");
        while (window.loopOnce()) ;
        window.destroy();

        terminate();
    }

    private static Map<String, Bitmap> bitmaps = new HashMap<>();

    /**
     * Registers a single bitmap with an unique identifier and returns if the operation was successfull.<br>
     * Please notice that you can only assign a bitmap to an identifier once, so you cannot re-register a bitmap twice with the same id
     *
     * @param id     a unique identifier for the bitmap
     * @param bitmap the bitmap to be registered
     * @return true if there was no bitmap registered with this id before
     */
    public static boolean registerBitmap(String id, Bitmap bitmap) {
        return bitmaps.putIfAbsent(id, bitmap) == null;
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
