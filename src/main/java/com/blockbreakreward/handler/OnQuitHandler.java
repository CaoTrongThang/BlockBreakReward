package com.blockbreakreward.handler;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.blockbreakreward.Plugin;
import com.blockbreakreward.PlayerHandler.PlayerProcessor;
import com.blockbreakreward.PlayerHandler.PlayerTemplate;

public class OnQuitHandler implements Listener {
    Plugin plugin;

    public OnQuitHandler(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void OnPlayerQuitHandler(PlayerQuitEvent e) {

        for (int i = 0; i < plugin.players.size(); i++) {
            if (plugin.players.get(i).p != null) {
                if (e.getPlayer().getUniqueId().equals(plugin.players.get(i).p.getUniqueId())) {
                    if (plugin.players.get(i).minedAfterJoin > 0) {
                        PlayerProcessor.CreatePlayerFileAndSetValue(plugin.players.get(i), i);
                    }
                }
            }
        }
    }
}
