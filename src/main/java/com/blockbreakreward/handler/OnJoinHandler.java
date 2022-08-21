package com.blockbreakreward.handler;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.blockbreakreward.Plugin;
import com.blockbreakreward.PlayerHandler.PlayerProcessor;
import com.blockbreakreward.PlayerHandler.PlayerTemplate;

public class OnJoinHandler implements Listener {
    Plugin plugin;

    public OnJoinHandler(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void OnPlayerJoinHandler(PlayerJoinEvent e) {
        String id = e.getPlayer().getUniqueId().toString();
        for (int x = 0; x < plugin.players.size(); x++) {
            if (plugin.players.get(x).p == null) {
                if (plugin.players.get(x).playerUUID.toString().equals(e.getPlayer().getUniqueId().toString())) {
                    plugin.players.get(x).p = e.getPlayer();
                    break;
                }
            } else if (x == plugin.players.size() - 1 && plugin.players.size() > 1) {
                PlayerProcessor.CreatePlayerFileAndSetValue(new PlayerTemplate(e.getPlayer(),
                        e.getPlayer().getUniqueId().toString(), e.getPlayer().getName(), 0, 0, 0, 0, 0, 0));
                PlayerProcessor.AddNewPlayerToList(e.getPlayer());
                break;

            } else {
                return;
            }
        }

    }
}
