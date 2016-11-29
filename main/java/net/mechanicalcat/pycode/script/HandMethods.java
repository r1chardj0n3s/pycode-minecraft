package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.entities.HandEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HandMethods extends BaseMethods {
    private HandEntity hand;

    public HandMethods(HandEntity hand, EntityPlayer player) {
        super(hand.getEntityWorld(), player);
        this.hand = hand;
    }

    public void forward() {
        this.forward(1);
    }
    public void forward(float distance) {
        this.hand.moveForward(distance);
    }

    public void backward() {
        this.backward(1);
    }
    public void backward(float distance) {
        this.hand.moveForward(-distance);
    }

    public void face(String direction) {
        EnumFacing turned = EnumFacing.byName(direction);
        if (turned != null) {
            this.hand.setYaw(turned.getHorizontalAngle());
        }
    }

    public void left() {
        this.hand.moveYaw(-90);
    }
    public void right() {
        this.hand.moveYaw(90);
    }
    public void reverse() {
        this.hand.moveYaw(180);
    }

    public void up() {this.up(1); }
    public void up(float distance) {
        this.hand.moveEntity(0, distance, 0);
    }

    public void down() {this.down(1); }
    public void down(float distance) {
        this.hand.moveEntity(0, -distance, 0);
    }

    public void move(int x, int y, int z) {
        this.hand.moveEntity(x, y, z);
    }

    public void line(int distance, Block block) {
        IBlockState block_state = block.getDefaultState();
        BlockPos pos = this.hand.getPosition();
        Vec3i direction = this.hand.getHorizontalFacing().getDirectionVec();
        for (int i=0; i<distance; i++) {
            pos = pos.add(direction);
            this.world.setBlockState(pos, block_state);
        }
    }

    public void put(Block block) {
        IBlockState block_state = block.getDefaultState();
        BlockPos pos = this.hand.getPosition();
        EnumFacing facing = this.hand.getHorizontalFacing();
        BlockPos faced = pos.add(facing.getDirectionVec());
        try {
            PropertyDirection direction = (PropertyDirection)block.getClass().getField("FACING").get(block);
            if (this.world.isAirBlock(faced)) {
                pos = faced;
            } else {
                // attach
                block_state = block_state.withProperty(direction, facing.getOpposite());
            }
        } catch (NoSuchFieldException|IllegalAccessException e) {
            pos = faced;
        }
        this.world.setBlockState(pos, block_state);
    }

    public void door(BlockDoor block) {
        IBlockState block_state = block.getDefaultState();
        BlockPos pos = this.hand.getPosition();
        Vec3i direction = this.hand.getHorizontalFacing().getDirectionVec();
        pos = pos.add(direction);
        block_state = block_state.withProperty(BlockDoor.FACING, this.hand.getHorizontalFacing());
        block_state = block_state.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER);
        this.world.setBlockState(pos, block_state);
        block_state = block_state.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
        pos = pos.add(0, 1, 0);
        this.world.setBlockState(pos, block_state);
    }

    public void ladder(int height, Block block) {
        IBlockState block_state = block.getDefaultState().withProperty(BlockLadder.FACING,
                this.hand.getHorizontalFacing().getOpposite());
        BlockPos pos = this.hand.getPosition();
        Vec3i direction = this.hand.getHorizontalFacing().getDirectionVec();
        pos = pos.add(direction);
        for (int i=0; i<height; i++) {
            this.world.setBlockState(pos, block_state);
            pos = pos.add(0, 1, 0);
        }
    }

    public void water() {
        this.water(this.hand.getPosition().add(this.hand.getHorizontalFacing().getDirectionVec()));
    }

    public void lava() {
        this.lava(this.hand.getPosition().add(this.hand.getHorizontalFacing().getDirectionVec()));
    }

    public void clear() {
        this.clear(this.hand.getPosition().add(this.hand.getHorizontalFacing().getDirectionVec()));
    }

    public void circle(int radius, Block block, boolean fill) {
        IBlockState block_state = block.getDefaultState();
        BlockPos pos = this.hand.getPosition();

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
        BlockPos pos = this.hand.getPosition();

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
}
