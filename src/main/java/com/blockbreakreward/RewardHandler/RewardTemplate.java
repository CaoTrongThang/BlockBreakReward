package com.blockbreakreward.RewardHandler;

import java.util.List;

public class RewardTemplate {
    public String permission;
    public int blockNeedToMine;
    public List<String> commands;
    public boolean randomCommand;

    public RewardTemplate(String permission, int blockNeedToMine, List<String> commands, boolean randomCommand) {
        this.permission = permission;
        this.blockNeedToMine = blockNeedToMine;
        this.commands = commands;
        this.randomCommand = randomCommand;
    }
}
