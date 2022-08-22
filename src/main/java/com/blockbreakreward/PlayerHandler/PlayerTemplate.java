package com.blockbreakreward.PlayerHandler;

import org.bukkit.entity.Player;

public class PlayerTemplate {
    public Player p;
    public String playerUUID;
    public int minedAfterJoin;
    public String playerName;
    public int minedBlocks;
    public int minedDiamonds;
    public int minedEmeralds;
    public int minedGolds;
    public int minedIrons;
    public int minedCoals;

    public PlayerTemplate(int minedAfterJoin, Player p, String playerUUID, String playerName, int minedBlocks,
            int minedDiamonds,
            int minedEmeralds,
            int minedGolds,
            int minedIrons, int minedCoals) {
        this.minedAfterJoin = minedAfterJoin;
        this.p = p;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.minedBlocks = minedBlocks;
        this.minedEmeralds = minedEmeralds;
        this.minedGolds = minedGolds;
        this.minedIrons = minedIrons;
        this.minedCoals = minedCoals;
    }

}
