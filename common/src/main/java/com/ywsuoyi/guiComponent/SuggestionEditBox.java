package com.ywsuoyi.guiComponent;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SuggestionEditBox extends EditBox {
    public static final int fontColor = 0x6639C5BB;
    private final List<String> allSuggestions;
    private final List<String> filteredSuggestions;
    private boolean showSuggestions = false;
    private int selectedIndex = 0; // 当前选中项在filteredSuggestions中的索引
    private final int suggestionHeight;

    public Font font;

    public SuggestionEditBox(Font font, int x, int y, int width, int height, Component component, List<String> suggestions) {
        super(font, x, y, width, height, component);
        this.font = font;
        this.allSuggestions = new ArrayList<>(suggestions);
        this.filteredSuggestions = new ArrayList<>(suggestions);
        suggestionHeight = (this.height + font.lineHeight) / 2 + 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean wasClickedOnBox = super.mouseClicked(mouseX, mouseY, button);

        if (wasClickedOnBox && showSuggestions) {
            // 如果已经显示建议且再次点击编辑框，填入选中的内容
            if (!filteredSuggestions.isEmpty()) {
                setValue(filteredSuggestions.get(selectedIndex));
                showSuggestions = false;
                return true;
            }
        }

        if (wasClickedOnBox) {
            showSuggestions = true;
            updateFilteredSuggestions();
        } else {
            if (showSuggestions) {
                showSuggestions = false;
            }
        }

        return wasClickedOnBox;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (showSuggestions && !filteredSuggestions.isEmpty()) {
            // 鼠标滚轮循环调整选中项
            if (scrollY > 0) {
                // 向上滚动
                selectedIndex = (selectedIndex - 1 + filteredSuggestions.size()) % filteredSuggestions.size();
            } else if (scrollY < 0) {
                // 向下滚动
                selectedIndex = (selectedIndex + 1) % filteredSuggestions.size();
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (showSuggestions && !filteredSuggestions.isEmpty()) {
            switch (keyCode) {
                case 264: // DOWN arrow
                    selectedIndex = (selectedIndex + 1) % filteredSuggestions.size();
                    return true;
                case 265: // UP arrow
                    selectedIndex = (selectedIndex - 1 + filteredSuggestions.size()) % filteredSuggestions.size();
                    return true;
                case 257: // ENTER
                case 335: // NUMPAD_ENTER
                    setValue(filteredSuggestions.get(selectedIndex));
                    showSuggestions = false;
                    return true;
                case 256: // ESCAPE
                    showSuggestions = false;
                    return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void insertText(String text) {
        super.insertText(text);
        if (showSuggestions) {
            updateFilteredSuggestions();
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean result = super.charTyped(codePoint, modifiers);
        if (showSuggestions) {
            updateFilteredSuggestions();
        }
        return result;
    }

    @Override
    public void deleteChars(int num) {
        super.deleteChars(num);
        if (showSuggestions) {
            updateFilteredSuggestions();
        }
    }

    @Override
    public void deleteWords(int num) {
        super.deleteWords(num);
        if (showSuggestions) {
            updateFilteredSuggestions();
        }
    }

    private void updateFilteredSuggestions() {
        String currentValue = getValue().toLowerCase();
        filteredSuggestions.clear();

        if (currentValue.isEmpty()) {
            filteredSuggestions.addAll(allSuggestions);
        } else {
            for (String suggestion : allSuggestions) {
                if (suggestion.toLowerCase().startsWith(currentValue)) {
                    filteredSuggestions.add(suggestion);
                }
            }
        }

        // 重置选中索引，确保在有效范围内
        if (!filteredSuggestions.isEmpty()) {
            selectedIndex = selectedIndex % filteredSuggestions.size();
        } else {
            selectedIndex = 0;
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染原始编辑框
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        // 渲染建议列表
        if (showSuggestions && !filteredSuggestions.isEmpty()) {
            renderSuggestions(guiGraphics);
        }
    }

    private void renderSuggestions(GuiGraphics guiGraphics) {
        int centerY = this.getY() + (this.height - 8) / 2;
        if (filteredSuggestions.size() == 1) {
            String suggestion = filteredSuggestions.getFirst();
            guiGraphics.drawString(font, suggestion, getX() + 4, centerY, fontColor, false);
            return;
        }
        // 计算要显示的三个选项
        int prevIndex = (selectedIndex - 1 + filteredSuggestions.size()) % filteredSuggestions.size();
        int currentIndex = selectedIndex;
        int nextIndex = (selectedIndex + 1) % filteredSuggestions.size();

        String prevSuggestion = filteredSuggestions.get(prevIndex);
        String currentSuggestion = filteredSuggestions.get(currentIndex);
        String nextSuggestion = filteredSuggestions.get(nextIndex);

        guiGraphics.drawString(font, prevSuggestion, getX() + 4, centerY - suggestionHeight, fontColor | 0xaa000000, true);

        guiGraphics.drawString(font, currentSuggestion, getX() + 4, centerY, fontColor, false);

        guiGraphics.drawString(font, nextSuggestion, getX() + 4, centerY + suggestionHeight, fontColor | 0xaa000000, true);
    }

    public void updateSuggestions(List<String> newSuggestions) {
        this.allSuggestions.clear();
        this.allSuggestions.addAll(newSuggestions);
        updateFilteredSuggestions();
    }
}