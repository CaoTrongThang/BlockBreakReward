package com.blockbreakreward.GUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.blockbreakreward.MyFunc;
import com.blockbreakreward.Plugin;
import com.blockbreakreward.PlayerHandler.PlayerTemplate;

public class LeaderboardManager {
    public static Inventory inv;
    public static Material mat;
    public static List<GUIProperties> guiProperties;

    public static void updateLeaderboardTop() {
        if (Plugin.players.size() > 1) {
            for (int x = 0; x < Plugin.players.size(); x++) {
                for (int y = x + 1; y < Plugin.players.size(); y++) {
                    if (Plugin.players.get(x).minedBlocks <= Plugin.players.get(y).minedBlocks) {
                        PlayerTemplate intermidiate = Plugin.players.get(x);
                        Plugin.players.set(x, Plugin.players.get(y));
                        Plugin.players.set(y, intermidiate);
                    }
                }
            }
        }
    }

    public static void reloadGUI() {
        updateLeaderboardTop();
        guiProperties = new ArrayList<>();
        File file = new File(Plugin.leaderboardFilePath);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        mat = Material.valueOf(yaml.getString("FillBackgroundWith"));

        Set<String> keys = yaml.getConfigurationSection("LeaderboardGUI").getKeys(false);

        for (String key : keys) {
            if (yaml.contains("LeaderboardGUI." + key + ".top")) {
                Integer top = yaml.getInt("LeaderboardGUI." + key + ".top") - 1;

                ItemStack item;
                if (yaml.getString("LeaderboardGUI." + key + ".material").equalsIgnoreCase("PLAYER_HEAD")) {
                    item = new ItemStack(Material.PLAYER_HEAD);
                } else {
                    item = new ItemStack(Material.valueOf(yaml.getString("LeaderboardGUI." + key + ".material")));
                }
                ItemMeta itemMT = item.getItemMeta();
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                if (top > Plugin.players.size()) {
                    Plugin.LOGGER.info("Leaderboard.yml wrong setting: " + key + ": " + top + " is to high");
                    return;
                }

                meta.setOwningPlayer(
                        Bukkit.getOfflinePlayer(UUID.fromString(Plugin.players.get(top).playerUUID)));

                itemMT.setDisplayName(MyFunc.ReplacePlaceHolder(yaml.getString("LeaderboardGUI." + key + ".name"),
                        Plugin.players.get(top)));
                itemMT.setLore(MyFunc.ReplacePlaceHolder(yaml.getStringList("LeaderboardGUI." + key + ".lores"),
                        Plugin.players.get(top)));
                item.setItemMeta(itemMT);

                guiProperties.add(new GUIProperties(item, yaml.getInt("LeaderboardGUI." + key + ".position")));
            }
        }

    }

    public static void openLeaderboardGUI(Player p) {
        inv = Bukkit.createInventory(null, 54, LeaderboardTemplate.leaderboardTitle);
        ItemStack fill = new ItemStack(Material.valueOf(LeaderboardTemplate.fillBackgroundWith));
        for (int x = 0; x < inv.getSize(); x++) {
            inv.setItem(x, fill);
            for (GUIProperties gui : guiProperties) {
                if (x == gui.pos - 1) {
                    inv.setItem(x, gui.item);
                    break;
                }
            }
        }
        p.openInventory(inv);
    }
}
