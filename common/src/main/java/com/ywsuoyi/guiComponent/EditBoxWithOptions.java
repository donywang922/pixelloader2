package com.ywsuoyi.guiComponent;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class EditBoxWithOptions extends EditBox {
    public static final int fontColor = 0xaa39C5BB;
    public final ArrayList<String> options;
    public final ArrayList<String> filteredOptions;
    public boolean showOptions = false;
    public int optionIndex = 0;
    public boolean lock = false;
    public int optionHeight;
    public Font font;

    public EditBoxWithOptions(Font font, int x, int y, int width, int height, Component component, List<String> options) {
        super(font, x, y, width, height, component);
        this.options = new ArrayList<>(options);
        this.filteredOptions = new ArrayList<>(options);
        this.optionHeight = (getHeight() + font.lineHeight) / 2 + 1;
        this.font = font;
    }

    public void updateFilteredOptions() {
        if (!showOptions) return;
        filteredOptions.clear();
        options.stream().filter(this::filter).forEach(filteredOptions::add);
        if (!filteredOptions.isEmpty()) {
            optionIndex = optionIndex % filteredOptions.size();
        } else {
            optionIndex = 0;
        }
    }

    @Override
    public void insertText(String text) {
        super.insertText(text);
        updateFilteredOptions();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean result = super.charTyped(codePoint, modifiers);
        updateFilteredOptions();
        return result;
    }

    @Override
    public void deleteChars(int num) {
        super.deleteChars(num);
        updateFilteredOptions();
    }

    @Override
    public void deleteWords(int num) {
        super.deleteWords(num);
        updateFilteredOptions();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean inside = isMouseOver(mouseX, mouseY);
        if (inside && showOptions) {
            // 如果已经显示建议且再次点击编辑框，填入选中的内容
            if (!filteredOptions.isEmpty()) {
                setValue(filteredOptions.get(optionIndex));
                showOptions = false;
                return true;
            }
        }

        if (inside && !lock) {
            showOptions = true;
            updateFilteredOptions();
        } else {
            if (showOptions) {
                showOptions = false;
            }
        }

        return inside;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (showOptions && !filteredOptions.isEmpty()) {
            // 鼠标滚轮循环调整选中项
            if (scrollY > 0) {
                // 向上滚动
                optionIndex = prevIndex();
            } else if (scrollY < 0) {
                // 向下滚动
                optionIndex = nextIndex();
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (showOptions && !filteredOptions.isEmpty()) {
            switch (keyCode) {
                case 264: // DOWN arrow
                    optionIndex = nextIndex();
                    return true;
                case 265: // UP arrow
                    optionIndex = prevIndex();
                    return true;
                case 257: // ENTER
                case 335: // NUMPAD_ENTER
                    setValue(filteredOptions.get(optionIndex));
                    showOptions = false;
                    return true;
                case 256: // ESCAPE
                    showOptions = false;
                    this.optionIndex = options.indexOf(getValue());
                    return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void renderOptions(GuiGraphics guiGraphics) {
        if (!showOptions || filteredOptions.isEmpty()) return;
        int centerY = getY() + (getHeight() - 8) / 2;
        if (filteredOptions.size() == 1) {
            String suggestion = filteredOptions.getFirst();
            guiGraphics.drawString(font, suggestion, getX() + 4, centerY, fontColor, false);
            return;
        }
        // 计算要显示的三个选项
        int prevIndex = prevIndex();
        int currentIndex = optionIndex;
        int nextIndex = nextIndex();

        String prevSuggestion = filteredOptions.get(prevIndex);
        String currentSuggestion = filteredOptions.get(currentIndex);
        String nextSuggestion = filteredOptions.get(nextIndex);

        guiGraphics.drawString(font, prevSuggestion, getX() + 4, centerY - optionHeight, color(), true);

        guiGraphics.drawString(font, currentSuggestion, getX() + 4, centerY, fontColor, false);

        guiGraphics.drawString(font, nextSuggestion, getX() + 4, centerY + optionHeight, color(), true);
    }

    public int nextIndex() {
        return (optionIndex + 1) % filteredOptions.size();
    }

    public int prevIndex() {
        return (optionIndex - 1 + filteredOptions.size()) % filteredOptions.size();
    }

    public boolean filter(String s) {
        return true;
    }

    public void updateOptions(List<String> newOptions) {
        options.clear();
        options.addAll(newOptions);
        updateFilteredOptions();
    }

    public int getOptionIndex() {
        return optionIndex;
    }

    public void setOptionIndex(int index) {
        if (!options.isEmpty() && index >= 0 && index < options.size()) {
            optionIndex = index;
            setValue(options.get(index));
        }
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public int color() {
        return color(true);
    }

    public int color(boolean alpha) {
        return alpha ? fontColor : fontColor | 0xff000000;
    }
}
