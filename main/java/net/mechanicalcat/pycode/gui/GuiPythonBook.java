package net.mechanicalcat.pycode.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import io.netty.buffer.Unpooled;
import net.mechanicalcat.pycode.items.PythonBookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class GuiPythonBook extends GuiScreen {
    static final ResourceLocation texture = new ResourceLocation("py:textures/gui/code_book.png");

    /** Update ticks since the gui was opened for cursor animation */
    private int updateCount;

    private static final int MAX_ROWS = 20;
    private static final int MAX_COLS = 40;

//    private final EntityPlayer editingPlayer;
    private final ItemStack bookObj;
    private NBTTagList bookPages;
    private int bookTotalPages = 1;
    private boolean bookIsModified;
    private int currPage = 0;

    private int cursorRow = 0;
    private int cursorColumn = 0;
    private String[] lines;

    private static final int BUTTON_DONE = 0;
    private static final int BUTTON_CANCEL = 1;
    private static final int BUTTON_NEXT = 2;
    private static final int BUTTON_PREV = 3;
    private GuiButton buttonDone;
    private GuiButton buttonCancel;
    private GuiButton buttonNextPage;
    private GuiButton buttonPreviousPage;

    public GuiPythonBook(EntityPlayer player, ItemStack book) {
        this.bookObj = book;
        this.bookIsModified = false;

        if (book.hasTagCompound()) {
            NBTTagCompound nbttagcompound = book.getTagCompound();
            this.bookPages = nbttagcompound.getTagList("pages", 8);

            this.bookPages = this.bookPages.copy();
            this.bookTotalPages = this.bookPages.tagCount();

            if (this.bookTotalPages < 1) {
                this.bookTotalPages = 1;
            }
        }

        if (this.bookPages == null) {
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag(new NBTTagString("\n"));
            this.bookTotalPages = 1;
        }

        this.setLines(this.pageGetCurrent().split("\n"));
    }

    private void setLines(String lines[]) {
        this.lines = lines;
        int last = this.lines.length - 1;
        if (this.lines[last].equals("")) {
            this.lines[last] = "\n";
        }
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        // func_189646_b adds a button to the buttonList
        int side = this.width / 2 + 252 / 2;
        this.buttonDone = this.func_189646_b(new GuiButton(BUTTON_DONE, side + 2, this.height / 2 - 24, 70, 20, I18n.format("gui.done", new Object[0])));
        this.buttonCancel = this.func_189646_b(new GuiButton(BUTTON_CANCEL, side + 2, this.height / 2 + 14, 70, 20, I18n.format("gui.cancel", new Object[0])));

        int i = (this.width - 252) / 2;
        this.buttonNextPage = this.func_189646_b(
            new GuiPythonBook.NextPageButton(BUTTON_NEXT, side - 44, 13, true)
        );
        this.buttonPreviousPage = this.func_189646_b(
            new GuiPythonBook.NextPageButton(BUTTON_PREV, i + 8, 13, false)
        );
        this.updateButtons();
    }

    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }

    private void updateButtons() {
        this.buttonNextPage.visible = true; // this.currPage < this.bookTotalPages - 1;
        this.buttonPreviousPage.visible = this.currPage > 0;
        this.buttonDone.visible = true;
        this.buttonCancel.visible = true;
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        boolean updateLines = false;
        if (button.enabled) {
            if (button.id == BUTTON_DONE) {
                this.sendBookToServer();
                this.mc.displayGuiScreen(null);
            } else if (button.id == BUTTON_NEXT) {
                if (this.currPage < this.bookTotalPages - 1) {
                    ++this.currPage;
                    updateLines = true;
                } else {
                    this.addNewPage();

                    if (this.currPage < this.bookTotalPages - 1) {
                        ++this.currPage;
                        updateLines = true;
                    }
                }
            } else if (button.id == BUTTON_PREV) {
                if (this.currPage > 0) {
                    --this.currPage;
                    updateLines = true;
                }
            } else if (button.id == BUTTON_CANCEL) {
                this.mc.displayGuiScreen(null);
            }

            if (updateLines) {
                this.setLines(this.pageGetCurrent().split("\n"));
            }

            this.updateButtons();
        }
    }

    private void sendBookToServer() throws IOException {
        if (!this.bookIsModified || this.bookPages == null) {
            return;
        }

        System.out.println("Writing book to server");

        while (this.bookPages.tagCount() > 1) {
            String s = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);

            if (!s.isEmpty()) {
                break;
            }

            this.bookPages.removeTag(this.bookPages.tagCount() - 1);
        }

        if (this.bookObj.hasTagCompound()) {
            NBTTagCompound nbttagcompound = this.bookObj.getTagCompound();
            nbttagcompound.setTag("pages", this.bookPages);
        } else {
            this.bookObj.setTagInfo("pages", this.bookPages);
        }

        PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
        packetbuffer.writeItemStackToBuffer(this.bookObj);
        this.mc.getConnection().sendPacket(new CPacketCustomPayload("MC|BEdit", packetbuffer));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int i = (this.width - 252) / 2;
        int j = 2;
        this.drawTexturedModalRect(i, 2, 0, 0, 252, 216);

        int line_width;
        if (this.cursorRow == this.lines.length) {
            line_width = 0;
        } else {
            line_width = this.lines[this.cursorRow].length();
        }

        // TODO debug stuff, make it go away
        String page_pos = "row:" + this.cursorRow + " col:" + this.cursorColumn + " lines:" + this.lines.length + " len:" + line_width;    // I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
        String content = "";

        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            content = this.bookPages.getStringTagAt(this.currPage);
        }

        content = content.replace("\n", "\u2424\n");

        // draw cursor
        if (this.cursorRow == this.lines.length) {
            // current line is empty
            line_width = 0;
        } else {
            line_width = this.fontRendererObj.getStringWidth(this.lines[this.cursorRow].substring(0, this.cursorColumn));
        }
        int cursor_x = i + 15 + line_width;
        int cursor_y = 24 + this.cursorRow * this.fontRendererObj.FONT_HEIGHT;
        if (this.updateCount / 6 % 2 == 0) {
            this.drawTexturedModalRect(cursor_x, cursor_y, 49, 217, 3, 11);
        } else {
            this.drawTexturedModalRect(cursor_x, cursor_y, 54, 217, 3, 11);
        }

        int stringWidth = this.fontRendererObj.getStringWidth(page_pos);
        this.fontRendererObj.drawString(page_pos, i - stringWidth + 252 / 2, 15, 0);
        this.fontRendererObj.drawSplitString(content, i + 17, 26, 230, 0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
        } else {
            int line_width;
            int last_line = this.lines.length - 1;
            switch (keyCode) {
                case Keyboard.KEY_BACK:
                    String line = this.lines[this.cursorRow];
                    if (this.cursorColumn == 0) {
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
                        this.pageSetCurrent(String.join("\n", temp));
                        this.cursorRow--;
                    } else {
                        String newline = line.substring(0, this.cursorColumn - 1) + line.substring(this.cursorColumn, line.length());
                        this.lines[this.cursorRow] = newline;
                        this.pageSetCurrent(String.join("\n", this.lines));
                        this.cursorColumn -= 1;
                    }
                    return;
                case Keyboard.KEY_RETURN:
                case Keyboard.KEY_NUMPADENTER:
                    if (this.cursorRow < MAX_ROWS) {
                        this.pageInsertIntoCurrent("\n");
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
                    this.cursorColumn++;
                    line_width = this.lines[this.cursorRow].length();
                    if (this.cursorRow < last_line) {
                        if (this.cursorColumn > line_width || this.cursorColumn > 40) {
                            this.cursorColumn = 0;
                            this.moveCursorToRow(this.cursorRow + 1);
                        }
                    } else {
                        if (this.cursorColumn > line_width) {
                            this.cursorColumn = line_width;
                        } else if (this.cursorColumn > MAX_COLS) {
                            this.cursorColumn = MAX_COLS;
                        }
                    }
                    return;
                case Keyboard.KEY_UP:
                    this.moveCursorToRow(this.cursorRow - 1);
                    return;
                case Keyboard.KEY_DOWN:
                    this.moveCursorToRow(this.cursorRow + 1);
                    return;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        this.pageInsertIntoCurrent(Character.toString(typedChar));
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
        else if (this.cursorRow > MAX_ROWS) this.cursorRow = MAX_ROWS;
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

    private void addNewPage() {
        if (this.bookPages != null && this.bookPages.tagCount() < 50) {
            this.bookPages.appendTag(new NBTTagString("\n"));
            ++this.bookTotalPages;
            this.bookIsModified = true;
        }
    }

    /**
     * Returns the entire text of the current page as determined by currPage
     */
    private String pageGetCurrent() {
        return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
    }

    /**
     * Sets the text of the current page as determined by currPage
     */
    private void pageSetCurrent(String text) {
        this.setLines(text.split("\n"));
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            this.bookPages.set(this.currPage, new NBTTagString(text));
            this.bookIsModified = true;
        }
    }

    /**
     * Processes any text getting inserted into the current page, enforcing the page size limit
     */
    private void pageInsertIntoCurrent(String text) {
        String line = this.lines[this.cursorRow];
        String newline = line.substring(0, this.cursorColumn) + text + line.substring(this.cursorColumn, line.length());
        this.lines[this.cursorRow] = newline;
        this.pageSetCurrent(String.join("\n", this.lines));
    }


    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            // TODO move cursor
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Executes the click event specified by the given chat component
     */
    protected boolean handleComponentClick(ITextComponent component) {
        ClickEvent clickevent = component.getStyle().getClickEvent();

        if (clickevent == null) {
            return false;
        } else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String s = clickevent.getValue();

            try {
                int i = Integer.parseInt(s) - 1;

                if (i >= 0 && i < this.bookTotalPages && i != this.currPage) {
                    this.currPage = i;
                    this.updateButtons();
                    return true;
                }
            } catch (Throwable var5) {
                ;
            }

            return false;
        } else {
            boolean flag = super.handleComponentClick(component);
            if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                this.mc.displayGuiScreen((GuiScreen)null);
            }
            return flag;
        }
    }

    @SideOnly(Side.CLIENT)
    static class NextPageButton extends GuiButton
    {
        private final boolean isForward;

        public NextPageButton(int id, int x, int y, boolean isForward) {
            super(id, x, y, 23, 13, "");
            this.isForward = isForward;
        }

        /**
         * Draws this button to the screen.
         */
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(GuiPythonBook.texture);
                int i = 0;
                int j = 217;

                if (flag) {
                    i += 23;
                }

                if (!this.isForward) {
                    j += 13;
                }

                this.drawTexturedModalRect(this.xPosition, this.yPosition, i, j, 23, 13);
            }
        }
    }
}