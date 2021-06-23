package io.egg.server;

import io.egg.server.commands.*;
import io.egg.server.database.Database;
import io.egg.server.generators.VoidWorldGenerator;
import io.egg.server.instances.InstanceManager;
import io.egg.server.profiles.delegates.LobbyProfileDelegate;
import io.egg.server.replay.Replay;
import io.egg.server.skins.SkinManager;
import io.egg.server.tasks.InstanceNameTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSkinInitEvent;
import net.minestom.server.extras.PlacementRules;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.scoreboard.TeamManager;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.time.TimeUnit;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        MinecraftServer m = MinecraftServer.init();
        MinecraftServer.setBrandName("EggServer");

        InstanceManager.init();
        Database.init("testing");
        MinecraftServer.getCommandManager().register(new SaveCommand());
        MinecraftServer.getSchedulerManager().buildTask(new InstanceNameTask()).repeat(100, TimeUnit.MILLISECOND).schedule();
        MinecraftServer.getSchedulerManager().buildTask(() -> InstanceManager.get().tick()).repeat(1, TimeUnit.TICK).schedule();
        MinecraftServer.getBiomeManager().addBiome(VoidWorldGenerator.LOBBY);
        MinecraftServer.getCommandManager().register(new StopCommand());
        MinecraftServer.getCommandManager().register(new GamemodeCommand());
        MinecraftServer.getCommandManager().register(new SwitchInstanceCommand());
        MinecraftServer.getCommandManager().register(new EditMapCommand());
        MinecraftServer.getCommandManager().register(new ExportWorldCommand());
        MinecraftServer.getCommandManager().register(new LoadSnapshotDebugCommand());
        MinecraftServer.getCommandManager().register(new ReplaySeekCommand());
        MinecraftServer.getCommandManager().register(new ReplayTPCommand());
        MinecraftServer.setChunkViewDistance(8);

        Replay.VIEWERS_TEAM = MinecraftServer.getTeamManager().createTeam("VIEWERS");
        Component base = Component.text("(").color(TextColor.color(0xffffff));
        base = base.append(Component.text("Replay").color(TextColor.color(0x16cc9f)));
        base = base.append(Component.text(") ").color(TextColor.color(0xffffff)));
        Replay.VIEWERS_TEAM.setPrefix(base);
        // placement rules
        PlacementRules.init();

        try {
            InstanceManager.get().spawn("lobby", new LobbyProfileDelegate());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return;
        }

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addEventCallback(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(InstanceManager.get().getInstance("lobby"));
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlying(true);
            player.setRespawnPoint(new Position(0.5, 65, 0.5));
        });
        globalEventHandler.addEventCallback(PlayerSkinInitEvent.class, event -> {
            event.setSkin(SkinManager.getName(event.getPlayer().getUsername()));
        });




        m.start("0.0.0.0", 25565, (playerConnection, responseData) -> {
            responseData.setOnline(69);
            responseData.setMaxPlayer(420);
            for (Player p : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                responseData.addPlayer(p);
            }
            responseData.setDescription(Component.text("Standard Testing Server Instance", TextColor.color(0xc13f6f)));
        });

    }
}
