package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

public class ClearLagCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public ClearLagCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("beaconlabs.core.clearlag")) {
            int itemsRemoved = 0;
            int animalsRemoved = 0;
            int monstersRemoved = 0;
            int minecartsRemoved = 0;
            int droppedItemsRemoved = 0;
            int experienceOrbsRemoved = 0;

            for (org.bukkit.World world : Bukkit.getWorlds()) { // Iterate over all worlds
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof Item && !(entity instanceof ItemFrame)) {
                        entity.remove();
                        itemsRemoved++;
                    } else if (entity instanceof Animals && !(entity instanceof Ambient)) {
                        entity.remove();
                        animalsRemoved++;
                    } else if (entity instanceof Monster) {
                        entity.remove();
                        monstersRemoved++;
                    } else if (entity instanceof Minecart) {
                        entity.remove();
                        minecartsRemoved++;
                    } else if (entity instanceof ItemFrame) {
                        // Do not remove item frames
                        continue;
                    } else if (entity instanceof Hanging) {
                        // Do not remove other hanging entities (like paintings)
                        continue;
                    } else if (entity instanceof ExperienceOrb) {
                        entity.remove();
                        experienceOrbsRemoved++;
                    } else if (entity instanceof Projectile || entity instanceof TNTPrimed ||
                            entity instanceof FallingBlock || entity instanceof ArmorStand || entity instanceof Boat ||
                            entity instanceof EnderCrystal || entity instanceof EnderSignal ||
                            entity instanceof EnderPearl || entity instanceof Firework ||
                            entity instanceof Fireball ||
                            entity instanceof Painting || entity instanceof Snowball || entity instanceof ThrownPotion || entity instanceof WitherSkull) {
                        entity.remove();
                    } else if (entity instanceof ExperienceOrb) {
                        entity.remove();
                        experienceOrbsRemoved++;
                    } else if (entity instanceof Item) {
                        entity.remove();
                        droppedItemsRemoved++;
                    }
                }
            }

            String message = String.format(
                    "%s§aEntities removed:\n" +
                            "§bItems: §e%d\n" +
                            "§bAnimals: §e%d\n" +
                            "§bMonsters: §e%d\n" +
                            "§bDropped Items: §e%d\n" +
                            "§bExperience Orbs: §e%d\n" +
                            "§bMinecarts: §e%d",
                    plugin.getPrefix(), itemsRemoved, animalsRemoved, monstersRemoved,
                    droppedItemsRemoved, experienceOrbsRemoved, minecartsRemoved
            );
            sender.sendMessage(message);
            return true; // Command executed successfully
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getNoPermsMessage());
            return true; // Command executed, but no permission
        }
    }
}
