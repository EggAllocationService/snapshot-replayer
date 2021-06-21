package io.egg.server.loading;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.egg.server.database.Database;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class World {
    public World() {
        chunks.put("place", "holder");
    }
    @BsonId
    public String id;

    // Maps position to UUID of WorldChunk
    volatile public HashMap<String, String> chunks = new HashMap<>();

    volatile HashMap<String, WorldChunk> cache = new HashMap<>();

    public WorldChunk getChunkAt(int x, int z) {
        if (!chunks.containsKey(getChunkKey(x, z))) {
            return null;
        }
        String chunkId = chunks.get(getChunkKey(x, z));
        if (cache.containsKey(chunkId)) {
            return cache.get(chunkId);
        }
        WorldChunk c = Database.getInstance().worldChunks.find(eq("_id", chunkId)).first();
        assert c != null;
        byte[] compressed = c.data.clone();
        c.data = new byte[c.rawSize];


        Inflater i = new Inflater();
        i.setInput(compressed);
        try {
            i.inflate(c.data);
            i.end();
        } catch (DataFormatException e) {
            e.printStackTrace();
            return null;
        }
        cache.put(chunkId, c);
        return c;
    }

    public WorldChunk getChunkKey(String chunkId) {
        if (cache.containsKey(chunkId)) {
            return cache.get(chunkId);
        }
        WorldChunk c = Database.getInstance().worldChunks.find(eq("_id", chunkId)).first();
        assert c != null;
        byte[] compressed = c.data.clone();
        c.data = new byte[c.rawSize];


        Inflater i = new Inflater();
        i.setInput(compressed);
        try {
            i.inflate(c.data);
            i.end();
        } catch (DataFormatException e) {
            e.printStackTrace();
            return null;
        }
        cache.put(chunkId, c);
        return c;
    }

    public void saveChunk(int x, int z, byte[] da) {
        Deflater compressor = new Deflater();
        compressor.setInput(da);
        compressor.finish();

        byte[] tmpdata = new byte[da.length];
        int compressedSize = compressor.deflate(tmpdata);
        compressor.end();
        byte[] data = new byte[compressedSize];
        System.arraycopy(tmpdata, 0, data, 0, compressedSize);
        String id;
        if (!chunks.containsKey(getChunkKey(x, z))) {
            // this chunk is being saved for the first time
            id = UUID.randomUUID().toString();
            chunks.put(getChunkKey(x, z), id);
            WorldChunk wc = new WorldChunk();
            wc.data = data;
            wc.z = z;
            wc.x = x;
            wc.world = this.id;
            wc.id = id;
            wc.rawSize = da.length;

            // put WorldChunk into database
            Database.getInstance().worldChunks.insertOne(wc);
            // put WorldChunk into local cache

            cache.put(id, wc);
            cache.get(id).data = da;
            // Update database listing for this world
            Database.getInstance().worlds.updateOne(eq("_id", this.id), combine(
                    set("chunks." + getChunkKey(x, z), id)
            ));


        } else {
            // chunk has already been saved, time to update
            id = chunks.get(getChunkKey(x, z));
            if (cache.containsKey(id)) {
                // there's one in the cache
                cache.get(id).data = da;
                cache.get(id).rawSize = da.length;
            }
            Database.getInstance().worldChunks.updateOne(eq("_id", id), combine(
                    set("data", data),
                    set("rawSize", da.length)
            ));
        }


    }
    private static String getChunkKey(int chunkX, int chunkZ) {
        return chunkX + "~" + chunkZ;
    }


    private static class ExportThread extends Thread {
        World world;
        Consumer<byte[]> call;
        ExportThread(World w, Consumer<byte[]> callback) {
            super();
            world = w;
            call = callback;
        }

        @Override
        public void run() {
            byte[] data = world.exportBytes();
            call.accept(data);
        }
    }

    public void export(Consumer<byte[]> callback) {
        new ExportThread(this, callback).start();
    }


    protected byte[] exportBytes() {
        ByteArrayDataOutput bb = ByteStreams.newDataOutput();
        bb.writeInt(chunks.size());
        for (String id : chunks.values()) {
            try {
                byte[] chunk = getChunkKey(id).encode();
                bb.writeInt(chunk.length);
                bb.write(chunk);
            } catch (NullPointerException e) {
                System.out.println("Error encoding chunk " + id);
            }
        }
        byte[] uncompressed = bb.toByteArray();
        Deflater deflater = new Deflater(Deflater.DEFLATED);
        deflater.setInput(uncompressed);
        deflater.finished();
        byte[] tmp = new byte[uncompressed.length];
        int compressedSize = deflater.deflate(tmp);
        byte[] compresssed = new byte[compressedSize];
        System.arraycopy(tmp, 0, compresssed, 0, compressedSize);

        ByteArrayDataOutput bbb = ByteStreams.newDataOutput();
        bbb.writeInt(uncompressed.length);
        bbb.write(compresssed);
        return bbb.toByteArray();
    }
}
