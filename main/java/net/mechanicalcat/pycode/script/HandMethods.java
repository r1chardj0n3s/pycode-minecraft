package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.entities.HandEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.FMLLog;
import org.python.core.ArgParser;
import org.python.core.PyObject;

import javax.print.AttributeException;

public class HandMethods extends BaseMethods {
    private HandEntity hand;
    private BlockPos storedPos;
    private EnumFacing storedFacing;

    public HandMethods(HandEntity hand, EntityPlayer player) {
        super(hand.getEntityWorld(), player);
        this.hand = hand;
    }

    public void storePos() {
        this.storedPos = this.hand.getPosition();
        this.storedFacing = this.hand.getHorizontalFacing();
    }
    public void recallPos() {
        this.hand.moveToBlockPosAndAngles(this.storedPos,
                this.storedFacing.getHorizontalAngle(), 0);
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

    public void sidle() {
        this.sidle(1);
    }
    public void sidle(float distance) {
        Vec3d pos = this.hand.getPositionVector();
        float rotation = this.hand.rotationYaw - 90;
        float f1 = -MathHelper.sin(rotation * 0.017453292F);
        float f2 = MathHelper.cos(rotation * 0.017453292F);
        pos = pos.addVector(distance * f1, 0, distance * f2);
        this.hand.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
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

    private Block getBlock(String blockName) throws BlockTypeError {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(blockName));
        FMLLog.info("getBlock asked for '%s', got '%s'", blockName, block.getUnlocalizedName());
        if (block.getUnlocalizedName().equals("tile.air") && !blockName.equals("air")) {
            throw new BlockTypeError(blockName);
        }
        return block;
    }

    public void put(String blockName) throws BlockTypeError {
        Block block = this.getBlock(blockName);
        IBlockState block_state = block.getDefaultState();
        BlockPos pos = this.hand.getPosition();
        EnumFacing facing = this.hand.getHorizontalFacing();
        EnumFacing opposite = facing.getOpposite();
        BlockPos faced = pos.add(facing.getDirectionVec());

        // TODO make .fine()
        FMLLog.info("Putting %s at %s", block, pos);

        if (block instanceof BlockDoor) {
            ItemDoor.placeDoor(this.world, faced, facing, block, true);
        } else if (block instanceof BlockBed) {
            BlockPos headpos = faced.offset(facing);
            if (this.world.getBlockState(faced.down()).isSideSolid(this.world, faced.down(), EnumFacing.UP) &&
                    this.world.getBlockState(headpos.down()).isSideSolid(this.world, headpos.down(), EnumFacing.UP)) {
                block_state = block_state
                        .withProperty(BlockBed.OCCUPIED, false).withProperty(BlockBed.FACING, facing)
                        .withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);
                if (this.world.setBlockState(faced, block_state, 11)) {
                    block_state = block_state.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
                    this.world.setBlockState(headpos, block_state, 11);
                }
            }
        } else {
            try {
                // TODO this only honors compass facings and not up/down
                PropertyDirection direction = (PropertyDirection) block.getClass().getField("FACING").get(block);
                if (this.world.isAirBlock(faced)) {
                    pos = faced;
                    // check whether the next pos along (pos -> faced -> farpos) is solid (attachable)
                    BlockPos farpos = faced.add(facing.getDirectionVec());
                    if (this.hand.worldObj.isSideSolid(farpos, opposite, true)) {
                        // attach in faced pos on farpos
                        block_state = block_state.withProperty(direction, opposite);
                    }
                } else {
                    if (this.hand.worldObj.isSideSolid(faced, opposite, true)) {
                        // attach in current pos on faced pos
                        block_state = block_state.withProperty(direction, opposite);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                pos = faced;
            }
            this.world.setBlockState(pos, block_state);
        }
    }

    public void water() {
        this.hand.code.water(this.hand.getPosition().add(this.hand.getHorizontalFacing().getDirectionVec()));
    }

    public void lava() {
        this.hand.code.lava(this.hand.getPosition().add(this.hand.getHorizontalFacing().getDirectionVec()));
    }

    public void clear() {
        this.hand.code.clear(this.hand.getPosition().add(this.hand.getHorizontalFacing().getDirectionVec()));
    }

    public void line(int distance, String blockName) throws BlockTypeError {
        Block block = getBlock(blockName);
        IBlockState block_state = block.getDefaultState();
        BlockPos pos = this.hand.getPosition();
        Vec3i direction = this.hand.getHorizontalFacing().getDirectionVec();
        for (int i=0; i<distance; i++) {
            pos = pos.add(direction);
            this.world.setBlockState(pos, block_state);
        }
    }

    public void ladder(int height, String blockName) throws BlockTypeError {
        Block block = getBlock(blockName);
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

    public void floor(int width, int depth, String blockName) throws BlockTypeError {
        BlockPos pos = this.hand.getPosition();
        EnumFacing facing = this.hand.getHorizontalFacing();
        this.floor(width, depth, blockName, pos.offset(facing), facing);
    }

    private void floor(int width, int depth, String blockName, BlockPos pos, EnumFacing facing) throws BlockTypeError {
        Block block = getBlock(blockName);
        IBlockState block_state = block.getDefaultState();
        Vec3i front = facing.getDirectionVec();
        Vec3i side = facing.rotateY().getDirectionVec();
        for (int j=0; j < width; j++) {
            BlockPos set = pos.add(side.getX() * j, 0, side.getZ() * j);
            for (int i = 0; i < depth; i++) {
                this.world.setBlockState(set, block_state);
                set = set.add(front);
            }
        }
    }

    public void wall(int depth, int height, String blockName) throws BlockTypeError {
        BlockPos pos = this.hand.getPosition();
        EnumFacing facing = this.hand.getHorizontalFacing();
        wall(depth, height, blockName, pos.offset(facing), facing);
    }

    private void wall(int depth, int height, String blockName, BlockPos pos, EnumFacing facing) throws BlockTypeError {
        Block block = getBlock(blockName);
        IBlockState block_state = block.getDefaultState();
        Vec3i front = facing.getDirectionVec();
        for (int j=0; j<height; j++) {
            BlockPos set = pos.add(0, j, 0);
            for (int i = 0; i < depth; i++) {
                this.world.setBlockState(set, block_state);
                set = set.add(front);
            }
        }
    }

    public void cube(int width, int height, int depth, String blockName) throws BlockTypeError {
        BlockPos pos = this.hand.getPosition();
        EnumFacing facing = this.hand.getHorizontalFacing();
        pos = pos.offset(facing);
        this.floor(width, depth, blockName, pos, facing);
        this.floor(width, depth, blockName, pos.offset(EnumFacing.UP, height), facing);
        for (int i=0; i<4; i++) {
            this.wall(depth, height, blockName, pos, facing);
            pos = pos.offset(facing, depth-1);
            facing = facing.rotateY();
        }
    }

    public void circle(int radius, String blockName) throws BlockTypeError {
        circle(radius, blockName, false);
    }
    public void disk(int radius, String blockName) throws BlockTypeError {
        circle(radius, blockName, true);
    }

    private void circle(int radius, String blockName, boolean fill) throws BlockTypeError {
        Block block = getBlock(blockName);
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

    public void ellipse(int radius_x, int radius_z, String blockName, boolean fill) throws BlockTypeError {
        Block block = getBlock(blockName);
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
