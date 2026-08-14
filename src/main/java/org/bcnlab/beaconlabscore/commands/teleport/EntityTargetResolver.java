package org.bcnlab.beaconlabscore.commands.teleport;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

final class EntityTargetResolver {

    private static final List<String> SELECTOR_OPTIONS = List.of(
            "type=", "name=", "distance=", "limit=", "sort=", "gamemode=", "level=",
            "tag=", "team=", "x=", "y=", "z=", "dx=", "dy=", "dz=", "scores=",
            "advancements=", "predicate=", "nbt=", "x_rotation=", "y_rotation=",
            "width=", "height=", "motion="
    );

    private EntityTargetResolver() {
    }

    static List<Entity> resolve(String token, Player source) {
        if (token == null || token.isBlank()) {
            return List.of();
        }

        String value = token.trim();
        if (value.equals("*")) {
            return loadedEntities();
        }
        if (value.equalsIgnoreCase("@a")) {
            return onlinePlayers();
        }
        if (value.equalsIgnoreCase("@s")) {
            return List.of(source);
        }
        if (value.equalsIgnoreCase("@p")) {
            return nearest(source, onlinePlayers(), 1);
        }
        if (value.equalsIgnoreCase("@r")) {
            List<Entity> players = onlinePlayers();
            return players.isEmpty() ? List.of()
                    : List.of(players.get(ThreadLocalRandom.current().nextInt(players.size())));
        }
        if (value.equalsIgnoreCase("@e")) {
            return loadedEntities();
        }
        if (value.regionMatches(true, 0, "@e[", 0, 3) && value.endsWith("]")) {
            return resolveEntitySelector(value.substring(3, value.length() - 1), source);
        }

        Player exact = Bukkit.getPlayerExact(value);
        return exact != null && exact.isOnline() ? List.of(exact) : List.of();
    }

    static List<String> selectorSuggestions(String partial, Player source) {
        String value = partial == null ? "" : partial;
        if (!value.regionMatches(true, 0, "@e[", 0, 3)) {
            return List.of("*", "@a", "@p", "@r", "@s", "@e");
        }

        String body = value.substring(3);
        int comma = body.lastIndexOf(',');
        String optionPrefix = comma < 0 ? body : body.substring(comma + 1);
        String optionBase = comma < 0 ? "@e[" : "@e[" + body.substring(0, comma + 1);
        List<String> suggestions = new ArrayList<>();

        if (optionPrefix.isBlank()) {
            for (String type : entityTypeNames()) {
                suggestions.add(optionBase + "type=" + type + "]");
                suggestions.add(optionBase + "type=!" + type + "]");
            }
        }

        if (optionPrefix.regionMatches(true, 0, "type=", 0, 5)) {
            String typePartial = optionPrefix.substring(5).toLowerCase(Locale.ROOT);
            for (String type : entityTypeNames()) {
                if (type.toLowerCase(Locale.ROOT).startsWith(typePartial)) {
                    suggestions.add(optionBase + "type=" + type + "]");
                }
                if (("!" + type).toLowerCase(Locale.ROOT).startsWith(typePartial)) {
                    suggestions.add(optionBase + "type=!" + type + "]");
                }
            }
            return suggestions;
        }

        int equals = optionPrefix.indexOf('=');
        if (equals >= 0) {
            String key = optionPrefix.substring(0, equals).toLowerCase(Locale.ROOT);
            String valuePartial = optionPrefix.substring(equals + 1).toLowerCase(Locale.ROOT);
            List<String> values = selectorValues(key, source);
            if (!values.isEmpty()) {
                boolean negatable = key.equals("name") || key.equals("gamemode")
                        || key.equals("tag") || key.equals("team");
                return completeSelectorValues(optionBase, key, valuePartial, values, negatable);
            }
            return List.of();
        }

        String keyPartial = optionPrefix.toLowerCase(Locale.ROOT);
        for (String option : SELECTOR_OPTIONS) {
            if (option.startsWith(keyPartial)) {
                suggestions.add(optionBase + option);
            }
        }
        return suggestions;
    }

