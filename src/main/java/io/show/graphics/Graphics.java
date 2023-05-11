package io.show.graphics;

import io.show.graphics.internal.Renderer;
import io.show.graphics.internal.Window;
import io.show.graphics.internal.gl.GLBuffer;
import io.show.graphics.internal.gl.Shader;
import io.show.graphics.internal.gl.VertexArray;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;

public class Graphics {

    public static void main(String[] args) {

        init();

        registerBitmap(0, new Bitmap(16, 16, 0xff00ff, 0.0f));
        registerBitmap(1, new Bitmap(16, 16, 0x888888, 1.0f));
        registerBitmap(2, new Bitmap(16, 16, 0x883322, 1.0f));

        VertexArray vertexArray = new VertexArray();

        int[] indices = new int[]{0, 1, 2, 2, 3, 0};
        ByteBuffer buffer = ByteBuffer.allocateDirect(indices.length * Integer.BYTES).order(ByteOrder.nativeOrder());
        buffer.asIntBuffer().put(indices);
        GLBuffer indexBuffer = new GLBuffer().setTarget(GL_ELEMENT_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setData(buffer).unbind();

        float[] vertices = new float[]{0, 0, 0, 1, 1, 1, 1, 0};
        buffer = ByteBuffer.allocateDirect(vertices.length * Float.BYTES).order(ByteOrder.nativeOrder());
        buffer.asFloatBuffer().put(vertices);
        GLBuffer vertexBuffer = new GLBuffer().setTarget(GL_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setData(buffer).unbind();

        VertexArray.Layout layout = new VertexArray.Layout().pushFloat(2).pushFloat(4);
        vertexArray.bind().bindBuffer(vertexBuffer, layout).unbind();

        Shader shader = null;
        try {
            shader = new Shader("res/shaders/blocks/opaque.shader");
        } catch (Shader.CompileStatusException e) {
            throw new RuntimeException(e);
        } catch (Shader.LinkStatusException e) {
            throw new RuntimeException(e);
        } catch (Shader.ValidateStatusException e) {
            throw new RuntimeException(e);
        }

        Window window = new Window(300, 300, "Hello World");
        while (window.loopOnce()) {
            glClear(GL_COLOR_BUFFER_BIT);
            Renderer.render(vertexArray, indexBuffer, shader);
        }
        window.destroy();

        terminate();
    }

    private static final Map<Long, Bitmap> bitmaps = new HashMap<>();

    /**
     * Registers a single bitmap with a unique identifier and returns if the operation was successfully.
     * <p>
     * Please notice that you can only assign a bitmap to an identifier once,
     * so you cannot re-register a bitmap twice with the same id
     *
     * @param id     a unique identifier for the bitmap
     * @param bitmap the bitmap to be registered
     * @return true if there was no bitmap registered with this id before
     */
    public static boolean registerBitmap(long id, Bitmap bitmap) {
        return bitmaps.putIfAbsent(id, bitmap) == null;
    }

    /**
     * Initializes GLFW and sets up the error callback to write to the system error stream
     */
    public static void init() {

        // Print out the currently used LWJGL version
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
    }

    public static boolean applyTextures() {

        bitmaps.forEach((id, bitmap) -> {
        });

        return false;
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
