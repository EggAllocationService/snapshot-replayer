package io.egg.server.snapshots;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.egg.server.loading.mogang.FakeChunkData;
import io.egg.server.replay.Replay;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;

import java.security.InvalidParameterException;
import java.util.HashMap;

public class Snapshot {
    public HashMap<String, FakeChunkData> chunks = new HashMap<>();


    public static Snapshot loadSnapshot(byte[] data, Replay r) {
        // snapshot format:
        // int - size of following array
        // Array of:
        // String - chunk position
        // int - size of byte array
        // byte[] - raw packet data for that chunk in the replay.
        // int - size of following array
        // Array Of:
        // int - entity id
        // boolean - has custom name?
        // if non-0: UTF string with name
        // String - Entity Type
        // double x
        // double y
        // double z
        // double pitch
        // double yaw
        Snapshot s = new Snapshot();
        ByteArrayDataInput bb = ByteStreams.newDataInput(data);
        int chunkCount = bb.readInt();
        long start = System.currentTimeMillis();
        for (int i = 0; i < chunkCount; i++) {
            String pos = bb.readUTF();

            int dataSize = bb.readInt();
            byte[] chunkData = new byte[dataSize];
            bb.readFully(chunkData);
            s.chunks.put(pos, FakeChunkData.fromPacket(chunkData));

        }
        System.out.println("Read " + chunkCount + "chunks in " + (System.currentTimeMillis() - start) + "ms");
        int entityCount = bb.readInt();
        for (int i = 0; i < entityCount; i++) {
            SEntityInfo entityInfo = new SEntityInfo();
            entityInfo.id = bb.readInt();
            boolean hasName = bb.readByte() != 0x0;
            if (hasName) {
                entityInfo.name = bb.readUTF();
            } else {
                entityInfo.name = null;
            }
            entityInfo.type = bb.readUTF();
            entityInfo.x = bb.readDouble();
            entityInfo.y = bb.readDouble();
            entityInfo.z = bb.readDouble();
            entityInfo.pitch = bb.readDouble();
            entityInfo.yaw = bb.readDouble();
            try {
                EntityType.valueOf(entityInfo.type);
            } catch (IllegalArgumentException e) {
                System.out.println("Skipping " + entityInfo.type);
                continue;
            }
            if (entityInfo.type.equals("PLAYER")) {
                ReplayPlayer pp = new ReplayPlayer(entityInfo);
                r.entities.put(entityInfo.id, pp);
                r.players.put(entityInfo.name, pp);
            } else {
                r.entities.put(entityInfo.id, new ReplayEntity(entityInfo));
            }
        }
        return s;
    }
}
