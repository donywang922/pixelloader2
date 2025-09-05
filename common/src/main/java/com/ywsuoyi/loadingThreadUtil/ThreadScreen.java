package com.ywsuoyi.loadingThreadUtil;

import com.ywsuoyi.guiComponent.IntegerEditBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class ThreadScreen extends Screen {
    public ThreadData data;
    public Button place;
    public IntegerEditBox percentage;
    public long cooldown = 0;
    public boolean actCool = false;

    protected ThreadScreen(BlockPos pos) {
        super(Component.translatable("pixelLoader.screen.thread"));
        data = ThreadData.data.get(pos);
    }

    @Override
    protected void init() {
        place = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.thread.place"),
                p_onPress_1_ -> {
                    if (data.thread.state == BaseThread.State.end) {
                        data.state = ThreadData.State.place;
                        onClose();
                    }
                }).bounds(width - 140, 20, 100, 20).build());
        place.visible = false;
        percentage = this.addRenderableWidget(new IntegerEditBox(this.font, width - 140, 60, 100, 20,
                Component.translatable("pixelLoader.screen.thread.percentage"), 100, 0));
        if (data != null) {
            percentage.setValue(String.valueOf(Math.round(data.renderPercentage * 100)));
            actCool = data.autoLowerPercentage;
        }
        percentage.setResponder(this::updatePercentage);
        percentage.visible = false;
    }

    public void updatePercentage(String s) {
        data.renderPercentage = Float.parseFloat(percentage.getValue()) / 100f;
    }

    @Override
    public void tick() {
        super.tick();
        place.visible = data != null && data.thread.state == BaseThread.State.end;
        percentage.visible = data != null;
        if (data != null && data.autoLowerPercentage != actCool) {
            actCool = data.autoLowerPercentage;
            percentage.setEditable(false);
            cooldown = System.currentTimeMillis();
            percentage.setValue("0");
        }
        if (!percentage.canConsumeInput() && System.currentTimeMillis() - cooldown > 2000)
            percentage.setEditable(true);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        if (data != null) {
            guiGraphics.drawString(this.font, Component.literal(data.thread.file.getName()), 20, 20, 0xFFFFFF);
            guiGraphics.drawString(this.font, data.thread.message, 20, 30, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.translatable("pixelLoader.screen.thread.center", data.center.toShortString()), 20, 40, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.translatable("pixelLoader.screen.thread.blocks", data.genBlocks.size()), 20, 50, 0xFFFFFF);

            guiGraphics.drawString(this.font, Component.translatable("pixelLoader.screen.thread.percentage"), width - 140, 50, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.translatable("pixelLoader.screen.thread.rendertip"), width - 140, 90, 0xFFFFFF);
            if (!data.autoLowerPercentage) {
                guiGraphics.drawString(this.font, Component.translatable("pixelLoader.screen.thread.renderwarning"), width - 140, 100, 0xDDDD00);
            }
        } else {
            guiGraphics.drawString(this.font, Component.literal("no data"), 20, 40, 0xFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
