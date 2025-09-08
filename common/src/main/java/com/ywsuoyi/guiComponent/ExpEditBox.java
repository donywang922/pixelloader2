package com.ywsuoyi.guiComponent;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class ExpEditBox extends EditBox {
    public ExpEditBox(Font font, int i, int j, int k, int l, Component component) {
        super(font, i, j, k, l, component);
        this.setMaxLength(100);
    }
}
