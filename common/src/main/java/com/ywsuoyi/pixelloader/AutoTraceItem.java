package com.ywsuoyi.pixelloader;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AutoTraceItem extends Item {
    public AutoTraceItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack arg) {
        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
        if (useOnContext.getLevel().getBlockState(useOnContext.getClickedPos()).is(PixelLoader.traceBlock)) {
            Level level = useOnContext.getLevel();
            BlockPos tracepos = useOnContext.getClickedPos();
            PixelLoader.traceBlock.tryPoint(level, tracepos, level.getBlockState(tracepos));
            while (level.getBlockState(tracepos).getValue(TraceBlock.point) != 0) {
                tracepos = tracepos.offset(TraceBlock.fromID(level.getBlockState(tracepos).getValue(TraceBlock.point)));
                PixelLoader.traceBlock.tryPoint(level, tracepos, level.getBlockState(tracepos));
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(useOnContext);
    }

}
