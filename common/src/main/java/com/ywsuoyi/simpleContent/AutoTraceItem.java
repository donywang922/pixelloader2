package com.ywsuoyi.simpleContent;

import com.ywsuoyi.PixelLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("pixelLoader.autoTrace.tip"));
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }
}
