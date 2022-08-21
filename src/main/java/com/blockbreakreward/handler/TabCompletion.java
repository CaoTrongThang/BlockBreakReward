package com.blockbreakreward.handler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.blockbreakreward.Plugin;

public class TabCompletion implements TabCompleter {

    Plugin plugin;

    public TabCompletion(Plugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("blockbreakreward").setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String lable, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("reload");
            suggestions.add("help");
            return suggestions;
        }
        return null;
    }

}
