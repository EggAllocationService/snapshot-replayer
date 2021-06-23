package io.egg.server.snapshots;

import io.egg.server.replay.Replay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.scoreboard.BelowNameTag;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class ReplayPlayer extends FakePlayer {
    public SEntityInfo data;
    static final String BELOW_NAME_ID = "rply";
    public ReplayPlayer(SEntityInfo d) {

        super(UUID.randomUUID(), d.name, defaultOptions(), null);
        data = d;
        setGravity(0, 0, 0);
        setSkin(PlayerSkin.fromUsername(d.name));
        setTeam(Replay.VIEWERS_TEAM);

    }
    public Component createName() {
        return Component.text("Replay Entity").color(TextColor.color(0x1dd17d));

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

        setBelowNameTag(new BelowNameTag(BELOW_NAME_ID, createName()));
        PlayerMeta meta = (PlayerMeta) getEntityMeta();
        meta.setJacketEnabled(true);
        meta.setHatEnabled(true);
        meta.setLeftLegEnabled(true);
        meta.setRightLegEnabled(true);
        meta.setLeftSleeveEnabled(true);
        meta.setRightSleeveEnabled(true);
    }

    public static FakePlayerOption defaultOptions() {
        FakePlayerOption o = new FakePlayerOption();
        o.setInTabList(false);
        o.setRegistered(false);
        return o;
    }
}
