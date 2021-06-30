package io.egg.server.profiles;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.event.Event;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public abstract class DefaultProfileDelegate {
    protected BossBar defaultBar;
    InstanceContainer instance;


    public void initEvents() {
        Method[] ms = this.getClass().getMethods();
        for (Method m : ms) {
            if (m.isAnnotationPresent(EventHandler.class)) {
                System.out.println("Registering method " + m.getName() + " on " + this.getClass().getName());
                // has thingy
                Parameter[] t = m.getParameters();
                if (t.length != 1) continue;
                Class<?> eventClass = t[0].getType();
                Class<? extends Event> argClass;
                try {
                    argClass = eventClass.asSubclass(Event.class);
                } catch (Exception e) {
                    continue;
                }
                System.out.println("Method " + m.getName() + " is listening for event " + eventClass.getName());
                instance.addEventCallback(argClass, event -> {
                    //System.out.println("invoking method " + m.getName() + " on " + this.getClass().getName());
                    try {
                        m.invoke(this, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

    }
    public void setInstance(InstanceContainer i ) {
        instance = i;
        defaultBar = BossBar.bossBar(Component.text("Please report this bar's existence!",TextColor.color(0xff00ff) ),  0.0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);

        initEvents();
    }
    public abstract ProfileData getData();

    public InstanceContainer getInstance() {
        return instance;
    }

    public abstract void setupInstance(Instance i);

    public void tick() {

    }
    public Component getBar() {
        return null;
    }

    @EventHandler
    public void placeBlock(PlayerBlockPlaceEvent e) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(Component.text("This instance does not have any delegate for placing blocks", TextColor.color(255, 25, 25)));
    }
    @EventHandler
    public void removeBlock(PlayerBlockBreakEvent e) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(Component.text("This instance does not have any delegate for breaking blocks", TextColor.color(255, 25, 25)));
    }

    public abstract String getName();

}
