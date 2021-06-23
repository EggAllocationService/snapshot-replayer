package io.egg.server.replay;

import io.egg.server.profiles.*;
import io.egg.server.replay.events.types.EntityMovementEvent;
import io.egg.server.replay.events.types.PlayerBreakBlockEvent;
import io.egg.server.replay.events.types.ReplayEvent;
import io.egg.server.snapshots.ReplayEntity;
import io.egg.server.snapshots.ReplayPlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class ReplayProfileDelegate extends DefaultProfileDelegate {
    public BossBar bar;
    public Replay r;
    final static String RIGHT_ARROW = "\u00BB";
    final static String LEFT_ARROW = "\u00AB";
    final static String PLAY = "\u25B6";
    final static String PAUSE = "\u23F8";
    static Component SPACER = Component.text("       ").color(TextColor.color(0xffffff));
    static TextColor PRIMARY = TextColor.color(0x24d473);
    static TextColor SECONDARY = TextColor.color(0xe60e96);
    static TextColor PLAYING = TextColor.color(0x4ded34);
    static TextColor PAUSED = TextColor.color(0xedea1f);
    public ReplayProfileDelegate(Replay re) {
        super();
        r = re;
        bar = BossBar.bossBar(Component.text("Placeholder"), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }
    @Override
    public ProfileData getData() {
        return new ProfileData("replay", false);
    }

    @Override
    @EventHandler
    public void placeBlock(PlayerBlockPlaceEvent e) {
        e.setCancelled(true);
        r.playing = !r.playing;
    }
    private void updateBar() {
        int maxTicks = r.capturedTicks.size();
        Component base = Component.text("Tick ").color(PRIMARY);
        base = base.append(Component.text(r.currentTick + "").color(SECONDARY));
        base = base.append(Component.text("/").color(PRIMARY));
        base = base.append(Component.text(maxTicks + "").color(SECONDARY));
        base = base.append(SPACER);
        if (r.direction == Replay.Direction.BACKWARDS) {
            base = base.append(Component.text(LEFT_ARROW).color(SECONDARY));
        }
        if (r.playing) {
            base = base.append(Component.text(" " + PLAY + " ").color(PLAYING));
            if (bar.color() != BossBar.Color.GREEN) {
                bar.color(BossBar.Color.GREEN);
            }
        } else {
            base = base.append(Component.text(" " + PAUSE + " ").color(PAUSED));
            if (bar.color() != BossBar.Color.YELLOW) {
                bar.color(BossBar.Color.YELLOW);
            }
        }
        if (r.direction == Replay.Direction.FORWARDS) {
            base = base.append(Component.text(RIGHT_ARROW).color(SECONDARY));
        }
        bar.name(base);
        bar.progress(((float) r.currentTick / (float) maxTicks));
    }
    @Override
    @EventHandler
    public void removeBlock(PlayerBlockBreakEvent e) {
        e.setCancelled(true);
        if (r.direction == Replay.Direction.FORWARDS) {
            r.direction = Replay.Direction.BACKWARDS;
        } else {
            r.direction = Replay.Direction.FORWARDS;

        }
    }

    @EventHandler
    public void join(PlayerJoinProfileEvent e) {
        getInstance().setBlock(0, 50, 0, Block.BEDROCK);
        e.getP().showBossBar(bar);
        updateBar();
    }

    @EventHandler
    public void tick(InstanceTickEvent e) {
        r.parentTick();
        updateBar();
    }

    @Override
    public void setupInstance(Instance i) {
        for (Entity e : r.entities.values()) {
            if (e instanceof ReplayEntity) {
                ((ReplayEntity) e).init(i);
            } else if (e instanceof ReplayPlayer) {
                ((ReplayPlayer) e).init(i);
            }
        }
    }



    @Override
    public String getName() {
        return "Replay Watcher";
    }
}
