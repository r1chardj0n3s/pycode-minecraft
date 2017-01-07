/*
 * Copyright (c) 2017 Richard Jones <richard@mechanicalcat.net>
 * All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.mechanicalcat.pycode.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class GuiTextArea extends Gui {
    static final ResourceLocation texture = new ResourceLocation("pycode:textures/gui/code_book.png");
    private static final int TEX_WIDTH = 334;
    private static final int TEX_HEIGHT = 238;

    private final int id;
    private FontRenderer fontRenderer;
    public int xPosition;
    public int yPosition;
    public int width;
    public int height;
    public int maxRows;

    /** If this value is true then keyTyped will process the keys. */
    private boolean isFocused;

    private int cursorCounter = 0;
    private int cursorRow = 0;
    private int cursorColumn = 0;
    private String[] lines = {"\n"};

    private GuiPageButtonList.GuiResponder guiResponder;

    GuiTextArea(int id, FontRenderer fontRenderer, int x, int y, int width, int height) {
        this.id = id;
        this.fontRenderer = fontRenderer;
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;

        this.maxRows = this.height / this.fontRenderer.FONT_HEIGHT;
    }

    public int getId()
    {
        return this.id;
    }

    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter() {
        // only interested in up to 12 ticks
        this.cursorCounter = (this.cursorCounter + 1) % 12;
    }

    /**
     * Sets the GuiResponder associated with this text area.
     */
    public void setGuiResponder(GuiPageButtonList.GuiResponder guiResponderIn)
    {
        this.guiResponder = guiResponderIn;
    }

    public void setString(String text) {
        this._setString(text);
        this.setCursorPosition(0, 0);
    }

    private void _editString(String text) {
        this._setString(text);
        if (this.guiResponder != null) {
            this.guiResponder.setEntryValue(this.id, this.getString());
        }
    }

    private void _setString(String text) {
        String s = text;
        // fudge the last line so it has content so a line is actually created
        if (text.endsWith("\n")) {
            s += " ";
        }
        this.lines = s.split("\n");
        int last = this.lines.length - 1;
        if (text.endsWith("\n")) {
            this.lines[last] = "";
        }
    }

    public String[] getLines() {
        return this.lines;
    }

    public String getString() {
        return String.join("\n", this.lines);
    }

    public void drawEditor() {
        String content = getString();
        int line_width;

        // draw cursor
        if (this.cursorRow == this.lines.length) {
            // current line is empty
            line_width = 0;
        } else {
            line_width = this.fontRenderer.getStringWidth(this.lines[this.cursorRow].substring(0, this.cursorColumn));
        }
        int cursor_x = this.xPosition + line_width - 2;
        int cursor_y = this.yPosition + this.cursorRow * this.fontRenderer.FONT_HEIGHT - 2;
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        if (this.cursorCounter / 6 % 2 == 0) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        drawModalRectWithCustomSizedTexture(cursor_x, cursor_y, 52, 215, 3, 11, TEX_WIDTH, TEX_HEIGHT);

        // draw content
        this.fontRenderer.drawSplitString(content, this.xPosition, this.yPosition, this.width, 0);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.isFocused) return;

        if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getString());
            return;
        }
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this._editString(GuiScreen.getClipboardString());
            this.setCursorPosition(0, 0);
            return;
        }

