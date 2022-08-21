package com.blockbreakreward.handler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.blockbreakreward.MyFunc;
import com.blockbreakreward.Plugin;
import com.blockbreakreward.PlayerHandler.PlayerProcessor;
import com.blockbreakreward.RewardHandler.RewardProcessor;

import net.md_5.bungee.api.ChatColor;

public class CommandHandler implements CommandExecutor {
    Plugin plugin;

    public CommandHandler(Plugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("blockbreakreward").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            MyFunc.PrintListTo(sender, plugin.getConfig().getStringList("HelpMessage"), InstanceOfPlayer(sender));
        } else if (args.length == 1) {

            if (args[0].equalsIgnoreCase("help")) {
                MyFunc.PrintListTo(sender, plugin.getConfig().getStringList("HelpMessage"), InstanceOfPlayer(sender));
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (p.hasPermission("blockbreakreward.reload")) {
                        plugin.saveDefaultConfig();
                        plugin.reloadConfig();
                        PlayerProcessor.SavePlayerDataToList();
                        RewardProcessor.SaveRewardsList(plugin);
                        MyFunc.PrintStringTo(sender, plugin.getConfig().getString("ReloadMessage"),
                                InstanceOfPlayer(sender));
                    } else {
                        MyFunc.PrintStringTo(sender, plugin.getConfig().getString("NeedPermission"),
                                InstanceOfPlayer(sender));
                    }
                } else {

                    plugin.saveDefaultConfig();
                    plugin.reloadConfig();
                    PlayerProcessor.SavePlayerDataToList();
                    RewardProcessor.SaveRewardsList(plugin);
                    MyFunc.PrintStringTo(sender, plugin.getConfig().getString("ReloadMessage"),
                            InstanceOfPlayer(sender));
                }

            }
        }
        return false;
    }

    public boolean InstanceOfPlayer(CommandSender sender) {
        return sender instanceof Player;
    }
}
