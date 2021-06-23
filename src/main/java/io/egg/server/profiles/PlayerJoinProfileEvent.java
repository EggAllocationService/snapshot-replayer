package io.egg.server.profiles;

import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinProfileEvent implements InstanceEvent {
    private String cancelReason = "[no reason] - THIS IS A BUG, PLEASE REPORT TO SERVER ADMIN";
    private Position joinPos = new Position(0.5, 65, 0.5);
    private final Player p;
    Instance target;
    public PlayerJoinProfileEvent(Player e, Instance t) {
        p = e;
        target = t;
    }

    public Player getP() {
        return p;
    }

    boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public Position getJoinPos() {
        return joinPos;
    }

    public void setJoinPos(Position joinPos) {
        this.joinPos = joinPos;
    }

    @Override
    public @NotNull Instance getInstance() {
        return target;
    }
}
