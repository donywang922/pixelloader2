package com.ywsuoyi.pixelloader;
// Made with Blockbench 4.7.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ProjectorModel extends Model {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation projectorLayer = new ModelLayerLocation(new ResourceLocation(PixelLoader.MOD_ID, "projector"), "main");
    public final ModelPart projector;
    public final ModelPart frametop;
    public final ModelPart framebot;

    public ProjectorModel(ModelPart root) {
        super(RenderType::entitySolid);
        this.projector = root.getChild("projector");
        this.frametop = root.getChild("frametop");
        this.framebot = root.getChild("framebot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition projector = partdefinition.addOrReplaceChild("projector", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.6047F));

        PartDefinition cube_r1 = projector.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.2777F, -11.9025F, -4.1336F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.6047F, -1.2217F, -0.2618F, -1.5708F));

        PartDefinition cube_r2 = projector.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(4, 0).addBox(2.2777F, -11.9025F, -4.1336F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.6047F, -1.2217F, 0.2618F, -1.5708F));

        PartDefinition cube_r3 = projector.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(8, 0).addBox(-3.2777F, -11.9025F, 3.1336F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.6047F, -1.9199F, -0.2618F, -1.5708F));

        PartDefinition cube_r4 = projector.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(8, 19).addBox(6.5F, -1.5F, -2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(20, 0).addBox(6.5F, 0.5F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(20, 4).addBox(6.5F, -1.5F, 1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(19, 14).addBox(-7.5F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 7).addBox(6.5F, -0.5F, -2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 19).addBox(-7.5F, -5.0F, 7.0F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 19).addBox(-7.5F, -5.0F, -8.0F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.6047F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r5 = projector.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(12, 0).addBox(2.2777F, -11.9025F, 3.1336F, 1.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.6047F, -1.9199F, 0.2618F, -1.5708F));

        PartDefinition cube_r6 = projector.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(16, 0).addBox(-7.5F, -7.0F, 5.0F, 1.0F, 14.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 15).addBox(-7.5F, -7.0F, -6.0F, 1.0F, 14.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.6047F, 0.0F, 1.5708F, 1.5708F));

        PartDefinition frametop = partdefinition.addOrReplaceChild("frametop", CubeListBuilder.create().texOffs(27, 31).addBox(-16.0F, 0.0F, -0.125F, 2.0F, 0.25F, 0.25F, new CubeDeformation(0.0F))
                .texOffs(31, 30).addBox(-16.0F, 0.25F, -0.125F, 0.25F, 1.75F, 0.25F, new CubeDeformation(0.0F))
                .texOffs(27, 31).addBox(14.0F, 0.0F, -0.125F, 2.0F, 0.25F, 0.25F, new CubeDeformation(0.0F))
                .texOffs(31, 30).addBox(15.75F, 0.25F, -0.125F, 0.25F, 1.75F, 0.25F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.125F));

        PartDefinition framebot = partdefinition.addOrReplaceChild("framebot", CubeListBuilder.create().texOffs(27, 31).addBox(-16.0F, -0.25F, -0.125F, 2.0F, 0.25F, 0.25F, new CubeDeformation(0.0F))
                .texOffs(31, 30).addBox(-16.0F, -2.0F, -0.125F, 0.25F, 1.75F, 0.25F, new CubeDeformation(0.0F))
                .texOffs(27, 31).addBox(14.0F, -0.25F, -0.125F, 2.0F, 0.25F, 0.25F, new CubeDeformation(0.0F))
                .texOffs(31, 30).addBox(15.75F, -2.0F, -0.125F, 0.25F, 1.75F, 0.25F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.125F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        projector.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        frametop.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        framebot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}