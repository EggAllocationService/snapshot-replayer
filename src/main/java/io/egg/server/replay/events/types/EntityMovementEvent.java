package io.egg.server.replay.events.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.server.replay.Replay;
import io.egg.server.snapshots.ReplayPlayer;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.Position;

public class EntityMovementEvent implements ReplayEvent<EntityMovementEvent> {
    public int entityId;
    public double x;
    public double y;
    public double z;
    public double pitch;
    public double yaw;
    @Override
    public byte[] serialize() {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        bb.writeInt(entityId);
        bb.writeDouble(x);
        bb.writeDouble(y);
        bb.writeDouble(z);
        bb.writeDouble(pitch);
        bb.writeDouble(yaw);
        return bb.toByteArray();
    }

    @Override
    public EntityMovementEvent fromBytes(byte[] data) {
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        entityId = bb.readInt();
        x = bb.readDouble();
        y = bb.readDouble();
        z = bb.readDouble();
        pitch = bb.readDouble();
        yaw = bb.readDouble();
        return this;
    }

    @Override
    public void apply(InstanceContainer i, Replay r) {
        Position newPos = new Position();
        newPos.setX(x);
        newPos.setY(y);
        newPos.setZ(z);
        newPos.setYaw((float)yaw);
        newPos.setPitch((float) pitch);
        if (!r.entities.containsKey(entityId)) {
            return;
        }
        r.entities.get(entityId).teleport(newPos);

    }
}
