package com.ywsuoyi.pixelloader;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NumberEditBox extends EditBox {

    int lastClick = 0;
    int frame = 0;

    public NumberEditBox(Font font, int i, int j, int k, int l, Component component) {
        super(font, i, j, k, l, component);
        this.setValue("0");
    }

    @Override
    public void setFocus(boolean bl) {
        super.setFocus(bl);
        try {
            Double.parseDouble(this.getValue());
        } catch (NumberFormatException exception) {
            this.setValue("0");
        }
    }

    @Override
    public void tick() {
        frame++;
        super.tick();
    }

    @Override
    protected void setFocused(boolean bl) {
        if (bl) {
            if (frame - lastClick < 10)
                this.setValue("0");
            lastClick = frame;
        }
        super.setFocused(bl);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        try {
            double v = Double.parseDouble(this.getValue());
            v += Screen.hasShiftDown() ? f * 0.1 : f;
            v = Math.round(v * 1000) / 1000.0;
            this.setValue(String.valueOf(v));
        } catch (NumberFormatException ignored) {
            this.setValue("0");
        }
        return true;
    }

    @Override
    protected void onDrag(double d, double e, double f, double g) {
        super.onDrag(d, e, f, g);
        mouseScrolled(d, e, f);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if ((c < '0' || c > '9') && c != '.' && c != '-') {
            return false;
        }
        return super.charTyped(c, i);
    }
}
