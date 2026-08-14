package org.bcnlab.beaconlabscore.utils;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bcnlab.beaconlabscore.commands.*;
import org.bcnlab.beaconlabscore.commands.chat.*;
import org.bcnlab.beaconlabscore.commands.player.*;
import org.bcnlab.beaconlabscore.commands.teleport.*;
import org.bcnlab.beaconlabscore.commands.time.*;
import org.bcnlab.beaconlabscore.commands.utils.*;
import org.bcnlab.beaconlabscore.commands.weather.*;
import org.bukkit.GameMode;

import java.util.List;

/** Registers every command through Paper's native BasicCommand lifecycle API. */
public final class CommandRegistry {

    private CommandRegistry() {
    }

    public static void registerAll(BeaconLabsCore plugin, Commands commands) {
        register(commands, "fly", "Toggles fly mode.", List.of(),
                new FlyCommand(legacyPrefix(plugin)));
        register(commands, "core", "Returns basic plugin information", List.of("beaconlabscore", "labscore"),
                new CoreCommand(plugin));
        register(commands, "heal", "Heals a player", List.of(), new HealCommand(plugin));
        register(commands, "enchant", "Enchants the item a player is holding", List.of("ench"), new EnchantCommand(plugin));
        register(commands, "repair", "Repairs the item a player is holding", List.of(), new RepairCommand(plugin));
        register(commands, "gamemode", "Changes a player's gamemode", List.of("gm", "mode"), new GamemodeCommand(plugin));
        register(commands, "invsee", "View another player's inventory.", List.of(), new InvseeCommand(plugin));
        register(commands, "workbench", "Use a portable workbench.", List.of("wb"), new WorkbenchCommand(plugin));
        register(commands, "enderchest", "Use a portable enderchest.", List.of("ec"), new EnderchestCommand(plugin));
        register(commands, "vanish", "Hide yourself from other players", List.of("v"), new VanishCommand(plugin));
        register(commands, "tp", "Teleport to a player or coordinates", List.of("teleport"), new TpCommand(plugin));
        register(commands, "back", "Return to your last death location", List.of(), new BackCommand(plugin));
        register(commands, "tphere", "Teleport a player to you", List.of("tph"), new TpHereCommand(plugin));
        register(commands, "spectate", "Spectate a player while being invisible", List.of("spec"), new SpectateCommand(plugin));
        register(commands, "endspectate", "Ends spectator mode", List.of("espec"), new EndSpectateCommand(plugin));
        register(commands, "chatsudo", "Sends chat messages as another player", List.of("csudo"),
                new ChatSudoCommand(plugin, net.luckperms.api.LuckPermsProvider.get()));
        register(commands, "sudo", "Executes commands as another player", List.of(), new SudoCommand(plugin));
        register(commands, "serverbroadcast", "Broadcast a message to the entire server", List.of("sbc"),
                new ServerBroadcastCommand(plugin));

        register(commands, "weather", "Change the weather", List.of("w"), new WeatherCommand(plugin));
        register(commands, "clear", "Change the weather to clear", List.of(), new ClearCommand(plugin));
        register(commands, "rain", "Change the weather to rain", List.of(), new RainCommand(plugin));
        register(commands, "storm", "Change the weather to storm", List.of(), new StormCommand(plugin));
        register(commands, "day", "Set the time to day", List.of(), new DayCommand(plugin));
        register(commands, "midnight", "Set the time to midnight", List.of(), new MidnightCommand(plugin));
        register(commands, "night", "Set the time to night", List.of(), new NightCommand(plugin));
        register(commands, "noon", "Set the time to noon", List.of(), new NoonCommand(plugin));
        register(commands, "time", "Change the time", List.of(), new TimeCommand(plugin));

        register(commands, "globalmute", "Mute the entire chat", List.of("gmute"), new GlobalMuteCommand(plugin));
        register(commands, "btps", "Returns the server TPS", List.of(), new TpsCommand(plugin));
        register(commands, "clearinventory", "Clears player inventories", List.of("clearinv", "cinv"),
                new ClearInventoryCommand(plugin));
        register(commands, "clearlag", "Removes disposable entities", List.of("clag"), new ClearLagCommand(plugin));

        register(commands, "tpa", "Request to teleport to another player", List.of(),
                new TpaCommand(legacyPrefix(plugin)));
        register(commands, "tpaccept", "Accept a teleportation request", List.of(),
                new TpAcceptCommand(legacyPrefix(plugin)));
        register(commands, "tpdeny", "Deny a teleportation request", List.of(),
                new TpDenyCommand(legacyPrefix(plugin)));
        register(commands, "randomteleport", "Teleport to a random safe location", List.of("rtp"),
                new RandomTeleportCommand(legacyPrefix(plugin), 5000));
        register(commands, "teleportcoordinates", "Teleport to coordinates", List.of("tpc", "teleportc"),
                new TpcCommand(plugin));

        register(commands, "warp", "Teleport to a saved warp location", List.of(),
                new WarpCommand(plugin, plugin.getWarpManager()));
        register(commands, "setwarp", "Create a warp at your current location", List.of(),
                new SetWarpCommand(plugin, plugin.getWarpManager()));
        register(commands, "delwarp", "Delete an existing warp", List.of(),
                new DelWarpCommand(plugin, plugin.getWarpManager()));
        register(commands, "warps", "List all available warps", List.of(),
                new WarpsCommand(plugin, plugin.getWarpManager()));

        register(commands, "gmc", "Change gamemode to creative", List.of(),
                new GamemodeShortcutCommand(plugin, GameMode.CREATIVE));
        register(commands, "gms", "Change gamemode to survival", List.of(),
                new GamemodeShortcutCommand(plugin, GameMode.SURVIVAL));
        register(commands, "gma", "Change gamemode to adventure", List.of(),
                new GamemodeShortcutCommand(plugin, GameMode.ADVENTURE));
        register(commands, "gmsp", "Change gamemode to spectator", List.of(),
                new GamemodeShortcutCommand(plugin, GameMode.SPECTATOR));
    }

    private static void register(Commands commands, String name, String description,
                                 List<String> aliases, BasicCommand command) {
        commands.register(name, description, aliases, command);
    }

    private static String legacyPrefix(BeaconLabsCore plugin) {
        return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacyAmpersand().serialize(plugin.getPrefix());
    }
}
