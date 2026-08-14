package org.bcnlab.beaconlabscore.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class CommandCompletion {

    private CommandCompletion() {
    }

    public static String argument(String[] args, int index) {
        return index >= 0 && index < args.length ? args[index] : "";
    }

    public static List<String> players(String partial) {
        String lower = partial == null ? "" : partial.toLowerCase(Locale.ROOT);
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(lower))
                .toList();
    }

    public static List<String> filter(Collection<String> values, String partial) {
        String lower = partial == null ? "" : partial.toLowerCase(Locale.ROOT);
        return values.stream()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(lower))
                .toList();
    }
}
