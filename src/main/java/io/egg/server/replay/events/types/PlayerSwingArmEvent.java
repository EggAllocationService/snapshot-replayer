package io.egg.server.replay.events.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.server.replay.Replay;
import io.egg.server.snapshots.ReplayPlayer;
import net.minestom.server.instance.InstanceContainer;

public class PlayerSwingArmEvent implements ReplayEvent<PlayerSwingArmEvent>{
    public int entityId;
    @Override
    public byte[] serialize() {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        bb.writeInt(entityId);
        return bb.toByteArray();
    }

    @Override
    public PlayerSwingArmEvent fromBytes(byte[] data) {
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        entityId = bb.readInt();
        return this;
    }

    @Override
    public void apply(InstanceContainer i, Replay r) {
        ((ReplayPlayer) r.entities.get(entityId)).swingMainHand();
    }
}
