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
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.Position;

public class ReplaySeekCommand extends Command {
    public ReplaySeekCommand() {
        super("rseek");
        var targetArgument = ArgumentType.Integer("tick");

        addSyntax(this::exec, targetArgument);
    }
    public void exec(CommandSender sender, CommandContext context) {
        int target = context.get("tick");
        ProfiledInstance pi = InstanceManager.get().getProfile((InstanceContainer) ((Player) sender).getInstance());
        if (!(pi.getDelegate() instanceof ReplayProfileDelegate)) {
            return;
        }
        Replay r = ((ReplayProfileDelegate) pi.getDelegate()).r;
        r.currentTick = target - 1;
        r.nextTick();


    }
}
