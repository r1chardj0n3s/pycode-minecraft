package net.mechanicalcat.pycode.gui;

import io.netty.buffer.Unpooled;
import net.mechanicalcat.pycode.script.PythonCode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GuiPythonBook extends GuiScreen {
    static final ResourceLocation texture = new ResourceLocation("pycode:textures/gui/code_book.png");

    // texture dimensions
    private static final int TEX_WIDTH = 334;
    private static final int TEX_HEIGHT = 238;

    // TEXTURE LOCATIONS
    private static final int BOOK_PX_WIDTH = 334;   // pixel width of the entire book
    private static final int BOOK_PX_HEIGHT = 213;  // pixel height of the entire book

    // TEXT AREA IN TEXTURE
    private static final int EDITOR_PX_WIDTH = 224;
    private static final int EDITOR_PX_HEIGHT = 190;
    private static final int EDITOR_PX_TOP = 11;
    private static final int EDITOR_PX_LEFT = 44;

    // BUTTONS LOCATION
    private static final int BUTTONS_PX_LEFT = 296;
    private static final int BUTTONS_PX_TOP = 90;

    // PAGE LOCATION
    private static final int LOC_PX_LEFT = 282;
    private static final int LOC_PX_TOP = 15;
    private static final int LOC_PX_WIDTH = 44;

    private static final int TITLE_PX_LEFT = 10;
    private static final int TITLE_PX_BOTTOM = 192; // bottom because we render upwards

    private int xPosition;
    private int yPosition;

//    private final EntityPlayer editingPlayer;
    private final ItemStack bookObj;
    private NBTTagList bookPages;
    private String bookTitle;
    private int bookTotalPages = 1;
    private boolean bookIsModified;
    private int currPage = 0;

    private static final int BUTTON_DONE = 0;
    private static final int BUTTON_CANCEL = 1;
    private static final int BUTTON_NEXT = 2;
    private static final int BUTTON_PREV = 3;
    private GuiButton buttonDone;
    private GuiButton buttonCancel;
    private GuiButton buttonNextPage;
    private GuiButton buttonPreviousPage;
    private GuiTextArea pageEdit;
    private GuiVertTextField titleEdit;

    private static String TITLE_PLACEHOLDER = "Edit Book Title";

    private PythonCode code;
    private ScriptException codeException;
    private int timeToCheck;
    private String oldContent;
    private boolean codeChecked;

    public GuiPythonBook(EntityPlayer player, ItemStack book) {
        this.bookObj = book;
        this.bookIsModified = false;

        if (book.hasTagCompound()) {
            NBTTagCompound nbttagcompound = book.getTagCompound();
            this.bookPages = nbttagcompound.getTagList("pages", 8);
            this.bookTitle = nbttagcompound.getString("title");

            this.bookPages = this.bookPages.copy();
            this.bookTotalPages = this.bookPages.tagCount();

            if (this.bookTotalPages < 1) {
                this.bookTotalPages = 1;
            }
        } else {
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag(new NBTTagString("\n"));
            this.bookTitle = "";
            this.bookTotalPages = 1;
        }

        if (this.bookTitle.isEmpty()) {
            this.bookTitle = TITLE_PLACEHOLDER;
        }

        this.code = new PythonCode();
        this.codeException = null;
        this.oldContent = "";
        this.codeChecked = false;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        // detetermine left side of book placement
        xPosition = (this.width - BOOK_PX_WIDTH) / 2;
        yPosition = 2;

        FMLLog.info("Rendering GuiPythonBook at %s,%s", xPosition, yPosition);

        // func_189646_b adds a button to the buttonList
        this.buttonDone = this.func_189646_b(new GuiButton(BUTTON_DONE,
                xPosition + BUTTONS_PX_LEFT, yPosition + BUTTONS_PX_TOP, 70, 20,
                I18n.format("gui.done", new Object[0])));
        this.buttonCancel = this.func_189646_b(new GuiButton(BUTTON_CANCEL,
                xPosition + BUTTONS_PX_LEFT, yPosition + BUTTONS_PX_TOP + 22, 70, 20,
                I18n.format("gui.cancel", new Object[0])));

        // TODO not sure why but these buttons seem to need to be offest by their width
        this.buttonNextPage = this.func_189646_b(
            new GuiPythonBook.NextPageButton(BUTTON_NEXT,
                    xPosition + LOC_PX_LEFT + 20, yPosition + LOC_PX_TOP + 25, true)
        );
        this.buttonPreviousPage = this.func_189646_b(
            new GuiPythonBook.NextPageButton(BUTTON_PREV,
                    xPosition + LOC_PX_LEFT, yPosition + LOC_PX_TOP + 25, false)
        );
        this.updateButtons();

        this.pageEdit = new GuiTextArea(1, this.fontRendererObj,
                xPosition + EDITOR_PX_LEFT, yPosition + EDITOR_PX_TOP, EDITOR_PX_WIDTH, EDITOR_PX_HEIGHT);
        String s = this.pageGetCurrent();
        this.pageEdit.setString(s);
        this.pageEdit.setFocused(true);
        this.pageEdit.setGuiResponder(new EditResponder(this));

        this.titleEdit = new GuiVertTextField(2, this.fontRendererObj, xPosition + TITLE_PX_LEFT, yPosition + TITLE_PX_BOTTOM, 100, 20);
        this.titleEdit.setFocused(false);
        this.titleEdit.setText(this.bookTitle);
        this.titleEdit.setEnableBackgroundDrawing(false);
        this.titleEdit.setTextColor(0);
    }

    @SideOnly(Side.CLIENT)
    class EditResponder implements GuiPageButtonList.GuiResponder {
        GuiPythonBook book;
        EditResponder(GuiPythonBook book) {
            this.book = book;
        }
        public void setEntryValue(int id, boolean value) { }

        public void setEntryValue(int id, float value) { }

        public void setEntryValue(int id, String value) {
            this.book.pageSetCurrent(value);
        }
    }

    public void updateScreen() {
        super.updateScreen();
        this.pageEdit.updateCursorCounter();
        this.titleEdit.updateCursorCounter();

        // test compilation?
        String content = pageEdit.getString();
        if (!this.oldContent.equals(content)) {
            this.codeException = null;
            this.timeToCheck = 60;
            this.codeChecked = false;
            this.oldContent = content;
        }
        if (!this.codeChecked && this.timeToCheck-- < 0) {
            this.codeChecked = true;
            try {
                this.code.check(content);
                this.codeException = null;
            } catch (ScriptException e) {
                this.codeException = e;
            }
        }
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
                this.pageEdit.setString(this.pageGetCurrent());
            }

            this.updateButtons();
        }
    }

    private void sendBookToServer() throws IOException {
        if (!this.bookIsModified || this.bookPages == null) {
            return;
        }
        FMLLog.fine("Writing book to server");

        while (this.bookPages.tagCount() > 1) {
            String s = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);
            if (!s.trim().isEmpty()) {
                break;
            }
            this.bookPages.removeTag(this.bookPages.tagCount() - 1);
        }
        this.bookObj.setTagInfo("pages", this.bookPages);
        String title = this.bookTitle;
        if (title.equals(TITLE_PLACEHOLDER)) title = "";
        this.bookObj.setTagInfo("title", new NBTTagString(title));

        PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
        packetbuffer.writeItemStackToBuffer(this.bookObj);
        this.mc.getConnection().sendPacket(new CPacketCustomPayload("MC|BEdit", packetbuffer));
    }

    // Notch hard-coded texture size to 256x256 in the built-in Gui code
    public static void drawTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        float f = 1F / (float)textureWidth;
        float f1 = 1F / (float)textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)(x), (double)(y + height), 0).tex((double)((float)(u) * f), (double)((float)(v + height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), 0).tex((double)((float)(u + width) * f), (double)((float)(v + height) * f1)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y), 0).tex((double)((float)(u + width) * f), (double)((float)(v) * f1)).endVertex();
        vertexbuffer.pos((double)(x), (double)(y), 0).tex((double)((float)(u) * f), (double)((float)(v) * f1)).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawString(text, (x - fontRendererIn.getStringWidth(text) / 2), y, color);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        drawTexturedRect(xPosition, yPosition, 0, 0, BOOK_PX_WIDTH, BOOK_PX_HEIGHT, TEX_WIDTH, TEX_HEIGHT);

        // draw the widgets
        // rebind the book texture for the cursor ... TODO MOVE IT!
        this.mc.getTextureManager().bindTexture(texture);
        this.pageEdit.drawEditor();
        this.titleEdit.drawTextBox();

        // render the page location
        // TODO consider using GuiLabel
        this.drawCenteredString(this.fontRendererObj, "Page",
                xPosition + LOC_PX_LEFT + LOC_PX_WIDTH / 2,
                yPosition + LOC_PX_TOP, 0);

        String page_pos = String.format("%d of %d", this.currPage + 1, this.bookTotalPages);
        this.drawCenteredString(this.fontRendererObj, page_pos,
                xPosition + LOC_PX_LEFT + LOC_PX_WIDTH / 2,
                yPosition + LOC_PX_TOP + this.fontRendererObj.FONT_HEIGHT, 0);

        if (this.codeException != null) {
            String err = this.codeException.getMessage();
            if (err == null) {
                err = this.codeException.getClass().getName();
            } else {
                // fracking Java encapsulation obsession I can't get to the gottamn detailMessage form the
                // ScriptException subclass
                Pattern p = Pattern.compile("^(\\p{Alpha}+: )(.+) in <script> at");
                Matcher m = p.matcher(err);
                if (m.find()) {
                    err = m.group(2);
                }
                if (err.startsWith("no viable alternative at input ")) {
                    err = "unexpected " + err.substring(31);
                }
            }

            // now draw a marker - TODO USE A TOOLTIP??!
            int row = this.codeException.getLineNumber() - this.currPage * this.pageEdit.maxRows - 1;
            int col = this.codeException.getColumnNumber();
            String[] lines = this.pageEdit.getLines();
            if (col > lines[row].length()) {
                col = lines[row].length();
            }
            // TODO magic 12 is???
            int x = this.pageEdit.xPosition + 12 + this.fontRendererObj.getStringWidth(lines[row].substring(0, col));
            int y = this.pageEdit.yPosition + (row + 1) * this.fontRendererObj.FONT_HEIGHT;
//            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//            this.mc.getTextureManager().bindTexture(texture);
//            // TODO THIS DOES NOT DISPLAY!!
//            this.drawTexturedModalRect(x, y, 45, 231, 10, 7);

            int w = this.fontRendererObj.getStringWidth(err);
            x -= w/2;
            y += 8;
            Gui.drawRect(x - 2, y - 2, x + w + 2, y + this.fontRendererObj.FONT_HEIGHT + 2, 0xfff1e2b8);
            this.fontRendererObj.drawString(err, x , y, 0);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.pageEdit.keyTyped(typedChar, keyCode);
        this.titleEdit.textboxKeyTyped(typedChar, keyCode);
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
        return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "\n";
    }

    /**
     * Sets the text of the current page as determined by currPage
     */
    private void pageSetCurrent(String text) {
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            this.bookPages.set(this.currPage, new NBTTagString(text));
            this.bookIsModified = true;
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.pageEdit.mouseClicked(mouseX, mouseY, mouseButton);
        this.titleEdit.mouseClicked(mouseX, mouseY, mouseButton);
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
    static class NextPageButton extends GuiButton {
        private final boolean isForward;

        public NextPageButton(int id, int x, int y, boolean isForward) {
            super(id, x, y, 18, 10, "");
            this.isForward = isForward;
        }

        /**
         * Draws this button to the screen.
         */
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition &&
                        mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(GuiPythonBook.texture);
                int x = 2;
                int y = 215;

                if (flag) {
                    x += 25;
                }

                if (!this.isForward) {
                    y += 13;
                }

                drawTexturedRect(this.xPosition, this.yPosition, x, y, 18, 10, TEX_WIDTH, TEX_HEIGHT);
            }
        }
    }
}