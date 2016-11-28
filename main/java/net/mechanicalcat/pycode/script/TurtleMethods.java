package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.entities.TurtleEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class TurtleMethods extends BaseMethods {
    private TurtleEntity turtle;
    private Block poop;

    public TurtleMethods(TurtleEntity turtle, EntityPlayer player) {
        super(turtle.getEntityWorld(), player);
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

    public void up() {this.up(1); }
    public void up(float distance) {
        this.turtle.moveEntity(0, distance, 0);
//        if (this.poop != null) {
//            this.turtle.getEntityWorld().setBlockState(this.turtle.getPosition(), this.poop.getDefaultState());
//        }
    }

    public void down() {this.up(1); }
    public void down(float distance) {
        this.turtle.moveEntity(0, -distance, 0);
//        if (this.poop != null) {
//            this.turtle.getEntityWorld().setBlockState(this.turtle.getPosition(), this.poop.getDefaultState());
//        }
    }


    public void line(int distance, Block block) {
        // TODO in the direction faced
        IBlockState block_state = block.getDefaultState();
        BlockPos pos = this.turtle.getPosition();
        Vec3i direction = this.turtle.getHorizontalFacing().getDirectionVec();
        for (int i=0; i<distance; i++) {
            pos = pos.add(direction);
            this.world.setBlockState(pos, block_state);
        }
    }

    public void circle(int radius, Block block, boolean fill) {
        IBlockState block_state = block.getDefaultState();
        BlockPos pos = this.turtle.getPosition();

        if (fill) {
            int r_squared = radius * radius;
            for (int y = -radius; y <= radius; y++) {
                int y_squared = y * y;
                for (int x = -radius; x <= radius; x++) {
                    if ((x * x) + y_squared <= r_squared) {
                        this.world.setBlockState(pos.add(x, 0, y), block_state);
                    }
                }
            }
        } else {
            int l = (int) (radius * Math.cos(Math.PI / 4));
            for (int x = 0; x <= l; x++) {
                int y = (int) Math.sqrt ((double) (radius * radius) - (x * x));
                this.world.setBlockState(pos.add(+x, 0, +y), block_state);
                this.world.setBlockState(pos.add(+y, 0, +x), block_state);
                this.world.setBlockState(pos.add(-y, 0, +x), block_state);
                this.world.setBlockState(pos.add(-x, 0, +y), block_state);
                this.world.setBlockState(pos.add(-x, 0, -y), block_state);
                this.world.setBlockState(pos.add(-y, 0, -x), block_state);
                this.world.setBlockState(pos.add(+y, 0, -x), block_state);
                this.world.setBlockState(pos.add(+x, 0, -y), block_state);
            }
        }
    }

    public void ellipse(int radius_x, int radius_z, Block block, boolean fill) {
        IBlockState block_state = block.getDefaultState();
        BlockPos pos = this.turtle.getPosition();

        if (fill) {
            for (int x=-radius_x; x <= radius_x; x++) {
                int dy = (int) (Math.sqrt((radius_z * radius_z) * (1.0 - (double)(x * x) / (double)(radius_x * radius_x))));
                for (int y=-dy; y <= dy; y++) {
                    this.world.setBlockState(pos.add(x, 0, y), block_state);
                }
            }
        } else {
            double radius_x_sq = radius_x * radius_x;
            double radius_z_sq = radius_z * radius_z;
            int x = 0, y = radius_z;
            double p, px = 0, py = 2 * radius_x_sq * y;

            this.world.setBlockState(pos.add(+x, 0, +y), block_state);
            this.world.setBlockState(pos.add(-x, 0, +y), block_state);
            this.world.setBlockState(pos.add(+x, 0, -y), block_state);
            this.world.setBlockState(pos.add(-x, 0, -y), block_state);

            // Region 1
            p = radius_z_sq - (radius_x_sq * radius_z) + (0.25 * radius_x_sq);
            while (px < py) {
                x++;
                px = px + 2 * radius_z_sq;
                if (p < 0) {
                    p = p + radius_z_sq + px;
                } else {
                    y--;
                    py = py - 2 * radius_x_sq;
                    p = p + radius_z_sq + px - py;

                }
                this.world.setBlockState(pos.add(+x, 0, +y), block_state);
                this.world.setBlockState(pos.add(-x, 0, +y), block_state);
                this.world.setBlockState(pos.add(+x, 0, -y), block_state);
                this.world.setBlockState(pos.add(-x, 0, -y), block_state);
            }

            // Region 2
            p = radius_z_sq*(x+0.5)*(x+0.5) + radius_x_sq*(y-1)*(y-1) - radius_x_sq*radius_z_sq;
            while (y > 0) {
                y--;
                py = py -2 * radius_x_sq;
                if (p > 0) {
                    p = p + radius_x_sq - py;
                } else {
                    x++;
                    px = px + 2 * radius_z_sq;
                    p = p + radius_x_sq - py + px;
                }
                this.world.setBlockState(pos.add(+x, 0, +y), block_state);
                this.world.setBlockState(pos.add(-x, 0, +y), block_state);
                this.world.setBlockState(pos.add(+x, 0, -y), block_state);
                this.world.setBlockState(pos.add(-x, 0, -y), block_state);
            }
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
