package com.ywsuoyi.pixelloader.fabric;

import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.ProjectorBlockRenderer;
import com.ywsuoyi.pixelloader.ProjectorModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class PixelLoaderFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(PixelLoader.projectorBlockEntity, ProjectorBlockRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ProjectorModel.projectorLayer, ProjectorModel::createBodyLayer);
    }
}
