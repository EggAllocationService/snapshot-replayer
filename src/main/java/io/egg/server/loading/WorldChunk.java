package io.egg.server.loading;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bson.codecs.pojo.annotations.BsonId;


public class WorldChunk {
    @BsonId
    public String id;
    public String world;
    public int x;
    public int z;
    public byte[] data;
    public int rawSize;

    public byte[] encode() {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        bb.writeUTF(id);
        bb.writeUTF(world);
        bb.writeInt(x);
        bb.writeInt(z);
        bb.writeInt(data.length);
        bb.write(data);
        bb.writeInt(rawSize);
        return bb.toByteArray();
    }
}
