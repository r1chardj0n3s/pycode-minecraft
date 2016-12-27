package net.mechanicalcat.pycode.items;

import net.mechanicalcat.pycode.Reference;
import net.mechanicalcat.pycode.entities.HandEntity;
import net.mechanicalcat.pycode.script.PythonCode;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class HandItem extends Item {
    public HandItem() {
        setUnlocalizedName(Reference.PyCodeRegistrations.HAND.getUnlocalizedName());
        setRegistryName(Reference.PyCodeRegistrations.HAND.getRegistryName());
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) return;
        if (compound.hasKey(PythonCode.CODE_NBT_TAG)) tooltip.add("[has code]");
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stackIn, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }
        if (stackIn.stackSize != 0) {
            float yaw = player.getHorizontalFacing().getHorizontalAngle();
            NBTTagCompound compound = stackIn.getTagCompound();
            HandEntity entity = new HandEntity(world, compound,
                    pos.getX() + .5, pos.getY() + 1.0, pos.getZ() + .5, yaw);
            world.spawnEntityInWorld(entity);
            --stackIn.stackSize;
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }
}
