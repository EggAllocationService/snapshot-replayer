package io.egg.server.generators;

import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;

public class VoidIslandPopulator implements ChunkPopulator {
    @Override
    public void populateChunk(ChunkBatch batch, Chunk chunk) {
        if (chunk.getChunkX() == 0 && chunk.getChunkZ() ==0) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    batch.setBlock(x, 64, z, Block.BEDROCK);
                }
            }

        }
    }
}
