package com.finderfeed.solarcraft.packet_handler.packets.crystal_energy_vines_puzzle;

import com.finderfeed.solarcraft.helpers.ClientHelpers;
import com.finderfeed.solarcraft.content.blocks.blockentities.clearing_ritual.CrystalEnergyVinesTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenPuzzleScreenPacket {

    private BlockPos tilePos;

    public OpenPuzzleScreenPacket(CrystalEnergyVinesTile tile){
        this.tilePos = tile.getBlockPos();
    }

    public OpenPuzzleScreenPacket(FriendlyByteBuf buf){
        this.tilePos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(tilePos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> ClientHelpers.handlePuzzlePacket(tilePos));
        ctx.get().setPacketHandled(true);
    }

}
