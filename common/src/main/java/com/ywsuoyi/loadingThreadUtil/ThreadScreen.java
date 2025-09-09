package com.ywsuoyi.loadingThreadUtil;

import com.ywsuoyi.guiComponent.IntegerEditBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class ThreadScreen extends Screen {
    public ThreadData data;
    public Button place, save;
    public IntegerEditBox percentage;
    public EditBox file;
    public long cooldown = 0;
    public boolean actCool = false;
    public SaveSchematicThread thread;

    protected ThreadScreen(BlockPos pos) {
        super(Component.translatable("pixelLoader.thread.screen"));
        data = ThreadData.data.get(pos);
    }

    @Override
    protected void init() {
        if (data == null) return;
        place = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.thread.screen.place"),
                p_onPress_1_ -> {
                    if (data.thread.state == BaseThread.State.end) {
                        data.state = ThreadData.State.place;
                        onClose();
                    }
                }).bounds(width - 20 - 100, 116, 100, 20).build());
        place.visible = false;
        percentage = this.addRenderableWidget(new IntegerEditBox(this.font, 20, 80, 90, 20,
                Component.translatable("pixelLoader.thread.screen.percentage"), 100, 0));
        if (data != null) {
            percentage.setValue(String.valueOf(Math.round(data.renderPercentage * 100)));
            actCool = data.autoLowerPercentage;
        }
        percentage.setResponder(this::updatePercentage);
        percentage.visible = false;

        file = this.addRenderableWidget(new EditBox(this.font, 20, 140, width - 40 - 100 - 4, 20,
                Component.translatable("pixelLoader.setting.screen.file")));
        file.setMaxLength(128);
        String fileName = data.thread.file.getName();
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }
        file.setValue(fileName);

        save = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.thread.screen.save"), button ->
        {
            thread = new SaveSchematicThread(minecraft.player, data.genBlocks, file.getValue());
            BaseThread.addThread(thread);
        }).bounds(width - 20 - 100, 140, 100, 20).build());
    }

    public void updatePercentage(String s) {
        data.renderPercentage = Float.parseFloat(percentage.getValue()) / 100f;
    }

    @Override
    public void tick() {
        super.tick();
        if (data == null) return;
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
        if (data == null) {
            guiGraphics.drawString(this.font, Component.literal("no data"), 20, 40, 0xFFFFFF);
            return;
        }
        guiGraphics.drawString(this.font, Component.literal(data.thread.file.getName()), 20, 20, 0xFFFFFF);
        guiGraphics.drawString(this.font, data.thread.message, 20, 30, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.thread.screen.center", data.center.toShortString()), 20, 40, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.thread.screen.blocks", data.genBlocks.size()), 20, 50, 0xFFFFFF);

        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.thread.screen.percentage"), 20, 70, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.thread.screen.rendertip"), 20, 104, 0xFFFFFF);
        if (!data.autoLowerPercentage) {
            guiGraphics.drawString(this.font, Component.translatable("pixelLoader.thread.screen.renderwarning"), 20, 114, 0xDDDD00);
        }
        if (thread != null) {
            guiGraphics.drawString(this.font, thread.message, 20, 164, 0xFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
