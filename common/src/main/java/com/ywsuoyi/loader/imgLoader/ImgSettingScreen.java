package com.ywsuoyi.loader.imgLoader;

import com.ywsuoyi.Selections;
import com.ywsuoyi.loader.SettingScreen;
import com.ywsuoyi.guiComponent.ExpEditBox;
import com.ywsuoyi.guiComponent.IntegerEditBox;
import com.ywsuoyi.guiComponent.NumberEditBox;
import com.ywsuoyi.guiComponent.SelectionOnlyBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ImgSettingScreen extends SettingScreen {
    public IntegerEditBox widthEditBox, heightEditBox;
    public NumberEditBox scaleEditBox;
    public SelectionOnlyBox preset, onfinish;
    public ExpEditBox x, y, z;
    public int w, hintWidth;
    public Component hx, hy, hz;

    @Override
    public void init() {
        super.init();
        dither.setOptionIndex(ImgSetting.dither);
        support.setOptionIndex(ImgSetting.support);
        cutout.setOptionIndex(ImgSetting.cutout);
        imgFile.setOptionIndex(ImgSetting.index);
        cover.setOptionIndex(ImgSetting.cover);
        imgFile.setResponder(s -> {
            ImgSetting.index = imgFile.getOptionIndex();
            reload();
        });

        w = (width - 40 - 4 * 3) / 4;
        widthEditBox = addRenderableWidget(new IntegerEditBox(font, 20, 80, w, 20,
                Component.translatable("pixelLoader.setting.screen.pixel.width"), 16384, 1));
        widthEditBox.setValue(String.valueOf(ImgSetting.genWidth));

        widthEditBox.setResponder(s -> {
            if (!widthEditBox.isFocused()) return;
            ImgSetting.keep = ImgSetting.Keep.width;
            recalc();
        });
        heightEditBox = addRenderableWidget(new IntegerEditBox(font, 20 + w + 4, 80, w, 20,
                Component.translatable("pixelLoader.setting.screen.pixel.height"), 16384, 1));
        heightEditBox.setValue(String.valueOf(ImgSetting.genHeight));
        heightEditBox.setResponder(s -> {
            if (!heightEditBox.isFocused()) return;
            ImgSetting.keep = ImgSetting.Keep.height;
            recalc();
        });
        scaleEditBox = addRenderableWidget(new NumberEditBox(font, 20 + w * 2 + 8, 80, w, 20,
                Component.translatable("pixelLoader.setting.screen.pixel.scale")));
        scaleEditBox.setValue(String.valueOf(ImgSetting.genScale * 100));
        scaleEditBox.setResponder(s -> {
            if (!scaleEditBox.isFocused()) return;
            ImgSetting.keep = ImgSetting.Keep.scale;
            recalc();
        });

        preset = addRenderableWidget(new SelectionOnlyBox(font, 20, 106, w * 2 + 4, 20,
                Component.translatable("pixelLoader.setting.screen.pixel.preset"), Selections.preset));
        preset.setOptionIndex(ImgSetting.preset);
        preset.setResponder(this::loadPreset);

        onfinish = addRenderableWidget(new SelectionOnlyBox(font, 20 + w * 2 + 8, 106, w * 2 + 4, 20,
                Component.translatable("pixelLoader.setting.screen.onfinish"), Selections.onfinish));
        onfinish.setOptionIndex(ImgSetting.onfinish);

        hx = Component.translatable("pixelLoader.setting.screen.pixel.xhint");
        hy = Component.translatable("pixelLoader.setting.screen.pixel.yhint");
        hz = Component.translatable("pixelLoader.setting.screen.pixel.zhint");
        hintWidth = Math.max(font.width(hx.getVisualOrderText()), font.width(hy.getVisualOrderText()));
        hintWidth = Math.max(hintWidth, font.width(hz.getVisualOrderText())) + 4;

        x = addRenderableWidget(new ExpEditBox(font, 20 + hintWidth, 132, width - 40 - hintWidth, 20,
                Component.translatable("pixelLoader.setting.screen.pixel.xfun")));
        y = addRenderableWidget(new ExpEditBox(font, 20 + hintWidth, 158, width - 40 - hintWidth, 20,
                Component.translatable("pixelLoader.setting.screen.pixel.yfun")));
        z = addRenderableWidget(new ExpEditBox(font, 20 + hintWidth, 184, width - 40 - hintWidth, 20,
                Component.translatable("pixelLoader.setting.screen.pixel.zfun")));
        x.setValue(ImgSetting.xExpr);
        y.setValue(ImgSetting.yExpr);
        z.setValue(ImgSetting.zExpr);
        reload();
    }

    public void recalc() {
        float s;
        int tmpw, tmph;
        try {
            switch (ImgSetting.keep) {
                case width:
                    ImgSetting.genWidth = Integer.parseInt(widthEditBox.getValue());
                    s = ImgSetting.genWidth / (float) ImgSetting.imgWidth;
                    tmph = Math.round(ImgSetting.imgHeight * s);
                    heightEditBox.setValue(String.valueOf(tmph));
                    ImgSetting.genHeight = tmph;
                    scaleEditBox.setValue(String.valueOf(Math.round(s * 100000) / 1000f));
                    ImgSetting.genScale = s;
                    break;
                case height:
                    ImgSetting.genHeight = Integer.parseInt(heightEditBox.getValue());
                    s = ImgSetting.genHeight / (float) ImgSetting.imgHeight;
                    tmpw = Math.round(ImgSetting.imgWidth * s);
                    widthEditBox.setValue(String.valueOf(tmpw));
                    ImgSetting.genWidth = tmpw;
                    scaleEditBox.setValue(String.valueOf(Math.round(s * 100000) / 1000f));
                    ImgSetting.genScale = s;
                    break;
                case scale:
                    ImgSetting.genScale = Float.parseFloat(scaleEditBox.getValue()) / 100;
                    s = ImgSetting.genScale;
                    tmpw = Math.round(ImgSetting.imgWidth * s);
                    widthEditBox.setValue(String.valueOf(tmpw));
                    ImgSetting.genWidth = tmpw;
                    tmph = Math.round(ImgSetting.imgHeight * s);
                    heightEditBox.setValue(String.valueOf(tmph));
                    ImgSetting.genHeight = tmph;
                    break;
            }
        } catch (NumberFormatException ignored) {

        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.setting.screen.pixel.width"), 20, 70, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.setting.screen.pixel.height"), 20 + w + 4, 70, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.setting.screen.pixel.scale"), 20 + w * 2 + 8, 70, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.setting.screen.pixel.urange", ImgSetting.genWidth), 20 + w * 3 + 12, 70, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.setting.screen.pixel.vrange", ImgSetting.genHeight), 20 + w * 3 + 12, 81, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.setting.screen.pixel.wrange", 1), 20 + w * 3 + 12, 92, 0xFFFFFF);

        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.setting.screen.pixel.xhint"), 20, 137, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.setting.screen.pixel.yhint"), 20, 163, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.setting.screen.pixel.zhint"), 20, 189, 0xFFFFFF);
    }

    public void reload() {
        ImgSetting.reload();
        recalc();
    }

    @Override
    public void onClose() {
        ImgSetting.cutout = cutout.getOptionIndex();
        ImgSetting.support = support.getOptionIndex();
        ImgSetting.dither = dither.getOptionIndex();
        ImgSetting.cover = cover.getOptionIndex();
        ImgSetting.preset = preset.getOptionIndex();
        ImgSetting.onfinish = onfinish.getOptionIndex();
        ImgSetting.xExpr = x.getValue();
        ImgSetting.yExpr = y.getValue();
        ImgSetting.zExpr = z.getValue();
        super.onClose();
    }

    public void loadPreset(String s) {
        List<String> functions = Selections.presets.get(preset.optionIndex);
        x.setValue(functions.getFirst());
        y.setValue(functions.get(1));
        z.setValue(functions.get(2));
    }
}
