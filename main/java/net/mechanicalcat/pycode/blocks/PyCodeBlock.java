package net.mechanicalcat.pycode.blocks;

import net.mechanicalcat.pycode.Reference;
import net.mechanicalcat.pycode.init.PyCodeItems;
import net.mechanicalcat.pycode.tileentity.PyCodeBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import javax.script.ScriptException;


public final class PyCodeBlock extends Block implements ITileEntityProvider {
    public PyCodeBlock() {
        super(Material.CLAY);
        setUnlocalizedName(Reference.PyCodeBlocks.PYCODE.getUnlocalizedName());
        setRegistryName(Reference.PyCodeBlocks.PYCODE.getRegistryName());
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setHardness(1.0f);
    }

    // TODO onBlockPlaced and harvestBlock to activate/deactivate the engine? or maybe the tile entity is managed??

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            // don't run on the client
            return true;
        }
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof PyCodeBlockTileEntity) {
            PyCodeBlockTileEntity code_block = (PyCodeBlockTileEntity) entity;
            if (heldItem != null) {
                Item held_item = heldItem.getItem();
                if (held_item == PyCodeItems.python_wand) {
                    try {
                        code_block.runCode(playerIn);
                    } catch (ScriptException e) {
                        System.out.println("Error running code: " + e.getMessage());
                    }
                } else if (held_item instanceof ItemWritableBook) {
                    NBTTagCompound bookData = heldItem.getTagCompound();
                    NBTTagList pages;
                    try {
                        // pages are all of type TAG_String == 8
                        pages = bookData.getTagList("pages", 8);
                    } catch (NullPointerException e) {
                        // this should not happen!
                        System.out.println("Could not get pages from the book!?");
                        return true;
                    }
                    // collapse the pages into one string
                    StringBuilder sbStr = new StringBuilder();
                    for(int i = 0;i<pages.tagCount();i++) {
                        String s = pages.getStringTagAt(i);
                        System.out.println("Line: '" + s + "'");
                        if (i > 0) sbStr.append("\n");
                        sbStr.append(s);
                    }
                    code_block.setCode(sbStr.toString());
                    System.out.println("Code set to:" + code_block.getCode());
                    WorldServer worldserver = (WorldServer) worldIn;
                    worldserver.spawnParticle(EnumParticleTypes.CRIT, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5, 20, 0, 0, 0, .5, new int[0]);
                }
            }
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new PyCodeBlockTileEntity();
    }
}
