package io.egg.server.snapshots;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class ReplayPlayer extends FakePlayer {
    public SEntityInfo data;

    public ReplayPlayer(SEntityInfo d) {

        super(UUID.randomUUID(), d.name, defaultOptions(), null);
        data = d;
        setGravity(0, 0, 0);
    }
    public void init(Instance target) {
        Position startingPos = new Position();
        startingPos.setX(data.x);
        startingPos.setY(data.y);
        startingPos.setZ(data.z);
        startingPos.setPitch((float) data.pitch);
        startingPos.setYaw((float) data.yaw);
        setInstance(target, startingPos);
        target.loadChunk(startingPos, chunk -> {
            currentChunk = chunk;
        });

    }



    public static FakePlayerOption defaultOptions() {
        FakePlayerOption o = new FakePlayerOption();
        o.setInTabList(false);
        o.setRegistered(false);
        return o;
    }
}
