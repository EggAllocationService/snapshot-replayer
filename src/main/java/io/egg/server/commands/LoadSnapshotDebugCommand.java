package io.egg.server.commands;

import io.egg.server.instances.InstanceManager;
import io.egg.server.profiles.delegates.WorldEditorDelegate;
import io.egg.server.replay.ReplayManager;
import io.egg.server.snapshots.*;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.InstanceContainer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoadSnapshotDebugCommand extends Command {
    public LoadSnapshotDebugCommand() {
        super("loadreplay");
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Creating instance replay-test");
            byte[] data;
            try {
                data = Files.readAllBytes(Path.of("test.replay"));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
                ReplayManager.create("replay-test", data);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
    }
}
