package com.ywsuoyi.pixelloader.guiComponent;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class IntegerEditBox extends EditBox {
    int lastClick = 0;
    int frame = 0;
    int max, min;

    public IntegerEditBox(Font font, int i, int j, int k, int l, Component component, int max, int min) {
        super(font, i, j, k, l, component);
        this.setValue("0");
        this.max = max;
        this.min = min;
    }

    @Override
    public void setFocus(boolean bl) {
        super.setFocus(bl);
        try {
            int i = Integer.parseInt(this.getValue());
            i = Mth.clamp(i, min, max);
            this.setValue(String.valueOf(i));
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
    public void setFocused(boolean bl) {
        if (bl && canConsumeInput()) {
            if (frame - lastClick < 10)
                this.setValue("0");
            lastClick = frame;
        }
        super.setFocused(bl);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        if (canConsumeInput()) {
            try {
                int v = Integer.parseInt(this.getValue());
                v += (int) Math.round(f);
                v = Mth.clamp(v, min, max);
                this.setValue(String.valueOf(v));
            } catch (NumberFormatException ignored) {
                this.setValue("0");
            }
            return true;
        }
        return super.mouseScrolled(d, e, f);
    }

    @Override
    public void onDrag(double d, double e, double f, double g) {
        super.onDrag(d, e, f, g);
        mouseScrolled(d, e, f);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if ((c < '0' || c > '9') && c != '-') {
            return false;
        }
        return super.charTyped(c, i);
    }
}
