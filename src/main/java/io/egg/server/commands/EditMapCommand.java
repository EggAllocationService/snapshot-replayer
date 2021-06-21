package io.egg.server.commands;

import io.egg.server.instances.InstanceManager;
import io.egg.server.profiles.delegates.WorldEditorDelegate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class EditMapCommand extends Command {
    public EditMapCommand() {
        super("editmap");
        var mapNameArg = ArgumentType.String("map");
        setDefaultExecutor(new CommandExecutor() {
            @Override
            public void apply(@NotNull CommandSender sender, @NotNull CommandContext context) {
                sender.sendMessage("Usage: /editmap <name>");
            }
        });
        addSyntax(this::execute, mapNameArg);
    }
    public void execute(CommandSender sender, CommandContext context) {
        InstanceContainer ic = InstanceManager.get().getInstance("edit-" + context.get("map"));
        if (ic != null) {
            // there is an instance already editing this map, tp!
            sender.sendMessage("Editor instance already exits for map " + context.get("map") + "!");
            return;
        }

        WorldEditorDelegate d = new WorldEditorDelegate(context.get("map"));
        try {
            ic = InstanceManager.get().spawn("edit-" + context.get("map"), d);
            InstanceManager.get().transfer((Player) sender, ic);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            sender.sendMessage(Component.text(e.getMessage(), TextColor.color(255, 0,0)));
        }



    }
}
