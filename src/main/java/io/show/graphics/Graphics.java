package io.show.graphics;

import io.show.graphics.internal.Renderer;
import io.show.graphics.internal.Window;
import io.show.graphics.internal.gl.GLBuffer;
import io.show.graphics.internal.gl.Shader;
import io.show.graphics.internal.gl.TextureAtlas;
import io.show.graphics.internal.gl.VertexArray;
import io.show.graphics.internal.scene.Material;
import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;

public class Graphics {

    public static void main(String[] args) {

        Graphics g = Graphics.getInstance();

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

        String[] textures = new String[]{

                "res/textures/block/liquid/lava.bmp",

                "res/textures/block/liquid/lava_surface.bmp",

                "res/textures/block/liquid/water.bmp",

                "res/textures/block/liquid/water_surface.bmp",

                "res/textures/block/overworld/grass_block.bmp",

                "res/textures/block/overworld/grass_wall.bmp",

                "res/textures/block/overworld/leaves.bmp",

                "res/textures/block/overworld/liane_wall.bmp",

                "res/textures/block/overworld/wood.bmp",

                "res/textures/block/panel/wood_panel.bmp",

                "res/textures/block/underworld/coal_ore.bmp",

                "res/textures/block/underworld/diamond_ore.bmp",

                "res/textures/block/underworld/lapis_ore.bmp",

                "res/textures/block/underworld/stone.bmp",

        };

        try {
            long id = 0;
            for (String path : textures)
                g.registerBitmap(id++, new Bitmap(ImageIO.read(new File(path))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        g.generateTextureAtlas(16, 16);

        g.setMaterial(new Material(shader).addTexture(g.getAtlas().getTexture()));

        Window window = g.getWindow();
        float w = window.getWidth();
        float h = window.getHeight();
        float a = w / h;
        Matrix4f mat = new Matrix4f().ortho2D(-a, a, -1.0f, 1.0f);

        shader.bind().setUniformInt("sampler", 0).setUniformFloatMat4("matrix", mat.get(new float[16])).unbind();

        while (g.loopOnce(vertexArray, indexBuffer)) {
        }

        g.destroy();
    }

    private static Graphics __instance;

    /**
     * This singleton method either returns the current singleton instance of the Graphics class or, if not yet done, creates one and returns it.
     *
     * @return the Graphics instance
     */
    public static Graphics getInstance() {
        if (__instance == null) __instance = new Graphics();
        return __instance;
    }

    private Window m_Window;
    private Material m_Material;
    private final Map<Long, Bitmap> m_BitmapMap = new HashMap<>();
    private TextureAtlas m_Atlas;

    /**
     * <p>Initializes GLFW and sets up the error callback to write to the system error stream.</p>
     * <p>After that it sets up the window for this new application.</p>
     */
    private Graphics() {

        // Print out the currently used LWJGL version
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        m_Window = new Window(800, 600, "CubeLand v1.0.0");
        m_Window.setResizeListener(this::onWindowResize);
    }

    private void onWindowResize() {
        float w = m_Window.getWidth();
        float h = m_Window.getHeight();
        float a = w / h;
        Matrix4f mat = new Matrix4f().ortho2D(-a, a, -1.0f, 1.0f);
        m_Material.getShader().bind().setUniformFloatMat4("matrix", mat.get(new float[16])).unbind();
    }

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
    public boolean registerBitmap(long id, Bitmap bitmap) {
        return m_BitmapMap.putIfAbsent(id, bitmap) == null;
    }

    public Graphics generateTextureAtlas(int tileW, int tileH) {

        int tilesEdgeNum = (int) Math.ceil(Math.sqrt(m_BitmapMap.size()));
        var bitmaps = m_BitmapMap.values().stream().toList();

        ByteBuffer buffer = ByteBuffer.allocateDirect(tileW * tileH * 4).order(ByteOrder.nativeOrder());
        TextureAtlas atlas = new TextureAtlas(tileW, tileH, tilesEdgeNum, tilesEdgeNum).bind();

        for (int i = 0; i < bitmaps.size(); i++) {
            final int x = i % tilesEdgeNum;
            final int y = (i - x) / tilesEdgeNum;

            byte[] data = bitmaps.get(i).getDataAsByteArray();

            atlas.setTile(x, y, buffer.clear().put(data).position(0));
        }

        m_Atlas = atlas.unbind();

        return this;
    }

    public Graphics setMaterial(Material material) {
        m_Material = material;
        return this;
    }

    public Window getWindow() {
        return m_Window;
    }

    public TextureAtlas getAtlas() {
        return m_Atlas;
    }

    public boolean loopOnce(VertexArray vertexArray, GLBuffer indexBuffer) {
        glClear(GL_COLOR_BUFFER_BIT);
        Renderer.render(vertexArray, indexBuffer, m_Material);

        return m_Window.loopOnce();
    }

    /**
     * Destroys the window object, terminates GLFW and frees the error callback
     */
    public void destroy() {
        // Destroy the window
        m_Window.close();
        m_Material.close();
        m_Atlas.close();

        // Terminate GLFW
        glfwTerminate();

        // Free the error callback
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        // Check if it already has been freed
        if (callback != null) callback.free();
    }
}
