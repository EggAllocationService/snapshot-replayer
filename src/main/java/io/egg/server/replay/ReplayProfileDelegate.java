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
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.adventure.bossbar.BossBarManager;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;

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

    }
    private void updateBar() {
        int maxTicks = r.capturedTicks.size();
        Component base = Component.text("Tick ").color(PRIMARY);
        base = base.append(Component.text((r.currentTick + 1) + "").color(SECONDARY));
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

    }

    @EventHandler
    public void join(PlayerJoinProfileEvent e) {
        getInstance().setBlock(0, 50, 0, Block.BEDROCK);
        e.getP().showBossBar(bar);
        updateBar();
        setupInventory(e.getP());
    }
    @EventHandler
    public void use(PlayerUseItemEvent e) {
        e.setCancelled(true);
        ItemStack a = e.getItemStack();
        if (a.hasTag(Tag.String("PlaybackSpeed"))) {
            Replay.PlaySpeed speed = Replay.PlaySpeed.valueOf(a.getTag(Tag.String("PlaybackSpeed")));
            r.setSpeed(speed);
        } else if (a.hasTag(Tag.String("PlayPause"))) {
            r.playing = !r.playing;
        } else if (a.hasTag(Tag.String("ReverseDirection"))) {
            if (r.direction == Replay.Direction.FORWARDS) {
                r.direction = Replay.Direction.BACKWARDS;
            } else {
                r.direction = Replay.Direction.FORWARDS;

            }
            r.tickProgress = 0;
        }
    }
    @EventHandler
    public void drop(ItemDropEvent e) {
        e.setCancelled(true);
    }



    @EventHandler
    public void tick(InstanceTickEvent e) {

        r.parentTick();
        updateBar();
    }


    @EventHandler
    public void leave(PlayerLeaveProfileEvent e) {
        e.getPlayer().hideBossBar(bar);
        boolean found = false;
        for (Player p : getInstance().getPlayers()) {
            if (!(p instanceof ReplayPlayer)) {
                found = true;
                break;
            }
        }
        if (!found) {
            r.destroy();
        }
    }
    @EventHandler
    public void disconnect(PlayerDisconnectEvent e) {
        boolean found = false;
        for (Player p : getInstance().getPlayers()) {
            if (!(p instanceof ReplayPlayer)) {
                found = true;
                break;
            }
        }
        if (!found) {
            r.destroy();
        }
    }


    private void setupInventory(Player p ) {
        // 1x
        ItemStack NormalSpeed = ItemStack.of(Material.GREEN_DYE).withTag(Tag.String("PlaybackSpeed"), Replay.PlaySpeed.NORMAL.toString())
                .withDisplayName(Component.text("Use for Normal Speed").color(TextColor.color(0x00ff00)).decoration(TextDecoration.ITALIC, false))
                ;
        ItemStack HalfSpeed = ItemStack.of(Material.LIGHT_BLUE_DYE).withTag(Tag.String("PlaybackSpeed"), Replay.PlaySpeed.HALF.toString())
                .withDisplayName(Component.text("Use for Half Speed").color(TextColor.color(0x6666ff)).decoration(TextDecoration.ITALIC, false))
                ;
        ItemStack QuarterSpeed = ItemStack.of(Material.BLUE_DYE).withTag(Tag.String("PlaybackSpeed"), Replay.PlaySpeed.QUARTER.toString())
                .withDisplayName(Component.text("Use for Quarter Speed").color(TextColor.color(0x0000ff)).decoration(TextDecoration.ITALIC, false))
                ;
        ItemStack DoubleSpeed = ItemStack.of(Material.PINK_DYE).withTag(Tag.String("PlaybackSpeed"), Replay.PlaySpeed.DOUBLE.toString())
                .withDisplayName(Component.text("Use for Double Speed").color(TextColor.color(0xff6666)).decoration(TextDecoration.ITALIC, false))
                ;
        ItemStack QuadrupleSpeed = ItemStack.of(Material.RED_DYE).withTag(Tag.String("PlaybackSpeed"), Replay.PlaySpeed.QUADRUPLE.toString())
                .withDisplayName(Component.text("Use for Quadruple Speed").color(TextColor.color(0xff0000)).decoration(TextDecoration.ITALIC, false))
                ;
        ItemStack PlayPause = ItemStack.of(Material.ENDER_EYE).withTag(Tag.String("PlayPause"), Replay.PlaySpeed.QUADRUPLE.toString())
                .withDisplayName(Component.text("Use for Play/Pause").color(TextColor.color(0xff66ff)).decoration(TextDecoration.ITALIC, false))
                ;
        ItemStack Direction = ItemStack.of(Material.BLAZE_ROD).withTag(Tag.String("ReverseDirection"), Replay.PlaySpeed.QUADRUPLE.toString())
                .withDisplayName(Component.text("Use for Quadruple Speed").color(TextColor.color(0x66ff66)).decoration(TextDecoration.ITALIC, false))
                ;
        ItemStack TenthSpeed = ItemStack.of(Material.BLACK_DYE).withTag(Tag.String("PlaybackSpeed"), Replay.PlaySpeed.TENTH.toString())
                .withDisplayName(Component.text("Use for 1/10th Speed").color(TextColor.color(0x000066)).decoration(TextDecoration.ITALIC, false))
                ;
        p.getInventory().setItemStack(1, TenthSpeed);
        p.getInventory().setItemStack(2, QuarterSpeed);
        p.getInventory().setItemStack(3, HalfSpeed);
        p.getInventory().setItemStack(4, NormalSpeed);
        p.getInventory().setItemStack(5, DoubleSpeed);
        p.getInventory().setItemStack(6, QuadrupleSpeed);
        p.getInventory().setItemStack(7, PlayPause);
        p.getInventory().setItemStack(8, Direction);


    }

    @Override
    public Component getBar() {
        int maxTicks = r.capturedTicks.size();
        Component base = Component.text("Tick ").color(PRIMARY);
        base = base.append(Component.text((r.currentTick + 1 )+ "").color(SECONDARY));
        base = base.append(Component.text("/").color(PRIMARY));
        base = base.append(Component.text(maxTicks + "").color(SECONDARY));
        base = base.append(SPACER);
        if (r.direction == Replay.Direction.BACKWARDS) {
            base = base.append(Component.text(LEFT_ARROW).color(SECONDARY));
        }
        if (r.playing) {
            base = base.append(Component.text(" " + PLAY + " ").color(PLAYING));

        } else {
            base = base.append(Component.text(" " + PAUSE + " ").color(PAUSED));

        }
        if (r.direction == Replay.Direction.FORWARDS) {
            base = base.append(Component.text(RIGHT_ARROW).color(SECONDARY));
        }
        base = base.append(Component.text("    "));
        base = base.append(Component.text(r.speed.text).color(PRIMARY));
        base = base.append(Component.text("x").color(SECONDARY));
        return base;
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
