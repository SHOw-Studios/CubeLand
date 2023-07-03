package io.show.graphics;

import imgui.ImGui;
import imgui.ImGuiTextFilter;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImBoolean;

import java.util.List;
import java.util.Vector;

public class LogWindow {

    private final ImGuiTextFilter m_Filter = new ImGuiTextFilter();
    private final StringBuffer m_Buffer = new StringBuffer();
    private final List<Integer> m_LineOffsets = new Vector<>();
    private boolean m_ScrollToBottom = false;

    public LogWindow clear() {
        m_Buffer.delete(0, m_Buffer.length());
        m_LineOffsets.clear();
        return this;
    }

    public LogWindow logf(String format, Object... params) {
        int oldSize = m_Buffer.length();
        m_Buffer.append(String.format(format, params));
        for (int newSize = m_Buffer.length(); oldSize < newSize; oldSize++) {
            if (m_Buffer.charAt(oldSize) == '\n') m_LineOffsets.add(oldSize);
        }
        m_ScrollToBottom = true;

        return this;
    }

    public void draw(String title, ImBoolean pOpen) {

        ImGui.setNextWindowSize(500, 400, ImGuiCond.FirstUseEver);
        ImGui.begin(title, pOpen);
        if (ImGui.button("Clear")) clear();
        ImGui.sameLine();
        boolean copy = ImGui.button("Copy");
        ImGui.sameLine();
        m_Filter.draw("Filter", -100.0f);
        ImGui.separator();
        ImGui.beginChild("scrolling");
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        if (copy) ImGui.logToClipboard();

        if (m_Filter.isActive()) {
            int line = 0;

            for (int line_no = 0; line_no < m_LineOffsets.size(); line_no++) {
                int lineEnd = m_LineOffsets.get(line_no);
                if (m_Filter.passFilter(m_Buffer.substring(line, lineEnd)))
                    ImGui.textUnformatted(m_Buffer.substring(line, lineEnd));
                line = lineEnd + 1;
            }
        } else {
            ImGui.textUnformatted(m_Buffer.toString());
        }

        if (m_ScrollToBottom) ImGui.setScrollHereY(1.0f);
        m_ScrollToBottom = false;

        ImGui.popStyleVar();
        ImGui.endChild();
        ImGui.end();
    }
}
