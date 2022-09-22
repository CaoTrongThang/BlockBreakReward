package com.blockbreakreward.handler;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.blockbreakreward.MyFunc;
import com.blockbreakreward.Plugin;
import com.blockbreakreward.ConfigHandler.ConfigHandler;
import com.blockbreakreward.GUI.LeaderboardManager;
import com.blockbreakreward.GUI.LeaderboardTemplate;
import com.blockbreakreward.MySQLConnection.MySQLHandler;
import com.blockbreakreward.PlayerHandler.PlayerProcessor;
import com.blockbreakreward.PlayerHandler.PlayerTemplate;
import com.blockbreakreward.RewardHandler.RewardProcessor;

import net.md_5.bungee.api.ChatColor;

public class CommandHandler implements CommandExecutor {
    Plugin plugin;

    public CommandHandler(Plugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("blockbreakreward").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            MyFunc.PrintListTo(sender, ConfigHandler.GetHelpMessage(), InstanceOfPlayer(sender));
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                MyFunc.PrintListTo(sender, ConfigHandler.GetHelpMessage(), InstanceOfPlayer(sender));
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (p.hasPermission("blockbreakreward.reload")) {
                        plugin.saveDefaultConfig();
                        plugin.reloadConfig();
                        plugin.LOGGER.info("Config.yml reloaded");
                        MyFunc.SetDefaultConfigValue();
                        ConfigHandler.UpdateConfigInstance();
                        RewardProcessor.SaveRewardsList(plugin);

                        LeaderboardManager.reloadGUI();
                        LeaderboardTemplate.updateLeaderboardTemplate();
                        plugin.LOGGER.info("Leaderboard Reloaded");

                        p.sendMessage(ConfigHandler.GetReloadMessage());
                        return true;
                    } else {
                        p.sendMessage(ConfigHandler.GetNeedPermissionMessage());
                    }
                } else {
                    plugin.saveDefaultConfig();
                    plugin.reloadConfig();
                    plugin.LOGGER.info("Config.yml reloaded");
                    MyFunc.SetDefaultConfigValue();
                    RewardProcessor.SaveRewardsList(plugin);
                    ConfigHandler.UpdateConfigInstance();

                    LeaderboardManager.reloadGUI();
                    LeaderboardTemplate.updateLeaderboardTemplate();
                    plugin.LOGGER.info("Leaderboard Reloaded");

                    plugin.LOGGER.info(ConfigHandler.GetReloadMessage());
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("YamltoMySQLDatabase")) {
                if (plugin.mysql.isConnected()) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (p.hasPermission("blockbreakreward.yamltomysqldatabase")) {
                            if (Plugin.playerDataFileList.length == 0 && Plugin.playerDataFileList.equals(null)) {
                                p.sendMessage(ChatColor.YELLOW + "There's no data to be converted to MySQL database");
                                return true;
                            }
                            MySQLHandler.ConvertYAMLToMySQL();
                            return true;
                        } else {
                            p.sendMessage(ConfigHandler.GetNeedPermissionMessage());
                        }
                    } else {
                        if (Plugin.playerDataFileList.length == 0 && Plugin.playerDataFileList.equals(null)) {
                            Plugin.LOGGER.info(ChatColor.YELLOW + "There's no data to be converted to MySQL database");
                            return true;
                        } else if (Plugin.playerDataFileList.length > 0) {
                            MySQLHandler.ConvertYAMLToMySQL();
                        }
                    }
                } else {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (p.hasPermission("blockbreakreward.yamltomysqldatabase")) {
                            p.sendMessage(ChatColor.RED + "MySQL Database is not connected!");
                            return true;
                        } else {
                            p.sendMessage(ConfigHandler.GetNeedPermissionMessage());
                        }
                    } else {
                        if (Plugin.playerDataFileList.length == 0 && Plugin.playerDataFileList.equals(null)) {
                            Plugin.LOGGER.info(ChatColor.RED + "MySQL Database is not connected!");
                            return true;
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("leaderboard")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (Plugin.players.size() == 0) {
                        Plugin.LOGGER.info(ChatColor.YELLOW + "There's no player in leaderboard...");
                        return false;
                    }

                    if (p.hasPermission("blockbreakreward.leaderboard")) {
                        LeaderboardManager.openLeaderboardGUI(p);
                    } else {
                        p.sendMessage(ConfigHandler.GetNeedPermissionMessage());
                    }

                } else {
                    plugin.LOGGER.info(ChatColor.YELLOW + "Leaderboard can't only be viewed by player!");
                    if (Plugin.players.size() == 0) {
                        Plugin.LOGGER.info(ChatColor.YELLOW + "There's no player in leaderboard...");
                        return false;
                    }
                    LeaderboardManager.updateLeaderboardTop();
                    for (int x = 0; x < Plugin.players.size(); x++) {
                        PlayerTemplate pt = Plugin.players.get(x);
                        plugin.LOGGER.info(pt.playerName + " #" + (x + 1) + ": " + pt.minedBlocks + " blocks");
                    }
                }

            }

        }
        return false;
    }

    public boolean InstanceOfPlayer(CommandSender sender) {
        return sender instanceof Player;
    }
}
