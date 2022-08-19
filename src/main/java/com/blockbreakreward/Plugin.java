package com.blockbreakreward;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.blockbreakreward.handler.OnBlockBreak;

import net.md_5.bungee.api.ChatColor;

/*
 * blockbreakreward java plugin
 */
public class Plugin extends JavaPlugin {
  public static final Logger LOGGER = Logger.getLogger("BlockBreakReward");
  public static Reward[] rewardType;
  public static FileConfiguration config;
  public static ArrayList<Reward> reward;

  public static String configPath = "/plugins/blockbreakreward/config.yml";

  // ! MAKE LEADERBOARD
  @Override
  public void onEnable() {
    LOGGER.info(ChatColor.YELLOW + "Block Break Reward is on");
    new OnBlockBreak(this);
    Plugin.LOGGER.info("Block Break Event is registered");
    new Commands(this);
    Plugin.LOGGER.info("Commands are registered");
    new TabCompletion(this);
    Plugin.LOGGER.info("Tab Completion is registered");
    Plugin.LOGGER.info("+-----------------+");
    Plugin.LOGGER.info("Plugin By TrongThang");
    Plugin.LOGGER.info("+-----------------+");
    config = getConfig();
    reward = new ArrayList<>();
    saveDefaultConfig();

    // SAVE CONFIG REWARDS TO LIST OF OBJECT
    if (!Convenient.RewardsToList()) {
      Bukkit.getPluginManager().disablePlugin(this);
    }
    Plugin.LOGGER.info(ChatColor.GREEN + "All Rewards has been save!");
    CreateFolder("playersData");

  }

  @Override
  public void onDisable() {
    LOGGER.info(ChatColor.RED + "Block Break Reward is off");
  }

  public void CreateFolder(String playersData) {
    File file = new File(getDataFolder(), playersData);
    if (!file.exists()) {
      file.mkdir();
    }
  }
}
