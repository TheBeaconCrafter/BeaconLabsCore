package org.bcnlab.beaconlabscore;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bcnlab.beaconlabscore.commands.*;
import org.bcnlab.beaconlabscore.commands.chat.ChatSudoCommand;
import org.bcnlab.beaconlabscore.commands.chat.GlobalMuteCommand;
import org.bcnlab.beaconlabscore.commands.chat.ServerBroadcastCommand;
import org.bcnlab.beaconlabscore.commands.player.*;
import org.bcnlab.beaconlabscore.commands.teleport.*;
import org.bcnlab.beaconlabscore.commands.time.*;
import org.bcnlab.beaconlabscore.commands.utils.*;
import org.bcnlab.beaconlabscore.commands.weather.ClearCommand;
import org.bcnlab.beaconlabscore.commands.weather.RainCommand;
import org.bcnlab.beaconlabscore.commands.weather.StormCommand;
import org.bcnlab.beaconlabscore.commands.weather.WeatherCommand;
import org.bcnlab.beaconlabscore.listeners.*;
import org.bcnlab.beaconlabscore.utils.WarpManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.TabCompleter;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public final class BeaconLabsCore extends JavaPlugin implements Listener {

    private String pluginPrefix;
    private String pluginVersion = "1.3.0";
    private ChatFormatter chatFormatter;
    private org.bcnlab.beaconlabscore.listeners.NametagGenerator nametagGenerator;
    private WarpManager warpManager;

    //CONFIG
    private boolean joinMessagesEnabled;
    private boolean leaveMessagesEnabled;
    private boolean deathMessagesEnabled;
    private String customJoinMessage;
    private String customLeaveMessage;
    private String customDeathMessage;
    private String noPermsMessage = "&cYou do not have permission to use this command.";
    private int warpDelay; // Delay in seconds for warp teleportation

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        // Save the default config if it doesn't exist
        createDefaultConfig();

        // Load the configuration
        loadConfig();

        LuckPerms luckPerms = LuckPermsProvider.get();
        chatFormatter = new ChatFormatter(this, luckPerms);
        
        // Initialize the WarpManager
        warpManager = new WarpManager(this);

        // Register JoinLeaveMessages listener
        new JoinLeaveMessages(this);

        // Register other commands and listeners
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new JoinLeaveMessages(this), this);
        getServer().getPluginManager().registerEvents(new DeathMessages(this), this);
        getServer().getPluginManager().registerEvents(new UnknownCommandListener(this), this);
        nametagGenerator = new org.bcnlab.beaconlabscore.listeners.NametagGenerator();
        getServer().getPluginManager().registerEvents(nametagGenerator, this);
        EnchantCommand enchantCommand = new EnchantCommand(this);
        RepairCommand repairCommand = new RepairCommand(this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            // /fly
            org.bukkit.command.CommandExecutor cmd_fly = new FlyCommand(pluginPrefix);
            commands.register(
                Commands.literal("fly")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_fly.onCommand(sender, null, "fly", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_fly.onCommand(sender, null, "fly", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "fly command",
                java.util.List.of()
            );

            // /core
            org.bukkit.command.CommandExecutor cmd_core = new CoreCommand(this);
            commands.register(
                Commands.literal("core")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_core.onCommand(sender, null, "core", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_core.onCommand(sender, null, "core", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "core command",
                java.util.List.of()
            );

            // /heal
            org.bukkit.command.CommandExecutor cmd_heal = new HealCommand(this);
            commands.register(
                Commands.literal("heal")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_heal.onCommand(sender, null, "heal", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_heal.onCommand(sender, null, "heal", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "heal command",
                java.util.List.of()
            );

            // /gamemode
            org.bukkit.command.CommandExecutor cmd_gamemode = new GamemodeCommand(this);
            commands.register(
                Commands.literal("gamemode")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_gamemode.onCommand(sender, null, "gamemode", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_gamemode.onCommand(sender, null, "gamemode", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "gamemode command",
                java.util.List.of()
            );

            // /invsee
            org.bukkit.command.CommandExecutor cmd_invsee = new InvseeCommand(this);
            commands.register(
                Commands.literal("invsee")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_invsee.onCommand(sender, null, "invsee", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_invsee.onCommand(sender, null, "invsee", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "invsee command",
                java.util.List.of()
            );

            // /workbench
            org.bukkit.command.CommandExecutor cmd_workbench = new WorkbenchCommand(this);
            commands.register(
                Commands.literal("workbench")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_workbench.onCommand(sender, null, "workbench", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_workbench.onCommand(sender, null, "workbench", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "workbench command",
                java.util.List.of()
            );

            // /enderchest
            org.bukkit.command.CommandExecutor cmd_enderchest = new EnderchestCommand(this);
            commands.register(
                Commands.literal("enderchest")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_enderchest.onCommand(sender, null, "enderchest", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_enderchest.onCommand(sender, null, "enderchest", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "enderchest command",
                java.util.List.of()
            );

            // /vanish
            org.bukkit.command.CommandExecutor cmd_vanish = new VanishCommand(this);
            commands.register(
                Commands.literal("vanish")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_vanish.onCommand(sender, null, "vanish", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_vanish.onCommand(sender, null, "vanish", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "vanish command",
                java.util.List.of()
            );

            // /tp
            org.bukkit.command.CommandExecutor cmd_tp = new TpCommand(this);
            commands.register(
                Commands.literal("tp")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_tp.onCommand(sender, null, "tp", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_tp.onCommand(sender, null, "tp", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "tp command",
                java.util.List.of()
            );

            // /tphere
            org.bukkit.command.CommandExecutor cmd_tphere = new TpHereCommand(this);
            commands.register(
                Commands.literal("tphere")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_tphere.onCommand(sender, null, "tphere", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_tphere.onCommand(sender, null, "tphere", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "tphere command",
                java.util.List.of()
            );

            // /spectate
            org.bukkit.command.CommandExecutor cmd_spectate = new SpectateCommand(this);
            commands.register(
                Commands.literal("spectate")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_spectate.onCommand(sender, null, "spectate", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_spectate.onCommand(sender, null, "spectate", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "spectate command",
                java.util.List.of()
            );

            // /endspectate
            org.bukkit.command.CommandExecutor cmd_endspectate = new EndSpectateCommand(this);
            commands.register(
                Commands.literal("endspectate")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_endspectate.onCommand(sender, null, "endspectate", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_endspectate.onCommand(sender, null, "endspectate", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "endspectate command",
                java.util.List.of()
            );

            // /chatsudo
            org.bukkit.command.CommandExecutor cmd_chatsudo = new ChatSudoCommand(this, luckPerms);
            commands.register(
                Commands.literal("chatsudo")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_chatsudo.onCommand(sender, null, "chatsudo", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_chatsudo.onCommand(sender, null, "chatsudo", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "chatsudo command",
                java.util.List.of()
            );

            // /sudo
            org.bukkit.command.CommandExecutor cmd_sudo = new SudoCommand(this);
            commands.register(
                Commands.literal("sudo")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_sudo.onCommand(sender, null, "sudo", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_sudo.onCommand(sender, null, "sudo", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "sudo command",
                java.util.List.of()
            );

            // /serverbroadcast
            org.bukkit.command.CommandExecutor cmd_serverbroadcast = new ServerBroadcastCommand(this);
            commands.register(
                Commands.literal("serverbroadcast")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_serverbroadcast.onCommand(sender, null, "serverbroadcast", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_serverbroadcast.onCommand(sender, null, "serverbroadcast", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "serverbroadcast command",
                java.util.List.of()
            );

            // /weather
            org.bukkit.command.CommandExecutor cmd_weather = new WeatherCommand(this);
            commands.register(
                Commands.literal("weather")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_weather.onCommand(sender, null, "weather", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_weather.onCommand(sender, null, "weather", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "weather command",
                java.util.List.of()
            );

            // /clear
            org.bukkit.command.CommandExecutor cmd_clear = new ClearCommand(this);
            commands.register(
                Commands.literal("clear")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_clear.onCommand(sender, null, "clear", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_clear.onCommand(sender, null, "clear", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "clear command",
                java.util.List.of()
            );

            // /rain
            org.bukkit.command.CommandExecutor cmd_rain = new RainCommand(this);
            commands.register(
                Commands.literal("rain")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_rain.onCommand(sender, null, "rain", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_rain.onCommand(sender, null, "rain", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "rain command",
                java.util.List.of()
            );

            // /storm
            org.bukkit.command.CommandExecutor cmd_storm = new StormCommand(this);
            commands.register(
                Commands.literal("storm")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_storm.onCommand(sender, null, "storm", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_storm.onCommand(sender, null, "storm", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "storm command",
                java.util.List.of()
            );

            // /day
            org.bukkit.command.CommandExecutor cmd_day = new DayCommand(this);
            commands.register(
                Commands.literal("day")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_day.onCommand(sender, null, "day", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_day.onCommand(sender, null, "day", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "day command",
                java.util.List.of()
            );

            // /midnight
            org.bukkit.command.CommandExecutor cmd_midnight = new MidnightCommand(this);
            commands.register(
                Commands.literal("midnight")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_midnight.onCommand(sender, null, "midnight", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_midnight.onCommand(sender, null, "midnight", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "midnight command",
                java.util.List.of()
            );

            // /night
            org.bukkit.command.CommandExecutor cmd_night = new NightCommand(this);
            commands.register(
                Commands.literal("night")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_night.onCommand(sender, null, "night", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_night.onCommand(sender, null, "night", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "night command",
                java.util.List.of()
            );

            // /noon
            org.bukkit.command.CommandExecutor cmd_noon = new NoonCommand(this);
            commands.register(
                Commands.literal("noon")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_noon.onCommand(sender, null, "noon", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_noon.onCommand(sender, null, "noon", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "noon command",
                java.util.List.of()
            );

            // /time
            org.bukkit.command.CommandExecutor cmd_time = new TimeCommand(this);
            commands.register(
                Commands.literal("time")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_time.onCommand(sender, null, "time", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_time.onCommand(sender, null, "time", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "time command",
                java.util.List.of()
            );

            // /globalmute
            org.bukkit.command.CommandExecutor cmd_globalmute = new GlobalMuteCommand(this);
            commands.register(
                Commands.literal("globalmute")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_globalmute.onCommand(sender, null, "globalmute", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_globalmute.onCommand(sender, null, "globalmute", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "globalmute command",
                java.util.List.of()
            );

            // /btps
            org.bukkit.command.CommandExecutor cmd_btps = new TpsCommand(this);
            commands.register(
                Commands.literal("btps")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_btps.onCommand(sender, null, "btps", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_btps.onCommand(sender, null, "btps", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "btps command",
                java.util.List.of()
            );

            // /clearinventory
            org.bukkit.command.CommandExecutor cmd_clearinventory = new ClearInventoryCommand(this);
            commands.register(
                Commands.literal("clearinventory")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_clearinventory.onCommand(sender, null, "clearinventory", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_clearinventory.onCommand(sender, null, "clearinventory", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "clearinventory command",
                java.util.List.of()
            );

            // /clearlag
            org.bukkit.command.CommandExecutor cmd_clearlag = new ClearLagCommand(this);
            commands.register(
                Commands.literal("clearlag")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_clearlag.onCommand(sender, null, "clearlag", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_clearlag.onCommand(sender, null, "clearlag", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "clearlag command",
                java.util.List.of()
            );

            // /tpa
            org.bukkit.command.CommandExecutor cmd_tpa = new TpaCommand(this.pluginPrefix);
            commands.register(
                Commands.literal("tpa")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_tpa.onCommand(sender, null, "tpa", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_tpa.onCommand(sender, null, "tpa", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "tpa command",
                java.util.List.of()
            );

            // /tpaccept
            org.bukkit.command.CommandExecutor cmd_tpaccept = new TpAcceptCommand(this.pluginPrefix);
            commands.register(
                Commands.literal("tpaccept")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_tpaccept.onCommand(sender, null, "tpaccept", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_tpaccept.onCommand(sender, null, "tpaccept", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "tpaccept command",
                java.util.List.of()
            );

            // /tpdeny
            org.bukkit.command.CommandExecutor cmd_tpdeny = new TpDenyCommand(this.pluginPrefix);
            commands.register(
                Commands.literal("tpdeny")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_tpdeny.onCommand(sender, null, "tpdeny", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_tpdeny.onCommand(sender, null, "tpdeny", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "tpdeny command",
                java.util.List.of()
            );

            // /randomteleport
            org.bukkit.command.CommandExecutor cmd_randomteleport = new RandomTeleportCommand(this.pluginPrefix, 5000);
            commands.register(
                Commands.literal("randomteleport")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_randomteleport.onCommand(sender, null, "randomteleport", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_randomteleport.onCommand(sender, null, "randomteleport", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "randomteleport command",
                java.util.List.of()
            );

            // /teleportcoordinates
            org.bukkit.command.CommandExecutor cmd_teleportcoordinates = new TpcCommand(this);
            commands.register(
                Commands.literal("teleportcoordinates")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_teleportcoordinates.onCommand(sender, null, "teleportcoordinates", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_teleportcoordinates.onCommand(sender, null, "teleportcoordinates", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "teleportcoordinates command",
                java.util.List.of()
            );

            // /warp
            org.bukkit.command.CommandExecutor cmd_warp = new WarpCommand(this, warpManager);
            commands.register(
                Commands.literal("warp")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_warp.onCommand(sender, null, "warp", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String input = builder.getRemaining();
                            String[] parts = input.isEmpty() ? new String[0] : input.split(" ", -1);
                            java.util.List<String> suggestions = ((org.bukkit.command.TabCompleter)cmd_warp).onTabComplete(sender, null, "warp", parts);
                            if (suggestions != null) {
                                for (String s : suggestions) builder.suggest(s);
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_warp.onCommand(sender, null, "warp", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "warp command",
                java.util.List.of()
            );

            // /setwarp
            org.bukkit.command.CommandExecutor cmd_setwarp = new SetWarpCommand(this, warpManager);
            commands.register(
                Commands.literal("setwarp")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_setwarp.onCommand(sender, null, "setwarp", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_setwarp.onCommand(sender, null, "setwarp", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "setwarp command",
                java.util.List.of()
            );

            // /delwarp
            org.bukkit.command.CommandExecutor cmd_delwarp = new DelWarpCommand(this, warpManager);
            commands.register(
                Commands.literal("delwarp")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_delwarp.onCommand(sender, null, "delwarp", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String input = builder.getRemaining();
                            String[] parts = input.isEmpty() ? new String[0] : input.split(" ", -1);
                            java.util.List<String> suggestions = ((org.bukkit.command.TabCompleter)cmd_delwarp).onTabComplete(sender, null, "delwarp", parts);
                            if (suggestions != null) {
                                for (String s : suggestions) builder.suggest(s);
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_delwarp.onCommand(sender, null, "delwarp", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "delwarp command",
                java.util.List.of()
            );

            // /warps
            org.bukkit.command.CommandExecutor cmd_warps = new WarpsCommand(this, warpManager);
            commands.register(
                Commands.literal("warps")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_warps.onCommand(sender, null, "warps", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String input = builder.getRemaining();
                            String[] parts = input.isEmpty() ? new String[0] : input.split(" ", -1);
                            java.util.List<String> suggestions = ((org.bukkit.command.TabCompleter)cmd_warps).onTabComplete(sender, null, "warps", parts);
                            if (suggestions != null) {
                                for (String s : suggestions) builder.suggest(s);
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_warps.onCommand(sender, null, "warps", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "warps command",
                java.util.List.of()
            );

            // /gmc
            org.bukkit.command.CommandExecutor cmd_gmc = new GamemodeShortcutCommand(this, GameMode.CREATIVE);
            commands.register(
                Commands.literal("gmc")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_gmc.onCommand(sender, null, "gmc", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_gmc.onCommand(sender, null, "gmc", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "gmc command",
                java.util.List.of()
            );

            // /gms
            org.bukkit.command.CommandExecutor cmd_gms = new GamemodeShortcutCommand(this, GameMode.SURVIVAL);
            commands.register(
                Commands.literal("gms")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_gms.onCommand(sender, null, "gms", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_gms.onCommand(sender, null, "gms", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "gms command",
                java.util.List.of()
            );

            // /gma
            org.bukkit.command.CommandExecutor cmd_gma = new GamemodeShortcutCommand(this, GameMode.ADVENTURE);
            commands.register(
                Commands.literal("gma")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_gma.onCommand(sender, null, "gma", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_gma.onCommand(sender, null, "gma", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "gma command",
                java.util.List.of()
            );

            // /gmsp
            org.bukkit.command.CommandExecutor cmd_gmsp = new GamemodeShortcutCommand(this, GameMode.SPECTATOR);
            commands.register(
                Commands.literal("gmsp")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_gmsp.onCommand(sender, null, "gmsp", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_gmsp.onCommand(sender, null, "gmsp", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "gmsp command",
                java.util.List.of()
            );

            // /ench
            org.bukkit.command.CommandExecutor cmd_ench = enchantCommand;
            commands.register(
                Commands.literal("ench")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_ench.onCommand(sender, null, "ench", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String input = builder.getRemaining();
                            String[] parts = input.isEmpty() ? new String[0] : input.split(" ", -1);
                            java.util.List<String> suggestions = ((org.bukkit.command.TabCompleter)cmd_ench).onTabComplete(sender, null, "ench", parts);
                            if (suggestions != null) {
                                for (String s : suggestions) builder.suggest(s);
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_ench.onCommand(sender, null, "ench", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "ench command",
                java.util.List.of()
            );

            // /repair
            org.bukkit.command.CommandExecutor cmd_repair = repairCommand;
            commands.register(
                Commands.literal("repair")
                    .executes(ctx -> {
                        org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                        cmd_repair.onCommand(sender, null, "repair", new String[0]);
                        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.argument("args", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String input = builder.getRemaining();
                            String[] parts = input.isEmpty() ? new String[0] : input.split(" ", -1);
                            java.util.List<String> suggestions = ((org.bukkit.command.TabCompleter)cmd_repair).onTabComplete(sender, null, "repair", parts);
                            if (suggestions != null) {
                                for (String s : suggestions) builder.suggest(s);
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            org.bukkit.command.CommandSender sender = ctx.getSource().getSender();
                            String argsStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "args");
                            String[] args = argsStr.split(" ");
                            cmd_repair.onCommand(sender, null, "repair", args);
                            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                        })
                    )
                    .build(),
                "repair command",
                java.util.List.of()
            );

        });


        // Plugin startup logic
        getLogger().info(pluginPrefix + "BeaconLabsCore was enabled!");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true); // Prevent default chat behavior
        final Player p = event.getPlayer();
        if (GlobalMuteCommand.globalmute) {
            if (!p.hasPermission("beaconlabs.core.globalmute.ignore")) {
                p.sendMessage(getPrefix().append(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<green>Chat is <red>deactivated!")));
                event.setCancelled(true);
            } else {
                chatFormatter.onPlayerChat(event.getPlayer(), event.getMessage());
            }
        }
        else {
            chatFormatter.onPlayerChat(event.getPlayer(), event.getMessage());
        }
    }

    @Override
    public void onDisable() {
        // Close the warp database connection
        if (warpManager != null) {
            warpManager.closeConnection();
        }

        // Plugin shutdown logic
        getLogger().info(pluginPrefix + "BeaconLabsCore was disabled!");
    }

    public org.bcnlab.beaconlabscore.listeners.NametagGenerator getNametagGenerator() {
        return nametagGenerator;
    }

    // Method to load the configuration
    private void loadConfig() {
        FileConfiguration config = getConfig();

        // Load plugin prefix from config
        pluginPrefix = config.getString("plugin-prefix", "<gradient:gold:yellow>BeaconLabs</gradient> <dark_gray>»</dark_gray> ");

        // Load join and leave messages enabled status
        joinMessagesEnabled = config.getBoolean("join-messages.enabled", true);
        leaveMessagesEnabled = config.getBoolean("leave-messages.enabled", true);
        deathMessagesEnabled = config.getBoolean("death-messages.enabled", true);
        customJoinMessage = config.getString("join-messages.custom", "&8[&a+&8] &7{player}");
        leaveMessagesEnabled = config.getBoolean("leave-messages.enabled", true);
        customLeaveMessage = config.getString("leave-messages.custom", "&8[&c-&8] &7{player}");
        customDeathMessage = config.getString("death-messages.custom", "&4&lERROR");
        
        // Load warp delay
        warpDelay = config.getInt("warp-delay", 3);

        // Prepend prefix to custom messages (legacy section codes from config)
        customDeathMessage = pluginPrefix + customDeathMessage;
    }

    // Method to create the default configuration if it doesn't exist
    private void createDefaultConfig() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        config.addDefault("plugin-prefix", "<gradient:gold:yellow>BeaconLabs</gradient> <dark_gray>»</dark_gray> ");
        config.addDefault("join-messages.enabled", true);
        config.addDefault("join-messages.custom", "&7[&a+&7] &9{player}");
        config.addDefault("leave-messages.enabled", true);
        config.addDefault("leave-messages.custom", "&7[&a-&7] &9{player}");
        config.addDefault("death-messages.enabled", true);
        config.addDefault("death-messages.custom", "&c{player} was killed by {other}");
        config.addDefault("warp-delay", 3);
        saveConfig();
    }

    public Component getPrefix() {
        return MiniMessage.miniMessage().deserialize(pluginPrefix);
    }

    public String getVersion() {
        return pluginVersion;
    }

    public boolean areJoinMessagesEnabled() {
        return joinMessagesEnabled;
    }

    public boolean areLeaveMessagesEnabled() {
        return leaveMessagesEnabled;
    }

    public boolean areDeathMessagesEnabled() {
        return deathMessagesEnabled;
    }

    public String getCustomJoinMessage() {
        return customJoinMessage;
    }

    public String getCustomLeaveMessage() {
        return customLeaveMessage;
    }

    public String getCustomDeathMessage() {
        return customDeathMessage;
    }

    public String getNoPermsMessage() {
        return noPermsMessage;
    }
    
    public int getWarpDelay() {
        return warpDelay;
    }
}
