package com.ywsuoyi.pixelloader.projector;

import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.loadingThreadUtil.ThreadData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ProjectorBlock extends BaseEntityBlock {
    public ProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public float getShadeBrightness(BlockState arg, BlockGetter arg2, BlockPos arg3) {
        return 1.0f;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState arg, BlockGetter arg2, BlockPos arg3) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return ProjectorSetting.get(blockPos).editing ? Shapes.empty() : super.getShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ProjectorBlockEntity(blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        ProjectorSetting setting = ProjectorSetting.get(blockPos);
        if (livingEntity != null) {
            setting.setRound(0, -livingEntity.getYHeadRot(), livingEntity.getXRot(), 15);
        } else {
            setting.set(0, 0, 0, 15);
        }
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ProjectorSetting setting = ProjectorSetting.get(blockPos);
        player.moveTo(blockPos.getX() + 0.5, blockPos.getY() - player.getEyeHeight() + 0.5, blockPos.getZ() + 0.5, (float) -setting.yaw, (float) setting.pitch + 0.0000001f);
        player.setDeltaMovement(Vec3.ZERO);
        setting.editing = true;
        if (level.isClientSide)
            Minecraft.getInstance().setScreen(new ProjectorScreen(blockPos, player));
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide)
            return createTickerHelper(blockEntityType, PixelLoader.projectorBlockEntity, ProjectorBlockEntity::renderTick);
        return createTickerHelper(blockEntityType, PixelLoader.projectorBlockEntity, ProjectorBlockEntity::serverTick);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        ProjectorSetting setting = ProjectorSetting.get(blockPos);
        if (setting.thread != null)
            setting.thread.forceStop();
        ProjectorSetting.settings.remove(blockPos);
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }
}
