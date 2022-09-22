package com.blockbreakreward;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import com.blockbreakreward.PlayerHandler.PlayerTemplate;
import com.blockbreakreward.RewardHandler.RewardTemplate;

import net.md_5.bungee.api.ChatColor;

public class MyFunc {
    public static String RemoveFileNameExtension(String fileName) {
        int pos = fileName.lastIndexOf('.');
        if (pos > 0 && pos < fileName.length() - 1) {
            return fileName.substring(0, pos);
        }
        return fileName;
    }

    public static boolean PlayerHasRewardPermission(Player p) {
        for (RewardTemplate rw : Plugin.plugin.rewards) {
            if (p.hasPermission(rw.permission)) {
                return true;
            }
        }
        return false;
    }

    public static RewardTemplate WhatRewardPermissonTheyHave(Player p) {
        List<RewardTemplate> rwtp = new ArrayList<>();
        for (RewardTemplate rw : Plugin.plugin.rewards) {
            if (p.hasPermission(rw.permission)) {
                rwtp.add(rw);
            }
        }
        RewardTemplate rwtpSmallest = null;
        if (rwtp.size() == 1) {
            return rwtp.get(0);
        } else if (rwtp.size() > 1) {
            for (int x = 0; x < rwtp.size(); x++) {
                for (int y = x + 1; y < rwtp.size(); y++) {
                    if (rwtp.get(x).blockNeedToMine <= rwtp.get(y).blockNeedToMine) {
                        rwtpSmallest = rwtp.get(x);
                    } else {
                        rwtpSmallest = rwtp.get(y);
                    }
                }
            }
            return rwtpSmallest;
        }
        return rwtpSmallest;
    }

    public static String RemoveForwardSlash(String str) {
        if (str.contains("/")) {
            int index = str.lastIndexOf("/");
            return str.substring(index, str.length());
        }
        return str;
    }

    public static void PrintListTo(CommandSender sender, List<String> msg, boolean isPlayer) {
        if (isPlayer) {
            Player p = (Player) sender;
            for (String str : msg) {
                p.sendMessage(ChatColor.YELLOW + str);
            }
        } else {
            for (String str : msg) {
                Plugin.plugin.LOGGER.info(ChatColor.YELLOW + str);
            }
        }
    }

    public static void PrintStringTo(CommandSender sender, String msg, boolean isPlayer) {
        if (isPlayer) {
            Player p = (Player) sender;
            p.sendMessage(ChatColor.YELLOW + msg);

        } else {
            Plugin.plugin.LOGGER.info(ChatColor.YELLOW + msg);
        }
    }

