package net.mechanicalcat.pycode.items;

import net.mechanicalcat.pycode.Reference;
import net.mechanicalcat.pycode.entities.HandEntity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HandItem extends Item {
    public HandItem() {
        setUnlocalizedName(Reference.PyCodeRegistrations.HAND.getUnlocalizedName());
        setRegistryName(Reference.PyCodeRegistrations.HAND.getRegistryName());
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stackIn, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (stackIn.stackSize != 0) {
                float yaw = player.getHorizontalFacing().getHorizontalAngle();
                world.spawnEntityInWorld(new HandEntity(world, pos.getX() + .5, pos.getY() + 1.0, pos.getZ() + .5, yaw));
                --stackIn.stackSize;
                return EnumActionResult.SUCCESS;
            } else {
                return EnumActionResult.FAIL;
            }
        }
        return EnumActionResult.PASS;
    }
}
