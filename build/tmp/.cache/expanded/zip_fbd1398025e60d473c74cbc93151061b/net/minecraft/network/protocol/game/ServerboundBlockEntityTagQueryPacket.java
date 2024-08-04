package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundBlockEntityTagQueryPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundBlockEntityTagQueryPacket> STREAM_CODEC = Packet.codec(
        ServerboundBlockEntityTagQueryPacket::write, ServerboundBlockEntityTagQueryPacket::new
    );
    private final int transactionId;
    private final BlockPos pos;

    public ServerboundBlockEntityTagQueryPacket(int pTransactionId, BlockPos pPos) {
        this.transactionId = pTransactionId;
        this.pos = pPos;
    }

    private ServerboundBlockEntityTagQueryPacket(FriendlyByteBuf p_328758_) {
        this.transactionId = p_328758_.readVarInt();
        this.pos = p_328758_.readBlockPos();
    }

    private void write(FriendlyByteBuf p_333511_) {
        p_333511_.writeVarInt(this.transactionId);
        p_333511_.writeBlockPos(this.pos);
    }

    @Override
    public PacketType<ServerboundBlockEntityTagQueryPacket> type() {
        return GamePacketTypes.SERVERBOUND_BLOCK_ENTITY_TAG_QUERY;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleBlockEntityTagQuery(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}