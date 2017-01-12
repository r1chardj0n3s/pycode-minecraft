/*
 * Copyright (c) 2017 Richard Jones <richard@mechanicalcat.net>
 * All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.mechanicalcat.pycode.net;

import io.netty.buffer.ByteBuf;
import net.mechanicalcat.pycode.init.ModItems;
import net.mechanicalcat.pycode.items.PythonWandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class InvokeWandMessage implements IMessage {
    private int entityId;
    private Vec3d hitVec;
    private RayTraceResult.Type typeOfHit;
    private BlockPos blockPos;
    private EnumFacing sideHit;
    RayTraceResult traceResult;

    public static class Handler implements IMessageHandler<InvokeWandMessage, IMessage> {
        @Override
        public IMessage onMessage(InvokeWandMessage message, MessageContext ctx) {
            // This ensures that we run on the main Minecraft thread. 'onMessage' itself
            // is called on the networking thread so it is not safe to do a lot of things
            // here.
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(InvokeWandMessage message, MessageContext ctx) {
            // note: we only get send types BLOCK and ENTITY and we only handle this message on the server
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            switch (message.typeOfHit) {
                case BLOCK:
                    FMLLog.info("Got a InvokeWandMessage block=%s", message.blockPos);
                    PythonWandItem.invokeOnBlock(player, message.blockPos);
                case ENTITY:
                    // in theory this should never be invoked on the client...
                    Entity entity = player.worldObj.getEntityByID(message.entityId);
                    FMLLog.info("Got a InvokeWandMessage entity=%s", entity);
                    if (entity == null) return;
                    PythonWandItem.invokeOnEntity(player, entity);
                    break;
                case MISS:
                    PythonWandItem.invokeInDirection(player, message.hitVec);
            }
        }
    }

    public InvokeWandMessage() {}

    public InvokeWandMessage(RayTraceResult traceResult) {
        this.traceResult = traceResult;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hitVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        typeOfHit = RayTraceResult.Type.MISS;
        switch (buf.readShort()) {
            case 1:
                typeOfHit = RayTraceResult.Type.BLOCK;
                blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
                sideHit = EnumFacing.values()[buf.readShort()];
                break;
            case 2:
                typeOfHit = RayTraceResult.Type.ENTITY;
                entityId = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.traceResult.hitVec.xCoord);
        buf.writeDouble(this.traceResult.hitVec.yCoord);
        buf.writeDouble(this.traceResult.hitVec.zCoord);
        buf.writeShort(this.traceResult.typeOfHit.ordinal());
        switch (this.traceResult.typeOfHit) {
            case ENTITY:
                buf.writeInt(this.traceResult.entityHit.getEntityId());
                break;
            case BLOCK:
                BlockPos blockPos = this.traceResult.getBlockPos();
                buf.writeInt(blockPos.getX());
                buf.writeInt(blockPos.getY());
                buf.writeInt(blockPos.getZ());
                buf.writeShort(this.traceResult.sideHit.ordinal());
        }
    }
}