//            GuiScreen.isShiftKeyDown();
//            GuiScreen.isCtrlKeyDown();

        int line_width;
        int last_line = this.lines.length - 1;
        switch (keyCode) {
            case Keyboard.KEY_BACK:
                String line = this.lines[this.cursorRow];
                if (this.cursorColumn == 0) {
                    // TODO joining long lines will be an issue
                    if (this.cursorRow == 0) {
                        return;
                    }
                    String s = this.lines[this.cursorRow - 1];
                    this.lines[this.cursorRow - 1] = s.substring(0, s.length()) + this.lines[this.cursorRow];
                    this.cursorColumn = s.length();
                    List<String> temp = new LinkedList<>();
                    for (int i = 0; i < this.lines.length; i++) {
                        if (i != this.cursorRow) {
                            temp.add(this.lines[i]);
                        }
                    }
                    this._editString(String.join("\n", temp));
                    this.cursorRow--;
                } else {
                    String newline = line.substring(0, this.cursorColumn - 1) + line.substring(this.cursorColumn, line.length());
                    this.lines[this.cursorRow] = newline;
                    this._editString(String.join("\n", this.lines));
                    this.cursorColumn -= 1;
                }
                return;
            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_NUMPADENTER:
                if (this.cursorRow < this.maxRows) {
                    this.insertIntoCurrent("\n");
                    this.cursorColumn = 0;
                    this.cursorRow += 1;
                }
                return;
            case Keyboard.KEY_LEFT:
                this.cursorColumn--;
                if (this.cursorColumn < 0) {
                    if (this.cursorRow > 0) {
                        this.cursorRow--;
                        this.cursorColumn = this.lines[this.cursorRow].length();
                    } else {
                        this.cursorColumn = 0;
                    }
                }
                return;
            case Keyboard.KEY_RIGHT:
                line_width = this.lines[this.cursorRow].length();
                this.cursorColumn++;
                if (this.cursorRow < last_line) {
                    if (this.cursorColumn > line_width || this.cursorColumn > 40) {
                        this.cursorColumn = 0;
                        this.moveCursorToRow(this.cursorRow + 1);
                    }
                } else {
                    if (this.cursorColumn > line_width) {
                        this.cursorColumn = line_width;
                    }
                }
                return;
            case Keyboard.KEY_UP:
                this.moveCursorToRow(this.cursorRow - 1);
                return;
            case Keyboard.KEY_DOWN:
                this.moveCursorToRow(this.cursorRow + 1);
                return;
            case Keyboard.KEY_HOME:
                this.cursorColumn = 0;
                return;
            case Keyboard.KEY_END:
                this.cursorColumn = this.lines[this.cursorRow].length();
                return;
            case Keyboard.KEY_PRIOR:
                this.moveCursorToRow(0);
                return;
            case Keyboard.KEY_NEXT:
                this.moveCursorToRow(this.lines.length - 1);
                return;
            default:
                if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    // allow typing until the (proportional font) hits the side
                    String typedString = Character.toString(typedChar);
                    String s = this.lines[this.cursorRow] + typedString;
                    if (this.fontRenderer.getStringWidth(s) < this.width) {
                        this.insertIntoCurrent(typedString);
                        this.cursorColumn++;
                    }
                }
        }
    }

    private void moveCursorToRow(int row) {
        this.cursorRow = row;
        int num_lines = this.lines.length;
        if (this.cursorRow < 0) this.cursorRow = 0;
        else if (this.cursorRow >= num_lines) this.cursorRow = num_lines - 1;
        else if (this.cursorRow > this.maxRows) this.cursorRow = this.maxRows;
        this.fixCursorColumn();
    }

    private void fixCursorColumn() {
        int num_lines = this.lines.length;
        int line_width;
        if (this.cursorRow == num_lines) {
            line_width = 0;
        } else {
            line_width = this.lines[this.cursorRow].length();
        }
        if (this.cursorColumn > line_width) this.cursorColumn = line_width;
    }


    /**
     * Processes any text getting inserted into the current page, enforcing the page size limit
     */
    private void insertIntoCurrent(String text) {
        String line = this.lines[this.cursorRow];
        String newline = line.substring(0, this.cursorColumn) + text + line.substring(this.cursorColumn, line.length());
        this.lines[this.cursorRow] = newline;
        this._editString(String.join("\n", this.lines));
    }

    /**
     * Called when mouse is clicked, regardless as to whether it is over this button or not.
     */
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        int modX = mouseX - xPosition;
        int modY = mouseY - yPosition;
        boolean inside = modX > 0 && modY > 0 && modX < width && modY < height;

        this.setFocused(inside);

        if (!inside || mouseButton != 0) {
            return;
        }

        int row = modY / this.fontRenderer.FONT_HEIGHT;
        if (row >= this.lines.length) {
            row = this.lines.length - 1;
        } else if (row < 0) {
            row = 0;
        }
        this.cursorRow = row;
        String line = this.lines[this.cursorRow];
        int width = 0;
        boolean set = false;
        for (int i = 0; i < line.length(); i++) {
            width += this.fontRenderer.getCharWidth(line.charAt(i));
            if (width > modX) {
                this.cursorColumn = i;
                set = true;
                break;
            }
        }
        if (!set) this.cursorColumn = line.length();
        this.fixCursorColumn();
    }

    public void setCursorPosition(int column, int row) {
        this.cursorColumn = column;
        this.cursorRow = row;
    }

    /**
     * Sets focus to this gui element
     */
    public void setFocused(boolean isFocusedIn) {
        if (isFocusedIn && !this.isFocused) {
            this.cursorCounter = 0;
        }

        this.isFocused = isFocusedIn;
    }
}
