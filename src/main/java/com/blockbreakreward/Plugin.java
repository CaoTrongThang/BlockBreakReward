package com.blockbreakreward;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.blockbreakreward.ConfigHandler.ConfigHandler;
import com.blockbreakreward.ConfigHandler.ConfigTemplate;
import com.blockbreakreward.GUI.LeaderboardManager;
import com.blockbreakreward.GUI.LeaderboardTemplate;
import com.blockbreakreward.MySQLConnection.MySQLConnect;
import com.blockbreakreward.MySQLConnection.MySQLHandler;
import com.blockbreakreward.PlayerHandler.PlayerProcessor;
import com.blockbreakreward.PlayerHandler.PlayerTemplate;
import com.blockbreakreward.RewardHandler.RewardProcessor;
import com.blockbreakreward.RewardHandler.RewardTemplate;
import com.blockbreakreward.handler.BlockBreakEventHandler;
import com.blockbreakreward.handler.CommandHandler;
import com.blockbreakreward.handler.InventoryEventHandler;
import com.blockbreakreward.handler.OnJoinHandler;
import com.blockbreakreward.handler.OnQuitHandler;
import com.blockbreakreward.handler.TabCompletion;

import net.md_5.bungee.api.ChatColor;

public class Plugin extends JavaPlugin {
  public static final Logger LOGGER = Logger.getLogger("BlockBreakReward");

  public static List<RewardTemplate> rewards;
  public static List<PlayerTemplate> players;
  public static String playerDataPath = "plugins/blockbreakreward/PlayerData";
  public static String leaderboardFilePath = "plugins/blockbreakreward/Leaderboard.yml";
  public static File[] playerDataFileList;
  public static Plugin plugin;
  public static ConfigTemplate configTemplate;
  PluginDescriptionFile pdf = this.getDescription();
  public static MySQLConnect mysql;

  @Override
  public void onEnable() {

    saveDefaultConfig();
    CreateFolder("PlayerData");
    LeaderboardTemplate.updateLeaderboardTemplate();

    new MetricsLite(this, 16216);

    plugin = this;

    rewards = new ArrayList<>();
    players = new ArrayList<>();

    MyFunc.SetDefaultConfigValue();

    ConfigHandler.UpdateConfigInstance();
    mysql = new MySQLConnect(this);

    playerDataFileList = new File("plugins/blockbreakreward/PlayerData").listFiles();

    if (!RewardProcessor.SaveRewardsList(this)) {
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }
    new CommandHandler(this);
    LOGGER.info("Commands are registered");
    new BlockBreakEventHandler(this);
    LOGGER.info("Block Break Event is registered");
    new OnQuitHandler(this);
    LOGGER.info("Player Quit Event is registered");
    new OnJoinHandler(this);
    LOGGER.info("Player Join Event is registered");
    new TabCompletion(this);
    LOGGER.info("Tab Completion is registered");
    new InventoryEventHandler(this);
    LOGGER.info("Invetory Event Handler is registered");

    // DATABASE CONNECT
    if (ConfigHandler.GetEnableMySQL()) {
      if ((ConfigHandler.GetMySQLDatabaseName().isEmpty()
          || ConfigHandler.GetMySQLPassword().isEmpty() || ConfigHandler.GetMySQLPort() == null
          || ConfigHandler.GetMySQLUsername().isEmpty()) || ConfigHandler.GetEnableMySQL() == false) {
        LOGGER.info(ChatColor.YELLOW + "You're not using database, will use default settings");
      } else if (!ConfigHandler.GetMySQLDatabaseName().isEmpty()
          && !ConfigHandler.GetMySQLPassword().isEmpty() &&
          ConfigHandler.GetMySQLPort() != null
          && !ConfigHandler.GetMySQLUsername().isEmpty()) {
        mysql.connect();
        if (!mysql.isConnected()) {
          LOGGER.info(ChatColor.RED + "MySQL Database wasn't setup correctly, please check again!");
          getServer().getPluginManager().disablePlugin(this);
          return;
        }
      }
      LOGGER.info(ChatColor.GREEN + "Connect to dabase successfully");
    } else {
      LOGGER.info(ChatColor.YELLOW + "MySQL is set to false, will use default settings");
    }

    if (!mysql.isConnected()) {
      if (!PlayerProcessor.SavePlayerDataToList()) {
        Bukkit.getPluginManager().disablePlugin(this);
        return;
      }
    } else if (mysql.isConnected() && ConfigHandler.GetEnableMySQL()) {
      MySQLHandler.LoadPlayerDataMySQLToList();
    }

    updateLeaderboardSchedule();
    new UpdateChecker(this, 104718).getVersion(version -> {
      if (pdf.getVersion().equals(version)) {
        LOGGER.info(ChatColor.YELLOW + "Block Break Reward is on");

        LOGGER.info(ChatColor.GRAY + "+-----------------------------+");
        LOGGER.info(ChatColor.GRAY + " BlockBreakReward ");
        LOGGER.info(ChatColor.GRAY + "     Version " + ChatColor.RED + "v" +
            pdf.getVersion());
        LOGGER.info(ChatColor.GREEN + "     No update available");
        LOGGER.info(ChatColor.GRAY + " Plugin by " + ChatColor.GREEN + "" +
            pdf.getAuthors());
        LOGGER.info(ChatColor.GRAY + "+-----------------------------+");
      } else {

        LOGGER.info(ChatColor.YELLOW + "Block Break Reward is on");

        LOGGER.info(ChatColor.GRAY + "+-----------------------------+");
        LOGGER.info(ChatColor.GRAY + " BlockBreakReward ");
        LOGGER.info(ChatColor.GRAY + "     Version " + ChatColor.RED + "v" +
            pdf.getVersion());
        LOGGER.info(ChatColor.RED + "New version" + version + "is released");
        LOGGER.info(ChatColor.GRAY + " Plugin by " + ChatColor.GREEN + "" +
            pdf.getAuthors());
        LOGGER.info(ChatColor.GRAY + "+-----------------------------+");
      }
    });

  }

