package org.bcnlab.beaconlabscore.utils;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bcnlab.beaconlabscore.commands.*;
import org.bcnlab.beaconlabscore.commands.chat.*;
import org.bcnlab.beaconlabscore.commands.player.*;
import org.bcnlab.beaconlabscore.commands.teleport.*;
import org.bcnlab.beaconlabscore.commands.time.*;
import org.bcnlab.beaconlabscore.commands.utils.*;
import org.bcnlab.beaconlabscore.commands.weather.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.GameMode;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;

public class CommandRegistry {

    public static void registerAll(BeaconLabsCore plugin, Commands commands) {
        String prefix = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().serialize(plugin.getPrefix());
        registerLegacyCommand(commands, "fly", "Toggles fly mode.", List.of(), new FlyCommand(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().serialize(plugin.getPrefix())));
        registerLegacyCommand(commands, "core", "Returns basic plugin information", List.of("beaconlabscore", "labscore"), new CoreCommand(plugin));
        registerLegacyCommand(commands, "heal", "Heals a player", List.of(), new HealCommand(plugin));
        registerLegacyCommand(commands, "gamemode", "Changes a players gamemode", List.of("gm", "mode"), new GamemodeCommand(plugin));
        registerLegacyCommand(commands, "invsee", "View another player's inventory.", List.of(), new InvseeCommand(plugin));
        registerLegacyCommand(commands, "workbench", "Use a portable workbench.", List.of("wb"), new WorkbenchCommand(plugin));
        registerLegacyCommand(commands, "enderchest", "Use a portable enderchest.", List.of("ec"), new EnderchestCommand(plugin));
        registerLegacyCommand(commands, "vanish", "Hide yourself from other players", List.of("v"), new VanishCommand(plugin));
        registerLegacyCommand(commands, "tp", "Teleport to a player or teleport players", List.of("teleport"), new TpCommand(plugin));
        registerLegacyCommand(commands, "tphere", "Teleport a player to you", List.of("tph"), new TpHereCommand(plugin));
        registerLegacyCommand(commands, "spectate", "Spectates a player while being invisible", List.of("spec"), new SpectateCommand(plugin));
        registerLegacyCommand(commands, "endspectate", "Ends spectator mode", List.of("espec"), new EndSpectateCommand(plugin));
        registerLegacyCommand(commands, "chatsudo", "Sends chat messages as another player", List.of("csudo"), new ChatSudoCommand(plugin, net.luckperms.api.LuckPermsProvider.get()));
        registerLegacyCommand(commands, "sudo", "Executes commands as another player", List.of(), new SudoCommand(plugin));
        registerLegacyCommand(commands, "serverbroadcast", "Broadcast a message to the entire server (not proxywide!)", List.of("sbc"), new ServerBroadcastCommand(plugin));
        registerLegacyCommand(commands, "weather", "Change the weather", List.of("w"), new WeatherCommand(plugin));
        registerLegacyCommand(commands, "clear", "Change the weather to clear", List.of(), new ClearCommand(plugin));
        registerLegacyCommand(commands, "rain", "Change the weather to rain", List.of(), new RainCommand(plugin));
        registerLegacyCommand(commands, "storm", "Change the weather to storm", List.of(), new StormCommand(plugin));
        registerLegacyCommand(commands, "day", "Set the time to day", List.of(), new DayCommand(plugin));
        registerLegacyCommand(commands, "midnight", "Set the time to midnight", List.of(), new MidnightCommand(plugin));
        registerLegacyCommand(commands, "night", "Set the time to night", List.of(), new NightCommand(plugin));
        registerLegacyCommand(commands, "noon", "Set the time to noon", List.of(), new NoonCommand(plugin));
        registerLegacyCommand(commands, "time", "Change the time", List.of(), new TimeCommand(plugin));
        registerLegacyCommand(commands, "globalmute", "Mute the entire chat", List.of("gmute"), new GlobalMuteCommand(plugin));
        registerLegacyCommand(commands, "btps", "Returns the servers TPS", List.of(), new TpsCommand(plugin));
        registerLegacyCommand(commands, "clearinventory", "Clears the inventory of players", List.of("clearinv", "cinv"), new ClearInventoryCommand(plugin));
        registerLegacyCommand(commands, "clearlag", "Removes entities to preserver preformance", List.of("clag"), new ClearLagCommand(plugin));
        registerLegacyCommand(commands, "tpa", "Allows a user to request to teleport to someone else", List.of(), new TpaCommand(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().serialize(plugin.getPrefix())));
        registerLegacyCommand(commands, "tpaccept", "Accept a teleportation request", List.of(), new TpAcceptCommand(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().serialize(plugin.getPrefix())));
        registerLegacyCommand(commands, "tpdeny", "Deny a teleportation request", List.of(), new TpDenyCommand(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().serialize(plugin.getPrefix())));
        registerLegacyCommand(commands, "randomteleport", "Teleport to a random location on the world", List.of("rtp"), new RandomTeleportCommand(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().serialize(plugin.getPrefix()), 5000));
        registerLegacyCommand(commands, "teleportcoordinates", "Teleports the user to specific coordinates", List.of("tpc", "teleportc"), new TpcCommand(plugin));
        registerLegacyCommand(commands, "warp", "Teleports to a warp location", List.of(), new WarpCommand(plugin, plugin.getWarpManager()));
        registerLegacyCommand(commands, "setwarp", "Creates a new warp at your current location", List.of(), new SetWarpCommand(plugin, plugin.getWarpManager()));
        registerLegacyCommand(commands, "delwarp", "Deletes an existing warp", List.of(), new DelWarpCommand(plugin, plugin.getWarpManager()));
        registerLegacyCommand(commands, "warps", "Lists all available warps", List.of(), new WarpsCommand(plugin, plugin.getWarpManager()));
        registerLegacyCommand(commands, "gmc", "Changes gamemode to creative", List.of(), new GamemodeShortcutCommand(plugin, GameMode.CREATIVE));
        registerLegacyCommand(commands, "gms", "Changes gamemode to survival", List.of(), new GamemodeShortcutCommand(plugin, GameMode.SURVIVAL));
        registerLegacyCommand(commands, "gma", "Changes gamemode to adventure", List.of(), new GamemodeShortcutCommand(plugin, GameMode.ADVENTURE));
        registerLegacyCommand(commands, "gmsp", "Changes gamemode to spectator", List.of(), new GamemodeShortcutCommand(plugin, GameMode.SPECTATOR));
    }

    private static void registerLegacyCommand(Commands commands, String name, String description, List<String> aliases, CommandExecutor executor) {
        LiteralCommandNode<CommandSourceStack> node = Commands.literal(name)
            .executes(ctx -> {
                CommandSender sender = ctx.getSource().getSender();
                executor.onCommand(sender, null, name, new String[0]);
                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.argument("args", StringArgumentType.greedyString())
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    String argsStr = StringArgumentType.getString(ctx, "args");
                    String[] args = argsStr.split(" ");
                    executor.onCommand(sender, null, name, args);
                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();
            
        commands.register(node, description, aliases);
    }
}
