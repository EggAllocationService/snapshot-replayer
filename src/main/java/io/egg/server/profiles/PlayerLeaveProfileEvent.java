package io.egg.server.profiles;

import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;

public class PlayerLeaveProfileEvent implements Event {
    private final Player p;
    public PlayerLeaveProfileEvent(Player e) {
        p = e;
    }

    public Player getPlayer() {
        return p;
    }


}
