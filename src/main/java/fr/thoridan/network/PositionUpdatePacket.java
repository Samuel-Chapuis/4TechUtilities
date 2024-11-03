package fr.thoridan.network;

import fr.thoridan.block.PrinterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PositionUpdatePacket {
    private final BlockPos blockEntityPos;
    private final BlockPos targetPos;

    public PositionUpdatePacket(BlockPos blockEntityPos, BlockPos targetPos) {
        this.blockEntityPos = blockEntityPos;
        this.targetPos = targetPos;
    }

    public PositionUpdatePacket(FriendlyByteBuf buf) {
        this.blockEntityPos = buf.readBlockPos();
        this.targetPos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockEntityPos);
        buf.writeBlockPos(targetPos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player != null) {
                var level = player.level();
                var blockEntity = level.getBlockEntity(blockEntityPos);
                if (blockEntity instanceof PrinterBlockEntity printerBlockEntity) {
                    // Update the block entity's stored target position
                    printerBlockEntity.setTargetPos(targetPos);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}