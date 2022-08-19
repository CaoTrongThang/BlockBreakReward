package com.blockbreakreward;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabCompletion implements TabCompleter {
    public TabCompletion(Plugin plugin) {
        plugin.getCommand("blockbreakreward").setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<String> arr = new ArrayList<>();
        if (args.length == 1) {
            arr.add("reload");
            arr.add("help");
        }
        return arr;
    }

}
