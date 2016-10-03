package net.mechanicalcat.pycode.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import io.netty.buffer.Unpooled;
import net.mechanicalcat.pycode.items.PythonBookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import java.util.List;


public class GuiPythonBook extends GuiScreen {
    static final ResourceLocation texture = new ResourceLocation("minecraft:textures/gui/book.png");

    /** Update ticks since the gui was opened for cursor animation */
    private int updateCount;

//    private final EntityPlayer editingPlayer;
    private final ItemStack bookObj;
    private NBTTagList bookPages;
    private int bookTotalPages = 1;
    private boolean bookIsModified;
    private int currPage = 0;

    private List<ITextComponent> cachedComponents;
    private int cachedPage = -1;

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

            if (this.bookTotalPages < 1)
            {
                this.bookTotalPages = 1;
            }
        }

        if (this.bookPages == null)
        {
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag(new NBTTagString(""));
            this.bookTotalPages = 1;
        }
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        // func_189646_b adds a button to the buttonList
        this.buttonDone = this.func_189646_b(new GuiButton(BUTTON_DONE, this.width / 2 - 100, 196, 98, 20, I18n.format("gui.done", new Object[0])));
        this.buttonCancel = this.func_189646_b(new GuiButton(BUTTON_CANCEL, this.width / 2 + 2, 196, 98, 20, I18n.format("gui.cancel", new Object[0])));

        int i = (this.width - 192) / 2;
        int j = 2;
        this.buttonNextPage = this.func_189646_b(
            new GuiPythonBook.NextPageButton(BUTTON_NEXT, i + 120, 156, true)
        );
        this.buttonPreviousPage = this.func_189646_b(
            new GuiPythonBook.NextPageButton(BUTTON_PREV, i + 38, 156, false)
        );
        this.updateButtons();
    }

    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }

    private void updateButtons()
    {
        this.buttonNextPage.visible = this.currPage < this.bookTotalPages - 1;
        this.buttonPreviousPage.visible = this.currPage > 0;
        this.buttonDone.visible = true;
        this.buttonCancel.visible = true;
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == BUTTON_DONE) {
                this.mc.displayGuiScreen(null);
                this.sendBookToServer();
            } else if (button.id == BUTTON_NEXT) {
                if (this.currPage < this.bookTotalPages - 1) {
                    ++this.currPage;
                } else {
                    this.addNewPage();

                    if (this.currPage < this.bookTotalPages - 1) {
                        ++this.currPage;
                    }
                }
            } else if (button.id == BUTTON_PREV) {
                if (this.currPage > 0) {
                    --this.currPage;
                }
            } else if (button.id == BUTTON_CANCEL) {
                this.mc.displayGuiScreen(null);
            }

            this.updateButtons();
        }
    }

    private void sendBookToServer() throws IOException {
        if (!this.bookIsModified || this.bookPages == null) {
            return;
        }

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
        int i = (this.width - 192) / 2;
        int j = 2;
        this.drawTexturedModalRect(i, 2, 0, 0, 192, 192);

        String s4 = I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
        String s5 = "";

        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            s5 = this.bookPages.getStringTagAt(this.currPage);
        }
        if (this.fontRendererObj.getBidiFlag()) {
            s5 = s5 + "_";
        } else if (this.updateCount / 6 % 2 == 0) {
            s5 = s5 + "" + TextFormatting.BLACK + "_";
        } else {
            s5 = s5 + "" + TextFormatting.GRAY + "_";
        }

        int j1 = this.fontRendererObj.getStringWidth(s4);
        this.fontRendererObj.drawString(s4, i - j1 + 192 - 44, 18, 0);
        this.fontRendererObj.drawSplitString(s5, i + 36, 34, 116, 0);
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
            switch (keyCode) {
                case Keyboard.KEY_BACK:
                    String s = this.pageGetCurrent();
                    if (!s.isEmpty()) {
                        this.pageSetCurrent(s.substring(0, s.length() - 1));
                    }
                    return;
                case Keyboard.KEY_RETURN:
                case Keyboard.KEY_NUMPADENTER:
                    this.pageInsertIntoCurrent("\n");
                    return;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        this.pageInsertIntoCurrent(Character.toString(typedChar));
                    }
            }
        }
    }

    private void addNewPage() {
        if (this.bookPages != null && this.bookPages.tagCount() < 50) {
            this.bookPages.appendTag(new NBTTagString(""));
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
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            this.bookPages.set(this.currPage, new NBTTagString(text));
            this.bookIsModified = true;
        }
    }

    /**
     * Processes any text getting inserted into the current page, enforcing the page size limit
     */
    private void pageInsertIntoCurrent(String text) {
        String s = this.pageGetCurrent();
        String s1 = s + text;
        int i = this.fontRendererObj.splitStringWidth(s1 + "" + TextFormatting.BLACK + "_", 118);

        if (i <= 128 && s1.length() < 256) {
            this.pageSetCurrent(s1);
        }
    }


    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            ITextComponent itextcomponent = this.getClickedComponentAt(mouseX, mouseY);
            if (itextcomponent != null && this.handleComponentClick(itextcomponent)) {
                return;
            }
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

    @Nullable
    public ITextComponent getClickedComponentAt(int p_175385_1_, int p_175385_2_) {
        if (this.cachedComponents == null) {
            return null;
        } else {
            int i = p_175385_1_ - (this.width - 192) / 2 - 36;
            int j = p_175385_2_ - 2 - 16 - 16;

            if (i >= 0 && j >= 0) {
                int k = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.cachedComponents.size());

                if (i <= 116 && j < this.mc.fontRendererObj.FONT_HEIGHT * k + k) {
                    int l = j / this.mc.fontRendererObj.FONT_HEIGHT;

                    if (l >= 0 && l < this.cachedComponents.size()) {
                        ITextComponent itextcomponent = (ITextComponent)this.cachedComponents.get(l);
                        int i1 = 0;

                        for (ITextComponent itextcomponent1 : itextcomponent) {
                            if (itextcomponent1 instanceof TextComponentString) {
                                i1 += this.mc.fontRendererObj.getStringWidth(((TextComponentString)itextcomponent1).getText());

                                if (i1 > i) {
                                    return itextcomponent1;
                                }
                            }
                        }
                    }

                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    static class NextPageButton extends GuiButton
    {
        private final boolean isForward;

        public NextPageButton(int p_i46316_1_, int p_i46316_2_, int p_i46316_3_, boolean p_i46316_4_) {
            super(p_i46316_1_, p_i46316_2_, p_i46316_3_, 23, 13, "");
            this.isForward = p_i46316_4_;
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
                int j = 192;

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