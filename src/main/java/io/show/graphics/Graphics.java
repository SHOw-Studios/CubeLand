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
import org.joml.SimplexNoise;
import org.joml.Vector2f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;

/**
 * @author Felix Schreiber
 */
public class Graphics {

    public static void main(String[] args) {
        demo();
    }

    private static int block_lava, block_lava_surface, block_water, block_water_surface, block_dirt, block_grass_block, block_grass_wall, block_leaves, block_liane_wall, block_wood, block_wood_panel, block_coal_ore, block_diamond_ore, block_lapis_ore, block_stone, block_air;

    private static int block(int x, int y, int z, float level, float scale) {
        float surface = level + SimplexNoise.noise(z * scale, y * scale, x * scale) * 10.0f;
        float d = surface - y;
        if (d <= 1 && d > 0) return block_grass_block;
        if (d <= 2 && d > 0) return block_dirt;
        if (y < surface) return block_stone;
        d = level - y;
        if (d <= 1 && d > 0) return block_water_surface;
        if (d > 0) return block_water;
        return block_air;
    }

    /**
     * A simple graphics demo, featuring graphs, textures and a (very simple) main loop
     */
    public static void demo() {

        // get the graphics instance
        final Graphics g = Graphics.getInstance();

        // you can register graphs that get drawn by ImGui as an overlay
        g.registerGraph((x) -> x * x, 100, -1.0f, 1.0f, 0.0f, 1.0f);
        g.registerGraph((x) -> (float) Math.sqrt(1.0f - x * x), 100, -1.0f, 1.0f, 0.0f, 1.0f);
        g.registerGraph((x) -> (float) -Math.sqrt(1.0f - x * x), 100, -1.0f, 1.0f, 0.0f, 1.0f);
        g.registerGraph(new float[]{0.0f, 0.454f, 0.142f, 0.654f, 0.1534f, 0.13f, 0.92f, 0.155f, 1.0f}, 0.0f, 1.0f, 0.0f, 1.0f);

        g.registerGraph((x) -> SimplexNoise.noise(x, 0.0f), 1000, -10.0f, 10.0f, -1.0f, 1.0f);

        final String[] textures = new String[]{ // just a temporary list of paths

                "res/textures/block/liquid/lava.bmp",

                "res/textures/block/liquid/lava_surface.bmp",

                "res/textures/block/liquid/water.bmp",

                "res/textures/block/liquid/water_surface.bmp",

                "res/textures/block/overworld/dirt.bmp",

                "res/textures/block/overworld/grass_block.bmp",

                "res/textures/block/overworld/grass_wall.bmp",

                "res/textures/block/overworld/leaves.bmp",

                "res/textures/block/overworld/liane_wall.bmp",

                "res/textures/block/overworld/wood.bmp",

                "res/textures/block/panel/wood_panel.bmp",

                "res/textures/block/underworld/coal_ore.bmp",

                "res/textures/block/underworld/diamond_ore.bmp",

                "res/textures/block/underworld/lapis_ore.bmp",

                "res/textures/block/underworld/stone.bmp"

        };

        final float[] opacities = new float[]{1.0f, 1.0f, 0.7f, 0.7f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};

        try {
            for (int id = 0; id < textures.length; id++) // go through every path and register its bitmap into the graphics object
                g.registerBitmap(new Bitmap(ImageIO.read(new File(textures[id])), opacities[id]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // generate a texture atlas out of the registered textures to upload them to the gpu
        g.generateTextureAtlas(16, 16);

        block_air = g.registerBlockType(null); // air
        block_lava = g.registerBlockType(new BlockType(0, true, false));
        block_lava_surface = g.registerBlockType(new BlockType(1, true, false));
        block_water = g.registerBlockType(new BlockType(2, true, true));
        block_water_surface = g.registerBlockType(new BlockType(3, true, true));
        block_dirt = g.registerBlockType(new BlockType(4, false, false));
        block_grass_block = g.registerBlockType(new BlockType(5, false, false));
        block_grass_wall = g.registerBlockType(new BlockType(6, false, false));
        block_leaves = g.registerBlockType(new BlockType(7, false, true));
        block_liane_wall = g.registerBlockType(new BlockType(8, false, true));
        block_wood = g.registerBlockType(new BlockType(9, false, false));
        block_wood_panel = g.registerBlockType(new BlockType(10, false, false));
        block_coal_ore = g.registerBlockType(new BlockType(11, false, false));
        block_diamond_ore = g.registerBlockType(new BlockType(12, false, false));
        block_lapis_ore = g.registerBlockType(new BlockType(13, false, false));
        block_stone = g.registerBlockType(new BlockType(14, false, false));

        final int width = 256;
        final int height = 256;
        final int depth = 2;
        final int[][][] world = new int[depth][height][width];
        for (int k = 0; k < depth; k++) {
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    world[k][j][i] = block(i, j, k, 64f, 0.1f);
                }
            }
        }
        g.generateMesh(world, 0, width, height, depth);

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

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof GraphInfo info)) return false;
            return xMin == info.xMin && xMax == info.xMax && yMin == info.yMin && yMax == info.yMax && graph.equals(info.graph) && resolution == info.resolution && Arrays.equals(y, info.y);
        }

    }

    private final Window m_Window;

    private final List<Bitmap> m_BitmapMap = new Vector<>();
    private TextureAtlas m_Atlas;

    private final Material m_Material;
    private VertexArray m_VertexArray;
    private GLBuffer m_IndexBuffer;
    private GLBuffer m_VertexBuffer;

    private final Material m_SkyboxMaterial;
    private VertexArray m_SkyboxVertexArray;
    private GLBuffer m_SkyboxIndexBuffer;
    private GLBuffer m_SkyboxVertexBuffer;

    private final List<GraphInfo> m_GraphMap = new Vector<>();
    private final ImGuiHelper m_ImGuiHelper;

    private final Vector2f m_CameraPosition = new Vector2f(8.0f, 64.0f);
    private final List<BlockType> m_BlockTypes = new Vector<>();

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
            m_SkyboxMaterial = new Material(new Shader("res/shaders/misc/skybox.shader"));
        } catch (Shader.CompileStatusException | Shader.LinkStatusException | Shader.ValidateStatusException |
                 IOException e) {
            throw new RuntimeException(e);
        }

        Matrix4f view = new Matrix4f().translate(m_CameraPosition.x(), m_CameraPosition.y(), 0.0f).invert();
        m_Material.getShader().bind().setUniformFloatMat4("view", view.get(new float[16])).unbind();

        onWindowResize(); // Set up the orthographic matrix for the first time

        // Create Skybox //

        final float[] skyboxVertices = new float[]{-1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f};
        ByteBuffer buffer = ByteBuffer.allocateDirect(Float.BYTES * skyboxVertices.length).order(ByteOrder.nativeOrder());
        buffer.asFloatBuffer().put(skyboxVertices);
        m_SkyboxVertexBuffer = new GLBuffer().setTarget(GL_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setData(buffer).unbind();

        final int[] skyboxIndices = new int[]{0, 1, 2, 2, 3, 0};
        buffer = ByteBuffer.allocateDirect(Integer.BYTES * skyboxIndices.length).order(ByteOrder.nativeOrder());
        buffer.asIntBuffer().put(skyboxIndices);
        m_SkyboxIndexBuffer = new GLBuffer().setTarget(GL_ELEMENT_ARRAY_BUFFER).setUsage(GL_STATIC_DRAW).bind().setData(buffer).unbind();

        final VertexArray.Layout skyboxLayout = new VertexArray.Layout().pushFloat(2);
        m_SkyboxVertexArray = new VertexArray().bind().bindBuffer(m_SkyboxVertexBuffer, skyboxLayout).unbind();

        // Init and setup ImGui //

        m_ImGuiHelper = new ImGuiHelper(m_Window.getHandle(), "#version 460 core");
        m_ImGuiHelper.setDefaultFont("res/fonts/Gothic3.ttf", 16.0f);
        m_ImGuiHelper.addFont("res/fonts/Bladeline-oXRa.ttf", 10.0f);
        m_ImGuiHelper.addFont("res/fonts/Evidence-M53Y.ttf", 18.0f);
        m_ImGuiHelper.addFont("res/fonts/Oasis-BW0JV.ttf", 16.0f);
        m_ImGuiHelper.addFont("res/fonts/CursedTimerUlil-Aznm.ttf", 14.0f);
        m_ImGuiHelper.updateFonts();
    }

    /**
     * This method gets called every time the window resizes; whenever this happens it updates the projection matrix in the shader program to its new size
     */
    private void onWindowResize() {
        final float w = m_Window.getWidth();
        final float h = m_Window.getHeight();
        final float a = w / h;
        final float scale = 10.0f;
        Matrix4f mat = new Matrix4f().ortho2D(-a * scale, a * scale, -scale, scale);
        m_Material.getShader().bind().setUniformFloatMat4("projection", mat.get(new float[16])).unbind();
        m_SkyboxMaterial.getShader().bind().setUniformFloatMat4("projection", mat.get(new float[16])).unbind();
    }

    /**
     * Registers a single bitmap with a unique identifier and returns if the operation was successfully.
     * <p>
     * Please notice that you can only assign a bitmap to an identifier once,
     * so you cannot re-register a bitmap twice with the same id
     *
     * @param bitmap the bitmap to be registered
     * @return a unique identifier for the bitmap
     */
    public int registerBitmap(Bitmap bitmap) {
        if (m_BitmapMap.contains(bitmap)) return m_BitmapMap.indexOf(bitmap);
        m_BitmapMap.add(bitmap);
        return m_BitmapMap.size() - 1;
    }

    /**
     * @param graph      the function graph implementation
     * @param resolution the resolution of the graph, so how smooth it looks; higher == smoother
     * @param xMin       the minimum x
     * @param xMax       the maximum x
     * @param yMin       the minimum y
     * @param yMax       the maximum y
     * @return a unique identifier for the graph
     */
    public int registerGraph(Graph graph, int resolution, float xMin, float xMax, float yMin, float yMax) {
        GraphInfo info = new GraphInfo(xMin, xMax, yMin, yMax, graph, resolution, null);
        if (m_GraphMap.contains(info)) return m_GraphMap.indexOf(info);
        m_GraphMap.add(info);
        return m_GraphMap.size() - 1;
    }

    /**
     * @param y    an array of y positions
     * @param xMin the minimum x
     * @param xMax the maximum x
     * @param yMin the minimum y
     * @param yMax the maximum y
     * @return a unique identifier for the graph
     */
    public int registerGraph(float[] y, float xMin, float xMax, float yMin, float yMax) {
        GraphInfo info = new GraphInfo(xMin, xMax, yMin, yMax, null, y.length, y);
        if (m_GraphMap.contains(info)) return m_GraphMap.indexOf(info);
        m_GraphMap.add(info);
        return m_GraphMap.size() - 1;
    }

    public int registerBlockType(BlockType blockType) {
        if (m_BlockTypes.contains(blockType)) return m_BlockTypes.indexOf(blockType);
        m_BlockTypes.add(blockType);
        return m_BlockTypes.size() - 1;
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

        final int tilesEdgeNum = (int) Math.ceil(Math.sqrt(m_BitmapMap.size()));

        final ByteBuffer buffer = ByteBuffer.allocateDirect(tileW * tileH * 4).order(ByteOrder.nativeOrder());
        final TextureAtlas atlas = new TextureAtlas(tileW, tileH, tilesEdgeNum, tilesEdgeNum, 0xffff00ff).bind();

        for (int i = 0; i < m_BitmapMap.size(); i++) {
            final int x = i % tilesEdgeNum;
            final int y = (i - x) / tilesEdgeNum;

            byte[] data = m_BitmapMap.get(i).getDataAsByteArray();

            atlas.setTile(x, y, buffer.clear().put(data).position(0));
        }

        m_Atlas = atlas.unbind();

        m_Material.clearTextures();
        m_Material.addTexture(m_Atlas.getTexture());

        m_Material.getShader().bind().setUniformInt("sampler", 0).unbind();

        return this;
    }

    public Graphics generateMesh(int[][][] world, int xOffset, int width, int height, int depth) {

        final int atlasW = m_Atlas.getWidth();
        final int atlasH = m_Atlas.getHeight();
        final int atlasTW = m_Atlas.getTileW();
        final int atlasTH = m_Atlas.getTileH();
        final int atlasTX = m_Atlas.getTilesX();

        final float invW = atlasTW / (float) atlasW;
        final float invH = atlasTH / (float) atlasH;

        record Vertex(float x, float y, float z, float u, float v) {
            public static final int BYTES = 20;
        }

        final List<Vertex> vertices = new Vector<>();
        final List<Integer> indices = new Vector<>();

        for (int k = 0; k < depth; k++) {
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {

                    final BlockType block = m_BlockTypes.get(world[k][j][i]);
                    if (block == null) continue; // air

                    if (k < depth - 1) {
                        final BlockType front = m_BlockTypes.get(world[k + 1][j][i]);
                        if (front != null && !front.isTransparent) continue; // block is blocked...
                    }

                    final int tx = (int) (block.textureIndex % atlasTX);
                    final int ty = (int) ((block.textureIndex - tx) / atlasTX);

                    final float u = tx * invW;
                    final float v = ty * invH;

                    final float x = i + xOffset;
                    final float y = j;
                    final float z = k;

                    Vertex v0 = new Vertex(x - 0.5f, y - 0.5f, z, u, v + invH);
                    Vertex v1 = new Vertex(x - 0.5f, y + 0.5f, z, u, v);
                    Vertex v2 = new Vertex(x + 0.5f, y + 0.5f, z, u + invW, v);
                    Vertex v3 = new Vertex(x + 0.5f, y - 0.5f, z, u + invW, v + invH);

                    vertices.add(v0);
                    vertices.add(v1);
                    vertices.add(v2);
                    vertices.add(v3);

                    indices.add(vertices.size() - 4); // 0
                    indices.add(vertices.size() - 3); // 1
                    indices.add(vertices.size() - 2); // 2
                    indices.add(vertices.size() - 2); // 2
                    indices.add(vertices.size() - 1); // 3
                    indices.add(vertices.size() - 4); // 0
                }
            }
        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(vertices.size() * Vertex.BYTES).order(ByteOrder.nativeOrder());
        for (Vertex vertex : vertices) {
            buffer.putFloat(vertex.x()).putFloat(vertex.y()).putFloat(vertex.z()).putFloat(vertex.u()).putFloat(vertex.v());
        }
        buffer.position(0);

        if (m_VertexBuffer == null)
            m_VertexBuffer = new GLBuffer().setTarget(GL_ARRAY_BUFFER).setUsage(GL_DYNAMIC_DRAW);
        m_VertexBuffer.bind().setData(buffer).unbind();

        buffer = ByteBuffer.allocateDirect(indices.size() * Integer.BYTES).order(ByteOrder.nativeOrder());
        for (int index : indices) {
            buffer.putInt(index);
        }
        buffer.position(0);

        if (m_IndexBuffer == null)
            m_IndexBuffer = new GLBuffer().setTarget(GL_ELEMENT_ARRAY_BUFFER).setUsage(GL_DYNAMIC_DRAW);
        m_IndexBuffer.bind().setData(buffer).unbind();

        if (m_VertexArray == null) m_VertexArray = new VertexArray();
        final VertexArray.Layout layout = new VertexArray.Layout().pushFloat(3).pushFloat(2);
        m_VertexArray.bind().bindBuffer(m_VertexBuffer, layout).unbind();

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
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Renderer.render(m_SkyboxVertexArray, m_SkyboxIndexBuffer, m_SkyboxMaterial);

        glEnable(GL_DEPTH_TEST);
        Renderer.render(m_VertexArray, m_IndexBuffer, m_Material);
        glDisable(GL_DEPTH_TEST);

        glDisable(GL_BLEND);

        m_ImGuiHelper.loopOnce(() -> {

            ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.PassthruCentralNode);

            ImGui.begin("Graphs");
            ImGui.beginTabBar("Tabs");

            for (int id = 0; id < m_GraphMap.size(); id++) {
                final GraphInfo info = m_GraphMap.get(id);

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
            }

            ImGui.endTabBar();
            ImGui.end();

            ImGui.showDemoWindow();
        });

        if (!ImGui.getIO().getWantCaptureKeyboard()) {
            final float speed = ImGui.getIO().getDeltaTime() * 20.0f;
            boolean move = false;

            if (m_Window.getKeyDown(GLFW_KEY_UP)) {
                m_CameraPosition.y += speed;
                move = true;
            }
            if (m_Window.getKeyDown(GLFW_KEY_DOWN)) {
                m_CameraPosition.y -= speed;
                move = true;
            }
            if (m_Window.getKeyDown(GLFW_KEY_RIGHT)) {
                m_CameraPosition.x += speed;
                move = true;
            }
            if (m_Window.getKeyDown(GLFW_KEY_LEFT)) {
                m_CameraPosition.x -= speed;
                move = true;
            }

            if (move) {
                Matrix4f view = new Matrix4f().translate(m_CameraPosition.x(), m_CameraPosition.y(), 0.0f).invert();
                m_Material.getShader().bind().setUniformFloatMat4("view", view.get(new float[16])).unbind();
            }
        }

        return m_Window.loopOnce();
    }

    /**
     * Destroys the window object, cleans up all the other resources, terminates GLFW and frees the error callback. Call this method after no longer using this graphics instance!
     */
    public void destroy() {
        m_Window.close(); // Destroy the window

        m_Atlas.close();
        m_ImGuiHelper.close();

        m_Material.close();
        m_VertexArray.close();
        m_IndexBuffer.close();
        m_VertexBuffer.close();

        m_SkyboxMaterial.close();
        m_SkyboxVertexArray.close();
        m_SkyboxIndexBuffer.close();
        m_SkyboxVertexBuffer.close();

        // Terminate GLFW
        glfwTerminate();

        // Free the error callback
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        // Check if it already has been freed
        if (callback != null) callback.free();

        __instance = null; // clear the singleton pointer
    }
}
