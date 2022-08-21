package com.blockbreakreward.handler;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.blockbreakreward.MyFunc;
import com.blockbreakreward.Plugin;
import com.blockbreakreward.PlayerHandler.PlayerProcessor;
import com.blockbreakreward.PlayerHandler.PlayerTemplate;
import com.blockbreakreward.RewardHandler.RewardTemplate;

import net.md_5.bungee.api.ChatColor;

public class BlockBreakEventHandler implements Listener {
    Plugin plugin;

    public BlockBreakEventHandler(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    boolean isRareBlock = false;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (plugin.getConfig().getBoolean("BlockToInventory") == true) {
            Collection<ItemStack> items = e.getBlock().getDrops();
            if (!(e.getPlayer().getInventory().firstEmpty() == -1)) {
                for (ItemStack item : items) {
                    e.getPlayer().getInventory().addItem(item);
                    e.getBlock().setType(Material.AIR);
                }
                if (plugin.players.size() == 0) {
                    PlayerProcessor.AddNewPlayerToList(e.getPlayer());
                }
            } else {
                e.getPlayer().sendMessage(ChatColor.RED + plugin.getConfig().getString("InventoryIsFullWarning"));
            }
        }
        if (plugin.players.size() == 0) {
            PlayerProcessor.CreatePlayerFileAndSetValue(new PlayerTemplate(e.getPlayer(),
                    e.getPlayer().getUniqueId().toString(), e.getPlayer().getName(), 0, 0, 0, 0, 0, 0));
            PlayerProcessor.AddNewPlayerToList(e.getPlayer());
        }
        for (PlayerTemplate p : plugin.players) {
            if (p.p != null) {
                if (e.getPlayer().getUniqueId().equals(p.p.getUniqueId())) {
                    AddBlockBreakToPlayersList(p, MaterialCompare(e.getBlock().getType()), e.getPlayer());
                    return;
                }

                if (p.playerUUID.equals(e.getPlayer().getUniqueId().toString())) {
                    p.p = e.getPlayer();
                    AddBlockBreakToPlayersList(p, MaterialCompare(e.getBlock().getType()), e.getPlayer());
                    return;
                }
            }
        }
        PlayerProcessor.AddNewPlayerToList(e.getPlayer());
    }

    public static void AddBlockBreakToPlayersList(PlayerTemplate p, Material m, Player pler) {
        p.minedBlocks++;

        if (m != null) {
            if (m == Material.DIAMOND_ORE) {
                p.minedDiamonds++;
            } else if (m == Material.EMERALD_ORE) {
                p.minedEmeralds++;
            } else if (m == Material.GOLD_ORE) {
                p.minedGolds++;
            } else if (m == Material.IRON_ORE) {
                p.minedIrons++;
            } else if (m == Material.COAL_ORE) {
                p.minedCoals++;
            }
        }
        if (Plugin.plugin.getConfig().getInt("SavePlayerDataAfter") > 0) {
            if (p.minedBlocks % Plugin.plugin.getConfig().getInt("SavePlayerDataAfter") == 0) {
                PlayerProcessor.CreatePlayerFileAndSetValue(p);
            }
        }

        RewardTemplate rw = MyFunc.WhatRewardPermissonTheyHave(pler);
        if (rw != null) {
            if (p.minedBlocks % rw.blockNeedToMine == 0) {
                ExecuteCommand(pler, rw.commands, rw.randomCommand);
            }
        } else {
            return;
        }
    }

    public static void ExecuteCommand(Player p, List<String> commands, boolean isRandom) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        if (commands.size() == 1) {
            System.out.println("SIZE = 1");
            Bukkit.dispatchCommand(console,
                    ReplacePlayerNameHolder(p, MyFunc.RemoveForwardSlash(commands.get(0))));

        } else if (commands.size() > 1) {
            System.out.println("SIZE = " + commands.size());
            if (isRandom) {

                double rand = 0;
                while (rand <= 0 && rand <= 3) {
                    rand = Math.random() * commands.size() - 1;
                }
                Bukkit.dispatchCommand(console,
                        ReplacePlayerNameHolder(p, MyFunc.RemoveForwardSlash(commands.get((int) rand))));
            } else {
                for (String cmd : commands) {
                    Bukkit.dispatchCommand(console,
                            ReplacePlayerNameHolder(p, MyFunc.RemoveForwardSlash(cmd)));
                }
            }
            return;
        }
    }

    public static String ReplacePlayerNameHolder(Player p, String cmd) {
        if (cmd.contains("%player_name%")) {
            return cmd.replace("%player_name%", p.getName());
        } else {
            return cmd;
        }
    }

    public static Material MaterialCompare(Material m) {
        if (m == Material.DIAMOND_ORE) {
            return Material.DIAMOND_ORE;
        } else if (m == Material.EMERALD_ORE) {
            return Material.EMERALD_ORE;
        } else if (m == Material.GOLD_ORE) {
            return Material.GOLD_ORE;
        } else if (m == Material.IRON_ORE) {
            return Material.IRON_ORE;
        } else if (m == Material.COAL_ORE) {
            return Material.COAL_ORE;
        } else {
            return null;
        }
    }
}
