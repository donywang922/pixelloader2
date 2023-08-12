package com.ywsuoyi.pixelloader.loadingThreadUtil;// Made with Blockbench 4.7.4
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

public class ThreadBlockModel extends Model {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation threadLayer = new ModelLayerLocation(new ResourceLocation("modid", "custom_model"), "main");
    public final ModelPart center;
    public final ModelPart l1;
    public final ModelPart l2;
    public final ModelPart l3;
    public final ModelPart l4;
    public final ModelPart l5;
    public final ModelPart l6;
    public final ModelPart l7;
    public final ModelPart l8;

    public final ModelPart[] modelParts;

    public ThreadBlockModel(ModelPart root) {
        super(RenderType::entitySolid);
        this.center = root.getChild("center");
        this.l1 = root.getChild("l1");
        this.l2 = root.getChild("l2");
        this.l3 = root.getChild("l3");
        this.l4 = root.getChild("l4");
        this.l5 = root.getChild("l5");
        this.l6 = root.getChild("l6");
        this.l7 = root.getChild("l7");
        this.l8 = root.getChild("l8");
        modelParts = new ModelPart[]{center, l1, l2, l3, l4, l5, l6, l7, l8};
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition center = partdefinition.addOrReplaceChild("center", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(3.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition cube_r1 = center.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(3.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition l1 = partdefinition.addOrReplaceChild("l1", CubeListBuilder.create().texOffs(1, 1).addBox(-0.5F, -5.0F, -8.0F, 1.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

        PartDefinition l2 = partdefinition.addOrReplaceChild("l2", CubeListBuilder.create().texOffs(1, 1).addBox(5.0F, -5.0F, -6.0F, 1.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

        PartDefinition l3 = partdefinition.addOrReplaceChild("l3", CubeListBuilder.create().texOffs(1, 1).addBox(7.0F, -5.0F, -0.5F, 1.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

        PartDefinition l4 = partdefinition.addOrReplaceChild("l4", CubeListBuilder.create().texOffs(1, 1).addBox(5.0F, -5.0F, 5.0F, 1.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

        PartDefinition l5 = partdefinition.addOrReplaceChild("l5", CubeListBuilder.create().texOffs(1, 1).addBox(-0.5F, -5.0F, 7.0F, 1.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

        PartDefinition l6 = partdefinition.addOrReplaceChild("l6", CubeListBuilder.create().texOffs(1, 1).addBox(-6.0F, -5.0F, 5.0F, 1.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

        PartDefinition l7 = partdefinition.addOrReplaceChild("l7", CubeListBuilder.create().texOffs(1, 1).addBox(-8.0F, -5.0F, -0.5F, 1.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

        PartDefinition l8 = partdefinition.addOrReplaceChild("l8", CubeListBuilder.create().texOffs(1, 1).addBox(-6.0F, -5.0F, -6.0F, 1.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        for (ModelPart part : modelParts) {
            part.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}