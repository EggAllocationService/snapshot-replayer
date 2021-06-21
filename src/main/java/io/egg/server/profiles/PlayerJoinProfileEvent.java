package io.egg.server.profiles;

import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.utils.Position;

public class PlayerJoinProfileEvent implements Event {
    private String cancelReason = "[no reason] - THIS IS A BUG, PLEASE REPORT TO SERVER ADMIN";
    private Position joinPos = new Position(0.5, 65, 0.5);
    private final Player p;
    public PlayerJoinProfileEvent(Player e) {
        p = e;
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
}