  public void info(String version) {
    LOGGER.info(ChatColor.YELLOW + "Block Break Reward is on");

    LOGGER.info(ChatColor.GRAY + "+-----------------------------+");
    LOGGER.info(ChatColor.GRAY + " BlockBreakReward ");
    LOGGER.info(ChatColor.GRAY + "     Version " + ChatColor.RED + "v" +
        pdf.getVersion());
    LOGGER.info(ChatColor.GRAY + " Plugin by " + ChatColor.GREEN + "" +
        pdf.getAuthors());
    LOGGER.info(ChatColor.GRAY + "+-----------------------------+");
  }

  @Override
  public void onDisable() {
    MyFunc.SetDefaultConfigValue();
    if (mysql.isConnected()) {
      try {
        for (int x = 0; x < players.size(); x++) {
          if (players.get(x).p != null & players.get(x).minedAfterJoin > 0) {
            MySQLHandler.SavePlayerDataToMySQL(players.get(x), x);
          }
        }
      } catch (Exception exception) {
        LOGGER.info(ChatColor.RED + "All players data can't be saved");
      }
      LOGGER.info(ChatColor.RED + "Block Break Reward is off");
    } else {
      try {
        for (int x = 0; x < players.size(); x++) {
          if (players.get(x).p != null & players.get(x).minedAfterJoin > 0) {
            PlayerProcessor.CreatePlayerFileAndSetValue(players.get(x), x);
          }
        }
      } catch (Exception exception) {
        LOGGER.info(ChatColor.RED + "All players data can't be saved");
      }
      LOGGER.info(ChatColor.RED + "Block Break Reward is off");
    }
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

  public static void CreateDefaultLeaderboardFile() {
    File leaderboard = new File(leaderboardFilePath);
    if (!leaderboard.exists()) {
      try {
        leaderboard.createNewFile();
      } catch (IOException e) {
        LOGGER.info(ChatColor.RED + "Can't create Leaderboard.yml");
        e.printStackTrace();
      }
    }
    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(leaderboard);
    if (!yaml.contains("FillBackgroundWith")) {
      yaml.set("FillBackgroundWith", "BLACK_STAINED_GLASS_PANE");
    }
    if (!yaml.contains("UpdateEverySeconds")) {
      yaml.set("UpdateEverySeconds", 7200);
    }
    if (!yaml.contains("LeaderboardTitle")) {
      yaml.set("LeaderboardTitle", "TOP MINER!");
    }
    List<String> lores = new ArrayList<>();
    lores.add("§eTOTAL MINED BLOCKS: §b%mined_blocks%");
    lores.add("§eTOTAL MINED DIAMONDS: §b%mined_diamonds%");
    lores.add("§eTOTAL MINED EMERALDS: §b%mined_emeralds%");
    lores.add("§eTOTAL MINED GOLDS: §b%mined_golds%");
    lores.add("§eTOTAL MINED IRONS: §b%mined_irons%");
    lores.add("§eTOTAL MINED COALS: §b%mined_coals%");
    if (!yaml.contains("LeaderboardGUI")
        || yaml.getConfigurationSection("LeaderboardGUI").getKeys(false) == null) {

      yaml.set("LeaderboardGUI.TOP1.material", "PLAYER_HEAD");
      yaml.set("LeaderboardGUI.TOP1.position", 14);
      yaml.set("LeaderboardGUI.TOP1.top", 1);
      yaml.set("LeaderboardGUI.TOP1.name", "§c%player_name% #1");
      yaml.set("LeaderboardGUI.TOP1.lores", lores);
    }
    try {
      yaml.save(leaderboard);
    } catch (IOException e) {
      LOGGER.info(ChatColor.RED + "Can't save Leaderboard.yml properties");
    }
  }

  public void updateLeaderboardSchedule() {
    new BukkitRunnable() {
      @Override
      public void run() {
        if (LeaderboardTemplate.updateEverySeconds < 7200) {
          LOGGER.info(ChatColor.RED + "Leaderboard is refreshed! (every " + LeaderboardTemplate.updateEverySeconds
              + " seconds, update time is pretty fast, might cause lag spikes!)");
        }
        LOGGER.info(ChatColor.GREEN + "Leaderboard is refreshed! (every " + LeaderboardTemplate.updateEverySeconds
            + " seconds)");
        LeaderboardManager.reloadGUI();
      }
    }.runTaskTimerAsynchronously(plugin, 0, LeaderboardTemplate.updateEverySeconds * 20);
  }
}
