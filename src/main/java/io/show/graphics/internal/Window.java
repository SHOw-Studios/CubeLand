package io.show.graphics.internal;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public static interface IListener {
        void call();
    }

    private final long m_Handle;

    private IListener m_ResizeListener;

    /**
     * Create a new Window object with GLFW and a fresh OpenGL context
     *
     * @param width  the window width
     * @param height the window height
     * @param title  the title of the window
     * @throws IllegalStateException failed to initialize GLFW
     * @throws RuntimeException      failed to create the GLFW window object
     */
    public Window(int width, int height, CharSequence title) {

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        m_Handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (m_Handle == NULL) throw new RuntimeException("Failed to create the GLFW window");

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(m_Handle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });
        glfwSetWindowSizeCallback(m_Handle, (window, w, h) -> {
            glViewport(0, 0, w, h);
            if (m_ResizeListener != null) m_ResizeListener.call();
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(m_Handle);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(m_Handle);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        System.out.println(glGetString(GL_VERSION));
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

    /**
     * Free all window callbacks and destroy the GLFW window object
     */
    public void destroy() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(m_Handle);
        glfwDestroyWindow(m_Handle);
    }
}
