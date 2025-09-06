package com.ywsuoyi.guiComponent;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SelectionOnlyBox extends EditBox {
    public static final int fontColor = 0x6639C5BB;
    private final List<String> allOptions;
    private boolean showOptions = false;
    private int selectedIndex = 0; // 当前选中项在allOptions中的索引
    private final int optionHeight;

    public Font font;

    public SelectionOnlyBox(Font font, int x, int y, int width, int height, Component component, List<String> options) {
        super(font, x, y, width, height, component);
        this.font = font;
        this.allOptions = new ArrayList<>(options);
        this.optionHeight = (this.height + font.lineHeight) / 2 + 1;

        // 设置初始值
        if (!allOptions.isEmpty()) {
            setValue(allOptions.getFirst());
        }
        this.setEditable(false);
        this.setBordered(false);
        this.setMaxLength(100);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean wasClickedOnBox = isMouseOver(mouseX, mouseY);

        if (wasClickedOnBox && showOptions && !allOptions.isEmpty()) {
            // 如果已经显示选项且再次点击，选择当前项
            setValue(allOptions.get(selectedIndex));
            showOptions = false;
            return true;
        }

        if (wasClickedOnBox) {
            showOptions = true;
        } else {
            // 点击外部区域，失去焦点
            if (showOptions) {
                showOptions = false;
                this.selectedIndex = allOptions.indexOf(getValue());
            }
        }

        return wasClickedOnBox;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (showOptions && !allOptions.isEmpty()) {
            // 鼠标滚轮循环调整选中项
            if (scrollY > 0) {
                // 向上滚动
                selectedIndex = (selectedIndex - 1 + allOptions.size()) % allOptions.size();
            } else if (scrollY < 0) {
                // 向下滚动
                selectedIndex = (selectedIndex + 1) % allOptions.size();
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (showOptions && !allOptions.isEmpty()) {
            switch (keyCode) {
                case 264: // DOWN arrow
                    selectedIndex = (selectedIndex + 1) % allOptions.size();
                    return true;
                case 265: // UP arrow
                    selectedIndex = (selectedIndex - 1 + allOptions.size()) % allOptions.size();
                    return true;
                case 257: // ENTER
                case 335: // NUMPAD_ENTER
                    setValue(allOptions.get(selectedIndex));
                    showOptions = false;
                    return true;
                case 256: // ESCAPE
                    showOptions = false;
                    this.selectedIndex = allOptions.indexOf(getValue());
                    return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染半透明黑色背景
        int backgroundColor = 0x80000000; // 半透明黑色
        int m = this.getX() + 4;
        int n = this.getY() + (this.height - 8) / 2;
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, backgroundColor);
        if (!this.showOptions)
            guiGraphics.drawString(font, Component.literal(this.getValue()), m, n, fontColor | 0xff000000);
        // 渲染选项列表
        if (showOptions) {
            renderOptions(guiGraphics);
        }
    }

    private void renderOptions(GuiGraphics guiGraphics) {
        int centerY = getY() + (height - 8) / 2;
        if (allOptions.size() == 1) {
            String option = allOptions.getFirst();
            guiGraphics.drawString(font, option, getX() + 4, centerY, fontColor, false);
            return;
        }
        // 计算要显示的三个选项
        int prevIndex = (selectedIndex - 1 + allOptions.size()) % allOptions.size();
        int currentIndex = selectedIndex;
        int nextIndex = (selectedIndex + 1) % allOptions.size();

        String prevOption = allOptions.get(prevIndex);
        String currentOption = allOptions.get(currentIndex);
        String nextOption = allOptions.get(nextIndex);

        // 渲染上一个选项（虚化）
        guiGraphics.drawString(font, prevOption, getX() + 4, centerY - optionHeight, fontColor | 0xaa000000, true);

        // 渲染当前选项（高亮）
        guiGraphics.drawString(font, currentOption, getX() + 4, centerY, fontColor, false);

        // 渲染下一个选项（虚化）
        guiGraphics.drawString(font, nextOption, getX() + 4, centerY + optionHeight, fontColor | 0xaa000000, true);
    }

    /**
     * 更新选项列表
     *
     * @param newOptions 新的选项列表
     */
    public void updateOptions(List<String> newOptions) {
        this.allOptions.clear();
        this.allOptions.addAll(newOptions);

        // 重置选中索引
        if (!allOptions.isEmpty()) {
            selectedIndex = selectedIndex % allOptions.size();
            // 如果当前值不在新选项中，重置为第一个选项
            if (!allOptions.contains(getValue())) {
                setValue(allOptions.getFirst());
                selectedIndex = 0;
            } else {
                // 更新选中索引为当前值的索引
                selectedIndex = allOptions.indexOf(getValue());
            }
        } else {
            selectedIndex = 0;
            setValue("");
        }
    }


    /**
     * 设置当前选中的值
     *
     * @param value 要设置的值
     */
    public void setValue(String value) {
        if (allOptions.contains(value)) {
            super.setValue(value);
            this.selectedIndex = allOptions.indexOf(value);
        } else if (!allOptions.isEmpty()) {
            // 如果值不存在，设置为第一个选项
            this.setValue(allOptions.getFirst());
            this.selectedIndex = 0;
        }
    }

    /**
     * 获取当前选中的索引
     *
     * @return 选中的索引
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * 设置选中的索引
     *
     * @param index 要选中的索引
     */
    public void setSelectedIndex(int index) {
        if (!allOptions.isEmpty() && index >= 0 && index < allOptions.size()) {
            this.selectedIndex = index;
            this.setValue(allOptions.get(index));
        }
    }
}