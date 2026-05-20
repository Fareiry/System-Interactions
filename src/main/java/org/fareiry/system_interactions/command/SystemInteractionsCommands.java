package org.fareiry.system_interactions.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.fareiry.system_interactions.network.SystemInteractionsNetwork;
import org.fareiry.system_interactions.util.SystemInteractionIcon;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class SystemInteractionsCommands {
    private SystemInteractionsCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                literal("si")
                        .requires(source -> source.hasPermission(2))
                        .then(literal("dialog")
                                .then(argument("targets", EntityArgument.players())
                                        .then(literal("info")
                                                .then(argument("title", StringArgumentType.string())
                                                        .then(argument("message", StringArgumentType.greedyString())
                                                                .executes(context -> sendDialog(context, SystemInteractionIcon.INFO)))))
                                        .then(literal("warning")
                                                .then(argument("title", StringArgumentType.string())
                                                        .then(argument("message", StringArgumentType.greedyString())
                                                                .executes(context -> sendDialog(context, SystemInteractionIcon.WARNING)))))
                                        .then(literal("error")
                                                .then(argument("title", StringArgumentType.string())
                                                        .then(argument("message", StringArgumentType.greedyString())
                                                                .executes(context -> sendDialog(context, SystemInteractionIcon.ERROR)))))
                                        // Simple/default form: /si dialog <targets> <title> <message>
                                        .then(argument("title", StringArgumentType.string())
                                                .then(argument("message", StringArgumentType.greedyString())
                                                        .executes(context -> sendDialog(context, SystemInteractionIcon.INFO))))
                                )
                        )
                        .then(literal("toast")
                                .then(argument("targets", EntityArgument.players())
                                        .then(literal("info")
                                                .then(argument("title", StringArgumentType.string())
                                                        .then(argument("message", StringArgumentType.greedyString())
                                                                .executes(context -> sendToast(context, SystemInteractionIcon.INFO)))))
                                        .then(literal("warning")
                                                .then(argument("title", StringArgumentType.string())
                                                        .then(argument("message", StringArgumentType.greedyString())
                                                                .executes(context -> sendToast(context, SystemInteractionIcon.WARNING)))))
                                        .then(literal("error")
                                                .then(argument("title", StringArgumentType.string())
                                                        .then(argument("message", StringArgumentType.greedyString())
                                                                .executes(context -> sendToast(context, SystemInteractionIcon.ERROR)))))
                                        // Simple/default form: /si toast <targets> <title> <message>
                                        .then(argument("title", StringArgumentType.string())
                                                .then(argument("message", StringArgumentType.greedyString())
                                                        .executes(context -> sendToast(context, SystemInteractionIcon.INFO))))
                                )
                        )
        );
    }

    private static int sendDialog(CommandContext<CommandSourceStack> context, SystemInteractionIcon icon) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        String title = StringArgumentType.getString(context, "title");
        String message = StringArgumentType.getString(context, "message");

        SystemInteractionsNetwork.sendDialog(targets, icon, title, message);
        sendFeedback(context, "dialog", targets.size());
        return Command.SINGLE_SUCCESS;
    }

    private static int sendToast(CommandContext<CommandSourceStack> context, SystemInteractionIcon icon) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        String title = StringArgumentType.getString(context, "title");
        String message = StringArgumentType.getString(context, "message");

        SystemInteractionsNetwork.sendToast(targets, icon, title, message);
        sendFeedback(context, "toast", targets.size());
        return Command.SINGLE_SUCCESS;
    }

    private static void sendFeedback(CommandContext<CommandSourceStack> context, String type, int targetCount) {
        String translationKey = switch (type) {
            case "dialog" -> "commands.system_interactions.sent.dialog";
            case "toast" -> "commands.system_interactions.sent.toast";
            default -> "commands.system_interactions.sent.unknown";
        };

        // Do not broadcast this to other operators/players. For ARG/horror usage,
        // the confirmation must stay visible only to the command source; otherwise
        // the target immediately sees that an effect was intentionally sent.
        context.getSource().sendSuccess(
                () -> Component.translatable(translationKey, targetCount),
                false
        );
    }
}
