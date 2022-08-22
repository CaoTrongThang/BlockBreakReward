package com.blockbreakreward.handler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.blockbreakreward.MyFunc;
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

        for (int x = 0; x < plugin.players.size(); x++) {

            if (plugin.players.get(x).p == null) {
                if (plugin.players.get(x).playerUUID.equals(e.getPlayer().getUniqueId().toString())) {
                    plugin.players.get(x).p = e.getPlayer();
                    break;
                }
            }
            if (e.getPlayer().getUniqueId().equals(plugin.players.get(x).p.getUniqueId())) {
                break;
            }
            if (x == plugin.players.size() - 1
                    && !plugin.players.get(x).playerUUID.equals(e.getPlayer().getUniqueId().toString())) {
                plugin.players.add(new PlayerTemplate(0, e.getPlayer(),
                        e.getPlayer().getUniqueId().toString(), e.getPlayer().getName(), 0, 0, 0, 0, 0, 0));
                break;

            } else {
                return;
            }
        }
    }
}
