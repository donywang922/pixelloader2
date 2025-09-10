package com.ywsuoyi.projector;

import com.ywsuoyi.ImageManager;
import com.ywsuoyi.Selections;
import com.ywsuoyi.loader.mapLoader.MapSetting;
import com.ywsuoyi.guiComponent.NumberEditBox;
import com.ywsuoyi.guiComponent.SelectionOnlyBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class ProjectorScreen extends Screen {

    public Component rollText = Component.translatable("pixelLoader.projector.screen.roll");
    public Component yawText = Component.translatable("pixelLoader.projector.screen.yaw");
    public Component pitchText = Component.translatable("pixelLoader.projector.screen.pitch");
    public Component scaleText = Component.translatable("pixelLoader.projector.screen.scale");
    public BlockPos pos;
    public ProjectorSetting setting;
    public Player player;
    public NumberEditBox roll;
    public NumberEditBox yaw;
    public NumberEditBox pitch;
    public NumberEditBox scale;

    public Button load;
    public Button place;
    public Button save;
    public SelectionOnlyBox imgFile, cutout, dither;

    protected ProjectorScreen(BlockPos pos, Player player) {
        super(Component.translatable("pixelLoader.projector.screen"));
        this.pos = pos;
        setting = ProjectorSetting.get(pos);
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        roll = addRenderableWidget(new NumberEditBox(font, 20, 80, 80, 20, rollText));
        yaw = addRenderableWidget(new NumberEditBox(font, 20, 114, 80, 20, yawText));
        pitch = addRenderableWidget(new NumberEditBox(font, 20, 148, 80, 20, pitchText));
        scale = addRenderableWidget(new NumberEditBox(font, 20, 182, 80, 20, scaleText));

        roll.setValue(String.valueOf(setting.roll));
        yaw.setValue(String.valueOf(setting.yaw));
        pitch.setValue(String.valueOf(setting.pitch));
        scale.setValue(String.valueOf(setting.scale));
        roll.setResponder(this::updateAngle);
        yaw.setResponder(this::updateAngle);
        pitch.setResponder(this::updateAngle);
        scale.setResponder(this::updateAngle);

        int w = (width - 48) / 3;

        cutout = this.addRenderableWidget(new SelectionOnlyBox(font, 20, 44, w, 20,
                Component.translatable("pixelLoader.setting.screen.cutout"), Selections.cutout));
        cutout.setOptionIndex(setting.cutout);
        cutout.setResponder(s -> setting.cutout = cutout.getOptionIndex());

        dither = this.addRenderableWidget(new SelectionOnlyBox(font, 20 + w + 4, 44, w, 20,
                Component.translatable("pixelLoader.setting.screen.dither"), Selections.dither));
        dither.setOptionIndex(setting.dither);
        dither.setResponder(s -> setting.dither = dither.getOptionIndex());

        load = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.projector.screen.load"), p -> {
            if (setting.state == ProjectorSetting.LoadState.Select) {
                setting.state = ProjectorSetting.LoadState.WaitStart;
                MapSetting.dither = setting.dither;
                MapSetting.cutout = setting.cutout;
            }
        }).bounds(this.width - 100, 156, 80, 20).build());
        place = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.projector.screen.place"), p -> {
            if (setting.state == ProjectorSetting.LoadState.Finish) setting.state = ProjectorSetting.LoadState.Placing;
        }).bounds(this.width - 100, 156, 80, 20).build());
        save = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.thread.screen.save"), p -> {
        }).bounds(this.width - 100, 180, 80, 20).build());

        imgFile = addRenderableWidget(new SelectionOnlyBox(font, 20, 20, width - 40, 20,
                Component.translatable("pixelLoader.setting.screen.file"), ImageManager.getImageListStr()));
        imgFile.setOptionIndex(setting.fileIndex);
        imgFile.setResponder(s -> setting.loadimg(imgFile.getOptionIndex()));
        this.tick();
        setting.loadimg();
    }

    @Override
    public void tick() {
        super.tick();
        boolean editable = setting.state == ProjectorSetting.LoadState.Select;
        load.visible = editable;
        place.visible = setting.state == ProjectorSetting.LoadState.Finish;
        save.visible = place.visible || setting.state == ProjectorSetting.LoadState.Done;
        roll.setEditable(editable);
        yaw.setEditable(editable);
        pitch.setEditable(editable);
        scale.setEditable(editable);
        imgFile.setLock(!editable);
        cutout.setLock(!editable);
        dither.setLock(!editable);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        roll.setFocused(false);
        yaw.setFocused(false);
        pitch.setFocused(false);
        scale.setFocused(false);
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (setting.state != ProjectorSetting.LoadState.Select) return true;
        boolean cot = super.mouseDragged(d, e, i, f, g);
        if (!cot) {
            try {
                double x = Double.parseDouble(pitch.getValue());
                double y = Double.parseDouble(yaw.getValue());
                x += Screen.hasShiftDown() ? g * 0.1 : g;
                x = Math.round(x * 1000) / 1000.0;
                y -= Screen.hasShiftDown() ? f * 0.1 : f;
                y = Math.round(y * 1000) / 1000.0;
                pitch.setValue(String.valueOf(x));
                yaw.setValue(String.valueOf(y));
            } catch (NumberFormatException ignored) {
                pitch.setValue("0");
                yaw.setValue("0");
            }
        }
        return cot;
    }

    @Override
    public void render(GuiGraphics poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        poseStack.drawString(this.font, rollText, 20, 70, 0xA0A0A0);
        poseStack.drawString(this.font, yawText, 20, 104, 0xA0A0A0);
        poseStack.drawString(this.font, pitchText, 20, 138, 0xA0A0A0);
        poseStack.drawString(this.font, scaleText, 20, 172, 0xA0A0A0);
        MutableComponent hintA = Component.translatable("pixelLoader.projector.screen.hintA");
        poseStack.drawString(this.font, hintA, this.width - 20 - this.font.width(hintA.getVisualOrderText()), 60, 0xFFFFFF);
        MutableComponent hintB = Component.translatable("pixelLoader.projector.screen.hintB");
        poseStack.drawString(this.font, hintB, this.width - 20 - this.font.width(hintB.getVisualOrderText()), 72, 0xFFFFFF);
        Component sz = Component.translatable("pixelLoader.projector.screen.size", setting.width + " " + setting.height);
        poseStack.drawString(this.font, sz, this.width - 20 - this.font.width(sz.getVisualOrderText()), 44, 0xFFFFFF);
        if (setting.state != ProjectorSetting.LoadState.Finish && setting.state != ProjectorSetting.LoadState.Done)
            poseStack.drawString(this.font, setting.message, this.width - 20 - this.font.width(setting.message.getVisualOrderText()), 88, 0xaa0000);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        setting.editing = false;
        super.onClose();
    }

    public void updateAngle(String s) {
        if (setting.state == ProjectorSetting.LoadState.Select)
            setting.set(roll.getValue(), yaw.getValue(), pitch.getValue(), scale.getValue(), player);
    }
}
