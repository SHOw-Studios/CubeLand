package io.show.graphics.internal;

import io.show.graphics.Bitmap;
import io.show.storage.Storage;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.IOException;

import static io.show.graphics.internal.Debug.disableDebug;
import static io.show.graphics.internal.Debug.enableDebug;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Felix Schreiber
 */
public class Window implements AutoCloseable {

    public interface IListener {
        void call();
    }

    public record Rect(int x, int y, int width, int height) {
    }

    public enum Mode {
        WINDOWED, FULLSCREEN, FULLSCREEN_WINDOWED
    }

    private final long m_Handle;
    private IListener m_ResizeListener;
    private Rect m_PreviousState;
    private Mode m_CurrentMode;

    private boolean m_LeftShiftPressed = false, m_RightShiftPressed = false;

    /**
     * Create a new Window object with GLFW and a fresh OpenGL context
     *
     * @param width  the window width
     * @param height the window height
     * @param title  the title of the window
     * @throws IllegalStateException failed to initialize GLFW
     * @throws RuntimeException      failed to create the GLFW window object
     */
    public Window(int width, int height, CharSequence title, String iconPath) {

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE); // make the window maximized on startup
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4); // set the opengl context
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6); // version to 4.6.0
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // set the opengl profile to core
        // glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE); // just for fun: make the window background transparent... :)

        m_Handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (m_Handle == NULL) throw new RuntimeException("Failed to create the GLFW window");

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(m_Handle, (window, key, scancode, action, mods) -> {
            switch (key) {
                case GLFW_KEY_ESCAPE:
                    if (action == GLFW_RELEASE)
                        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                    break;

                case GLFW_KEY_F11:
                    if (action == GLFW_RELEASE)
                        toggleFullscreenMode(m_LeftShiftPressed || m_RightShiftPressed); // toggle fullscreen mode if f11 key is pressed
                    break;

                case GLFW_KEY_LEFT_SHIFT:
                    m_LeftShiftPressed = action == GLFW_PRESS || action == GLFW_REPEAT;
                    break;

                case GLFW_KEY_RIGHT_SHIFT:
                    m_RightShiftPressed = action == GLFW_PRESS || action == GLFW_REPEAT;
                    break;
            }
        });
        // Set up a resize callback, it will be called every time the window size changes
        glfwSetWindowSizeCallback(m_Handle, (window, w, h) -> {
            glViewport(0, 0, w, h);
            if (m_ResizeListener != null) m_ResizeListener.call();
        });

        try {
            Bitmap bitmap = new Bitmap(Storage.readImage(iconPath));

            GLFWImage.Buffer iconImage = GLFWImage.create(1);
            iconImage.width(bitmap.getWidth());
            iconImage.height(bitmap.getHeight());
            iconImage.pixels(bitmap.getByteBuffer());
            glfwSetWindowIcon(m_Handle, iconImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(m_Handle);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(m_Handle);

        m_CurrentMode = Mode.WINDOWED;

        // OpenGL Stuff //

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // print the current opengl version
        System.out.println("Hello from OpenGL version " + glGetString(GL_VERSION));

        // set the background clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // enable debug messages from opengl api (TJesus)
        enableDebug();
    }

    /**
     * Retrieve the windows GLFW window object handle
     *
     * @return GLFW window handle
     */
    public final long getHandle() {
        return m_Handle;
    }

    /**
     * Executes the window loop once:
     * <p>
     * 1. clear the framebuffer
     * <p>
     * 2. swap the framebuffer
     * <p>
     * 3. poll for window events
     *
     * @return <b>true</b> while the window is open
     */
    public boolean loopOnce() {

        // glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        glfwSwapBuffers(m_Handle); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        return !glfwWindowShouldClose(m_Handle);
    }

    public Window setResizeListener(IListener listener) {
        m_ResizeListener = listener;
        return this;
    }

    public Window toggleFullscreenMode(boolean fullscreenWindowed) {

        if (m_CurrentMode == Mode.WINDOWED) { // make it fullscreen
            long monitor = getCurrentMonitor(); // get suitable monitor
            if (monitor == NULL) throw new NullPointerException("monitor is null; might be a bug");

            GLFWVidMode vidMode = glfwGetVideoMode(monitor); // get monitor state
            if (vidMode == null) throw new NullPointerException("vidMode is null; might be a bug");

            int[] mx = new int[1], my = new int[1];
            glfwGetMonitorPos(monitor, mx, my); // get monitor position

            m_PreviousState = getCurrentState(); // save current state

            if (fullscreenWindowed) { // make it only frameless
                glfwSetWindowAttrib(m_Handle, GLFW_DECORATED, GLFW_FALSE);
                glfwSetWindowMonitor(m_Handle, NULL, mx[0], my[0], vidMode.width(), vidMode.height(), vidMode.refreshRate()); // apply monitor mode
                m_CurrentMode = Mode.FULLSCREEN_WINDOWED; // update mode
            } else { // complete fullscreen please
                glfwSetWindowMonitor(m_Handle, monitor, mx[0], my[0], vidMode.width(), vidMode.height(), vidMode.refreshRate()); // apply monitor mode
                m_CurrentMode = Mode.FULLSCREEN; // update mode
            }
        } else { // make it windowed
            if (m_PreviousState == null) throw new NullPointerException("previous state is null; might be a bug");

            if (m_CurrentMode == Mode.FULLSCREEN_WINDOWED) glfwSetWindowAttrib(m_Handle, GLFW_DECORATED, GLFW_TRUE);

            glfwSetWindowMonitor(m_Handle, NULL, m_PreviousState.x(), m_PreviousState.y(), m_PreviousState.width(), m_PreviousState.height(), GLFW_DONT_CARE); // remove the monitor handle, restore old state

            m_CurrentMode = Mode.WINDOWED; // update mode
        }

        return this;
    }

    public int getWidth() {
        int[] width = new int[1];
        glfwGetWindowSize(m_Handle, width, new int[1]);
        return width[0];
    }

    public int getHeight() {
        int[] height = new int[1];
        glfwGetWindowSize(m_Handle, new int[1], height);
        return height[0];
    }

    public Rect getCurrentState() {
        int[] wxa = new int[1], wya = new int[1], wwa = new int[1], wha = new int[1];
        glfwGetWindowPos(m_Handle, wxa, wya);
        glfwGetWindowSize(m_Handle, wwa, wha);
        return new Rect(wxa[0], wya[0], wwa[0], wha[0]);
    }

    public long getCurrentMonitor() {
        Rect state;
        int mx, my, mw, mh;
        int overlap, bestoverlap;
        long bestmonitor;
        PointerBuffer monitors;
        GLFWVidMode mode;

        bestoverlap = 0;
        bestmonitor = NULL;

        state = getCurrentState();

        monitors = glfwGetMonitors();
        if (monitors == null) throw new NullPointerException("monitors is null; might be a bug");

        while (monitors.remaining() > 0) {
            long monitor = monitors.get();

            mode = glfwGetVideoMode(monitor);
            if (mode == null) throw new NullPointerException("mode is null; might be a bug");

            int[] mxa = new int[1], mya = new int[1];
            glfwGetMonitorPos(monitor, mxa, mya);
            mx = mxa[0];
            my = mya[0];

            mw = mode.width();
            mh = mode.height();

            overlap = Math.max(0, Math.min(state.x() + state.width(), mx + mw) - Math.max(state.x(), mx)) * Math.max(0, Math.min(state.y() + state.height(), my + mh) - Math.max(state.y(), my));

            if (bestoverlap < overlap) {
                bestoverlap = overlap;
                bestmonitor = monitor;
            }
        }

        return bestmonitor;
    }

    public boolean getKeyDown(int code) {
        return glfwGetKey(m_Handle, code) == GLFW_PRESS;
    }

    public boolean getMouseButtonDown(int button) {
        return glfwGetMouseButton(m_Handle, button) == GLFW_PRESS;
    }

    @Override
    public void close() {
        // FreeJesus...
        disableDebug();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(m_Handle);
        glfwDestroyWindow(m_Handle);
    }
}
