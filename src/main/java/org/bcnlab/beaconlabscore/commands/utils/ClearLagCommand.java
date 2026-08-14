package org.bcnlab.beaconlabscore.commands.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WitherSkull;

/** Removes disposable entities while preserving players and map decorations. */
public class ClearLagCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public ClearLagCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!sender.hasPermission("beaconlabs.core.clearlag")) {
            sender.sendMessage(plugin.getPrefix(sender).append(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getNoPermsMessage())));
            return;
        }

        int animalsRemoved = 0;
        int monstersRemoved = 0;
        int minecartsRemoved = 0;
        int droppedItemsRemoved = 0;
        int experienceOrbsRemoved = 0;

        for (org.bukkit.World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item) {
                    entity.remove();
                    droppedItemsRemoved++;
                } else if (entity instanceof Animals && !(entity instanceof Ambient)) {
                    entity.remove();
                    animalsRemoved++;
                } else if (entity instanceof Monster) {
                    entity.remove();
                    monstersRemoved++;
                } else if (entity instanceof Minecart) {
                    entity.remove();
                    minecartsRemoved++;
                } else if (entity instanceof ExperienceOrb) {
                    entity.remove();
                    experienceOrbsRemoved++;
                } else if (isDisposableEffect(entity)) {
                    entity.remove();
                }
            }
        }

        String message = String.format(
                "<gray>Entities removed:\n"
                        + "<gray>Animals: <gold>%d\n"
                        + "<gray>Monsters: <gold>%d\n"
                        + "<gray>Dropped Items: <gold>%d\n"
                        + "<gray>Experience Orbs: <gold>%d\n"
                        + "<gray>Minecarts: <gold>%d",
                animalsRemoved, monstersRemoved, droppedItemsRemoved,
                experienceOrbsRemoved, minecartsRemoved);
        sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize(message)));
        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.clearlag");
    }

    private boolean isDisposableEffect(Entity entity) {
        return entity instanceof Projectile
                || entity instanceof TNTPrimed
                || entity instanceof FallingBlock
                || entity instanceof ArmorStand
                || entity instanceof Boat
                || entity instanceof EnderCrystal
                || entity instanceof EnderSignal
                || entity instanceof EnderPearl
                || entity instanceof Firework
                || entity instanceof Fireball
                || entity instanceof Snowball
                || entity instanceof ThrownPotion
                || entity instanceof WitherSkull;
    }
}
