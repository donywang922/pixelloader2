package com.ywsuoyi.simpleContent;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class TagSplitItem extends Item {
    public TagSplitItem(Properties properties) {
        super(properties);
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        // 只在服务端执行逻辑
        if (level.isClientSide()) {
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }

        // 获取副手物品
        ItemStack offhandItem = player.getOffhandItem();

        // 检查副手是否有物品且是方块物品
        if (offhandItem.isEmpty() || !(offhandItem.getItem() instanceof BlockItem blockItem)) {
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        }

        // 获取方块
        Block block = blockItem.getBlock();

        // 获取该方块的所有标签
        Collection<TagKey<Block>> tags = BuiltInRegistries.BLOCK.getHolderOrThrow(
                BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow()
        ).tags().toList();

        // 如果没有标签，则不执行任何操作
        if (tags.isEmpty()) {
            player.displayClientMessage(Component.translatable("pixelloader.tag_split_item.no_tags_found"),true);
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        }

        // 为每个标签创建命名牌
        for (TagKey<Block> tag : tags) {
            // 创建命名牌
            ItemStack nameTag = new ItemStack(Items.NAME_TAG);

            // 设置命名牌的名字为标签的字符串形式
            String tagName = tag.location().toString();
            nameTag.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                    Component.literal(tagName));

            // 将命名牌添加到玩家背包，如果背包满了则掉落
            if (!player.getInventory().add(nameTag)) {
                player.drop(nameTag, false);
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("pixelloader.tag_split_item.tip"));
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }
}
