package com.ywsuoyi.pixelloader.colorspace;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SelectBlockScreen extends Screen {
    float scrollY = 0;

    int lineHeight = 24;
    int h = 0;

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
        if (i == 1) {
            scrollY = Mth.clamp((float) e / height * h, 0, h);
        }
        return true;
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        renderBackground(poseStack);
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
                drawString(poseStack, font, Component.literal("☑"), width - 24, yOffset + 5, 0xFFFFFF);

            }
        }
    }
}
