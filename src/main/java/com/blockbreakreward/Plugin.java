package com.blockbreakreward;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.blockbreakreward.PlayerHandler.PlayerProcessor;
import com.blockbreakreward.PlayerHandler.PlayerTemplate;
import com.blockbreakreward.RewardHandler.RewardProcessor;
import com.blockbreakreward.RewardHandler.RewardTemplate;
import com.blockbreakreward.handler.BlockBreakEventHandler;
import com.blockbreakreward.handler.CommandHandler;
import com.blockbreakreward.handler.OnJoinHandler;
import com.blockbreakreward.handler.OnQuitHandler;
import com.blockbreakreward.handler.TabCompletion;

import net.md_5.bungee.api.ChatColor;

/*
 * blockbreakreward java plugin
 */
public class Plugin extends JavaPlugin {
  public static final Logger LOGGER = Logger.getLogger("BlockBreakReward");

  public List<RewardTemplate> rewards;
  public List<PlayerTemplate> players;
  public static String playerDataPath = "plugins/blockbreakreward/PlayerData";
  public static File[] playerDataFileList;
  public static Plugin plugin;

  @Override
  public void onEnable() {
    PluginDescriptionFile pdf = this.getDescription();
    plugin = this;
    rewards = new ArrayList<>();
    players = new ArrayList<>();
    saveDefaultConfig();
    playerDataFileList = new File("plugins/blockbreakreward/PlayerData").listFiles();
    CreateFolder("PlayerData");
    if (!RewardProcessor.SaveRewardsList(this)) {
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }
    if (!PlayerProcessor.SavePlayerDataToList()) {
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }
    new CommandHandler(this);
    LOGGER.info(ChatColor.WHITE + "Commands are registered");
    new BlockBreakEventHandler(this);
    LOGGER.info(ChatColor.WHITE + "BlockBreakEvent is registered");
    new OnQuitHandler(this);
    LOGGER.info(ChatColor.WHITE + "Player Quit Event is registered");
    new OnJoinHandler(this);
    LOGGER.info(ChatColor.WHITE + "Player Join Event is registered");
    new TabCompletion(this);
    LOGGER.info(ChatColor.WHITE + "Tab Completion is registered");

    LOGGER.info(ChatColor.YELLOW + "Block Break Reward is on");

    LOGGER.info(ChatColor.WHITE + "+-----------------------------+");
    LOGGER.info(ChatColor.WHITE + "  BlockBreakReward ");
    LOGGER.info(ChatColor.WHITE + "       Version v" + ChatColor.RED + "" + pdf.getVersion());
    LOGGER.info(ChatColor.WHITE + " Plugin by " + ChatColor.GREEN + "" + pdf.getAuthors());
    LOGGER.info(ChatColor.WHITE + "+-----------------------------+");
  }

  @Override
  public void onDisable() {
    try {
      for (PlayerTemplate p : players) {
        if (p.p != null) {
          PlayerProcessor.CreatePlayerFileAndSetValue(p);
        }
      }
    } catch (Exception exception) {
      LOGGER.info(ChatColor.RED + "All players data can't be saved");
    }

    LOGGER.info(ChatColor.RED + "Block Break Reward is off");
  }

  public void CreateFolder(String path) {
    String[] folders = path.split("/");
    String newPath = "";
    for (int x = 0; x < folders.length; x++) {
      newPath += "/" + folders[x];
      File file = new File("plugins/blockbreakreward" + newPath);
      if (!file.exists()) {
        file.mkdir();
      }
    }
  }
}
