package io.show.graphics.internal;

import imgui.ImGui;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;

public class ImGuiHelper implements AutoCloseable {

    private final ImGuiImplGlfw m_ImGuiImplGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 m_ImGuiImplGl3 = new ImGuiImplGl3();

    public ImGuiHelper(final long window, final String glslVersion) {
        ImGui.createContext();
        ImPlot.createContext();

        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable | ImGuiConfigFlags.DockingEnable);

        m_ImGuiImplGlfw.init(window, true);
        m_ImGuiImplGl3.init(glslVersion);
    }

    public void loopOnce(final Runnable imGuiFunc) {
        m_ImGuiImplGlfw.newFrame();
        ImGui.newFrame();

        imGuiFunc.run();

        ImGui.render();
        m_ImGuiImplGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupPtr);
        }
    }

    @Override
    public void close() {
        m_ImGuiImplGlfw.dispose();
        m_ImGuiImplGl3.dispose();

        ImPlot.destroyContext(ImPlot.getCurrentContext());
        ImGui.destroyContext();
    }

}
