package io.egg.server.snapshots;

import io.egg.server.instances.InstanceManager;
import io.egg.server.replay.Replay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.scoreboard.BelowNameTag;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class ReplayPlayer extends FakePlayer {
    public SEntityInfo edata;
    public InstanceContainer sendMeHere;
    /**
     * Initializes a new {@link FakePlayer} with the given {@code uuid}, {@code username} and {@code option}'s.
     *
     * @param uuid          The unique identifier for the fake player.
     * @param username      The username for the fake player.
     * @param option        Any option for the fake player.
     * @param spawnCallback
     */
    protected ReplayPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull FakePlayerOption option, @Nullable Consumer<FakePlayer> spawnCallback, SEntityInfo da, InstanceContainer target) {
        super(uuid, username, option, spawnCallback);
        edata = da;
        Position startingPos = new Position();
        startingPos.setX(edata.x);
        startingPos.setY(edata.y);
        startingPos.setZ(edata.z);
        startingPos.setPitch((float) edata.pitch);
        startingPos.setYaw((float) edata.yaw);
        setNoGravity(true);
        setRespawnPoint(startingPos);
        sendMeHere = target;
        System.out.println("Spawning fake player with username " + da.name);
    }

    public Component createName() {
        return Component.text("Replay Entity").color(TextColor.color(0x1dd17d));

    }
    public static ReplayPlayer create(SEntityInfo e, InstanceContainer ic, UUID thing) {
        return new ReplayPlayer(thing, "[E_" + e.id + "]", new FakePlayerOption(), null, e, ic);
    }
    public void init(Instance target) {


        PlayerMeta meta = (PlayerMeta) getEntityMeta();
        meta.setJacketEnabled(true);
        meta.setHatEnabled(true);
        meta.setLeftLegEnabled(true);
        meta.setRightLegEnabled(true);
        meta.setLeftSleeveEnabled(true);
        meta.setRightSleeveEnabled(true);
        setTeam(Replay.REPLAY_TEAM);

    }

    public static FakePlayerOption defaultOptions() {
        FakePlayerOption o = new FakePlayerOption();
        o.setInTabList(false);
        o.setRegistered(true);
        return o;
    }
}

/*

public class ReplayPlayer extends EntityCreature {
    SEntityInfo edata;
    public ReplayPlayer(SEntityInfo e) {
        super(EntityType.PLAYER, UUID.randomUUID());
        edata = e;
    }
    public void init(Instance target) {
        Position startingPos = new Position();
        startingPos.setX(edata.x);
        startingPos.setY(edata.y);
        startingPos.setZ(edata.z);
        startingPos.setPitch((float) edata.pitch);
        startingPos.setYaw((float) edata.yaw);
        setInstance(target, startingPos);
        PlayerMeta meta = (PlayerMeta) getEntityMeta();
        meta.setJacketEnabled(true);
        meta.setHatEnabled(true);
        meta.setLeftLegEnabled(true);
        meta.setRightLegEnabled(true);
        meta.setLeftSleeveEnabled(true);
        meta.setRightSleeveEnabled(true);



    }

}

 */