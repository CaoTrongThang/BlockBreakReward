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
        if (plugin.getConfig().getBoolean("EnableFullInventoryWarning")) {
            e.getPlayer().sendMessage(ChatColor.RED + plugin.getConfig().getString("InventoryIsFullWarning"));
        }

        if (plugin.getConfig().getBoolean("BlockToInventory") == true) {
            if (!(e.getPlayer().getInventory().firstEmpty() == -1)) {
                Collection<ItemStack> items = e.getBlock().getDrops();
                for (ItemStack item : items) {
                    e.getPlayer().getInventory().addItem(item);
                    e.getBlock().setType(Material.AIR);
                }
            }
        }

        if (plugin.getConfig().getStringList("ExceptBlocks").size() >= 1
                && plugin.getConfig().getStringList("SpecificTools").size() >= 1) {
            if (ExceptBlocksCheck(plugin.getConfig().getStringList("ExceptBlocks"), e)
                    && SpecificToolsCheck(plugin.getConfig().getStringList("SpecificTools"), e.getPlayer())) {
                CheckIfPlayerInPlayersListAndAddBlock(e);
            }
        }
    }

    public void CheckIfPlayerInPlayersListAndAddBlock(BlockBreakEvent e) {
        if (plugin.players.size() == 0) {
            plugin.players.add(new PlayerTemplate(1, e.getPlayer(),
                    e.getPlayer().getUniqueId().toString(), e.getPlayer().getName(), 1, 0, 0, 0, 0, 0));
        }
        for (PlayerTemplate p : plugin.players) {
            if (p.p != null) {
                if (e.getPlayer().getUniqueId().equals(p.p.getUniqueId())) {
                    AddBlockBreakToPlayersList(p, PlayerDataBlocksCompare(e.getBlock().getType()), e.getPlayer());
                    return;
                }

                if (p.playerUUID.equals(e.getPlayer().getUniqueId().toString())) {
                    p.p = e.getPlayer();
                    AddBlockBreakToPlayersList(p, PlayerDataBlocksCompare(e.getBlock().getType()), e.getPlayer());
                    return;
                }
            }
        }
    }

    public static void AddBlockBreakToPlayersList(PlayerTemplate p, Material m, Player pler) {
        p.minedAfterJoin++;
        p.minedAfterJoin += p.minedAfterJoin;
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
                for (int x = 0; x < Plugin.players.size(); x++) {
                    if (Plugin.players.get(x).equals(p) && p.minedAfterJoin > 0) {
                        PlayerProcessor.CreatePlayerFileAndSetValue(p, x);
                    }
                }
            }
        }

        RewardTemplate rw = MyFunc.WhatRewardPermissonTheyHave(pler);
        if (rw != null) {
            if (p.minedBlocks % rw.blockNeedToMine == 0) {
                ExecuteCommand(pler, rw.commands, rw.randomCommand, p);
            }
        } else {
            return;
        }
    }

    public static void ExecuteCommand(Player p, List<String> commands, boolean isRandom, PlayerTemplate pt) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        if (commands.size() == 1) {
            Bukkit.dispatchCommand(console,
                    MyFunc.ReplacePlaceHolder(p, MyFunc.RemoveForwardSlash(commands.get(0)), pt));

        } else if (commands.size() > 1) {
            if (isRandom) {

                double rand = 0;
                while (rand <= 0 && rand <= 3) {
                    rand = Math.random() * commands.size() - 1;
                }
                Bukkit.dispatchCommand(console,
                        MyFunc.ReplacePlaceHolder(p, MyFunc.RemoveForwardSlash(commands.get((int) rand)), pt));
            } else {
                for (String cmd : commands) {
                    Bukkit.dispatchCommand(console,
                            MyFunc.ReplacePlaceHolder(p, MyFunc.RemoveForwardSlash(cmd), pt));
                }
            }
            return;
        }
    }

    public static Material PlayerDataBlocksCompare(Material m) {
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

    public static boolean SpecificToolsCheck(List<String> tools, Player p) {
        if (tools.contains("all")) {
            return true;
        }
        for (String tool : tools) {
            if (p.getInventory().getItemInMainHand().getType().toString().toLowerCase().contains(tool)) {
                return true;
            }
        }
        return false;
    }

    public static boolean ExceptBlocksCheck(List<String> blocks, BlockBreakEvent b) {
        if (blocks.contains("none")) {
            return true;
        }
        for (String block : blocks) {
            if (block.equalsIgnoreCase(b.getBlock().getType().toString().toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}
