package com.ywsuoyi.pixelloader.colorspace;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.HashSet;
import java.util.Set;

public class SelectBlockScreen extends Screen {
    float scrollY = 0;

    int lineHeight = 24;
    Component check = Component.literal("☑"), uncheck = Component.literal("☐");
    int h = 0;
    Set<Integer> pass = new HashSet<>();

    protected SelectBlockScreen() {
        super(Component.translatable("pixelLoader.screen.selectblock"));
    }

    @Override
    protected void init() {
        super.init();
        h = ColorSpace.selectBlocks.size() * lineHeight - height + 30;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        scrollY -= (float) f * 10;
        scrollY = Mth.clamp(scrollY, 0, h);
        return true;
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (i == 1 || (i == 0 && d > width - 20)) {
            scrollY = Mth.clamp((float) e / height * h, 0, h);
        }
        if (i == 0 && d < width - 20 && d > width - 40) {
            int idx = (int) ((scrollY + e - 12.5) / lineHeight);
            if (pass.contains(idx) || idx < 0 || idx >= ColorSpace.selectBlocks.size()) return true;
            SelectBlock block = ColorSpace.selectBlocks.get(idx);
            block.active = !block.active;
            pass.add(idx);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (i == 0 && d < width - 20 && d > width - 40) {
            int idx = (int) ((scrollY + e - 12.5) / lineHeight);
            if (idx < 0 || idx >= ColorSpace.selectBlocks.size()) return true;
            SelectBlock block = ColorSpace.selectBlocks.get(idx);
            block.active = !block.active;
            pass.clear();
            pass.add(idx);
        }
        return true;
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        fill(poseStack, 0, 0, width, height, 0x66000000);
        int j1 = (int) (scrollY / h * (height - lineHeight));
        fill(poseStack, width - 10, j1, width, j1 + lineHeight, 0x33ffffff);
        for (int idx = 0; idx < ColorSpace.selectBlocks.size(); idx++) {
            if (idx * lineHeight + lineHeight > scrollY && idx * lineHeight - lineHeight < scrollY + height) {
                SelectBlock block = ColorSpace.selectBlocks.get(idx);
                int yOffset = Math.round(idx * lineHeight - scrollY) + 15;
                itemRenderer.renderAndDecorateItem(
                        block.block.asItem().getDefaultInstance(),
                        15,
                        yOffset + 2
                );
                if (block.bc != null)
                    drawString(poseStack, font, Component.translatable("pixelLoader.screen.selectblock.color",
                                    String.format("%02x%02x%02x", block.bc.r, block.bc.g, block.bc.b)),
                            40, yOffset, block.bc.rgb);
                if (block.mapB != null)
                    drawString(poseStack, font, Component.translatable("pixelLoader.screen.selectblock.color",
                                    String.format("%02x%02x%02x", block.mapB.r, block.mapB.g, block.mapB.b)),
                            40, yOffset + 10, block.mapB.rgb);
                if (block.map != null)
                    drawString(poseStack, font, Component.translatable("pixelLoader.screen.selectblock.color",
                                    String.format("%02x%02x%02x", block.map.r, block.map.g, block.map.b)),
                            100, yOffset + 10, block.map.rgb);
                if (block.mapT != null)
                    drawString(poseStack, font, Component.translatable("pixelLoader.screen.selectblock.color",
                                    String.format("%02x%02x%02x", block.mapT.r, block.mapT.g, block.mapT.b)),
                            160, yOffset + 10, block.mapT.rgb);
                String descriptionId = block.block.getDescriptionId();
                drawString(poseStack, font, block.block.getName(), 220, yOffset, 0xFFFFFF);
                drawString(poseStack, font, descriptionId, 220, yOffset + 10, 0xFFFFFF);
                drawString(poseStack, font, block.active ? check : uncheck, width - 34, yOffset + 5, 0xFFFFFF);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        ColorSpace.reBuildAll();
    }
}
