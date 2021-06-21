package io.egg.server.loading;

import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.binary.BinaryReader;
import net.minestom.server.utils.chunk.ChunkCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DatabaseWorldLoader implements IChunkLoader {
    InstanceContainer i;
    String worldName;
    public DatabaseWorldLoader(InstanceContainer b, String world) {
        i = b;
        worldName = world;
    }

    @Override
    public boolean loadChunk(@NotNull Instance instance, int chunkX, int chunkZ, @Nullable ChunkCallback callback) {
        World w = WorldManager.getWorld(worldName);

        WorldChunk c = w.getChunkAt(chunkX, chunkZ);
        if (c == null) {
            // chunk has never been loaded
            return false;

        } else {
            //chunk has been loaded, lets have fun!
            BinaryReader b = new BinaryReader(c.data);
            Chunk chunk = (i).getChunkSupplier().createChunk(i,null, chunkX, chunkZ);
            chunk.readChunk(b, callback);
            return true; // chunk has been loaded from db and is being read
        }

    }

    @Override
    public void saveChunk(@NotNull Chunk chunk, @Nullable Runnable callback) {

        World w = WorldManager.getWorld(worldName);
        if (w == null) {
            return; // shits really fucked like REALLY FUCKED
        }
        byte[] data = chunk.getSerializedData();
        w.saveChunk(chunk.getChunkX(), chunk.getChunkZ(), data);
        if (callback != null) callback.run();
    }


    @Override
    public boolean supportsParallelSaving() {
        return true;
    }

    @Override
    public boolean supportsParallelLoading() {
        return true;
    }
    private static String getChunkKey(int chunkX, int chunkZ) {
        return chunkX + "~" + chunkZ;
    }
}
