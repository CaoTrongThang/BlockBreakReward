package com.blockbreakreward;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {
    Plugin plugin;

    public Commands(Plugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("blockbreakreward").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player;

        if (args.length == 0) {
            if (sender instanceof Player) {
                player = (Player) sender;
                Convenient.PrintStringList(player, plugin.getConfig().getStringList("HelpMessage"), ChatColor.YELLOW);

            } else {
                Convenient.PrintStringListToConsole(plugin.getConfig().getStringList("HelpMessage"), ChatColor.YELLOW);

            }

            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!Convenient.RewardsToList()) {
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
            File file = new File(Plugin.configPath);
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            try {
                yaml.save(file);
            } catch (IOException exception) {
                Plugin.LOGGER.info(ChatColor.RED + "Can't save config.yml");
            }
            plugin.reloadConfig();

        } else if (args[0].equalsIgnoreCase("help")) {
            if (sender instanceof Player) {
                player = (Player) sender;
                Convenient.PrintStringList(player, plugin.getConfig().getStringList("HelpMessage"), ChatColor.YELLOW);

            } else {
                Convenient.PrintStringListToConsole(plugin.getConfig().getStringList("HelpMessage"), ChatColor.YELLOW);
            }
        }
        return false;
    }

}
