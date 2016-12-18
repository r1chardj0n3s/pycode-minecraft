package net.mechanicalcat.pycode.blocks;

import net.mechanicalcat.pycode.Reference;
import net.mechanicalcat.pycode.script.MyEntity;
import net.mechanicalcat.pycode.script.MyEntityLiving;
import net.mechanicalcat.pycode.script.MyEntityPlayer;
import net.mechanicalcat.pycode.tileentity.PyCodeBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public final class PythonBlock extends Block implements ITileEntityProvider {
    private boolean isOn = false;

    public PythonBlock() {
        super(Material.CLAY);
        setUnlocalizedName(Reference.PyCodeRegistrations.BLOCK.getUnlocalizedName());
        setRegistryName(Reference.PyCodeRegistrations.BLOCK.getRegistryName());
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setHardness(1.0f);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
                                    @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            // don't run on the client
            return true;
        }
        PyCodeBlockTileEntity code_block = this.getEntity(world, pos);
        if (code_block != null) {
            code_block.handleItemInteraction(world, playerIn, pos, heldItem);
        }
        return true;
    }

    @Nullable
    private PyCodeBlockTileEntity getEntity(World world, BlockPos pos) {
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof PyCodeBlockTileEntity) {
            return (PyCodeBlockTileEntity) entity;
        }
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new PyCodeBlockTileEntity();
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            // don't run on the client
            return;
        }
        PyCodeBlockTileEntity code_block = this.getEntity(world, pos);
        if (entity instanceof EntityPlayer) {
            code_block.handleEntityInteraction(new MyEntityPlayer((EntityPlayer) entity), "onPlayerWalk");
        } else if (entity instanceof EntityLivingBase) {
            code_block.handleEntityInteraction(new MyEntityLiving((EntityLivingBase)entity), "onEntityWalk");
        }
    }

//    public int tickRate(World world) {
//        return 10;
//    }

//    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
//    canProvidePower

//
//    @Override
//    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
//        super.onNeighborChange(world, pos, neighbor);
//    }


    // TODO onBlockPlaced and harvestBlock to activate/deactivate the engine? or maybe the tile entity is managed??
}
