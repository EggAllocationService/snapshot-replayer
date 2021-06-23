package io.egg.server.replay.events.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.server.replay.Replay;
import io.egg.server.snapshots.ReplayEntity;
import io.egg.server.snapshots.ReplayPlayer;
import io.egg.server.snapshots.SEntityInfo;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.InstanceContainer;

public class EntityDiesEvent implements ReplayEvent<EntityDiesEvent>, ReversibleEvent{
    int entityId;
    public double x;
    public double y;
    public double z;
    public double pitch;
    public double yaw;
    public String type;
    @Override
    public byte[] serialize() {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        bb.writeInt(entityId);
        bb.writeUTF(type);
        bb.writeDouble(x);
        bb.writeDouble(y);
        bb.writeDouble(z);
        bb.writeDouble(pitch);
        bb.writeDouble(yaw);
        return bb.toByteArray();
    }

    @Override
    public EntityDiesEvent fromBytes(byte[] data) {
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        entityId = bb.readInt();
        type = bb.readUTF();
        x = bb.readDouble();
        y = bb.readDouble();
        z = bb.readDouble();
        pitch = bb.readDouble();
        yaw = bb.readDouble();
        return this;
    }

    @Override
    public void apply(InstanceContainer i, Replay r) {
        LivingEntity e = (LivingEntity) r.entities.get(entityId);
        if (e == null) {
            return;
        }
        e.kill();
        if (e instanceof ReplayEntity) {
            r.entities.remove(entityId);
        }
    }

    @Override
    public void reverse(InstanceContainer i, Replay r) {

        if (r.entities.containsKey(entityId) && r.entities.get(entityId) instanceof ReplayPlayer) {
            ReplayPlayer p = (ReplayPlayer) r.entities.get(entityId);
            p.setRespawnPoint(p.getPosition());
            p.respawn();
        } else {
            if (r.entities.containsKey(entityId)) {
                r.entities.get(entityId).remove();
                r.entities.remove(entityId);
            }
            SEntityInfo s = new SEntityInfo();
            s.type = type;
            s.name = null;
            s.pitch = pitch;
            s.yaw = yaw;
            s.x = x;
            s.y = y;
            s.z = z;
            s.id = entityId;
            try {
                EntityType.valueOf(s.type);
            } catch (IllegalArgumentException e) {
                System.out.println("Skipping SPAWN(" + s.type + ")");
                return;
            }
            ReplayEntity ee = new ReplayEntity(s);
            ee.init(i);
            r.entities.put(entityId, ee);
        }
    }
}
