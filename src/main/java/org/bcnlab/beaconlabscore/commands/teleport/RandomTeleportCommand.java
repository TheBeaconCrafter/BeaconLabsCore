package org.bcnlab.beaconlabscore.commands.teleport;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

/** Teleports a player to a random safe surface location in their current world. */
public class RandomTeleportCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final Component pluginPrefix;
    private final int maxRange;

    public RandomTeleportCommand(String pluginPrefix, int maxRange) {
        this.pluginPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(pluginPrefix);
        this.maxRange = Math.max(1, maxRange);
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage()
                    .deserialize("<gray>This command can only be used by players.")));
            return;
        }

        if (!sender.hasPermission("beaconlabs.core.rtp")) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage()
                    .deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        Location randomLocation = getRandomSafeLocation(player.getWorld(), maxRange);
        if (randomLocation == null) {
            player.sendMessage(pluginPrefix.append(MiniMessage.miniMessage()
                    .deserialize("<gray>Failed to find a safe location to teleport.")));
            return;
        }

        player.teleport(randomLocation);
        player.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize(
                "<gray>Teleported to a random location: X: " + randomLocation.getBlockX()
                        + " Y: " + randomLocation.getBlockY()
                        + " Z: " + randomLocation.getBlockZ())));
        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.rtp");
    }

    private Location getRandomSafeLocation(World world, int range) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int attempt = 0; attempt < 10; attempt++) {
            int x = random.nextInt(-range, range + 1);
            int z = random.nextInt(-range, range + 1);
            int y = findSafeY(world, x, z);
            if (y != -1) {
                return new Location(world, x + 0.5, y, z + 0.5);
            }
        }
        return null;
    }

    /** Checks the terrain surface and a small underground window instead of every Y level. */
    private int findSafeY(World world, int x, int z) {
        int highest = Math.min(world.getMaxHeight() - 2, world.getHighestBlockYAt(x, z));
        int lowest = Math.max(world.getMinHeight() + 1, highest - 16);
        for (int y = highest; y >= lowest; y--) {
            Block feet = world.getBlockAt(x, y, z);
            Block head = world.getBlockAt(x, y + 1, z);
            Block ground = world.getBlockAt(x, y - 1, z);
            if (isSafeLocation(feet, head, ground)) {
                return y;
            }
        }
        return -1;
    }

    private boolean isSafeLocation(Block feet, Block head, Block ground) {
        Material groundType = ground.getType();
        return feet.isPassable()
                && head.isPassable()
                && groundType.isSolid()
                && !ground.isLiquid()
                && groundType != Material.MAGMA_BLOCK
                && groundType != Material.CACTUS
                && groundType != Material.CAMPFIRE
                && groundType != Material.SOUL_CAMPFIRE
                && groundType != Material.POWDER_SNOW;
    }
}
