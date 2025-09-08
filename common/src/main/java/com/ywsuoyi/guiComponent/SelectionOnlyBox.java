package com.ywsuoyi.guiComponent;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SelectionOnlyBox extends EditBoxWithOptions {
    public SelectionOnlyBox(Font font, int x, int y, int width, int height, Component component, List<String> options) {
        super(font, x, y, width, height, component, options);
        if (!options.isEmpty()) {
            setValue(options.getFirst());
        }
        this.setEditable(false);
        this.setBordered(false);
        this.setMaxLength(100);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染半透明黑色背景
        int backgroundColor = 0x80000000; // 半透明黑色
        int m = this.getX() + 4;
        int n = this.getY() + (this.height - 8) / 2;
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, backgroundColor);
        if (!showOptions)
            guiGraphics.drawString(font, Component.literal(this.getValue()), m, n, color(false));
        // 渲染选项列表
        renderOptions(guiGraphics);
    }
}