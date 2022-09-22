package com.blockbreakreward.ConfigHandler;

import java.util.List;

public class ConfigTemplate {
    public List<String> helpMessage;
    public String reloadMessage;
    public String needPermission;
    public String inventoryFullWaring;
    public boolean enableFullInventoryWarning;
    public boolean blockToInventory;
    public int savePlayerDataAfter;
    public List<String> exceptBlocks;
    public List<String> specificTools;
    public boolean progressActionBar;
    public String progessReachSound;
    public String actionBarTemplate;
    public String mySQLUsername;
    public String mySQLPassword;
    public int mySQLPort;
    public String mySQLDatabaseName;
    public boolean enableMySQL;

    public ConfigTemplate(
            List<String> helpMessage,
            String reloadMessage,
            String needPermission,
            String inventoryFullWarning,
            boolean enableFullInventoryWarning,
            boolean blockToInventory,
            int savePlayerDataAfter,
            List<String> exceptBlocks,
            List<String> specificTools,
            boolean progressActionBar,
            String progessReachSound,
            String actionBarTemplate,
            String mySQLUsername,
            String mySQLPassword,
            int mySQLPort,
            String mySQLDatabaseName,
            boolean enableMySQL) {

        this.helpMessage = helpMessage;
        this.reloadMessage = reloadMessage;
        this.needPermission = needPermission;
        this.inventoryFullWaring = inventoryFullWarning;
        this.enableFullInventoryWarning = enableFullInventoryWarning;
        this.blockToInventory = blockToInventory;
        this.savePlayerDataAfter = savePlayerDataAfter;
        this.exceptBlocks = exceptBlocks;
        this.specificTools = specificTools;
        this.progressActionBar = progressActionBar;
        this.progessReachSound = progessReachSound;
        this.actionBarTemplate = actionBarTemplate;
        this.mySQLUsername = mySQLUsername;
        this.mySQLPassword = mySQLPassword;
        this.mySQLPort = mySQLPort;
        this.mySQLDatabaseName = mySQLDatabaseName;
        this.enableMySQL = enableMySQL;
    }
}
