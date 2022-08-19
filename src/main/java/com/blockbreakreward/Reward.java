package com.blockbreakreward;

import java.util.List;

public class Reward {
    String permission;
    int blockNeedToMine;
    List<String> commands;
    boolean randomCommands;

    public Reward(String permission, int blockNeedToMine, List<String> commands, boolean randomCommands) {
        this.permission = permission;
        this.blockNeedToMine = blockNeedToMine;
        this.commands = commands;
        this.randomCommands = randomCommands;
    }
}
