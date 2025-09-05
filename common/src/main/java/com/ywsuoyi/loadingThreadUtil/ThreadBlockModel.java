package com.ywsuoyi.loadingThreadUtil;// Made with Blockbench 4.7.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ThreadBlockModel{
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
}