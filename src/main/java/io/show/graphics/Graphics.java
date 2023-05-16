package io.show.graphics;

import imgui.ImGui;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiDockNodeFlags;
import io.show.graphics.internal.ImGuiHelper;
import io.show.graphics.internal.Renderer;
import io.show.graphics.internal.Window;
import io.show.graphics.internal.gl.GLBuffer;
import io.show.graphics.internal.gl.Shader;
import io.show.graphics.internal.gl.TextureAtlas;
import io.show.graphics.internal.gl.VertexArray;
import io.show.graphics.internal.scene.Material;
import org.joml.Math;
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

/**
 * @author Felix Schreiber
 */
public class Graphics {

    public static void main(String[] args) {
        demo();
    }

    /**
     * A simple graphics demo, featuring graphs, textures and a (very simple) main loop
     */
    public static void demo() {

        // get the graphics instance
        final Graphics g = Graphics.getInstance();

        // you can register graphs that get drawn by ImGui as an overlay
        g.registerGraph(0, (x) -> x * x, 100, -1.0f, 1.0f, 0.0f, 1.0f);
        g.registerGraph(1, (x) -> (float) Math.sqrt(1.0f - x * x), 100, -1.0f, 1.0f, 0.0f, 1.0f);
        g.registerGraph(2, (x) -> (float) -Math.sqrt(1.0f - x * x), 100, -1.0f, 1.0f, 0.0f, 1.0f);
        g.registerGraph(3, new float[]{0.0f, 0.454f, 0.142f, 0.654f, 0.1534f, 0.13f, 0.92f, 0.155f, 1.0f}, 0.0f, 1.0f, 0.0f, 1.0f);

        final String[] textures = new String[]{ // just a temporary list of paths

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
            for (String path : textures) // go through every path and register its bitmap into the graphics object
                g.registerBitmap(id++, new Bitmap(ImageIO.read(new File(path))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // generate a texture atlas out of the registered textures to upload them to the gpu
        g.generateTextureAtlas(16, 16);

        // this is the main loop, it stops when the graphics' window closes
        while (g.loopOnce()) {
        }

        // do not forget to destroy all resources after you are done using them
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

    /**
     * An interface to represent function graphs
     */
    public interface Graph {
        float at(float x);
    }

    /**
     * This class saves the pointer to a graph implementation and some additional information for how to draw the graph
     *
     * @param xMin
     * @param xMax
     * @param yMin
     * @param yMax
     * @param graph
     * @param resolution
     * @param y
     */
    private record GraphInfo(float xMin, float xMax, float yMin, float yMax, Graph graph, int resolution, float[] y) {
    }

    private final Window m_Window;

    private final Material m_Material;
    private final Map<Long, Bitmap> m_BitmapMap = new HashMap<>();
    private TextureAtlas m_Atlas;

    private VertexArray m_VertexArray;
    private GLBuffer m_IndexBuffer;

    private final Map<Long, GraphInfo> m_GraphMap = new HashMap<>();
    private final ImGuiHelper m_ImGuiHelper;

    /**
     * Initializes GLFW, creates a window and sets up some other things like ImGui, the main materials and preps some drawing data
     */
    private Graphics() {

        // Print out the currently used LWJGL version
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Init the window //

        m_Window = new Window(800, 600, "CubeLand v1.0.0");
        m_Window.setResizeListener(this::onWindowResize);

        // Create the material //

        try {
            m_Material = new Material(new Shader("res/shaders/block/opaque.shader"));
        } catch (Shader.CompileStatusException | Shader.LinkStatusException | Shader.ValidateStatusException |
                 IOException e) {
            throw new RuntimeException(e);
        }

        onWindowResize(); // Set up the orthographic matrix for the first time

        // Init and setup ImGui //

        m_ImGuiHelper = new ImGuiHelper(m_Window.getHandle(), "#version 130");
        m_ImGuiHelper.setDefaultFont("res/fonts/Gothic3.ttf", 16.0f);
        m_ImGuiHelper.addFont("res/fonts/Bladeline-oXRa.ttf", 10.0f);
        m_ImGuiHelper.addFont("res/fonts/Evidence-M53Y.ttf", 18.0f);
        m_ImGuiHelper.addFont("res/fonts/Oasis-BW0JV.ttf", 16.0f);
        m_ImGuiHelper.addFont("res/fonts/CursedTimerUlil-Aznm.ttf", 14.0f);
        m_ImGuiHelper.updateFonts();

        // Prepare rendering data //

        m_VertexArray = new VertexArray();

        final int[] indices = new int[]{0, 1, 2, 2, 3, 0};
        ByteBuffer buffer = ByteBuffer.allocateDirect(indices.length * Integer.BYTES).order(ByteOrder.nativeOrder());
        buffer.asIntBuffer().put(indices);
        m_IndexBuffer = new GLBuffer().setTarget(GL_ELEMENT_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setData(buffer).unbind();

        final float[] vertices = new float[]{-0.5f, -0.5f, 0, 0, 0, 0, 0, 1, -0.5f, 0.5f, 0, 1, 0, 1, 0, 1, 0.5f, 0.5f, 1, 1, 1, 1, 0, 1, 0.5f, -0.5f, 1, 0, 1, 0, 0, 1};
        buffer = ByteBuffer.allocateDirect(vertices.length * Float.BYTES).order(ByteOrder.nativeOrder());
        buffer.asFloatBuffer().put(vertices);
        GLBuffer vertexBuffer = new GLBuffer().setTarget(GL_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setData(buffer).unbind();

        final VertexArray.Layout layout = new VertexArray.Layout().pushFloat(2).pushFloat(2).pushFloat(4);
        m_VertexArray.bind().bindBuffer(vertexBuffer, layout).unbind();
    }

    /**
     * This method gets called every time the window resizes; whenever this happens it updates the projection matrix in the shader program to its new size
     */
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

    /**
     * @param id         a unique identifier for the graph
     * @param graph      the function graph implementation
     * @param resolution the resolution of the graph, so how smooth it looks; higher == smoother
     * @param xMin       the minimum x
     * @param xMax       the maximum x
     * @param yMin       the minimum y
     * @param yMax       the maximum y
     * @return true if there was no graph registered with this id before
     */
    public boolean registerGraph(long id, Graph graph, int resolution, float xMin, float xMax, float yMin, float yMax) {
        GraphInfo info = new GraphInfo(xMin, xMax, yMin, yMax, graph, resolution, null);
        return m_GraphMap.putIfAbsent(id, info) == null;
    }

    /**
     * @param id   a unique identifier for the graph
     * @param y    an array of y positions
     * @param xMin the minimum x
     * @param xMax the maximum x
     * @param yMin the minimum y
     * @param yMax the maximum y
     * @return true if there was no graph registered with this id before
     */
    public boolean registerGraph(long id, float[] y, float xMin, float xMax, float yMin, float yMax) {
        GraphInfo info = new GraphInfo(xMin, xMax, yMin, yMax, null, y.length, y);
        return m_GraphMap.putIfAbsent(id, info) == null;
    }

    /**
     * Generates a texture atlas out of all the registered bitmaps and uploads it to the gpu
     * <p>
     * Notice that the width and height of all the bitmaps must be the same for this to work
     *
     * @param tileW the tile width
     * @param tileH the tile height
     * @return this
     */
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

        m_Material.getTextures().clear();
        m_Material.addTexture(m_Atlas.getTexture());

        m_Material.getShader().bind().setUniformInt("sampler", 0).unbind();

        return this;
    }

    /**
     * @return the window object
     */
    public Window getWindow() {
        return m_Window;
    }

    /**
     * @return the main material
     */
    public Material getMaterial() {
        return m_Material;
    }

    /**
     * @return the texture atlas
     */
    public TextureAtlas getAtlas() {
        return m_Atlas;
    }

    /**
     * The rendering loop: call it from your main loop from the main thread. Every time this loop is called it will clear the screen, render the scene and the gui and then return whether to close the application
     *
     * @return true while the window has not been closed
     */
    public boolean loopOnce() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        // glDisable(GL_CULL_FACE);

        Renderer.render(m_VertexArray, m_IndexBuffer, m_Material);

        m_ImGuiHelper.loopOnce(() -> {

            ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.PassthruCentralNode);

            ImGui.begin("Graphs");
            ImGui.beginTabBar("Tabs");

            m_GraphMap.forEach((id, info) -> {
                if (ImGui.beginTabItem("Graph #" + id)) {
                    if (ImPlot.beginPlot("Plot #" + id)) {
                        final int res = info.resolution();
                        final float invRes = 1.0f / (res - 1.0f);

                        final Float[] xa = new Float[res];
                        final Float[] ya = new Float[res];

                        for (int i = 0; i < res; i++) {
                            final float x = (info.xMax() - info.xMin()) * invRes * i + info.xMin();
                            final float y = (info.yMax() - info.yMin()) * (info.graph() != null ? info.graph().at(x) : info.y() != null ? info.y()[i] : 0.0f) + info.yMin();
                            xa[i] = x;
                            ya[i] = y;
                        }

                        ImPlot.plotLine("Graph", xa, ya);
                        ImPlot.endPlot();
                    }
                    ImGui.endTabItem();
                }
            });

            ImGui.endTabBar();
            ImGui.end();

            ImGui.showDemoWindow();
        });

        return m_Window.loopOnce();
    }

    /**
     * Destroys the window object, cleans up all the other resources, terminates GLFW and frees the error callback
     */
    public void destroy() {
        // Destroy the window
        m_Window.close();
        m_Material.close();
        m_Atlas.close();
        m_ImGuiHelper.close();

        // Terminate GLFW
        glfwTerminate();

        // Free the error callback
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        // Check if it already has been freed
        if (callback != null) callback.free();
    }
}
