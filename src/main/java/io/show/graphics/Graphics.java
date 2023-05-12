package io.show.graphics;

import io.show.graphics.internal.Renderer;
import io.show.graphics.internal.Window;
import io.show.graphics.internal.gl.GLBuffer;
import io.show.graphics.internal.gl.Shader;
import io.show.graphics.internal.gl.Texture;
import io.show.graphics.internal.gl.VertexArray;
import io.show.graphics.internal.scene.Material;
import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLUtil;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT_SYNCHRONOUS;

public class Graphics {

    private static Window window;
    private static Material material;

    public static void main(String[] args) {

        init();

        window = new Window(300, 300, "Hello World");
        GLUtil.setupDebugMessageCallback();

        Renderer.debug();
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);

        registerBitmap(0, new Bitmap(16, 16, 0xff00ff, 0.0f));
        registerBitmap(1, new Bitmap(16, 16, 0x888888, 1.0f));
        registerBitmap(2, new Bitmap(16, 16, 0x883322, 1.0f));

        VertexArray vertexArray = new VertexArray();

        int[] indices = new int[]{0, 1, 2, 2, 3, 0};
        ByteBuffer buffer = ByteBuffer.allocateDirect(indices.length * Integer.BYTES).order(ByteOrder.nativeOrder());
        buffer.asIntBuffer().put(indices);
        buffer.position(0);
        GLBuffer indexBuffer = new GLBuffer().setTarget(GL_ELEMENT_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setSize(indices.length * Integer.BYTES).setData(buffer).unbind();

        float[] vertices = new float[]{-0.5f, -0.5f, 0, 0, 0, 0, 0, 1, -0.5f, 0.5f, 0, 1, 0, 1, 0, 1, 0.5f, 0.5f, 1, 1, 1, 1, 0, 1, 0.5f, -0.5f, 1, 0, 1, 0, 0, 1};
        buffer = ByteBuffer.allocateDirect(vertices.length * Float.BYTES).order(ByteOrder.nativeOrder());
        buffer.asFloatBuffer().put(vertices);
        buffer.position(0);
        GLBuffer vertexBuffer = new GLBuffer().setTarget(GL_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setSize(vertices.length * Float.BYTES).setData(buffer).unbind();

        VertexArray.Layout layout = new VertexArray.Layout().pushFloat(2).pushFloat(2).pushFloat(4);
        vertexArray.bind().bindBuffer(vertexBuffer, layout).unbind();

        Shader shader = null;
        try {
            shader = new Shader("res/shaders/block/opaque.shader");
        } catch (Shader.CompileStatusException | Shader.LinkStatusException | Shader.ValidateStatusException |
                 IOException e) {
            throw new RuntimeException(e);
        }

        Bitmap bitmap = null;
        try {
            bitmap = new Bitmap(ImageIO.read(new File("res/textures/block/overworld/grass_block.bmp")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer = ByteBuffer.allocateDirect(bitmap.getData().length * Integer.BYTES).order(ByteOrder.nativeOrder());
        buffer.put(bitmap.getDataAsByteArray());
        buffer.position(0);

        Texture texture = new Texture().bind().setDefaultParameters().setData(bitmap.getWidth(), bitmap.getHeight(), buffer).unbind();
        material = new Material(shader).addTexture(texture);

        shader.bind().setUniformInt("sampler", 0).unbind();
        windowresize();

        window.setResizeListener(Graphics::windowresize);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        while (window.loopOnce()) {
            glClear(GL_COLOR_BUFFER_BIT);
            Renderer.render(vertexArray, indexBuffer, material);
        }
        window.destroy();

        terminate();
    }

    private static void windowresize() {
        float w = window.getWidth();
        float h = window.getHeight();
        float a = w / h;
        Matrix4f mat = new Matrix4f().ortho2D(-a, a, -1.0f, 1.0f);
        material.getShader().bind().setUniformFloatMat4("matrix", mat.get(new float[16])).unbind();
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
