package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.entities.HandEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.FMLLog;
import org.python.core.Py;
import org.python.core.PyObject;


public class HandMethods extends BaseMethods {
    private HandEntity hand;

    public HandMethods(HandEntity hand, EntityPlayer player) {
        super(hand.getEntityWorld(), player);
        this.hand = hand;
    }

    public PyObject remember() {
        return new HandStateContextManager(this.hand);
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

    public PyObject put(PyObject[] args, String[] kws) {
        if (args.length < kws.length + 1) {
            throw Py.TypeError("Missing first argument blockName");
        }
        String blockName = args[0].asString();
        Block block;
        try {
            block = this.getBlock(blockName);
        } catch (BlockTypeError e) {
            throw Py.TypeError("Unknown block " + blockName);
        }
        IBlockState block_state = block.getDefaultState();
        boolean facingSet = false;
        BlockPos pos = this.hand.getPosition();
        EnumFacing handFacing = this.hand.getHorizontalFacing();
        EnumFacing opposite = handFacing.getOpposite();
        BlockPos faced = pos.add(handFacing.getDirectionVec());
        PropertyDirection direction;
        boolean replace = false;        // do not try to surface attach, always replace

        for (int i=0; i<kws.length; i++) {
            if (kws[i].equals("replace")) {
                replace = args[i + 1].asInt() != 0;
            } else if (kws[i].equals("color")) {
                String color = args[i + 1].toString();
                EnumDyeColor dye = PythonCode.COLORMAP.get(color);
                if (dye == null) {
                    throw Py.TypeError(blockName + " color " + color);
                }
                PropertyEnum<EnumDyeColor> prop;
                try {
                    prop = (PropertyEnum<EnumDyeColor>) block.getClass().getField("COLOR").get(block);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw Py.TypeError(blockName + " cannot be colored");
                }
                block_state = block_state.withProperty(prop, dye);
            } else if (kws[i].equals("facing")) {
                String s = args[i + 1].toString();
                EnumFacing facing;
                if (s.equals("left")) {
                    facing = handFacing.rotateYCCW();
                } else if (s.equals("right")) {
                    facing = handFacing.rotateY();
                } else if (s.equals("back")) {
                    facing = handFacing.getOpposite();
                } else {
                    facing = PythonCode.FACINGMAP.get(s);
                }
                if (facing == null) {
                    throw Py.TypeError("Invalid facing " + s);
                }
                try {
                    direction = (PropertyDirection) block.getClass().getField("FACING").get(block);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw Py.TypeError(blockName + " does not have facing");
                }
                block_state = block_state.withProperty(direction, facing);
                facingSet = true;
                pos = faced;
            } else if (kws[i].equals("half") && block instanceof BlockStairs) {
                String s = args[i + 1].toString();
                BlockStairs.EnumHalf half;
                switch (s) {
                    case "top":
                        half = BlockStairs.EnumHalf.TOP;
                        break;
                    case "bottom":
                        half = BlockStairs.EnumHalf.BOTTOM;
                        break;
                    default:
                        throw Py.TypeError(blockName + " unknown half " + s);
                }
                block_state = block_state.withProperty(BlockStairs.HALF, half);
            } else if (kws[i].equals("shape") && block instanceof BlockStairs) {
                String s = args[i + 1].toString();
                BlockStairs.EnumShape shape;
                switch (s) {
                    case "straight":
                        shape = BlockStairs.EnumShape.STRAIGHT;
                        break;
                    case "inner_left":
                        shape = BlockStairs.EnumShape.INNER_LEFT;
                        break;
                    case "inner_right":
                        shape = BlockStairs.EnumShape.INNER_RIGHT;
                        break;
                    case "outer_left":
                        shape = BlockStairs.EnumShape.OUTER_LEFT;
                        break;
                    case "outer_right":
                        shape = BlockStairs.EnumShape.OUTER_RIGHT;
                        break;
                    default:
                        throw Py.TypeError(blockName + " unknown shape " + s);
                }
                block_state = block_state.withProperty(BlockStairs.SHAPE, shape);
            } else {
                throw Py.TypeError("Unexpected keyword argument " + kws[i]);
            }
        }

        // if we haven't had an explicit facing set then try to determine a good one
        if (!facingSet) {
            // try to automatically determine facing from the hand facing
            try {
                direction = (PropertyDirection) block.getClass().getField("FACING").get(block);
                if (replace || this.world.isAirBlock(faced)) {
                    pos = faced;
                    // check whether the next pos along (pos -> faced -> farpos) is solid (attachable)
                    BlockPos farpos = faced.add(handFacing.getDirectionVec());
                    if (this.hand.worldObj.isSideSolid(farpos, opposite, true)) {
                        // attach in faced pos on farpos
                        block_state = block_state.withProperty(direction, opposite);
                        FMLLog.fine("attach in faced pos=%s on farpos=%s", pos, opposite);
                    }
                } else {
                    if (this.hand.worldObj.isSideSolid(faced, opposite, true)) {
                        // attach in current pos on faced pos
                        FMLLog.fine("attach in current pos=%s on faced pos=%s", pos, opposite);
                        block_state = block_state.withProperty(direction, opposite);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                FMLLog.fine("attach in current pos=%s no facing", pos);
                pos = faced;
            }
        }

        FMLLog.fine("Adding %s with state %s", block, block_state);
        this.put(pos, block, block_state);
        return Py.java2py(null);
    }

    private void put(BlockPos pos, Block block, IBlockState block_state) {
        EnumFacing facing = this.hand.getHorizontalFacing();

        FMLLog.fine("Putting %s at %s", block, pos);

        // handle special cases
        if (block instanceof BlockDoor) {
            ItemDoor.placeDoor(this.world, pos, facing, block, true);
        } else if (block instanceof BlockBed) {
            BlockPos headpos = pos.offset(facing);
            if (this.world.getBlockState(pos.down()).isSideSolid(this.world, pos.down(), EnumFacing.UP) &&
                    this.world.getBlockState(headpos.down()).isSideSolid(this.world, headpos.down(), EnumFacing.UP)) {
                block_state = block_state
                        .withProperty(BlockBed.OCCUPIED, false).withProperty(BlockBed.FACING, facing)
                        .withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);
                if (this.world.setBlockState(pos, block_state, 11)) {
                    block_state = block_state.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
                    this.world.setBlockState(headpos, block_state, 11);
                }
            }
        } else {
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
