package com.ywsuoyi.guiComponent;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NumberEditBox extends EditBox {

    long lastClick = 0;

    public NumberEditBox(Font font, int i, int j, int k, int l, Component component) {
        super(font, i, j, k, l, component);
        this.setValue("0");
    }

    @Override
    public void setFocused(boolean bl) {
        if (bl && canConsumeInput()) {
            if (Util.getMillis() - lastClick < 500)
                this.setValue("0");
            lastClick = Util.getMillis();
        }
        if (!bl) {
            try {
                Double.parseDouble(this.getValue());
            } catch (NumberFormatException exception) {
                this.setValue("0");
            }
        }
        super.setFocused(bl);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        if (canConsumeInput()) {
            try {
                double v = Double.parseDouble(this.getValue());
                v += Screen.hasShiftDown() ? g * 0.1 : g;
                v = Math.round(v * 1000) / 1000.0;
                this.setValue(String.valueOf(v));
            } catch (NumberFormatException ignored) {
                this.setValue("0");
            }
            return true;
        }
        return super.mouseScrolled(d, e, f, g);
    }

    @Override
    protected void onDrag(double d, double e, double f, double g) {
        super.onDrag(d, e, f, g);
        mouseScrolled(d, e, g, f);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if ((c < '0' || c > '9') && c != '.' && c != '-') {
            return false;
        }
        return super.charTyped(c, i);
    }
}
