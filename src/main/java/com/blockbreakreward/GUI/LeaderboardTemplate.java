package com.blockbreakreward.GUI;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.blockbreakreward.Plugin;

public class LeaderboardTemplate {
    public static String fillBackgroundWith;
    public static Integer updateEverySeconds;
    public static String leaderboardTitle;

    public static void updateLeaderboardTemplate() {
        Plugin.CreateDefaultLeaderboardFile();
        File file = new File(Plugin.leaderboardFilePath);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        fillBackgroundWith = yaml.getString("FillBackgroundWith");
        updateEverySeconds = yaml.getInt("UpdateEverySeconds");
        leaderboardTitle = yaml.getString("LeaderboardTitle");
    }
}
