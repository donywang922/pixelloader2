package com.ywsuoyi.guiComponent;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class ExpEditBox extends EditBox {
    Font font;

    // 支持的函数列表
    private static final List<String> FUNCTIONS = Arrays.asList(
            "sin", "cos", "tan", "asin", "acos", "atan",
            "sqrt", "log", "log10", "exp", "abs",
            "floor", "ceil", "round"
    );

    // 支持的变量列表
    private static final List<String> VARIABLES = Arrays.asList(
            "u", "v", "w", "h", "z", "PI", "E"
    );

    private String currentSuggestion = "";
    private int suggestionStart = -1;

    public ExpEditBox(Font font, int i, int j, int k, int l, Component component) {
        super(font, i, j, k, l, component);
        this.font = font;
        this.setMaxLength(100);
    }

    @Override
    public void insertText(String text) {
        super.insertText(text);
        if ("(".equals(text)) {
            super.insertText(")");                        // 再添加右括号
            moveCursor(-1,false); // 光标移到括号内
        }
        updateSuggestion();
    }

    @Override
    public void deleteChars(int i) {
        super.deleteChars(i);
        updateSuggestion();
    }

    @Override
    public void deleteWords(int i) {
        super.deleteWords(i);
        updateSuggestion();
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Tab键处理自动补全
        if (keyCode == GLFW.GLFW_KEY_TAB && !currentSuggestion.isEmpty()) {
            applyAutoCompletion();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    /**
     * 更新当前的建议提示
     */
    private void updateSuggestion() {
        currentSuggestion = "";
        suggestionStart = -1;

        String value = getValue();
        int cursorPos = getCursorPosition();

        if (value.isEmpty() || cursorPos == 0) {
            return;
        }

        // 找到当前单词的开始位置
        int wordStart = cursorPos - 1;
        while (wordStart >= 0 && Character.isLetterOrDigit(value.charAt(wordStart))) {
            wordStart--;
        }
        wordStart++;

        if (wordStart < cursorPos) {
            String currentWord = value.substring(wordStart, cursorPos);
            if (!currentWord.isEmpty()) {
                String suggestion = findBestMatch(currentWord);
                if (suggestion != null && !suggestion.equals(currentWord)) {
                    currentSuggestion = suggestion;
                    suggestionStart = wordStart;
                }
            }
        }
    }

    /**
     * 找到最佳匹配的建议
     */
    private String findBestMatch(String input) {
        input = input.toLowerCase();

        // 优先匹配函数
        for (String func : FUNCTIONS) {
            if (func.startsWith(input)) {
                return func + "()";
            }
        }

        // 然后匹配变量
        for (String var : VARIABLES) {
            if (var.toLowerCase().startsWith(input)) {
                return var;
            }
        }

        return null;
    }

    /**
     * 应用自动补全
     */
    private void applyAutoCompletion() {
        if (currentSuggestion.isEmpty() || suggestionStart == -1) {
            return;
        }

        String value = getValue();
        int cursorPos = getCursorPosition();

        // 替换当前单词
        String newValue = value.substring(0, suggestionStart) +
                currentSuggestion +
                value.substring(cursorPos);

        setValue(newValue);

        // 如果是函数，将光标移动到括号内
        if (currentSuggestion.endsWith("()")) {
            moveCursorTo(suggestionStart + currentSuggestion.length() - 1,false);
        } else {
            moveCursorTo(suggestionStart + currentSuggestion.length(),false);
        }

        // 清除建议
        currentSuggestion = "";
        suggestionStart = -1;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        // 渲染建议提示
        if (!currentSuggestion.isEmpty() && suggestionStart != -1 && isFocused()) {
            renderSuggestion(guiGraphics);
        }
    }

    /**
     * 渲染建议提示
     */
    private void renderSuggestion(GuiGraphics guiGraphics) {
        String value = getValue();
        int cursorPos = getCursorPosition();

        if (suggestionStart >= cursorPos) {
            return;
        }

        // 计算当前输入的部分
        String typedPart = value.substring(suggestionStart, cursorPos);
        String suggestionPart = currentSuggestion.substring(typedPart.length());

        // 计算渲染位置
        Font font = this.font;
        String beforeCursor = value.substring(0, cursorPos);
        int textWidth = font.width(beforeCursor);

        // 计算在输入框中的位置
        int renderX = getX() + 4 + textWidth;
        int renderY = getY() + (getHeight() - 8) / 2;

        // 检查是否超出输入框边界
        if (renderX < getX() + getWidth() - 4) {
            // 渲染半透明的建议文本
            guiGraphics.fill(RenderType.guiOverlay(),renderX, renderY - 1, renderX + font.width(suggestionPart), renderY + font.lineHeight + 1, 0x90000000);
            guiGraphics.drawString(font, suggestionPart, renderX, renderY, 0xCC39C5BB);
        }
    }

    @Override
    public void setFocused(boolean bl) {
        super.setFocused(bl);
        if (!isFocused()) {
            // 失去焦点时清除建议
            currentSuggestion = "";
            suggestionStart = -1;
        } else {
            // 获得焦点时更新建议
            updateSuggestion();
        }
    }
}