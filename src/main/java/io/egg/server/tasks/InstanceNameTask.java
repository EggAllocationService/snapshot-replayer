package io.egg.server.tasks;

import io.egg.server.instances.InstanceManager;
import io.egg.server.instances.ProfiledInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

public class InstanceNameTask implements Runnable{
    @Override
    public void run() {
        for (Player p : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            ProfiledInstance s = InstanceManager.get().getProfile((InstanceContainer) p.getInstance());

            if (s == null) continue;
            Component iBar = s.getDelegate().getBar();
            if ( iBar != null) {
                p.sendActionBar(iBar);
            } else {
                Component t = Component.text("You are playing ", TextColor.color(0x23bbf3))
                        .append(
                                Component.text(s.getDelegate().getName(), TextColor.color(0x8a329b))
                        )
                        .append(
                                Component.text(" on instance ", TextColor.color(0x23bbf3))
                        )
                        .append(
                                Component.text(s.getName(),TextColor.color(0x8a329b))

                        );
                p.sendActionBar(t);
            }
        }
    }

}
