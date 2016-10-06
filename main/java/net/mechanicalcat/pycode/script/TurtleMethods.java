package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.entities.TurtleEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TurtleMethods extends BaseMethods {
    private TurtleEntity turtle;
    private Block poop;

    public TurtleMethods(TurtleEntity turtle) {
        this.turtle = turtle;
    }

    public void forward() {
        this.forward(1);
    }
    public void forward(float distance) {
        if (this.poop != null) {
            this.turtle.getEntityWorld().setBlockState(this.turtle.getPosition(), this.poop.getDefaultState());
        }
        this.turtle.moveForward(distance);
    }

    public void left() {
        this.left(90);
    }
    public void left(float amount) {
        this.turtle.moveYaw(-amount);
    }

    public void right() {
        this.right(90);
    }
    public void right(float amount) {
        this.turtle.moveYaw(amount);
    }

    public void setPoop(Block block) {
        this.poop = block;
    }

    public void stopPoop() {
        this.poop = null;
    }

    public void setBlock(BlockPos pos, Block block) {
        this.turtle.getEntityWorld().setBlockState(pos, block.getDefaultState());

//        Block block = Block.REGISTRY.getObject(new ResourceLocation(recipe.getID()));
//        Item item = Item.REGISTRY.getObject(new ResourceLocation(recipe.getID()));
    }
}
