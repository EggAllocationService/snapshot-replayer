package io.egg.server.replay.events.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import io.egg.server.replay.Replay;
import io.egg.server.snapshots.ReplayEntity;
import io.egg.server.snapshots.ReplayPlayer;
import io.egg.server.snapshots.SEntityInfo;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.Vector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

public class EntitySpawnEvent implements ReplayEvent<EntitySpawnEvent>,ReversibleEvent {

    public int entityId;
    public double x;
    public double y;
    public double z;
    public double pitch;
    public double yaw;
    public String type;
    public String name = null;
    public double sx;
    public double sy;
    public double sz;
    UUID forSuff = UUID.randomUUID();
    boolean alreadySpawned = false;
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
    public EntitySpawnEvent fromBytes(byte[] data) {
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        entityId = bb.readInt();
        type = bb.readUTF();
        x = bb.readDouble();
        y = bb.readDouble();
        z = bb.readDouble();
        pitch = bb.readDouble();
        yaw = bb.readDouble();
        sx = bb.readDouble();
        sy = bb.readDouble();
        sz = bb.readDouble();
        if (bb.readBoolean()) {
            name = bb.readUTF();
        }
        if (type.equals("PRIMED_TNT")) {
            type = "TNT";
        }
        if (type.equals("PLAYER")) {
            try {
                Files.write(data, Path.of("help_" + entityId + ".dump").toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    @Override
    public void apply(InstanceContainer i, Replay r) {
        if(alreadySpawned) return;
        SEntityInfo s = new SEntityInfo();
        s.type = type;
        s.name = name;
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
        if (s.type.equals("PLAYER")) {
            ReplayPlayer p = ReplayPlayer.create(s, i, forSuff);
            r.players.put(s.name, p);
            r.entities.put(s.id, p);
            p.setRespawnPoint(new Position(x, y, z, (float) yaw, (float) pitch));
            p.init(i);
            System.out.println("SPAWNPLAYER(" + s.name + ")");
            alreadySpawned = true;
        } else {
            ReplayEntity e = new ReplayEntity(s);
            e.init(i);
            r.entities.put(entityId, e);
            alreadySpawned = true;
            Vector velocity = new Vector(sx, sy, sz);

        }
        //e.setVelocity(velocity);
    }

    @Override
    public void reverse(InstanceContainer i, Replay r) {
        if (!r.entities.containsKey(entityId)) {
            System.out.println("WARN: Skipping REMOVE() for entity "  + entityId);
            return;
        }
        r.entities.get(entityId).remove();

        alreadySpawned = false;
        r.entities.remove(entityId);
        if(type.equals("PLAYER")) {
            r.players.remove(name);
            System.out.println("REMOVE_PLAYER(" + entityId + ")");
        }

    }
}
