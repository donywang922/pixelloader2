package com.ywsuoyi.colorspace;

import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FilterInv implements Container {
    @Override
    public int getContainerSize() {
        return 54;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : ColorSpaces.filter) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return index >= 0 && index < ColorSpaces.filter.size() ? ColorSpaces.filter.get(index) : ItemStack.EMPTY;
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(@NotNull Player p_18946_) {
        return true;
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(ColorSpaces.filter, index, count);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        ItemStack itemstack = ColorSpaces.filter.get(index);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            ColorSpaces.filter.set(index, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        ColorSpaces.filter.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }


    @Override
    public void clearContent() {
        ColorSpaces.filter.clear();
    }
}
