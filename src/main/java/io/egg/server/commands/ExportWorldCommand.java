package io.egg.server.commands;

import io.egg.server.loading.World;
import io.egg.server.loading.WorldManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.color.DyeColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ExportWorldCommand extends Command {
    public ExportWorldCommand() {
        super("exportmap");
        var mapArg = ArgumentType.String("map");
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Not Implemented!");
        });
        addSyntax(this::saveMap, mapArg);
    }

    public void saveMap(CommandSender sender, CommandContext context) {
        String map = context.get("map");
        World w = WorldManager.getWorld(map);
        if (w.chunks.size() == 0) {
            sender.sendMessage(Component.text("There is no world by that name!", DyeColor.RED.getColor().asLegacyChatColor().asTextColor()));
            return;

        }
       sender.sendMessage("Preparing export, this may take a while");
        w.export(raw -> {
            ByteBuffer data = ByteBuffer.wrap(raw);
            sender.sendMessage(Component.text("Successfully exported world ", TextColor.color(0x49c98d))
                    .append(Component.text(map, TextColor.color(0x9449c9)))
                    .append(Component.text(", compressing ", TextColor.color(0x49c98d)))
                    .append(Component.text(data.getInt(0) / 1000000, TextColor.color(0x9449c9)))
                    .append(Component.text(" megabytes to ", TextColor.color(0x49c98d)))
                    .append(Component.text((data.array().length -4 ) / 1000000, TextColor.color(0x9449c9)))
                    .append(Component.text(" megabytes.", TextColor.color(0x49c98d)))
            );
            File f =new File(map + ".egg");
            try {
                FileUtils.writeByteArrayToFile(f, data.array());
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage(Component.text("Could not save to file!", TextColor.color(0xff0000)));
            }
        });



    }
}
