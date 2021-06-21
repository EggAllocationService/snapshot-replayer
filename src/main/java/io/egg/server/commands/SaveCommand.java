package io.egg.server.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class SaveCommand extends Command {
    public SaveCommand() {
        super("save");
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Saving chunks");
            if (sender.isPlayer()) {
                Player p = (Player) sender;
                p.getInstance().saveChunksToStorage();
            }
        });
    }
}
