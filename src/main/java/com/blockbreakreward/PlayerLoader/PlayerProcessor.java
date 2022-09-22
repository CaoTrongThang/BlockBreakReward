package com.blockbreakreward.PlayerHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.blockbreakreward.MyFunc;
import com.blockbreakreward.Plugin;
import com.blockbreakreward.ConfigHandler.ConfigHandler;

import net.md_5.bungee.api.ChatColor;

public class PlayerProcessor {

    public static boolean SavePlayerDataToList() {
        Plugin.plugin.players = new ArrayList<>();
        // Return if playerData is null on server enable
        if (Plugin.plugin.playerDataFileList == null) {
            Plugin.LOGGER.info(ChatColor.YELLOW + "No data in PlayerData to be saved");
            return true;
        }
        // Add player.yml to a List so we can use RAM to perform
        for (File file : Plugin.playerDataFileList) {
            // CHECK IF FILE NAME IS A REAL UUID OR NOT
            try {
                Bukkit.getPlayer(UUID.fromString(MyFunc.RemoveFileNameExtension(file.getName())));
            } catch (Exception e) {
                Plugin.LOGGER.info(ChatColor.RED + file.getName()
                        + " is not a legal player ID, please remove it in PlayerData folder...");
                return false;
            }

            // Set Value for players list if they offline nor online
            Player p;
            UUID id = UUID.fromString(MyFunc.RemoveFileNameExtension(file.getName()));
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

            if (Bukkit.getOfflinePlayer(id).isOnline()) {
                p = Bukkit.getPlayer(id);
            } else {
                p = null;
            }

            AddToList(yaml, file, p);
        }
        return true;

    }

    public static void AddToList(YamlConfiguration yaml, File file, Player p) {
        Plugin.plugin.players.add(
                new PlayerTemplate(0, p, MyFunc.RemoveFileNameExtension(file.getName()), yaml.getString("playerName"),
                        yaml.getInt("minedBlocks"), yaml.getInt("minedDiamonds"),
                        yaml.getInt("minedEmeralds"), yaml.getInt("minedGolds"), yaml.getInt("minedIrons"),
                        yaml.getInt("minedCoals")));

    }

    public static List<String> DefaultPlayerValueList() {
        List<String> requireStrings = new ArrayList<>();
        requireStrings.add("playerName");
        requireStrings.add("minedBlocks");
        requireStrings.add("minedDiamonds");
        requireStrings.add("minedEmeralds");
        requireStrings.add("minedGolds");
        requireStrings.add("minedIrons");
        requireStrings.add("minedCoals");
        return requireStrings;
    }

    public static boolean SetMissedValue(File file, YamlConfiguration yaml, Player p) {
        List<String> notContaintString = new ArrayList<>();
        for (String str : DefaultPlayerValueList()) {
            if (!yaml.contains(str)) {
                notContaintString.add(str);
            }
        }
        if (notContaintString.size() > 0) {
            for (String str : notContaintString) {
                if (str.equals("playerName")) {
                    yaml.set("playerName", p.getName());
                } else {
                    yaml.set(str, 0);
                }
            }
            try {
                yaml.save(file);
            } catch (IOException e) {
                Plugin.LOGGER.info(ChatColor.RED + "Can't save " + file.getName());
            }
            return true;
        }
        return false;
    }

    public static boolean CreatePlayerFileAndSetValue(PlayerTemplate pt, int ptPos) {
        File newPlayerFile = new File("plugins/blockbreakreward/PlayerData/" + pt.p.getUniqueId().toString() + ".yml");
        try {
            newPlayerFile.createNewFile();
        } catch (IOException e) {
            Plugin.LOGGER.info(ChatColor.RED + newPlayerFile.getName() + " Can't be created");
            return false;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(newPlayerFile);

        yaml.set("playerName", pt.p.getName());
        yaml.set("minedBlocks", pt.minedAfterJoin + pt.minedBlocks);
        yaml.set("minedDiamonds", pt.minedDiamonds);
        yaml.set("minedEmeralds", pt.minedEmeralds);
        yaml.set("minedGolds", pt.minedGolds);
        yaml.set("minedIrons", pt.minedIrons);
        yaml.set("minedCoals", pt.minedCoals);
        Plugin.plugin.players.get(ptPos).minedAfterJoin = 0;
        try {
            yaml.save(newPlayerFile);
        } catch (IOException df) {
            Plugin.LOGGER.info(ChatColor.RED + "Can't save " + newPlayerFile.getName());
        }
        return false;
    }
}