    private static List<String> selectorValues(String key, Player source) {
        return switch (key) {
            case "sort" -> List.of("nearest", "furthest", "random", "arbitrary");
            case "gamemode" -> List.of("survival", "creative", "adventure", "spectator");
            case "limit" -> List.of("1", "2", "5", "10", "20", "50", "100");
            case "distance" -> List.of("..5", "..10", "..32", "5..", "5..10");
            case "level" -> List.of("0", "1", "10", "..10", "10..");
            case "x" -> coordinateValues(source.getLocation().getX());
            case "y" -> coordinateValues(source.getLocation().getY());
            case "z" -> coordinateValues(source.getLocation().getZ());
            case "dx", "dy", "dz" -> List.of("0", "1", "5", "10");
            case "name" -> loadedEntities().stream().map(Entity::getName).distinct().sorted().toList();
            case "tag" -> loadedEntities().stream().flatMap(entity -> entity.getScoreboardTags().stream())
                    .distinct().sorted().toList();
            case "team" -> Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream()
                    .map(team -> team.getName()).sorted().toList();
            default -> List.of();
        };
    }

    private static List<String> completeSelectorValues(String optionBase, String key, String partial,
                                                        List<String> values, boolean negatable) {
        List<String> suggestions = new ArrayList<>();
        for (String value : values) {
            if (value.toLowerCase(Locale.ROOT).startsWith(partial)) {
                suggestions.add(optionBase + key + "=" + value + "]");
            }
            if (negatable && ("!" + value).toLowerCase(Locale.ROOT).startsWith(partial)) {
                suggestions.add(optionBase + key + "=!" + value + "]");
            }
        }
        return suggestions;
    }

    private static List<String> coordinateValues(double coordinate) {
        return List.of("~", "~0", String.format(Locale.ROOT, "%.2f", coordinate),
                String.valueOf((int) Math.floor(coordinate)));
    }

    private static List<Entity> resolveEntitySelector(String expression, Player source) {
        List<Entity> result = new ArrayList<>(loadedEntities());
        String sort = "arbitrary";
        Integer limit = null;
        Double minDistance = null;
        Double maxDistance = null;
        Double originX = source.getLocation().getX();
        Double originY = source.getLocation().getY();
        Double originZ = source.getLocation().getZ();
        Double deltaX = null;
        Double deltaY = null;
        Double deltaZ = null;

        for (String rawOption : expression.split(",")) {
            String[] option = rawOption.split("=", 2);
            if (option.length != 2) {
                continue;
            }

            String key = option[0].trim().toLowerCase(Locale.ROOT);
            String value = stripQuotes(option[1].trim());
            switch (key) {
                case "type" -> {
                    boolean negated = value.startsWith("!");
                    String typeName = negated ? value.substring(1) : value;
                    EntityType type = entityType(typeName);
                    if (type == null) {
                        return List.of();
                    }
                    result.removeIf(entity -> negated == (entity.getType() == type));
                }
                case "name" -> {
                    boolean negated = value.startsWith("!");
                    String name = negated ? value.substring(1) : value;
                    result.removeIf(entity -> negated == entity.getName().equalsIgnoreCase(name));
                }
                case "distance" -> {
                    double[] range = parseDistance(value);
                    if (range == null) {
                        return List.of();
                    }
                    minDistance = range[0];
                    maxDistance = range[1];
                }
                case "sort" -> sort = value.toLowerCase(Locale.ROOT);
                case "limit" -> {
                    try {
                        limit = Math.max(0, Integer.parseInt(value));
                    } catch (NumberFormatException ignored) {
                        return List.of();
                    }
                }
                case "gamemode" -> result.removeIf(entity -> !matchesGameMode(entity, value));
                case "level" -> result.removeIf(entity -> !matchesLevel(entity, value));
                case "tag" -> {
                    boolean negated = value.startsWith("!");
                    String tag = negated ? value.substring(1) : value;
                    result.removeIf(entity -> negated == entity.getScoreboardTags().contains(tag));
                }
                case "team" -> result.removeIf(entity -> !matchesTeam(entity, value));
                case "x" -> originX = parseNumber(value, originX);
                case "y" -> originY = parseNumber(value, originY);
                case "z" -> originZ = parseNumber(value, originZ);
                case "dx" -> deltaX = parseNumber(value, null);
                case "dy" -> deltaY = parseNumber(value, null);
                case "dz" -> deltaZ = parseNumber(value, null);
                default -> {
                    // scores, advancements, predicates, NBT, and rotation/motion
                    // require server internals and are not silently used as filters.
                }
            }
        }

        double minimum = minDistance == null ? 0.0 : minDistance;
        double maximum = maxDistance == null ? Double.MAX_VALUE : maxDistance;
        final double selectionX = originX;
        final double selectionY = originY;
        final double selectionZ = originZ;
        final Double selectionDeltaX = deltaX;
        final Double selectionDeltaY = deltaY;
        final Double selectionDeltaZ = deltaZ;
        result.removeIf(entity -> {
            if (entity.getWorld() != source.getWorld()) {
                return true;
            }
            double distance = entity.getLocation().distance(source.getLocation());
            if (distance < minimum || distance > maximum) {
                return true;
            }
            if (selectionDeltaX != null && !withinBox(entity.getLocation().getX(), selectionX, selectionX + selectionDeltaX)) {
                return true;
            }
            if (selectionDeltaY != null && !withinBox(entity.getLocation().getY(), selectionY, selectionY + selectionDeltaY)) {
                return true;
            }
            return selectionDeltaZ != null
                    && !withinBox(entity.getLocation().getZ(), selectionZ, selectionZ + selectionDeltaZ);
        });

        if (sort.equals("nearest")) {
            result.sort(Comparator.comparingDouble(entity -> distanceSquared(source, entity)));
        } else if (sort.equals("furthest")) {
            result.sort(Comparator.comparingDouble((Entity entity) -> distanceSquared(source, entity)).reversed());
        } else if (sort.equals("random")) {
            java.util.Collections.shuffle(result);
        }

        if (limit != null && result.size() > limit) {
            return List.copyOf(result.subList(0, limit));
        }
        return List.copyOf(result);
    }

