package com.ywsuoyi.pixelloader.imgLoader;

import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.imgLoader.TraceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TraceCenterBlock extends Block {
    public static final IntegerProperty pointX = IntegerProperty.create("pointx", 0, 26);
    public static final IntegerProperty pointY = IntegerProperty.create("pointy", 0, 26);

    public TraceCenterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(pointX, 0).setValue(pointY, 0));
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(pointX).add(pointY);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        updatePoint(level, blockPos, blockState);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        updatePoint(level, blockPos, blockState);
    }

    public void updatePoint(LevelAccessor level, BlockPos blockPos, BlockState blockState) {
        boolean flagPX = false;
        blockState = blockState.setValue(pointX,0).setValue(pointY,0);
        for (int i = 0; i < 26; i++) {
            BlockPos tmpPos = blockPos.offset(TraceBlock.neb[i * 3], TraceBlock.neb[i * 3 + 1], TraceBlock.neb[i * 3 + 2]);
            BlockState nb = level.getBlockState(tmpPos);
            if (nb.is(PixelLoader.traceBlock)) {
                boolean flagNpt = true;
                for (int j = 0; j < 26; j++) {
                    BlockPos tmpPos2 = tmpPos.offset(TraceBlock.neb[j * 3], TraceBlock.neb[j * 3 + 1], TraceBlock.neb[j * 3 + 2]);
                    BlockState nb2 = level.getBlockState(tmpPos2);
                    if (nb2.is(PixelLoader.traceBlock) && tmpPos2.offset(TraceBlock.fromID(nb2.getValue(TraceBlock.point))).equals(tmpPos)) {
                        flagNpt = false;
                        break;
                    }
                }
                if (flagNpt) {
                    if (!flagPX) {
                        blockState = blockState.setValue(pointX, TraceBlock.toID(TraceBlock.neb[i * 3], TraceBlock.neb[i * 3 + 1], TraceBlock.neb[i * 3 + 2]));
                        flagPX = true;
                    } else {
                        blockState = blockState.setValue(pointY, TraceBlock.toID(TraceBlock.neb[i * 3], TraceBlock.neb[i * 3 + 1], TraceBlock.neb[i * 3 + 2]));
                        break;
                    }
                }
            }
        }
        level.setBlock(blockPos, blockState, 3);
    }
}
