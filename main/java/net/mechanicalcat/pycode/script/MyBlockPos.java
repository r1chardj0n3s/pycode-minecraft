package net.mechanicalcat.pycode.script;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

// This exists solely to provide a de-obfuscated BlockPos for the player
public class MyBlockPos {
    public BlockPos blockPos;

    MyBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockPos add(double x, double y, double z) {
        return this.blockPos.add(x, y, z);
    }

    public BlockPos add(int x, int y, int z) {
        return this.blockPos.add(x, y, z);
    }

    public BlockPos add(Vec3i vec) {
        return this.blockPos.add(vec);
    }

    public BlockPos subtract(Vec3i vec) {
        return this.blockPos.subtract(vec);
    }

    public BlockPos up()
    {
        return this.blockPos.up(1);
    }

    public BlockPos up(int n)
    {
        return this.blockPos.offset(EnumFacing.UP, n);
    }

    public BlockPos down()
    {
        return this.blockPos.down(1);
    }

    public BlockPos down(int n)
    {
        return this.blockPos.offset(EnumFacing.DOWN, n);
    }

    public BlockPos north()
    {
        return this.blockPos.north(1);
    }

    public BlockPos north(int n)
    {
        return this.blockPos.offset(EnumFacing.NORTH, n);
    }

    public BlockPos south()
    {
        return this.blockPos.south(1);
    }

    public BlockPos south(int n)
    {
        return this.blockPos.offset(EnumFacing.SOUTH, n);
    }

    public BlockPos west()
    {
        return this.blockPos.west(1);
    }

    public BlockPos west(int n)
    {
        return this.blockPos.offset(EnumFacing.WEST, n);
    }

    public BlockPos east()
    {
        return this.blockPos.east(1);
    }

    public BlockPos east(int n)
    {
        return this.blockPos.offset(EnumFacing.EAST, n);
    }

    public String toString() {
        return this.blockPos.toString();
    }
}
