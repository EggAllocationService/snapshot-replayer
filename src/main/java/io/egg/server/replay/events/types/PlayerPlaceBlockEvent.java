package io.egg.server.replay.events.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.server.replay.Replay;
import io.egg.server.snapshots.ReplayPlayer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

public class PlayerPlaceBlockEvent implements ReplayEvent<PlayerPlaceBlockEvent>, ReversibleEvent {
    public int entityId;
    public int x;
    public int y;
    public int z;
    public String material;

    @Override
    public byte[] serialize() {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        bb.writeInt(entityId);
        bb.writeInt(x);
        bb.writeInt(y);
        bb.writeInt(z);
        bb.writeUTF(material);
        return bb.toByteArray();
    }

    @Override
    public PlayerPlaceBlockEvent fromBytes(byte[] data) {
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        entityId = bb.readInt();
        x = bb.readInt();
        y = bb.readInt();
        z = bb.readInt();
        material = bb.readUTF();
        return this;
    }

    @Override
    public void apply(InstanceContainer i, Replay r) {
        try {
            Block m = Material.valueOf(material).getBlock();
            i.setBlock(x,y , z, m);
           // ((ReplayPlayer) r.entities.get(entityId)).swingMainHand();
        }catch (IllegalArgumentException e){
            // do nothing
        }
    }

    @Override
    public void reverse(InstanceContainer i, Replay r) {
        i.setBlock(x,y , z, Block.AIR);
        ((ReplayPlayer) r.entities.get(entityId)).swingMainHand();
    }
}
