package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.entities.HandEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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

import javax.annotation.Nullable;
import java.util.HashMap;


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

    public void back() {
        this.back(1);
    }
    public void back(float distance) {
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

    private IBlockState getSlabBlock(String material) throws BlockTypeError {
        Block slab;
        if (PLANKTYPES.containsKey(material)) {
            slab = getBlock("wooden_slab");
            return slab.getDefaultState().withProperty(BlockPlanks.VARIANT, PLANKTYPES.get(material));
        } else if (STONETYPES.containsKey(material)) {
            slab = getBlock("stone_slab");
            return slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, STONETYPES.get(material));
        } else {
            return getBlock(material.concat("_slab")).getDefaultState();
        }
    }

    private IBlockState getStairBlock(String material) throws BlockTypeError {
        if (material.equals("cobblestone")) {
            return getBlock("stone_stairs").getDefaultState();
        } else {
            return getBlock(material.concat("_stairs")).getDefaultState();
        }
    }

    // this is just a little crazypants
    private String[] s(String ... strings) {
        return strings;
    }

    public PyObject put(PyObject[] args, String[] kws) {
        ArgParser r = new ArgParser("put", s("blockname"), s("color", "facing", "type", "half", "shape", "seamless"));
        r.parse(args, kws);
        this.put(this.hand.getFacedPos(), getBlockVariant(r));
        return Py.java2py(null);
    }

    private IBlockState getBlockVariant(ArgParser spec) {
        String blockName = spec.getString("blockname");
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
        BlockPos faced = this.hand.getFacedPos();
        PropertyDirection direction;

        if (spec.has("color")) {
            String color = spec.getString("color");
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
        }

        if (spec.has("facing")) {
            String s = spec.getString("facing");
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
                direction = (PropertyDirection) block_state.getBlock().getClass().getField("FACING").get(block_state.getBlock());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw Py.TypeError(blockName + " does not have facing");
            }
            block_state = block_state.withProperty(direction, facing);
            facingSet = true;
        }

        if (spec.has("type"))
            if (block_state.getBlock() instanceof BlockPlanks) {
                String s = spec.getString("type");
                BlockPlanks.EnumType type = PLANKTYPES.get(s);
                if (s == null) throw Py.TypeError(blockName + " unknown type " + s);
                block_state = block_state.withProperty(BlockPlanks.VARIANT, type);
            } else if (block_state.getBlock() instanceof BlockStoneSlab) {
                String s = spec.getString("type");
                BlockStoneSlab.EnumType type = STONETYPES.get(s);
                if (s == null) throw Py.TypeError(blockName + " unknown type " + s);
                block_state = block_state.withProperty(BlockStoneSlab.VARIANT, type);
        }

        if (spec.has("half")) {
            if (block_state.getBlock() instanceof BlockStairs) {
                String s = spec.getString("half");
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
            } else if (block_state.getBlock() instanceof BlockSlab) {
                String s = spec.getString("half");
                BlockSlab.EnumBlockHalf half;
                switch (s) {
                    case "top":
                        half = BlockSlab.EnumBlockHalf.TOP;
                        break;
                    case "bottom":
                        half = BlockSlab.EnumBlockHalf.BOTTOM;
                        break;
                    default:
                        throw Py.TypeError(blockName + " unknown half " + s);
                }
                block_state = block_state.withProperty(BlockSlab.HALF, half);
            }
        }

        if (spec.has("seamless") && block_state.getBlock() instanceof BlockStoneSlab) {
            block_state = block_state.withProperty(BlockStoneSlab.SEAMLESS, spec.getBoolean("seamless"));
        }

        if (spec.has("shape") && block_state.getBlock() instanceof BlockStairs) {
            String s = spec.getString("shape");
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
        }

        // if we haven't had an explicit facing set then try to determine a good one
        if (!facingSet) {
            try {
                direction = (PropertyDirection) block_state.getBlock().getClass().getField("FACING").get(block_state.getBlock());
                if (this.world.isAirBlock(faced)) {
                    // check whether the next pos along (pos -> faced -> farpos) is solid (attachable)
                    BlockPos farpos = faced.add(handFacing.getDirectionVec());
                    if (this.hand.worldObj.isSideSolid(farpos, opposite, true)) {
                        // attach in faced pos on farpos
                        block_state = block_state.withProperty(direction, opposite);
                        FMLLog.fine("attach in faced pos=%s on farpos=%s", pos, opposite);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                FMLLog.fine("attach in current pos=%s no facing", pos);
            }
        }

        return block_state;
    }

    private void put(BlockPos pos, IBlockState block_state) {
        Block block = block_state.getBlock();
        EnumFacing facing = this.hand.getHorizontalFacing();

        FMLLog.info("Putting %s at %s", block_state, pos);

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

    public PyObject line(PyObject[] args, String[] kws) {
        ArgParser r = new ArgParser("line", s("distance", "blockname"),
                s("color", "facing", "type", "half", "shape"));
        r.parse(args, kws);
        int distance = r.getInteger("distance");
        IBlockState block_state = getBlockVariant(r);
        BlockPos pos = this.hand.getPosition();
        Vec3i direction = this.hand.getHorizontalFacing().getDirectionVec();
        for (int i=0; i<distance; i++) {
            pos = pos.add(direction);
            this.world.setBlockState(pos, block_state);
        }
        return Py.java2py(null);
    }

    public void ladder(PyObject[] args, String[] kws) {
        ArgParser r = new ArgParser("line", s("height", "blockname"),
                s("color", "facing", "type", "half", "shape"));
        r.parse(args, kws);
        int height = r.getInteger("height");
        IBlockState block_state = getBlockVariant(r);
        BlockPos pos = this.hand.getFacedPos();
        for (int i=0; i<height; i++) {
            this.world.setBlockState(pos, block_state);
            pos = pos.add(0, 1, 0);
        }
    }

    public void floor(PyObject[] args, String[] kws) {
        ArgParser r = new ArgParser("line", s("width", "depth", "blockname"),
                s("color", "facing", "type", "half", "shape"));
        r.parse(args, kws);
        IBlockState block_state = getBlockVariant(r);
        BlockPos pos = this.hand.getFacedPos();
        EnumFacing facing = this.hand.getHorizontalFacing();
        this.floor(r.getInteger("width"), r.getInteger("depth"), block_state, pos, facing);
    }

    private void floor(int width, int depth, IBlockState block_state, BlockPos pos, EnumFacing facing) {
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

    public void wall(PyObject[] args, String[] kws) {
        ArgParser r = new ArgParser("line", s("depth", "height", "blockname"),
                s("color", "facing", "type", "half", "shape"));
        r.parse(args, kws);
        IBlockState block_state = getBlockVariant(r);
        BlockPos pos = this.hand.getFacedPos();
        EnumFacing facing = this.hand.getHorizontalFacing();
        this.wall(r.getInteger("depth"), r.getInteger("height"), block_state, pos, facing);
    }

    private void wall(int depth, int height, IBlockState block_state, BlockPos pos, EnumFacing facing) {
        Vec3i front = facing.getDirectionVec();
        for (int j=0; j<height; j++) {
            BlockPos set = pos.add(0, j, 0);
            for (int i = 0; i < depth; i++) {
                this.world.setBlockState(set, block_state);
                set = set.add(front);
            }
        }
    }

    public void cube(PyObject[] args, String[] kws) {
        ArgParser r = new ArgParser("line", s("width", "depth", "height", "blockname"),
                s("color", "facing", "type", "half", "shape"));
        r.parse(args, kws);
        int width = r.getInteger("width");
        int depth = r.getInteger("depth");
        int height = r.getInteger("height");
        IBlockState block_state = getBlockVariant(r);
        BlockPos pos = this.hand.getFacedPos();
        EnumFacing facing = this.hand.getHorizontalFacing();
        this.floor(width, depth, block_state, pos, facing);
        this.floor(width, depth, block_state, pos.offset(EnumFacing.UP, height), facing);
        for (int i=0; i<4; i++) {
            this.wall(depth, height, block_state, pos, facing);
            pos = pos.offset(facing, depth-1);
            facing = facing.rotateY();
        }
    }

    public void circle(PyObject[] args, String[] kws) {
        ArgParser r = new ArgParser("line", s("radius", "blockname"),
                s("color", "facing", "type", "half", "shape", "fill"));
        r.parse(args, kws);
        int radius = r.getInteger("radius");
        IBlockState block_state = getBlockVariant(r);
        BlockPos pos = this.hand.getPosition();

        if (r.getBoolean("fill", false)) {
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

    public void ellipse(PyObject[] args, String[] kws) {
        ArgParser r = new ArgParser("line", s("radius_x", "radius_z", "blockname"),
                s("color", "facing", "type", "half", "shape", "fill"));
        r.parse(args, kws);
        int radius_x = r.getInteger("radius_x");
        int radius_z = r.getInteger("radius_z");
        IBlockState block_state = getBlockVariant(r);
        BlockPos pos = this.hand.getPosition();

        if (r.getBoolean("fill", false)) {
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

    // please don't ask why I coded the roofing to be EAST-facing predominantly
    // it made sense at the time
    public void roof(PyObject[] args, String[] kws) throws BlockTypeError {
        ArgParser r = new ArgParser("roof", s("width", "depth", "material"), s("style"));
        r.parse(args, kws);
        int aWidth = r.getInteger("width");
        int aDepth = r.getInteger("depth");
        BlockPos pos = this.hand.getPosition();

        if (aWidth < 2) {
            throw Py.TypeError("width must be > 1");
        }
        if (aDepth < 2) {
            throw Py.TypeError("depth must be > 1");
        }

        // alter pos, width and depth based on orientation so that the
        // code which always generates in the EAST facing will work
        int width, depth;
        switch (this.hand.getHorizontalFacing()) {
            case EAST:
                width = aWidth;
                depth = aDepth;
                pos = pos.add(1, 0, 0);
                break;
            case WEST:
                width = aWidth;
                depth = aDepth;
                pos = pos.add(-aDepth, 0, -(aWidth-1));
                break;
            case NORTH:
                width = aDepth;
                depth = aWidth;
                pos = pos.add(0, 0, -aDepth);
                break;
            case SOUTH:
                width = aDepth;
                depth = aWidth;
                pos = pos.add(-(aWidth-1), 0, 1);
                break;
            default:
                // should never happen - hand should always be horizontal
                return;
        }

        switch (r.getString("style", "hip")) {
            case "hip":
                hipRoof(r.getString("material"), width, depth, pos);
                break;
            case "gable":
                gableRoof(r.getString("material"), width, depth, pos, false);
                break;
            case "box-gable":
                gableRoof(r.getString("material"), width, depth, pos, true);
                break;
            default:
                throw Py.TypeError(String.format("unknown style '%s'", r.getString("style")));
        }
    }

    private static final HashMap<String, String> FILLER = new HashMap<>();
    private static final HashMap<String, BlockPlanks.EnumType> PLANKTYPES = new HashMap<>();
    private static final HashMap<String, BlockStoneSlab.EnumType> STONETYPES = new HashMap<>();
    static {
        FILLER.put("oak", "planks");
        FILLER.put("stone", "stone");
        FILLER.put("brick", "brick_block");
        FILLER.put("stone_brick", "stonebrick");
        FILLER.put("nether_brick", "nether_brick");
        FILLER.put("sandstone", "sandstone");
        FILLER.put("spruce", "planks");
        FILLER.put("birch", "planks");
        FILLER.put("jungle", "planks");
        FILLER.put("acacia", "planks");
        FILLER.put("dark_oak", "planks");
        FILLER.put("quartz", "quartz_block");
        FILLER.put("red_sandstone", "red_sandstone");
        FILLER.put("purpur", "purpur_block");
        PLANKTYPES.put("oak", BlockPlanks.EnumType.OAK);
        PLANKTYPES.put("spruce", BlockPlanks.EnumType.SPRUCE);
        PLANKTYPES.put("birch", BlockPlanks.EnumType.BIRCH);
        PLANKTYPES.put("jungle", BlockPlanks.EnumType.JUNGLE);
        PLANKTYPES.put("acacia", BlockPlanks.EnumType.ACACIA);
        PLANKTYPES.put("dark_oak", BlockPlanks.EnumType.DARK_OAK);
        STONETYPES.put("stone", BlockStoneSlab.EnumType.STONE);
        STONETYPES.put("sandstone", BlockStoneSlab.EnumType.SAND);
        STONETYPES.put("wood_old", BlockStoneSlab.EnumType.WOOD);
        STONETYPES.put("cobblestone", BlockStoneSlab.EnumType.COBBLESTONE);
        STONETYPES.put("brick", BlockStoneSlab.EnumType.BRICK);
        STONETYPES.put("stone_brick", BlockStoneSlab.EnumType.SMOOTHBRICK);
        STONETYPES.put("nether_brick", BlockStoneSlab.EnumType.NETHERBRICK);
        STONETYPES.put("quartz", BlockStoneSlab.EnumType.QUARTZ);
    }

    private IBlockState getRoofFiller(String material) throws BlockTypeError {
        String fillMaterial = FILLER.get(material);
        Block filler = getBlock(fillMaterial);
        filler.getDefaultState();
        if (fillMaterial.equals("planks") && !material.equals("oak")) {
            return filler.getDefaultState().withProperty(BlockPlanks.VARIANT, PLANKTYPES.get(material));
        }
        return filler.getDefaultState();
    }

    private void hipRoof(String material, int width, int depth, BlockPos pos) throws BlockTypeError {
        IBlockState stair = getStairBlock(material);
        stair = stair.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM);

        IBlockState fill_state = this.getRoofFiller(material);

        // always construct facing east; width is the Z axis and depth is the X axis
        EnumFacing facing = EnumFacing.EAST;

        BlockPos current;
        while (true) {
            for (int x=0; x < depth; x++) {
                for (int z=0; z < width; z++) {
                    IBlockState block_state = stair;
                    current = pos.add(x, 0, z);
                    if (x == 0) {
                        // bottom side
                        block_state = block_state.withProperty(BlockStairs.FACING, facing);
                        if (z == 0) {
                            block_state = block_state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
                        } else if (z == width-1) {
                            block_state = block_state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
                        }
                    } else if (x == depth-1) {
                        // top side
                        block_state = block_state.withProperty(BlockStairs.FACING, facing.getOpposite());
                        if (z == 0) {
                            block_state = block_state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
                        } else if (z == width-1) {
                            block_state = block_state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
                        }
                    } else if (z == 0) {
                        // left side
                        block_state = block_state.withProperty(BlockStairs.FACING, facing.rotateY());
                    } else if (z == width - 1) {
                        // right side
                        block_state = block_state.withProperty(BlockStairs.FACING, facing.rotateYCCW());
                    } else if (z < width-1) {
                        block_state = fill_state;
                    }
                    this.world.setBlockState(current, block_state);
                }
            }

            // move up a layer
            width -=2;
            depth -= 2;
            if (width == 1) {
                roofCap(material, width, depth, pos.add(1, 1, 1));
            } else if (depth == 1) {
                roofCap(material, width, depth, pos.add(1, 1, 1));
            }
            if (width <= 1 || depth <= 1) {
                break;
            }
            pos = pos.add(1, 1, 1);
        }
    }

    private void gableRoof(String material, int width, int depth, BlockPos pos, boolean box) throws BlockTypeError {
        IBlockState stair = getStairBlock(material);
        stair = stair.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM);

        IBlockState fill_state = this.getRoofFiller(material);

        // always construct facing east; width is the Z axis and depth is the X axis
        EnumFacing facing = EnumFacing.EAST;
        EnumFacing actualFacing = this.hand.getHorizontalFacing();

        boolean north_south = actualFacing == EnumFacing.NORTH || actualFacing == EnumFacing.SOUTH;

        BlockPos current;
        while (true) {
            for (int x=0; x < depth; x++) {
                for (int z=0; z < width; z++) {
                    IBlockState block_state = stair;
                    current = pos.add(x, 0, z);
                    if (north_south) {
                        if (z == 0) {
                            block_state = block_state.withProperty(BlockStairs.FACING, facing.rotateY());
                        } else if (z == width - 1) {
                            block_state = block_state.withProperty(BlockStairs.FACING, facing.rotateYCCW());
                        } else if (box) {
                            // only fill if box gable
                            block_state = fill_state;
                        } else {
                            continue;
                        }
                    } else {
                        if (x == 0) {
                            block_state = block_state.withProperty(BlockStairs.FACING, facing);
                        } else if (x == depth - 1) {
                            block_state = block_state.withProperty(BlockStairs.FACING, facing.getOpposite());
                        } else if (box) {
                            // only fill if box gable
                            block_state = fill_state;
                        } else {
                            continue;
                        }
                    }
                    this.world.setBlockState(current, block_state);
                }
            }

            // move up a layer, only decreasing depth
            if (north_south) {
                width -= 2;
                if (width == 1) {
                    roofCap(material, width, depth, pos.add(0, 1, 1));
                }
                if (width <= 1) {
                    break;
                }
                pos = pos.add(0, 1, 1);
            } else {
                depth -= 2;
                if (depth == 1) {
                    roofCap(material, width, depth, pos.add(1, 1, 0));
                }
                if (depth <= 1) {
                    break;
                }
                pos = pos.add(1, 1, 0);
            }
        }
    }

    private void roofCap(String material, int width, int depth, BlockPos pos) throws BlockTypeError {
        IBlockState slab_state = getSlabBlock(material);

        // TODO add slab half to general put options
        // stone slabs also have a "seamless" property
        slab_state = slab_state.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        if (width == 1) {
            for (int x = 0; x < depth; x++) {
                this.world.setBlockState(pos.add(x, 0, 0), slab_state);
            }
        } else if (depth == 1) {
            for (int z = 0; z < width; z++) {
                this.world.setBlockState(pos.add(0, 0, z), slab_state);
            }
        }
    }
}
