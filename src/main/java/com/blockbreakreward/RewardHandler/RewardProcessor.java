package com.blockbreakreward.RewardHandler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.permissions.Permission;

import com.blockbreakreward.Plugin;

import net.md_5.bungee.api.ChatColor;

public class RewardProcessor {

    public static boolean SaveRewardsList(Plugin plugin) {
        plugin.rewards.clear();
        List<String> keys = new ArrayList<>(plugin.getConfig().getConfigurationSection("Rewards").getKeys(false));
        if (keys.size() > 0) {
            for (String key : keys) {
                String permission = plugin.getConfig().getString("Rewards." + key + ".permission");
                int blockNeedToMine = plugin.getConfig().getInt("Rewards." + key + ".blockNeedToMine");
                List<String> commands = plugin.getConfig().getStringList("Rewards." + key + ".commands");
                boolean randomCommand = plugin.getConfig().getBoolean("Rewards." + key + ".randomCommand");

                plugin.rewards.add(new RewardTemplate(permission, blockNeedToMine, commands, randomCommand));
            }
            for (int x = 0; x < plugin.rewards.size(); x++) {
                for (int i = x + 1; i < plugin.rewards.size(); i++) {
                    if (plugin.rewards.get(x).permission.equalsIgnoreCase(plugin.rewards.get(i).permission)) {
                        plugin.LOGGER.info(
                                ChatColor.RED + "Permission: " + plugin.rewards.get(x).permission + " is duplicated!");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}