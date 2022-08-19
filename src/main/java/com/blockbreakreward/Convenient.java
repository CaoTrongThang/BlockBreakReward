package com.blockbreakreward;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import net.md_5.bungee.api.ChatColor;

public class Convenient {
    private static final Logger LOGGER = Logger.getLogger("blockbreakreward");

    public static String GetFileNameWithoutExtension(String fileName) {
        int indexOfExtension = fileName.lastIndexOf(".");

        return fileName.substring(0, indexOfExtension);
    }

    // Create new playerData.yml with default value
    public static void CreateNewPlayerFile(BlockBreakEvent e) {

        File newFile = new File(
                "plugins/blockbreakreward/playersData/" + e.getPlayer().getUniqueId().toString() + ".yml");
        try {
            newFile.createNewFile();
        } catch (IOException exception) {

            Plugin.LOGGER.info("Can't create new file");
        }
        YamlConfiguration yaml = new YamlConfiguration();
        yaml = YamlConfiguration.loadConfiguration(newFile);

        yaml.set("playerName", e.getPlayer().getName());
        yaml.set("minedBlocks", 1);
        yaml.set("timestamp", 0);

        try {
            yaml.save(newFile);
        } catch (IOException exception) {
            Plugin.LOGGER.info("Can't save file");
        }
    }

    // Add Block when player mine a block
    public static void AddBlockToPlayerData(BlockBreakEvent e) {
        File newFile = new File(
                "plugins/blockbreakreward/playersData/" + e.getPlayer().getUniqueId().toString() + ".yml");

        YamlConfiguration yaml = new YamlConfiguration();
        yaml = YamlConfiguration.loadConfiguration(newFile);
        if (yaml.getString("playerName").equals(e.getPlayer().getName())) {
            yaml.set("playerName", e.getPlayer().getName());
        }
        int minedblock = yaml.getInt("minedBlocks") + 1;
        yaml.set("minedBlocks", minedblock);
        try {
            yaml.save(newFile);
        } catch (IOException exception) {
            Plugin.LOGGER.info("Can't save file");
        }
    }

    // Check if player reach the require block to get reward (with permission)
    public static void CheckIfReachedReward(BlockBreakEvent e, String playerDataPath) {
        File file = new File(playerDataPath);
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (Reward rw : Plugin.reward) {
            if (e.getPlayer().hasPermission(rw.permission)) {
                if (yaml.getInt("minedBlocks") == yaml.getInt("timestamp") + rw.blockNeedToMine) {
                    if (rw.commands.size() > 0) {
                        if (rw.randomCommands) {
                            double rand = Math.round((Math.random() * rw.commands.size()));
                            if (rw.commands.get((int) rand).indexOf("/") == 0) {
                                String substr = rw.commands.get((int) rand).substring(0,
                                        rw.commands.get((int) rand).length() - 1);
                                Bukkit.dispatchCommand(console, ReplacePlaceHolder(substr, yaml));
                                yaml.set("timestamp", yaml.getInt("minedBlock"));
                                return;
                            }
                            Bukkit.dispatchCommand(console, rw.commands.get((int) rand));
                            yaml.set("timestamp", yaml.getInt("minedBlock"));
                            return;
                        }
                        for (int x = 0; x < rw.commands.size(); x++) {
                            if (rw.commands.get(x).indexOf("/") == 0) {
                                String substr = rw.commands.get(x).substring(0,
                                        rw.commands.get(x).length() - 1);
                                Bukkit.dispatchCommand(console, ReplacePlaceHolder(substr, yaml));
                                yaml.set("timestamp", yaml.getInt("minedBlock"));
                                return;
                            }
                            Bukkit.dispatchCommand(console, ReplacePlaceHolder(rw.commands.get(x), yaml));
                            yaml.set("timestamp", yaml.getInt("minedBlock"));
                            return;
                        }
                    }
                }
            }
        }

    }