    private static boolean withinBox(double value, double first, double second) {
        return value >= Math.min(first, second) && value <= Math.max(first, second);
    }

    private static boolean matchesGameMode(Entity entity, String value) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        boolean negated = value.startsWith("!");
        String mode = negated ? value.substring(1) : value;
        boolean matches = player.getGameMode().name().equalsIgnoreCase(mode);
        return negated != matches;
    }

    private static boolean matchesLevel(Entity entity, String value) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        try {
            if (value.contains("..")) {
                String[] bounds = value.split("\\.\\.", -1);
                int minimum = bounds[0].isBlank() ? Integer.MIN_VALUE : Integer.parseInt(bounds[0]);
                int maximum = bounds.length < 2 || bounds[1].isBlank()
                        ? Integer.MAX_VALUE : Integer.parseInt(bounds[1]);
                return player.getLevel() >= minimum && player.getLevel() <= maximum;
            }
            return player.getLevel() == Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static boolean matchesTeam(Entity entity, String value) {
        if (!(entity instanceof Player player)) {
            return value.startsWith("!");
        }
        boolean negated = value.startsWith("!");
        String team = negated ? value.substring(1) : value;
        boolean matches = player.getScoreboard().getEntryTeam(player.getName()) != null
                && player.getScoreboard().getEntryTeam(player.getName()).getName().equalsIgnoreCase(team);
        return negated != matches;
    }

    private static Double parseNumber(String value, Double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static double[] parseDistance(String value) {
        try {
            if (!value.contains("..")) {
                double distance = Double.parseDouble(value);
                return new double[]{distance, distance};
            }
            String[] bounds = value.split("\\.\\.", -1);
            double minimum = bounds[0].isBlank() ? 0.0 : Double.parseDouble(bounds[0]);
            double maximum = bounds.length < 2 || bounds[1].isBlank()
                    ? Double.MAX_VALUE : Double.parseDouble(bounds[1]);
            return new double[]{minimum, maximum};
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static List<Entity> nearest(Player source, List<Entity> entities, int limit) {
        return entities.stream()
                .sorted(Comparator.comparingDouble(entity -> distanceSquared(source, entity)))
                .limit(limit)
                .toList();
    }

    private static double distanceSquared(Player source, Entity entity) {
        return source.getWorld() == entity.getWorld()
                ? source.getLocation().distanceSquared(entity.getLocation())
                : Double.MAX_VALUE;
    }

    private static List<Entity> onlinePlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    private static List<Entity> loadedEntities() {
        return Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntities().stream())
                .toList();
    }

    private static EntityType entityType(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("minecraft:")) {
            normalized = normalized.substring("minecraft:".length());
        }
        for (EntityType type : EntityType.values()) {
            if (type.getKey() != null && type.getKey().getKey().equalsIgnoreCase(normalized)) {
                return type;
            }
            if (type.name().equalsIgnoreCase(normalized)) {
                return type;
            }
        }
        return null;
    }

    private static List<String> entityTypeNames() {
        return java.util.Arrays.stream(EntityType.values())
                .filter(type -> type != EntityType.UNKNOWN && type.getKey() != null)
                .map(type -> type.getKey().getKey())
                .flatMap(key -> java.util.stream.Stream.of(key, "minecraft:" + key))
                .distinct()
                .sorted()
                .toList();
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
