package com.blockbreakreward.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.blockbreakreward.Convenient;
import com.blockbreakreward.Plugin;

public class OnBlockBreak implements Listener {
    Plugin plugin;
    YamlConfiguration data;
    List<String> allPlayerData;

    public OnBlockBreak(Plugin plugin) {
        this.plugin = plugin;
        allPlayerData = new ArrayList<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onBlockBreakHandler(BlockBreakEvent e) {

        File[] getPlayerList = new File("plugins/blockbreakreward/playersData").listFiles();

        boolean isInList = false;
        if (getPlayerList.length >= 1) {
            for (int x = 0; x < getPlayerList.length; x++) {
                if (Convenient.GetFileNameWithoutExtension(getPlayerList[x].getName())
                        .equals(e.getPlayer().getUniqueId().toString())) {
                    isInList = true;
                    Convenient.AddBlockToPlayerData(e);
                    Convenient.CheckIfReachedReward(e,
                            "plugins/blockbreakreward/playersData/" + getPlayerList[x].getName());

                    return;
                } else if (x == getPlayerList.length - 1 && isInList == false) {
                    Convenient.CreateNewPlayerFile(e);
                }
            }
        } else {
            Convenient.CreateNewPlayerFile(e);
        }
        return;

    }
}
