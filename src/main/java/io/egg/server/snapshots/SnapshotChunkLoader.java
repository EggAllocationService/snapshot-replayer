package io.egg.server.snapshots;

import io.egg.server.loading.mogang.ChunkSection;
import io.egg.server.loading.mogang.FakeChunkData;
import net.minestom.server.instance.*;
import net.minestom.server.utils.binary.BinaryReader;
import net.minestom.server.utils.chunk.ChunkCallback;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;

public class SnapshotChunkLoader implements IChunkLoader {
    Snapshot snapshot;
    public SnapshotChunkLoader(Snapshot s) {
        snapshot = s;
    }
    HashMap<String, byte[]> cached = new HashMap<>();

    @Override
    public boolean loadChunk(@NotNull Instance instance, int chunkX, int chunkZ, @Nullable ChunkCallback callback) {
        String id = chunkX + ":" + chunkZ;
        if (cached.containsKey(id)) {
            Chunk cachedChunk = ((InstanceContainer) instance).getChunkSupplier().createChunk(instance, null, chunkX, chunkZ);
            cachedChunk.readChunk(new BinaryReader(cached.get(id)), callback);
            return true;
        }
        FakeChunkData chunk = snapshot.chunks.get(chunkX + ":" + chunkZ);
        if (chunk == null) {
            return false;
        }
        Biome[] biomes = new Biome[1024];
        Arrays.fill(biomes, Biome.PLAINS);
        Chunk target = ((InstanceContainer) instance).getChunkSupplier().createChunk(instance, biomes, chunkX, chunkZ);


        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    ChunkSection section = chunk.getSection((int) Math.floor(y / 16));
                    short block = (short) section.getBlock(x, y % 16, z);
                    if (block == 0) {
                        continue;
                    }
                    target.UNSAFE_setBlock(x, y, z, block, (short) 0, null, false);
                }
            }
        }
        if (callback != null) {
            callback.accept(target);
        }
        return true;
    }

    @Override
    public boolean supportsParallelLoading() {
        return true;
    }

    @Override
    public void saveChunk(@NotNull Chunk chunk, @Nullable Runnable callback) {
        String id = chunk.getChunkX() + ":" + chunk.getChunkZ();
        cached.put(id, chunk.getSerializedData());
    }
}
