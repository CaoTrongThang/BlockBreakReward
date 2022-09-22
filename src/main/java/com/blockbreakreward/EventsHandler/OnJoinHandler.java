package com.blockbreakreward.EventsHandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.blockbreakreward.Plugin;
import com.blockbreakreward.ConfigHandler.ConfigHandler;
import com.blockbreakreward.MySQLConnection.MySQLHandler;
import com.blockbreakreward.PlayerLoader.PlayerTemplate;

public class OnJoinHandler implements Listener {
    Plugin plugin;

    public OnJoinHandler(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void OnPlayerJoinHandler(PlayerJoinEvent e) {
        if (e.getPlayer().isOp() && plugin.playerDataFileList.length > 0 && plugin.mysql.isConnected()) {
            e.getPlayer().sendMessage(
                    "§f[§eBlockBreakReward§f] §bMySQL §eis enabled but there're still players data in folder, do you want to transfer it to database? type §b/blockbreakreward YamltoMySQLDatabase §eto transfer to database.");
        }
        for (int x = 0; x < plugin.players.size(); x++) {

            if (plugin.players.get(x).p == null) {
                if (plugin.players.get(x).playerUUID.equals(e.getPlayer().getUniqueId().toString())) {
                    plugin.players.get(x).p = e.getPlayer();
                    break;
                }
            } else {

                if (e.getPlayer().getUniqueId().equals(plugin.players.get(x).p.getUniqueId())) {
                    break;
                }
            }
            if (x == plugin.players.size() - 1
                    && !plugin.players.get(x).playerUUID.equals(e.getPlayer().getUniqueId().toString())) {
                plugin.players.add(new PlayerTemplate(0, e.getPlayer(),
                        e.getPlayer().getUniqueId().toString(), e.getPlayer().getName(), 0, 0, 0, 0, 0, 0));
                break;
            }
        }
        if (Plugin.mysql.isConnected() && ConfigHandler.GetEnableMySQL()) {
            MySQLHandler.LoadPlayerFromDatabaseWhenJoin(e.getPlayer());
        }
    }
}
