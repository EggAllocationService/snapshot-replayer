package io.egg.server.commands;

import io.egg.server.instances.InstanceManager;
import io.egg.server.instances.ProfiledInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import org.apache.logging.log4j.util.Strings;

public class SwitchInstanceCommand extends Command {
    public SwitchInstanceCommand() {
        super("instance");
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Available instances:");
            sender.sendMessage(Strings.join(InstanceManager.get().getNames(), ','));
        });
        var instanceArgument = ArgumentType.String("instance");
        instanceArgument.setSuggestionCallback((sender, context, suggestion) -> {
            for (String s : InstanceManager.get().getNames()) {
                ProfiledInstance pi = InstanceManager.get().getProfile(s);
                
                suggestion.addEntry(new SuggestionEntry(s, Component.text(pi.getDelegate().getName(), TextColor.color(0xff00ff))));

            }
        });
        addSyntax(this::switchInstance, instanceArgument);
    }
    public void switchInstance(CommandSender sender, CommandContext context) {
        InstanceContainer i = InstanceManager.get().getInstance(context.get("instance"));
        if ( i == null) {
            sender.sendMessage("Available instances:");
            sender.sendMessage(Strings.join(InstanceManager.get().getNames(), ' '));
            return;
        }
        Player p = (Player) sender;
        InstanceManager.get().transfer(p, i);


    }
}

