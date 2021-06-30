package io.egg.server.replay.events.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.server.replay.Replay;
import io.egg.server.snapshots.ReplayPlayer;
import io.egg.server.snapshots.SEntityInfo;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.Position;

import java.util.UUID;

public class PlayerDespawnEvent implements ReplayEvent<PlayerDespawnEvent>, ReversibleEvent{
    public int entityId;
    SEntityInfo cache = null;
    UUID cachedUUID = null;
    boolean alreadySpawned = true;
    @Override
    public byte[] serialize() {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput(4);
        bb.writeInt(entityId);
        return bb.toByteArray();
    }

    @Override
    public PlayerDespawnEvent fromBytes(byte[] data) {
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        entityId = bb.readInt();
        return null;
    }

    @Override
    public void apply(InstanceContainer i, Replay r) {
        if (!r.entities.containsKey(entityId)) return;
        ReplayPlayer p = (ReplayPlayer) r.entities.get(entityId);
        if (cache == null) {
            cache = new SEntityInfo();
            cache.x = p.getPosition().getX();
            cache.y = p.getPosition().getY();
            cache.z = p.getPosition().getZ();
            cache.yaw = p.getPosition().getYaw();
            cache.pitch = p.getPosition().getPitch();
            cache.type = "PLAYER";
            cache.id = entityId;
            cache.name = p.edata.name + "";
            cachedUUID = p.getUuid();
        }
        p.remove();
        r.players.remove(cache.name);
        r.entities.remove(entityId);
        alreadySpawned = false;
    }
    @Override
    public void reverse(InstanceContainer i, Replay r) {
        if (alreadySpawned) return;
        ReplayPlayer p = ReplayPlayer.create(cache, i, cachedUUID);
        r.players.put(cache.name, p);
        r.entities.put(cache.id, p);
        p.setRespawnPoint(new Position(cache.x, cache.y, cache.z, (float) cache.yaw, (float) cache.pitch));
        p.init(i);
        alreadySpawned = true;

    }
}
