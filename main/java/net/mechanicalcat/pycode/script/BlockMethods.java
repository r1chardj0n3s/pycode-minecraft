package net.mechanicalcat.pycode.script;


import net.mechanicalcat.pycode.tileentity.PyCodeBlockTileEntity;
import net.minecraft.entity.player.EntityPlayer;

public class BlockMethods extends BaseMethods {
    private PyCodeBlockTileEntity block;

    public BlockMethods(PyCodeBlockTileEntity block, EntityPlayer player) {
        super(player);
        this.block = block;
    }
}
