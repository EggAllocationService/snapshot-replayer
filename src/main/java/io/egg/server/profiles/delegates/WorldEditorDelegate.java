package io.egg.server.profiles.delegates;

import io.egg.server.generators.*;
import io.egg.server.loading.World;
import io.egg.server.loading.WorldManager;
import io.egg.server.profiles.*;
import io.egg.server.profiles.DefaultProfileDelegate;
import io.egg.server.profiles.ProfileData;
import net.minestom.server.data.SerializableData;
import net.minestom.server.data.SerializableDataImpl;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.world.biomes.Biome;

public class WorldEditorDelegate extends DefaultProfileDelegate {
    World w;
    public WorldEditorDelegate(String world) {
        w = WorldManager.getWorld(world);

    }
    @Override
    public ProfileData getData() {
        return new ProfileData(w.id, true);
    }

    @Override
    public void setupInstance(Instance i) {
        i.setChunkGenerator(new VoidWorldGenerator(Biome.PLAINS));

    }

    @Override
    public void placeBlock(PlayerBlockPlaceEvent e) {

    }

    @Override
    @EventHandler
    public void removeBlock(PlayerBlockBreakEvent e) {

    }

    @EventHandler
    public void bucket(PlayerUseItemOnBlockEvent e) {
        if (e.getItemStack().getMaterial() == Material.LAVA_BUCKET && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            Instance i = e.getPlayer().getInstance();
            i.setBlock(e.getPosition().add(0, 1, 0), Block.LAVA);
        } else if (e.getItemStack().getMaterial() == Material.WATER_BUCKET && e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            Instance i = e.getPlayer().getInstance();
            i.setBlock(e.getPosition().add(0, 1, 0), Block.WATER);
        }
    }

    @Override
    public String getName() {
        return "World Editor";
    }
}