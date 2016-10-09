package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.entities.TurtleEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TurtleMethods extends BaseMethods {
    private TurtleEntity turtle;
    private Block poop;

    public TurtleMethods(TurtleEntity turtle, EntityPlayer player) {
        super(player);
        this.turtle = turtle;
    }

    public void forward() {
        this.forward(1);
    }
    public void forward(float distance) {
//        if (this.poop != null) {
//            this.turtle.getEntityWorld().setBlockState(this.turtle.getPosition(), this.poop.getDefaultState());
//        }
        this.turtle.moveForward(distance);
    }

    public void left() {
        this.left(90);
    }
    public void reverse() {
        this.left(180);
    }
    private void left(float amount) {
        this.turtle.moveYaw(-amount);
    }

    public void right() {
        this.right(90);
    }
    private void right(float amount) {
        this.turtle.moveYaw(amount);
    }

    public void line(int distance, Block block) {
        // TODO in the direction faced
        IBlockState block_state = block.getDefaultState();
        World world = this.turtle.getEntityWorld();
        BlockPos pos = this.turtle.getPosition();
        for (int i=0; i<distance; i++) {
            world.setBlockState(pos.add(0, 0, i + 1), block_state);
        }
    }

//    public void setPoop(Block block) {
//        this.poop = block;
//    }
//
//    public void stopPoop() {
//        this.poop = null;
//    }

}
