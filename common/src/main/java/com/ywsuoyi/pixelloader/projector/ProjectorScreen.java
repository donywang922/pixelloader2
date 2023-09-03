package com.ywsuoyi.pixelloader.projector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ywsuoyi.pixelloader.guiComponent.NumberEditBox;
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

    protected ProjectorScreen(BlockPos pos, Player player) {
        super(Component.translatable("pixelLoader.projector.screen"));
        this.pos = pos;
        setting = ProjectorSetting.get(pos);
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        roll = addRenderableWidget(new NumberEditBox(font, 20, 40, 80, 20, rollText));
        yaw = addRenderableWidget(new NumberEditBox(font, 20, 80, 80, 20, yawText));
        pitch = addRenderableWidget(new NumberEditBox(font, 20, 120, 80, 20, pitchText));
        scale = addRenderableWidget(new NumberEditBox(font, 20, 160, 80, 20, scaleText));
        roll.setValue(String.valueOf(setting.roll));
        yaw.setValue(String.valueOf(setting.yaw));
        pitch.setValue(String.valueOf(setting.pitch));
        scale.setValue(String.valueOf(setting.scale));
        roll.setResponder(this::updateAngle);
        yaw.setResponder(this::updateAngle);
        pitch.setResponder(this::updateAngle);
        scale.setResponder(this::updateAngle);

        load = addRenderableWidget(new Button(this.width - 100, 130, 80, 20, Component.translatable("pixelLoader.projector.screen.load"), p -> {
            if (setting.state == ProjectorSetting.LoadState.Select) {
                setting.state = ProjectorSetting.LoadState.WaitStart;
            }
        }));
        place = addRenderableWidget(new Button(this.width - 100, 130, 80, 20, Component.translatable("pixelLoader.projector.screen.place"), p -> {
            if (setting.state == ProjectorSetting.LoadState.Finish)
                setting.state = ProjectorSetting.LoadState.Placing;
        }));
        save = addRenderableWidget(new Button(this.width - 100, 160, 80, 20, Component.translatable("pixelLoader.projector.screen.save"), p -> {
        }));
    }

    @Override
    public void tick() {
        super.tick();
        boolean editable = setting.state == ProjectorSetting.LoadState.Select;
        load.visible = editable;
        place.visible = setting.state == ProjectorSetting.LoadState.Finish;
        save.visible = place.visible || setting.state == ProjectorSetting.LoadState.Done;
        roll.tick();
        yaw.tick();
        pitch.tick();
        scale.tick();
        roll.setEditable(editable);
        yaw.setEditable(editable);
        pitch.setEditable(editable);
        scale.setEditable(editable);

    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        roll.setFocus(false);
        yaw.setFocus(false);
        pitch.setFocus(false);
        scale.setFocus(false);
        if (setting.state == ProjectorSetting.LoadState.Select) {
            Component s = setting.getFileText();
            if (d > this.width - 20 - this.font.width(s) && e > 60 && e < 80) {
                setting.addindex();
                return true;
            }
        }
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
                y += Screen.hasShiftDown() ? f * 0.1 : f;
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
    public void render(PoseStack poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        drawString(poseStack, this.font, rollText, 20, 30, 0xA0A0A0);
        drawString(poseStack, this.font, yawText, 20, 70, 0xA0A0A0);
        drawString(poseStack, this.font, pitchText, 20, 110, 0xA0A0A0);
        drawString(poseStack, this.font, scaleText, 20, 150, 0xA0A0A0);
        MutableComponent hintA = Component.translatable("pixelLoader.projector.screen.hintA");
        drawString(poseStack, this.font, hintA, this.width - 20 - this.font.width(hintA.getVisualOrderText()), 30, 0xFFFFFF);
        MutableComponent hintB = Component.translatable("pixelLoader.projector.screen.hintB");
        drawString(poseStack, this.font, hintB, this.width - 20 - this.font.width(hintB.getVisualOrderText()), 50, 0xFFFFFF);
        Component s = setting.getFileText();
        drawString(poseStack, this.font, s, this.width - 20 - this.font.width(s), 70, 0xd5b767);
        String sz = setting.width + " " + setting.height;
        drawString(poseStack, this.font, sz, this.width - 20 - this.font.width(sz), 90, 0xFFFFFF);
        if (setting.state != ProjectorSetting.LoadState.Finish && setting.state != ProjectorSetting.LoadState.Done)
            drawString(poseStack, this.font, setting.message, this.width - 20 - this.font.width(setting.message.getVisualOrderText()), 160, 0xaa0000);
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
