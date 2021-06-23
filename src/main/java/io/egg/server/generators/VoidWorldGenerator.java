package io.egg.server.generators;

import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeEffects;
import net.minestom.server.world.biomes.BiomeParticles;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoidWorldGenerator implements ChunkGenerator {
    private static final BiomeEffects DEFAULT_EFFECTS = BiomeEffects.builder()
            .fogColor(0x15d6ac)
            .skyColor(0x15d6ac)
            //.grassColor(0x21cc2c)
            .grassColor(0x9bffe6)
            .waterColor(0x02c99b)
            .waterFogColor(0x02c99b)
            //.foliageColor(0xffc2fb)
            .foliageColor(0xff9ef9) //kinda pink
           // .foliageColor(0xffd1fc) //white with tinge of pink
            .biomeParticles(new BiomeParticles(0.02F, new BiomeParticles.NormalParticle(NamespaceID.from("minecraft", "white_ash"))))
            .build();

    public static final Biome LOBBY = Biome.builder()
            .category(Biome.Category.NONE)
            .name(NamespaceID.from("kyle:lobby"))
            .temperature(0.8F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(DEFAULT_EFFECTS)
            .build();
    Biome biome;
    public VoidWorldGenerator(Biome bb) {
        biome = bb;
    }

    @Override
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {

        for (byte x = 0; x < Chunk.CHUNK_SIZE_X; x++)
            for (byte z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                for (byte y = 0; y < 32; y++) {
                    batch.setBlock(x, y, z, Block.AIR);
                }
            }
    }

    @Override
    public void fillBiomes(@NotNull Biome[] biomes, int chunkX, int chunkZ) {
        Arrays.fill(biomes, biome);

    }

    @Override
    public @Nullable List<ChunkPopulator> getPopulators() {
        ArrayList<ChunkPopulator> e = new ArrayList<>();
        e.add(new VoidIslandPopulator());
        return e;
    }
}
