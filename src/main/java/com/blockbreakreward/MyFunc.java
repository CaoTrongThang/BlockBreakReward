package com.blockbreakreward;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        return null;
    }

    public static String RemoveForwardSlash(String str) {
        if (str.contains("/")) {
            int index = str.lastIndexOf("/");
            return str.substring(index, str.length());
        }
        return str;
    }

    public static void PrintPlayersList(String sentence) {
        System.out.println("PLAYER LIST " + sentence);
        for (PlayerTemplate p : Plugin.plugin.players) {
            System.out.println("player Enity" + p.p);
            System.out.println("playerName: " + p.playerName);
            System.out.println("playerUUID: " + p.playerUUID);
            System.out.println("minedBlocks: " + p.minedBlocks);
        }
    }

    public static void PrintRewardList(String sentence) {
        System.out.println("REWARD LIST  " + sentence);
        for (RewardTemplate rw : Plugin.plugin.rewards) {
            System.out.println("permisison " + rw.permission);
            System.out.println("blockNeedToMine: " + rw.blockNeedToMine);
            System.out.println("commands: " + rw.commands);
            System.out.println("randomCommand: " + rw.randomCommand);
        }
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
}
