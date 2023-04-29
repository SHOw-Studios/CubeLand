package io.show.graphics.internal;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private final long mWindow;

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

        mWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if (mWindow == NULL) throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(mWindow, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(mWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(mWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        System.out.println(glGetString(GL_VERSION));

        // Set the clear color to pure red
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Retrieve the windows GLFW window object handle
     *
     * @return GLFW window handle
     */
    public final long getHandle() {
        return mWindow;
    }

    /**
     * Executes the window loop once:<br>
     * 1. clear the framebuffer<br>
     * 2. swap the framebuffer<br>
     * 3. poll for window events<br>
     *
     * @return <b>true</b> while the window is open
     */
    public boolean loopOnce() {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        glfwSwapBuffers(mWindow); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        return !glfwWindowShouldClose(mWindow);
    }

    /**
     * Free all window callbacks and destroy the GLFW window object
     */
    public void destroy() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(mWindow);
        glfwDestroyWindow(mWindow);
    }
}
