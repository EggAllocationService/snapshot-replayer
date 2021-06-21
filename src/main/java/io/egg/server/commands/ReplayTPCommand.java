package io.egg.server.commands;

import io.egg.server.instances.InstanceManager;
import io.egg.server.instances.ProfiledInstance;
import io.egg.server.replay.Replay;
import io.egg.server.replay.ReplayProfileDelegate;
import io.egg.server.snapshots.ReplayPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReplayTPCommand extends Command {
    public ReplayTPCommand() {
        super("rtp");
        var targetArgument = ArgumentType.String("target");
        targetArgument.setSuggestionCallback((sender, context, suggestion) -> {
            ProfiledInstance pi = InstanceManager.get().getProfile((InstanceContainer) ((Player) sender).getInstance());
            if (!(pi.getDelegate() instanceof ReplayProfileDelegate)) {
                return;
            }
            for (String s : ((ReplayProfileDelegate) pi.getDelegate()).r.players.keySet()) {


                suggestion.addEntry(new SuggestionEntry(s, Component.text(s, TextColor.color(0xff00ff))));

            }
        });
        addSyntax(this::exec, targetArgument);
    }
    public void exec(CommandSender sender, CommandContext context) {
        String target = context.get("target");
        ProfiledInstance pi = InstanceManager.get().getProfile((InstanceContainer) ((Player) sender).getInstance());
        if (!(pi.getDelegate() instanceof ReplayProfileDelegate)) {
            return;
        }
        Replay r = ((ReplayProfileDelegate) pi.getDelegate()).r;
        ReplayPlayer pp = r.players.get(target);
        if (pp == null) return;
        Position s = pp.getPosition();
       // s.setY(200);
        ((Player) sender).teleport(s);
        sender.sendMessage("Remove: " + pp.removeViewer((Player) sender));
        sender.sendMessage("Add: " + pp.addViewer((Player) sender));

    }
}
