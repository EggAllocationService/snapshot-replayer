package io.egg.server.commands;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpawnBlockCommand extends Command {
    public SpawnBlockCommand() {
        super("spawnblock");
        setDefaultExecutor((sender, context) -> {
            Player p = (Player) sender;
            ShittyDiamondEye e = new ShittyDiamondEye();
            e.setInstance(p.getInstance(), p.getPosition());
        });
    }
}
