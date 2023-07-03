package io.show.graphics;

import imgui.ImGui;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.type.ImBoolean;
import io.show.graphics.internal.ImGuiHelper;
import io.show.graphics.internal.Window;
import io.show.graphics.internal.gl.Shader;
import io.show.graphics.internal.gl.TextureAtlas;
import io.show.graphics.internal.gl.VertexArray;
import io.show.graphics.internal.scene.Material;
import io.show.graphics.internal.scene.Mesh;
import io.show.graphics.internal.scene.Model;
import io.show.graphics.internal.scene.QuadMesh;
import io.show.storage.Storage;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.json.JSONObject;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

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

    private static void regBitmaps(JSONObject json) throws IOException {
        if (json.has("path")) {
            float opacity;
            if (json.has("opacity")) opacity = json.getFloat("opacity");
            else opacity = 1.0f;
            Graphics.getInstance().registerBitmap(new Bitmap(json.getString("path"), opacity));
            return;
        }

        for (String key : json.keySet()) {
            regBitmaps(json.getJSONObject(key));
        }
    }

    public static void main(String[] args) {
        final Graphics g = Graphics.getInstance();

        try {
            JSONObject block = Storage.readJson("res/textures/textures.json").getJSONObject("block");
            regBitmaps(block);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        g.generateTextureAtlas(16, 16);

        while (true) if (!g.loopOnce()) break;

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
    private TextureAtlas m_TerrainAtlas;
    private TextureAtlas m_PlayerAtlas;

    private final Model m_TerrainModel;
    private final Model m_SkyboxModel;
    private final Model m_PlayerModel;

    private final List<GraphInfo> m_GraphInfoList = new Vector<>();
    private final ImGuiHelper m_ImGuiHelper;

    private boolean m_Moved = false;

    private final Vector2f m_CameraPosition = new Vector2f(0.0f, 0.0f);
    private final List<BlockType> m_BlockTypeList = new Vector<>();

    private final Player m_Player = new Player();

    private final ImBoolean m_ShowSettingsWindow = new ImBoolean(false);
    private final ImBoolean m_ShowDemoWindow = new ImBoolean(false);
    private final ImBoolean m_ShowGraphWindow = new ImBoolean(false);
    private final ImBoolean m_ShowPlayerAtlasWindow = new ImBoolean(false);
    private final ImBoolean m_ShowTerrainAtlasWindow = new ImBoolean(false);
    private final ImBoolean m_ShowDebugInfoWindow = new ImBoolean(false);

    private final LogWindow m_DebugInfoWindow = new LogWindow();

    /**
     * Initializes GLFW, creates a window and sets up some other things like ImGui, the main materials and preps some drawing data
     */
    private Graphics() {

        // Print out the currently used LWJGL version
        System.out.println("Hello from LWJGL version " + Version.getVersion());
        m_DebugInfoWindow.logf("Hello from LWJGL version %s%n", Version.getVersion());

        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        System.out.println("Hello from GLFW version " + glfwGetVersionString());
        m_DebugInfoWindow.logf("Hello from GLFW version %s%n", glfwGetVersionString());

        // Window //

        m_Window = new Window(800, 600, "CubeLand v1.0.0", "res/textures/block/panel/wood_panel.bmp");
        m_Window.setResizeListener(this::onWindowResize);

        // Models //

        Shader terrainShader;
        Shader skyboxShader;
        Shader playerShader;

        try {
            terrainShader = new Shader("res/shaders/block/opaque.shader");
            skyboxShader = new Shader("res/shaders/misc/skybox.shader");
            playerShader = new Shader("res/shaders/player.shader");
        } catch (Shader.CompileStatusException | Shader.LinkStatusException | Shader.ValidateStatusException |
                 IOException e) {
            throw new RuntimeException(e);
        }

        m_TerrainModel = new Model(new Mesh(), new Material(terrainShader));
        m_PlayerModel = new Model(new Mesh(), new Material(playerShader));
        m_SkyboxModel = new Model(new QuadMesh(), new Material(skyboxShader));

        Matrix4f view = new Matrix4f().translate(m_CameraPosition.x(), m_CameraPosition.y(), 0.0f).invert();
        terrainShader.bind().setUniformFloatMat4("view", view.get(new float[16])).unbind();
        playerShader.bind().setUniformFloatMat4("view", view.get(new float[16])).unbind();

        m_PlayerModel.getMesh().setVertices(new float[]{0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1}).setIndices(new int[]{0, 1, 2, 2, 3, 0}).setVertexLayout(new VertexArray.Layout().pushFloat(3).pushFloat(2));

        onWindowResize(); // Set up the orthographic matrix for the first time

        // ImGui //

        m_ImGuiHelper = new ImGuiHelper(m_Window.getHandle(), "#version 460 core");
        m_ImGuiHelper.setDefaultFont("res/fonts/Gothic3.ttf", 16.0f);
        m_ImGuiHelper.addFont("res/fonts/Bladeline-oXRa.ttf", 10.0f);
        m_ImGuiHelper.addFont("res/fonts/Evidence-M53Y.ttf", 18.0f);
        m_ImGuiHelper.addFont("res/fonts/Oasis-BW0JV.ttf", 16.0f);
        m_ImGuiHelper.addFont("res/fonts/CursedTimerUlil-Aznm.ttf", 14.0f);
        m_ImGuiHelper.updateFonts();

        // Player animations //
        try {
            registerPlayerAnimations();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerPlayerAnimations() throws IOException {
        JSONObject player = Storage.readJson("res/textures/textures.json").getJSONObject("player");

        int frame_width = player.getInt("frame_width");
        int frame_height = player.getInt("frame_height");

        System.out.printf("frame_width: %d, frame_height: %d%n", frame_width, frame_height);
        m_DebugInfoWindow.logf("frame_width: %d, frame_height: %d%n", frame_width, frame_height);

        JSONObject animations = player.getJSONObject("animations");

        List<Bitmap> bitmaps = new Vector<>();
        int w = 0, h = 0;

        List<String> anim = animations.keySet().stream().sorted().toList();

        for (String key : anim) {
            String path = animations.getJSONObject(key).getString("path");

            System.out.println(path);
            m_DebugInfoWindow.logf("animation at %s%n", path);

            Bitmap bitmap = new Bitmap(Storage.readImage(path));

            w = Math.max(w, bitmap.getWidth());
            h += frame_height;

            bitmaps.add(bitmap);
        }

        final int nx = w / frame_width;
        final int ny = h / frame_height;
        final TextureAtlas atlas = new TextureAtlas(frame_width, frame_height, nx, ny, 0x00000000).bind();

        int y = 0;
        for (final Bitmap bitmap : bitmaps) {
            final int bw = bitmap.getWidth();

            ByteBuffer buffer = bitmaps.get(y).getByteBuffer();
            atlas.setTiles(0, y++, bw / frame_width, buffer);
        }

        m_PlayerAtlas = atlas.unbind();

        m_PlayerModel.getMaterial().clearTextures();
        m_PlayerModel.getMaterial().addTexture(m_PlayerAtlas.getTexture());

        m_PlayerModel.getMaterial().getShader().bind()

                .setUniformInt("sampler", 0)

                .setUniformInt("atlas_width", w)

                .setUniformInt("atlas_height", h)

                .setUniformInt("frame_width", frame_width)

                .setUniformInt("frame_height", frame_height)

                .unbind();
    }

    /**
     * This method gets called every time the window resizes; whenever this happens it updates the projection matrix in the shader program to its new size
     */
    private void onWindowResize() {
        final float w = m_Window.getWidth();
        final float h = m_Window.getHeight();
        final float a = w / h;
        final float scale = 8.0f;
        Matrix4f mat = new Matrix4f().setOrtho2D(-a * scale, a * scale, -scale, scale);
        m_TerrainModel.getMaterial().getShader().bind().setUniformFloatMat4("projection", mat.get(new float[16])).unbind();
        m_PlayerModel.getMaterial().getShader().bind().setUniformFloatMat4("projection", mat.get(new float[16])).unbind();
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
        if (m_GraphInfoList.contains(info)) return m_GraphInfoList.indexOf(info);
        m_GraphInfoList.add(info);
        return m_GraphInfoList.size() - 1;
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
        if (m_GraphInfoList.contains(info)) return m_GraphInfoList.indexOf(info);
        m_GraphInfoList.add(info);
        return m_GraphInfoList.size() - 1;
    }

    public int registerBlockType(BlockType blockType) {
        if (m_BlockTypeList.contains(blockType)) return m_BlockTypeList.indexOf(blockType);
        m_BlockTypeList.add(blockType);
        return m_BlockTypeList.size() - 1;
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

            byte[] data = m_BitmapMap.get(i).getByteArray();

            atlas.setTile(x, y, buffer.clear().put(data).position(0));
        }

        m_TerrainAtlas = atlas.unbind();

        m_TerrainModel.getMaterial().clearTextures();
        m_TerrainModel.getMaterial().addTexture(m_TerrainAtlas.getTexture());

        m_TerrainModel.getMaterial().getShader().bind().setUniformInt("sampler", 0).unbind();

        return this;
    }

    public Graphics generateWorldMesh(int[][][] world, int xOffset, int width, int height, int depth) {

        final int atlasW = m_TerrainAtlas.getWidth();
        final int atlasH = m_TerrainAtlas.getHeight();
        final int atlasTW = m_TerrainAtlas.getTileW();
        final int atlasTH = m_TerrainAtlas.getTileH();
        final int atlasTX = m_TerrainAtlas.getTilesX();

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

                    final BlockType block = m_BlockTypeList.get(world[k][j][i]);
                    if (block == null) continue; // air

                    if (k < depth - 1) {
                        final BlockType front = m_BlockTypeList.get(world[k + 1][j][i]);
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
        m_TerrainModel.getMesh().setVertices(buffer);

        buffer = ByteBuffer.allocateDirect(indices.size() * Integer.BYTES).order(ByteOrder.nativeOrder());
        for (int index : indices) {
            buffer.putInt(index);
        }
        buffer.position(0);
        m_TerrainModel.getMesh().setIndices(buffer);

        final VertexArray.Layout layout = new VertexArray.Layout().pushFloat(3).pushFloat(2);
        m_TerrainModel.getMesh().setVertexLayout(layout);

        return this;
    }

    /**
     * @return the window object
     */
    public Window getWindow() {
        return m_Window;
    }

    /**
     * @return the texture atlas
     */
    public TextureAtlas getAtlas() {
        return m_TerrainAtlas;
    }

    public Vector2f getCameraPosition() {
        return m_CameraPosition;
    }

    public Vector2f getPlayerPosition() {
        return m_Player.getPosition();
    }

    public int getPlayerLayer() {
        return m_Player.getLayer();
    }

    public Graphics moveCamera(Vector2f translation) {
        m_CameraPosition.add(translation);
        m_Moved = true;
        return this;
    }

    public Graphics setCameraPosition(Vector2f position) {
        m_CameraPosition.set(position);
        m_Moved = true;
        return this;
    }

    public Graphics movePlayer(Vector2f translation) {
        m_Player.getPosition().add(translation);
        m_Moved = true;
        return this;
    }

    public Graphics setPlayerPosition(Vector2f position) {
        m_Player.getPosition().set(position);
        m_Moved = true;
        return this;
    }

    public Graphics setPlayerLayer(int layer) {
        m_Player.setLayer(layer);
        m_Moved = true;
        return this;
    }

    public Graphics updateCamera() {
        Matrix4f view = new Matrix4f().setTranslation(-m_CameraPosition.x(), -m_CameraPosition.y(), 0.0f);
        m_TerrainModel.getMaterial().getShader().bind().setUniformFloatMat4("view", view.get(new float[16])).unbind();
        m_PlayerModel.getMaterial().getShader().bind().setUniformFloatMat4("view", view.get(new float[16])).unbind();
        return this;
    }

    public Graphics updatePlayer() {
        float sy = 3.0f;
        float sx = sy * 120.0f / 80.0f;
        if (m_Player.isLookingLeft()) sx = -sx;
        Matrix4f model = new Matrix4f().scale(sx, sy, 1.0f).translate(m_Player.getPosition().x() / sx - 0.5f, m_Player.getPosition().y() / sy - 0.5f, m_Player.getLayer() - 0.5f);
        m_PlayerModel.getMaterial().getShader().bind().setUniformFloatMat4("model", model.get(new float[16])).unbind();
        return this;
    }

    public Player getPlayer() {
        return m_Player;
    }

    public LogWindow getDebugInfoWindow() {
        return m_DebugInfoWindow;
    }

    private float m_Tick = 0;

    private void updateAnimation() {
        m_Tick += ImGui.getIO().getDeltaTime();
        if (m_Tick > 0.05f) {
            m_Tick = 0;
            m_Player.nextFrame();
        }

        m_PlayerModel.getMaterial().getShader().bind()

                .setUniformInt("animation", m_Player.getCurrentAnimation().index())

                .setUniformInt("frame", m_Player.getCurrentFrame())

                .unbind();
    }

    private void updateGUI() {
        m_ImGuiHelper.loopOnce(() -> {

            if (Input.canUseKeyboard())
                if (Input.getKeyRelease(Input.KeyCode.O)) m_ShowSettingsWindow.set(!m_ShowSettingsWindow.get());

            ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.PassthruCentralNode);

            if (m_ShowSettingsWindow.get() && ImGui.begin("Settings", m_ShowSettingsWindow)) {
                ImGui.checkbox("Show Demo", m_ShowDemoWindow);
                ImGui.checkbox("Show Graphs", m_ShowGraphWindow);
                ImGui.checkbox("Show Terrain Atlas", m_ShowTerrainAtlasWindow);
                ImGui.checkbox("Show Player Atlas", m_ShowPlayerAtlasWindow);
                ImGui.checkbox("Show Debug Info", m_ShowDebugInfoWindow);

                ImGui.end();
            }

            if (m_ShowDemoWindow.get()) ImGui.showDemoWindow(m_ShowDemoWindow);

            if (m_ShowGraphWindow.get() && ImGui.begin("Graphs", m_ShowGraphWindow)) {

                ImGui.beginTabBar("Tabs");

                for (int id = 0; id < m_GraphInfoList.size(); id++) {
                    final GraphInfo info = m_GraphInfoList.get(id);

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
            }

            if (m_ShowTerrainAtlasWindow.get() && ImGui.begin("Terrain Atlas", m_ShowTerrainAtlasWindow)) {

                float a = m_TerrainAtlas.getWidth() / (float) m_TerrainAtlas.getHeight();

                float ww = ImGui.getContentRegionAvailX();
                float wh = ImGui.getContentRegionAvailY();

                int w = 0;
                int h = 0;

                if (ww < wh) {
                    w = (int) ww;
                    h = (int) (ww / a);
                } else {
                    w = (int) (wh * a);
                    h = (int) wh;
                }

                ImGui.image(m_TerrainAtlas.getTexture().getHandle(), w, h);

                ImGui.end();
            }

            if (m_ShowPlayerAtlasWindow.get() && ImGui.begin("Player Atlas", m_ShowPlayerAtlasWindow)) {

                float a = m_PlayerAtlas.getWidth() / (float) m_PlayerAtlas.getHeight();

                float ww = ImGui.getContentRegionAvailX();
                float wh = ImGui.getContentRegionAvailY();

                int w = 0;
                int h = 0;

                if (ww < wh) {
                    w = (int) ww;
                    h = (int) (ww / a);
                } else {
                    w = (int) (wh * a);
                    h = (int) wh;
                }

                ImGui.image(m_PlayerAtlas.getTexture().getHandle(), w, h);

                ImGui.end();
            }

            if (m_ShowDebugInfoWindow.get()) m_DebugInfoWindow.draw("Debug Info", m_ShowDebugInfoWindow);
        });
    }

    /**
     * The rendering loop: call it from your main loop from the main thread. Every time this loop is called it will clear the screen, render the scene and the gui and then return whether to close the application
     *
     * @return true while the window has not been closed
     */
    public boolean loopOnce() {

        // Update camera and player if necessary
        if (m_Moved) {
            updateCamera();
            updatePlayer();
            m_Moved = false;
        }

        updateAnimation();

        // Update Input System
        Input.loopOnce();

        // Set clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Clear Screen
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Enable Blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Render skybox
        m_SkyboxModel.render();

        // Enable Depth Testing
        glEnable(GL_DEPTH_TEST);

        // Render player
        m_PlayerModel.render();

        // Render terrain
        m_TerrainModel.render();

        // Disable Depth Testing and Blending
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        // Do ImGui
        updateGUI();

        // Check if window is still open
        return m_Window.loopOnce();
    }

    /**
     * Destroys the window object, cleans up all the other resources, terminates GLFW and frees the error callback. Call this method after no longer using this graphics instance!
     */
    public void destroy() {
        m_Window.close(); // Destroy the window

        if (m_TerrainAtlas != null) m_TerrainAtlas.close();
        if (m_PlayerAtlas != null) m_PlayerAtlas.close();

        m_ImGuiHelper.close();

        m_TerrainModel.close();
        m_SkyboxModel.close();
        m_PlayerModel.close();

        // Terminate GLFW
        glfwTerminate();

        // Free the error callback
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        // Check if it already has been freed
        if (callback != null) callback.free();

        __instance = null; // clear the singleton pointer
    }
}