    public static void SetDefaultConfigValue() {
        File file = new File("plugins/blockbreakreward/config.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<String> configValue = new ArrayList<>();
        configValue.add("MySQL.enableMySQL");
        configValue.add("MySQL.username");
        configValue.add("MySQL.password");
        configValue.add("MySQL.port");
        configValue.add("MySQL.databaseName");

        configValue.add("HelpMessage");
        configValue.add("ReloadMessage");
        configValue.add("NeedPermission");
        configValue.add("InventoryFullWarning");
        configValue.add("EnableFullInventoryWarning");
        configValue.add("BlockToInventory");

        configValue.add("ProgressActionBar");
        configValue.add("ActionBarTemplate");
        configValue.add("ProgressReachSound");

        configValue.add("SavePlayerDataAfter");
        configValue.add("SpecificTools");
        configValue.add("ExceptBlocks");

        configValue.add("Rewards");
        List<String> defaultHelpMessage = new ArrayList<>();
        defaultHelpMessage.add("/blockbreakreward reload | to reload the plugin");

        List<String> defaultSpecificTools = new ArrayList<>();
        defaultSpecificTools.add("all");
        List<String> defaultExceptBlocks = new ArrayList<>();
        defaultExceptBlocks.add("none");
        for (String str : configValue) {
            if (!yaml.contains(str)) {
                switch (str) {
                    case "MySQL.enableMySQL":
                        yaml.set("MySQL.enableMySQL", false);
                    case "MySQL.username":
                        yaml.set("MySQL.username", "root");
                    case "MySQL.password":
                        yaml.set("MySQL.password", "");
                    case "MySQL.port":
                        yaml.set("MySQL.port", 3306);
                    case "MySQL.databaseName":
                        yaml.set("MySQL.databaseName", "");
                    case "HelpMessage":
                        yaml.set("HelpMessage", defaultHelpMessage);
                    case "ReloadMessage":
                        yaml.set("ReloadMessage", "Block Break Reward is reloaded, have fun!");
                    case "NeedPermission":
                        yaml.set("NeedPermission", "You don't have permission to use this command");
                    case "InventoryFullWarning":
                        yaml.set("InventoryFullWarning", "[BlockBreakEvent] Your inventory is full");
                    case "EnableFullInventoryWarning":
                        yaml.set("EnableFullInventoryWarning", false);
                    case "ProgressIncreaseSound":
                        yaml.set("ProgressReachSound", "none");
                    case "ActionBarTemplate":
                        yaml.set("ActionBarTemplate",
                                "%block_just_mined% | TOTAL: %mined_blocks% | PROGRESS: %progression_state%%");
                    case "ProgressActionBar":
                        yaml.set("ProgressActionBar", true);
                    case "SavePlayerDataAfter":
                        yaml.set("SavePlayerDataAfter", 0);
                    case "BlockToInventory":
                        yaml.set("BlockToInventory", false);
                    case "SpecificTools":
                        yaml.set("SpecificTools", defaultSpecificTools);
                    case "ExceptBlocks":
                        yaml.set("ExceptBlocks", defaultExceptBlocks);
                }
            }
        }
        RewardsInitialize(yaml, file);
        if (yaml.getStringList("SpecificTools").size() == 0) {
            yaml.set("SpecificTools", defaultSpecificTools);
        } else if (yaml.getStringList("ExceptBlocks").size() == 0) {
            yaml.set("ExceptBlocks", defaultExceptBlocks);
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            Plugin.LOGGER.info(ChatColor.RED + "New config.yml element can't be saved");
        }
    }

    public static void RewardsInitialize(YamlConfiguration yaml, File file) {
        List<String> cmds = new ArrayList<>();
        cmds.add("eco give %player_name% 500");
        cmds.add("give %player% iron_ingot");
        if (!yaml.contains("Rewards")) {
            yaml.set("Rewards.Reward1.permission", "blockbreakreward.default");
            yaml.set("Rewards.Reward1.blockNeedToMined", 1000);
            yaml.set("Rewards.Reward1.randomCommand", true);
            yaml.set("Rewards.Reward1.commands", cmds);
        }
        if (Plugin.plugin.getConfig().getConfigurationSection("Rewards").getKeys(false) == null) {
            if (!yaml.contains("Rewards")) {
                yaml.set("Rewards.Reward1.permission", "blockbreakreward.default");
                yaml.set("Rewards.Reward1.blockNeedToMined", 1000);
                yaml.set("Rewards.Reward1.randomCommand", true);
                yaml.set("Rewards.Reward1.commands", cmds);
            }
        }

        try {
            yaml.save(file);
        } catch (IOException e) {
            Plugin.LOGGER.info(ChatColor.RED + "New config.yml element can't be saved");
        }
    }

    public static String ReplacePlaceHolder(BlockBreakEvent e, String cmd, PlayerTemplate pt) {

        return cmd.replace("%player_name%", e.getPlayer().getName())
                .replace("%mined_blocks%", Integer.toString(pt.minedBlocks))
                .replace("%mined_diamonds%", Integer.toString(pt.minedDiamonds))
                .replace("%mined_emeralds%", Integer.toString(pt.minedEmeralds))
                .replace("%mined_golds%", Integer.toString(pt.minedGolds))
                .replace("%mined_irons%", Integer.toString(pt.minedIrons))
                .replace("%mined_coals%", Integer.toString(pt.minedCoals))
                .replace("%mined_after_join%", Integer.toString(pt.minedAfterJoin))
                .replace("%progression_state%", pt.progresstionState.toString())
                .replace("%block_just_mined%", e.getBlock().getType().toString().replace("_", " "));
    }

    public static List<String> ReplacePlaceHolder(List<String> cmds, PlayerTemplate pt) {
        List<String> newCMD = new ArrayList<>();
        for (String cmd : cmds) {
            newCMD.add(cmd.replace("%player_name%", pt.playerName)
                    .replace("%mined_blocks%", Integer.toString(pt.minedBlocks))
                    .replace("%mined_diamonds%", Integer.toString(pt.minedDiamonds))
                    .replace("%mined_emeralds%", Integer.toString(pt.minedEmeralds))
                    .replace("%mined_golds%", Integer.toString(pt.minedGolds))
                    .replace("%mined_irons%", Integer.toString(pt.minedIrons))
                    .replace("%mined_coals%", Integer.toString(pt.minedCoals))
                    .replace("%mined_after_join%", Integer.toString(pt.minedAfterJoin))
                    .replace("%progression_state%", pt.progresstionState.toString()));
        }
        return newCMD;
    }

    public static String ReplacePlaceHolder(String cmd, PlayerTemplate pt) {

        return cmd.replace("%player_name%", pt.playerName)
                .replace("%mined_blocks%", Integer.toString(pt.minedBlocks))
                .replace("%mined_diamonds%", Integer.toString(pt.minedDiamonds))
                .replace("%mined_emeralds%", Integer.toString(pt.minedEmeralds))
                .replace("%mined_golds%", Integer.toString(pt.minedGolds))
                .replace("%mined_irons%", Integer.toString(pt.minedIrons))
                .replace("%mined_coals%", Integer.toString(pt.minedCoals))
                .replace("%mined_after_join%", Integer.toString(pt.minedAfterJoin))
                .replace("%progression_state%", pt.progresstionState.toString());
    }
}
