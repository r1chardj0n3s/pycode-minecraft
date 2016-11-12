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
        for (int i=0; i<distance; i++) {
            this.world.setBlockState(pos.add(0, 0, i + 1), block_state);
        }
    }

    public void circle(int radius, Block block, boolean filled) {
        this.ellipse(radius, radius, block, filled);
    }

    public void ellipse(int radius_x, int radius_z, Block block, boolean filled) {
        double ratio = radius_x / radius_z;
        int maxblocks_x, maxblocks_y;
        IBlockState block_state = block.getDefaultState();
        BlockPos pos = this.turtle.getPosition();

        if ((radius_x * 2) % 2 == 0) {
            maxblocks_x = (int)Math.ceil(radius_x - .5) * 2 + 1;
        } else {
            maxblocks_x = (int)Math.ceil(radius_x) * 2;
        }

        if ((radius_z * 2) % 2 == 0) {
            maxblocks_y = (int)Math.ceil(radius_z - .5) * 2 + 1;
        } else {
            maxblocks_y = (int)Math.ceil(radius_z) * 2;
        }

        for (int y = -maxblocks_y / 2 + 1; y <= maxblocks_y / 2 - 1; y++) {
            for (int x = -maxblocks_x / 2 + 1; x <= maxblocks_x / 2 - 1; x++) {
                boolean xfilled = false;

                if (filled) {
                    xfilled = this.filled(x, y, radius_x, ratio);
                } else {
                    xfilled = this.fatfilled(x, y, radius_x, ratio);
                }
//                    case "thin":
//                        xfilled = fatfilled(x, y, radius_x, ratio) &&
//                                !(fatfilled(x + (x > 0 ? 1 : -1), y, radius_x, ratio) &&
//                                  fatfilled(x, y + (y > 0 ? 1 : -1), radius_x, ratio));
//                        break;
//                }
                if (xfilled) this.world.setBlockState(pos.add(x, 0, y), block_state);
            }
        }
    }

    private double distance(double x, double y, double ratio) {
        return Math.sqrt((Math.pow(y * ratio, 2)) + Math.pow(x, 2));
    }

    private boolean filled(int x, int y, double radius, double ratio) {
        return distance(x, y, ratio) <= radius;
    }

    private boolean fatfilled(int x, int y, double radius, double ratio) {
        return filled(x, y, radius, ratio) && !(
               filled(x + 1, y, radius, ratio) &&
               filled(x - 1, y, radius, ratio) &&
               filled(x, y + 1, radius, ratio) &&
               filled(x, y - 1, radius, ratio) &&
               filled(x + 1, y + 1, radius, ratio) &&
               filled(x + 1, y - 1, radius, ratio) &&
               filled(x - 1, y - 1, radius, ratio) &&
               filled(x - 1, y + 1, radius, ratio)
        );
    }

//    public void setPoop(Block block) {
//        this.poop = block;
//    }
//
//    public void stopPoop() {
//        this.poop = null;
//    }

}
