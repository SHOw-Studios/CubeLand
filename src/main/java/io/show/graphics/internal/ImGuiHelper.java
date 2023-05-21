package io.show.graphics.internal;

import imgui.ImFont;
import imgui.ImGui;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;

/**
 * @author Felix Schreiber
 */
public class ImGuiHelper implements AutoCloseable {

    private final ImGuiImplGlfw m_ImGuiImplGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 m_ImGuiImplGl3 = new ImGuiImplGl3();

    public ImGuiHelper(final long window, final String glslVersion) {
        ImGui.createContext();
        ImPlot.createContext();

        System.out.printf("ImGui %s%n", ImGui.getVersion());
        
        ImGui.styleColorsDark();
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable | ImGuiConfigFlags.DockingEnable | ImGuiConfigFlags.NavEnableKeyboard);

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

    public ImFont addFont(String path, float pixelSize) {
        return ImGui.getIO().getFonts().addFontFromFileTTF(path, pixelSize);
    }

    public ImFont setDefaultFont(String path, float pixelSize) {
        ImFont font = addFont(path, pixelSize);
        ImGui.getIO().setFontDefault(font);
        return font;
    }

    public void updateFonts() {
        m_ImGuiImplGl3.updateFontsTexture();
    }

    @Override
    public void close() {
        m_ImGuiImplGlfw.dispose();
        m_ImGuiImplGl3.dispose();

        ImPlot.destroyContext(ImPlot.getCurrentContext());
        ImGui.destroyContext();
    }

}