    public static boolean RewardsToList() {
        Plugin.reward.clear();

        for (String key : Plugin.config.getConfigurationSection("Rewards").getKeys(false)) {
            String permission = Plugin.config.getString("Rewards." + key + ".permission");
            int blockNeedToMine = Plugin.config.getInt("Rewards." + key + ".blockNeedToMine");
            if (blockNeedToMine <= 0) {
                Convenient.PrintStringToConsole(key + "'s" + "blockNeedToMine" + " need to better than 0",
                        ChatColor.RED);
                return false;
            }
            List<String> commands = Plugin.config.getStringList("Rewards." + key + ".commands");
            boolean randomCommands = Plugin.config.getBoolean("Rewards." + key + ".randomCommands");

            Plugin.reward.add(new Reward(permission, blockNeedToMine, commands, randomCommands));
            for (int x = 1; x < Plugin.reward.size(); x++) {
                if (CheckDuplicateString(Plugin.reward.get(0).permission, Plugin.reward.get(x).permission)) {
                    Convenient.PrintStringToConsole(
                            key + "'s permission is duplicated " + Plugin.reward.get(0).permission,
                            ChatColor.RED);
                    return false;
                } else if (CheckDuplicateString(Plugin.reward.get(0).permission, "blockbreakplugin.reload")) {
                    Convenient.PrintStringToConsole(
                            key + "'s permission is duplicated " + "blockbreakplugin.reload",
                            ChatColor.RED);
                    return false;
                } else if (CheckDuplicateString(Plugin.reward.get(0).permission, "blockbreakplugin.help")) {
                    Convenient.PrintStringToConsole(
                            key + "'s permission is duplicated " + "blockbreakplugin.help",
                            ChatColor.RED);
                    return false;
                }
            }
        }
        return true;
    }

    public static void PrintString(Player p, String str, ChatColor color) {
        p.sendMessage(color + str);
        LOGGER.info(color + str);
    }

    public static void PrintStringList(Player p, List<String> str, ChatColor color) {
        for (String string : str) {
            p.sendMessage(color + string);
        }
    }

    public static void PrintStringListToConsole(List<String> str, ChatColor color) {
        for (String string : str) {
            LOGGER.info(color + string);
        }
    }

    public static void PrintStringToConsole(String str, ChatColor color) {
        LOGGER.info(color + str);
    }

    public static boolean CheckDuplicateString(String str1, String str2) {
        if (str1.equalsIgnoreCase(str2)) {
            return true;
        } else {
            return false;
        }
    }

    public static String ReplacePlaceHolder(String cmd, YamlConfiguration yaml) {
        if (cmd.contains("{player}") && cmd.contains("{minedBlock}") && cmd.contains("{timestamp}")) {
            cmd.replace("{player}", yaml.getString("playerName"));
            cmd.replace("{minedBlocks}", yaml.getString("minedBlocks"));
            cmd.replace("{timestamp}", yaml.getString("timestamp"));
            return cmd;
        } else if (cmd.contains("{player}") && cmd.contains("{minedBlock}")) {
            cmd.replace("{player}", yaml.getString("playerName"));
            cmd.replace("{minedBlocks}", yaml.getString("minedBlocks"));
            return cmd;
        } else if (cmd.contains("{player}") && cmd.contains("{timestamp}")) {
            cmd.replace("{timestamp}", yaml.getString("timestamp"));
            cmd.replace("{player}", yaml.getString("playerName"));
            return cmd;
        } else if (cmd.contains("{minedBlock}") && cmd.contains("{timestamp}")) {
            cmd.replace("{minedBlocks}", yaml.getString("minedBlocks"));
            cmd.replace("{timestamp}", yaml.getString("timestamp"));
            return cmd;
        } else if (cmd.contains("{minedBlock}")) {
            cmd.replace("{minedBlocks}", yaml.getString("minedBlocks"));
            return cmd;
        } else if (cmd.contains("{timestamp}")) {
            cmd.replace("{timestamp}", yaml.getString("timestamp"));
            return cmd;
        } else if (cmd.contains("{player}")) {
            cmd.replace("{player}", yaml.getString("playerName"));
            return cmd;
        }

        return cmd;
    }
}
