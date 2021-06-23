package io.egg.server.replay;

import io.egg.server.profiles.*;
import io.egg.server.replay.events.types.EntityMovementEvent;
import io.egg.server.replay.events.types.PlayerBreakBlockEvent;
import io.egg.server.replay.events.types.ReplayEvent;
import io.egg.server.snapshots.ReplayEntity;
import io.egg.server.snapshots.ReplayPlayer;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;

public class ReplayProfileDelegate extends DefaultProfileDelegate {
    public Replay r;
    public ReplayProfileDelegate(Replay re) {
        super();
        r = re;
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
    public void tick(InstanceTickEvent e) {
        r.parentTick();
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

    @EventHandler
    public void debugClick(PlayerEntityInteractEvent e) {
        if (e.getTarget() instanceof ReplayPlayer) {

            int filter = ((ReplayPlayer) e.getTarget()).data.id;
            e.getPlayer().sendMessage("DEBUG: ENTITY(" + filter + ") TICK(" + r.currentTick + ")");
            for (ReplayEvent eve : r.capturedTicks.get(r.currentTick).events) {

                if (eve instanceof EntityMovementEvent) {

                    var p = (EntityMovementEvent) eve;
                    if(p.entityId != filter) continue;
                    e.getPlayer().sendMessage("DEBUG: MOVE(" + p.x + "," + p.y + "," + p.z + ")");
                } else if (eve instanceof PlayerBreakBlockEvent) {
                    var p = (PlayerBreakBlockEvent) eve;
                    if(p.entityId != filter) continue;
                    e.getPlayer().sendMessage("DEBUG: BLOCKBREAK(" + p.x + "," + p.y + "," + p.z + ")");
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Replay Watcher";
    }
}
