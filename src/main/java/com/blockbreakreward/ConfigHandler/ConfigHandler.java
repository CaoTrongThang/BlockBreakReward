package com.blockbreakreward.ConfigHandler;

import java.util.List;

import com.blockbreakreward.Plugin;

public class ConfigHandler {
    public static void UpdateConfigInstance() {

        Plugin.configTemplate = new ConfigTemplate(Plugin.plugin.getConfig().getStringList("HelpMessage"),
                Plugin.plugin.getConfig().getString("ReloadMessage"),
                Plugin.plugin.getConfig().getString("NeedPermission"),
                Plugin.plugin.getConfig().getString("InventoryInFullWarning"),
                Plugin.plugin.getConfig().getBoolean("EnableInventoryFullWarning"),
                Plugin.plugin.getConfig().getBoolean("BlockToInventory"),
                Plugin.plugin.getConfig().getInt("SavePlayerDataAfter"),
                Plugin.plugin.getConfig().getStringList("ExceptBlocks"),
                Plugin.plugin.getConfig().getStringList("SpecificTools"),
                Plugin.plugin.getConfig().getBoolean("ProgressActionBar"),
                Plugin.plugin.getConfig().getString("ProgressReachSound"),
                Plugin.plugin.getConfig().getString("ActionBarTemplate"),
                Plugin.plugin.getConfig().getString("MySQL.username"),
                Plugin.plugin.getConfig().getString("MySQL.password"),
                Plugin.plugin.getConfig().getInt("MySQL.port"),
                Plugin.plugin.getConfig().getString("MySQL.databaseName"),
                Plugin.plugin.getConfig().getBoolean("MySQL.enableMySQL"));

    }

    public static boolean GetEnableMySQL() {
        return Plugin.configTemplate.enableMySQL;
    }

    public static String GetMySQLUsername() {
        return Plugin.configTemplate.mySQLUsername;
    }

    public static String GetMySQLPassword() {
        return Plugin.configTemplate.mySQLPassword;
    }

    public static String GetMySQLDatabaseName() {
        return Plugin.configTemplate.mySQLDatabaseName;
    }

    public static Integer GetMySQLPort() {
        return Plugin.configTemplate.mySQLPort;
    }

    public static List<String> GetHelpMessage() {
        return Plugin.configTemplate.helpMessage;
    }

    public static String GetReloadMessage() {
        return Plugin.configTemplate.reloadMessage;
    }

    public static String GetNeedPermissionMessage() {
        return Plugin.configTemplate.needPermission;
    }

    public static String GetInventoryFullWarning() {
        return Plugin.configTemplate.inventoryFullWaring;
    }

    public static boolean GetEnableFullInventoryWarning() {
        return Plugin.configTemplate.enableFullInventoryWarning;
    }

    public static boolean GetBlockToInventory() {
        return Plugin.configTemplate.blockToInventory;
    }

    public static List<String> GetExceptBlocks() {
        return Plugin.configTemplate.exceptBlocks;
    }

    public static List<String> GetSpecificTools() {
        return Plugin.configTemplate.specificTools;
    }

    public static int GetSavePlayerDataAfter() {
        return Plugin.configTemplate.savePlayerDataAfter;
    }

    public static boolean GetProgressActionBar() {
        return Plugin.configTemplate.progressActionBar;
    }

    public static String GetProgessReachSound() {
        return Plugin.configTemplate.progessReachSound.replace(".", "_").toUpperCase();
    }

    public static String GetActionBarTemplate() {
        return Plugin.configTemplate.actionBarTemplate;
    }
}
