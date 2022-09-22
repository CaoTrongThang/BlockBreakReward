package com.blockbreakreward.MySQLConnection;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.blockbreakreward.MyFunc;
import com.blockbreakreward.Plugin;
import com.blockbreakreward.PlayerLoader.PlayerTemplate;

import net.md_5.bungee.api.ChatColor;

public class MySQLHandler {

    public static Connection getConnection() {
        return Plugin.mysql.getConnetion();
    }

    public static void LoadPlayerFromDatabaseWhenJoin(Player p) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM playerdata WHERE UUID=? LIMIT 1");
            ps.setString(1, p.getUniqueId().toString());

            ResultSet result = ps.executeQuery();

            while (result.next()) {
                for (PlayerTemplate pt : Plugin.players) {
                    if (p.getUniqueId().toString().equalsIgnoreCase(pt.playerUUID)) {
                        pt.minedBlocks = result.getInt("MINEDBLOCKS");
                        pt.minedDiamonds = result.getInt("MINEDDIAMONDS");
                        pt.minedEmeralds = result.getInt("MINEDEMERALDS");
                        pt.minedGolds = result.getInt("MINEDGOLDS");
                        pt.minedIrons = result.getInt("MINEDIRONS");
                        pt.minedCoals = result.getInt("MINEDCOALS");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean ConvertYAMLToMySQL() {
        YamlConfiguration yaml = new YamlConfiguration();
        String sqlStatement = "CREATE TABLE IF NOT EXISTS playerdata(UUID INT,PLAYERNAME VARCHAR(50),MINEDBLOCKS INT,MINEDDIAMONDS INT,MINEDEMERALDS INT,MINEDGOLDS INT,MINEDIRON INT,MINEDCOALS INT,PRIMARY KEY(UUID));";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sqlStatement);
            ps.executeUpdate();
            ps.close();

            Plugin.LOGGER.info(ChatColor.GREEN + "Create playerdata database successfully!");

        } catch (SQLException e) {
            Plugin.LOGGER.info(ChatColor.RED + "Can't create playerdata database!");
        }

        PreparedStatement ps1 = null;
        if (Plugin.mysql.isConnected() && Plugin.playerDataFileList.length > 0) {
            for (File file : Plugin.playerDataFileList) {
                yaml = YamlConfiguration.loadConfiguration(file);
                try {
                    String sqlStatement1 = "INSERT INTO playerdata" +
                            "(UUID, PLAYERNAME, MINEDBLOCKS, MINEDDIAMONDS,MINEDEMERALDS, MINEDGOLDS,MINEDIRONS,MINEDCOALS)"
                            +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)" +
                            "ON DUPLICATE KEY UPDATE " +
                            "PLAYERNAME=VALUES(PLAYERNAME)," +
                            "MINEDBLOCKS=VALUES(MINEDBLOCKS)," +
                            "MINEDDIAMONDS=VALUES(MINEDDIAMONDS)," +
                            "MINEDEMERALDS=VALUES(MINEDEMERALDS)," +
                            "MINEDGOLDS=VALUES(MINEDGOLDS)," +
                            "MINEDIRONS=VALUES(MINEDIRONS)," +
                            "MINEDCOALS=VALUES(MINEDCOALS);";

                    ps1 = getConnection().prepareStatement(sqlStatement1);

                    ps1.setString(1, MyFunc.RemoveFileNameExtension(file.getName()));
                    ps1.setString(2, yaml.getString("playerName"));
                    ps1.setInt(3, yaml.getInt("minedBlocks"));
                    ps1.setInt(4, yaml.getInt("minedDiamonds"));
                    ps1.setInt(5, yaml.getInt("minedEmeralds"));
                    ps1.setInt(6, yaml.getInt("minedGolds"));
                    ps1.setInt(7, yaml.getInt("minedIrons"));
                    ps1.setInt(8, yaml.getInt("minedCoals"));
                    ps1.executeUpdate();
                } catch (SQLException e) {
                    Plugin.LOGGER.info(ChatColor.RED + "Can't migrate " + file.getName() + " to MySQL database");
                    return false;
                }

                try {
                    file.delete();
                } catch (Exception e) {
                    Plugin.LOGGER.info(ChatColor.RED + "Can't delete " + file.getName() + " from playerData folder!");
                }

            }
            try {
                ps1.close();
            } catch (SQLException e) {

            }
            return true;
        } else {
            return false;
        }
    }

    public static void SavePlayerDataToMySQL(PlayerTemplate pt, int ptPos) {
        try {
            String sqlStatement = "INSERT INTO playerdata" +
                    "(UUID, PLAYERNAME, MINEDBLOCKS, MINEDDIAMONDS,MINEDEMERALDS, MINEDGOLDS,MINEDIRONS,MINEDCOALS)"
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)" +
                    "ON DUPLICATE KEY UPDATE " +
                    "PLAYERNAME=VALUES(PLAYERNAME)," +
                    "MINEDBLOCKS=VALUES(MINEDBLOCKS)," +
                    "MINEDDIAMONDS=VALUES(MINEDDIAMONDS)," +
                    "MINEDEMERALDS=VALUES(MINEDEMERALDS)," +
                    "MINEDGOLDS=VALUES(MINEDGOLDS)," +
                    "MINEDIRONS=VALUES(MINEDIRONS)," +
                    "MINEDCOALS=VALUES(MINEDCOALS);";
            PreparedStatement ps1 = getConnection().prepareStatement(sqlStatement);

            ps1.setString(1, pt.playerUUID);
            ps1.setString(2, pt.p.getName());
            ps1.setInt(3, pt.minedBlocks);
            ps1.setInt(4, pt.minedDiamonds);
            ps1.setInt(5, pt.minedEmeralds);
            ps1.setInt(6, pt.minedGolds);
            ps1.setInt(7, pt.minedIrons);
            ps1.setInt(8, pt.minedCoals);

            Plugin.players.get(ptPos).minedAfterJoin = 0;
            ps1.executeUpdate();
            ps1.closeOnCompletion();
        } catch (SQLException e) {
            Plugin.LOGGER.info(ChatColor.RED + "Can't save playerdata to MySQL!");
            e.printStackTrace();
        }
    }

    public static boolean LoadPlayerDataMySQLToList() {
        String sqlStatement = "SELECT * FROM playerdata";
        try {
            PreparedStatement ps = MySQLHandler.getConnection().prepareStatement(sqlStatement);
            ResultSet results = ps.executeQuery();
            ps.closeOnCompletion();
            Player p;
            while (results.next()) {
                UUID id = UUID.fromString(results.getString("UUID"));
                if (Bukkit.getOfflinePlayer(id).isOnline()) {
                    p = Bukkit.getPlayer(id);
                } else {
                    p = null;
                }
                Plugin.players.add(new PlayerTemplate(0, p, results.getString("UUID"), results.getString("PLAYERNAME"),
                        results.getInt("MINEDBLOCKS"), results.getInt("MINEDDIAMONDS"), results.getInt("MINEDEMERALDS"),
                        results.getInt("MINEDGOLDS"), results.getInt("MINEDIRONS"), results.getInt("MINEDCOALS")));
            }
        } catch (SQLException e) {
            Plugin.LOGGER.info(ChatColor.RED + "Can't load playerdata from MySQL Database to server!");
            e.printStackTrace();
        }

        return true;
    }
}
