package com.ywsuoyi.guiComponent;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SelectionEditBox extends EditBoxWithOptions {
    public SelectionEditBox(Font font, int x, int y, int width, int height, Component component, List<String> suggestions) {
        super(font, x, y, width, height, component, suggestions);
    }

    @Override
    public boolean filter(String s) {
        return s.toLowerCase().startsWith(this.getValue().toLowerCase());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染原始编辑框
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        // 渲染建议列表
        renderOptions(guiGraphics);
    }
}