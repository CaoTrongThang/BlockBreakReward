package com.blockbreakreward.MySQLConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.blockbreakreward.Plugin;
import com.blockbreakreward.ConfigHandler.ConfigHandler;

import net.md_5.bungee.api.ChatColor;

public class MySQLConnect {

    private Connection connect;
    Plugin plugin;
    private String url = "jdbc:mysql://localhost" + ":" + ConfigHandler.GetMySQLPort().toString() + "/"
            + ConfigHandler.GetMySQLDatabaseName()
            + "?useSSl=false";

    public MySQLConnect(Plugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {

        if (!isConnected() && ConfigHandler.GetEnableMySQL()) {
            try {
                connect = DriverManager.getConnection(url, ConfigHandler.GetMySQLUsername(),
                        ConfigHandler.GetMySQLPassword());
                String sqlStatement = "CREATE TABLE IF NOT EXISTS playerdata(UUID INT,PLAYERNAME VARCHAR(50),MINEDBLOCKS INT,MINEDDIAMONDS INT,MINEDEMERALDS INT,MINEDGOLDS INT,MINEDIRON INT,MINEDCOALS INT,PRIMARY KEY(UUID));";
                try {
                    PreparedStatement ps = connect.prepareStatement(sqlStatement);
                    ps.executeUpdate();

                    Plugin.LOGGER.info(ChatColor.GREEN + "Create playerdata database successfully!");

                } catch (SQLException e) {
                    Plugin.LOGGER.info(ChatColor.RED + "Can't create playerdata database!");
                }

            } catch (SQLException e) {
                plugin.LOGGER.info(ChatColor.RED + "Unknown database " + ChatColor.BOLD
                        + ConfigHandler.GetMySQLDatabaseName()
                        + " name, please check if its a connect database name, not table. Or maybe your password or username is wrong");

            }
        }
    }

    public boolean isConnected() {
        if (!ConfigHandler.GetEnableMySQL()) {
            return false;
        }
        if (connect == null) {
            return false;
        }
        return true;
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connect.close();
            } catch (SQLException e) {
                plugin.LOGGER.info(ChatColor.RED + "Can't disconnect with MySQL");
            }

        } else {
            plugin.LOGGER.info("There's no MySQL connection to disconnect");
        }
    }

    public Connection getConnetion() {
        return connect;
    }
}
