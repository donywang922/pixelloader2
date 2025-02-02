package com.ywsuoyi.fabric;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.projector.ProjectorBlockRenderer;
import com.ywsuoyi.projector.ProjectorModel;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockModel;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class PixelLoaderFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(PixelLoader.projectorBlockEntity, ProjectorBlockRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ProjectorModel.projectorLayer, ProjectorModel::createBodyLayer);

        BlockEntityRenderers.register(PixelLoader.threadBlockEntity, ThreadBlockRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ThreadBlockModel.threadLayer, ThreadBlockModel::createBodyLayer);
    }
}
