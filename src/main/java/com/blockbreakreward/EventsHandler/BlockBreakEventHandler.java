package com.blockbreakreward.EventsHandler;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.blockbreakreward.MyFunc;
import com.blockbreakreward.Plugin;
import com.blockbreakreward.ConfigHandler.ConfigHandler;
import com.blockbreakreward.MySQLConnection.MySQLHandler;
import com.blockbreakreward.PlayerLoader.PlayerProcessor;
import com.blockbreakreward.PlayerLoader.PlayerTemplate;
import com.blockbreakreward.RewardLoader.RewardTemplate;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class BlockBreakEventHandler implements Listener {
    Plugin plugin;
    public static Double progressState = 0.0;

    public BlockBreakEventHandler(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    boolean isRareBlock = false;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (ConfigHandler.GetEnableFullInventoryWarning()) {
            e.getPlayer().sendMessage(ChatColor.RED + ConfigHandler.GetInventoryFullWarning());
        }
        if (ConfigHandler.GetBlockToInventory() == true) {
            if (!(e.getPlayer().getInventory().firstEmpty() == -1)) {
                Collection<ItemStack> items = e.getBlock().getDrops();
                for (ItemStack item : items) {
                    e.getPlayer().getInventory().addItem(item);
                    e.getBlock().setType(Material.AIR);
                }
            }
        }

        if (ConfigHandler.GetExceptBlocks().size() >= 1
                && ConfigHandler.GetSpecificTools().size() >= 1) {
            if (ExceptBlocksCheck(ConfigHandler.GetExceptBlocks(), e)
                    && SpecificToolsCheck(ConfigHandler.GetSpecificTools(), e.getPlayer())) {
                CheckIfPlayerInPlayersListAndAddBlock(e);
            }
        }
    }

    public void CheckIfPlayerInPlayersListAndAddBlock(BlockBreakEvent e) {
        if (plugin.players.size() == 0) {
            plugin.players.add(new PlayerTemplate(0, e.getPlayer(),
                    e.getPlayer().getUniqueId().toString(), e.getPlayer().getName(), 0, 0, 0, 0, 0, 0));
        }
        for (PlayerTemplate p : plugin.players) {
            if (p.p != null) {
                if (e.getPlayer().getUniqueId().equals(p.p.getUniqueId())) {
                    AddBlockBreakToPlayersList(p, PlayerDataBlocksCompare(e.getBlock().getType()), e);
                    return;
                }

                if (p.playerUUID.equals(e.getPlayer().getUniqueId().toString())) {
                    p.p = e.getPlayer();
                    AddBlockBreakToPlayersList(p, PlayerDataBlocksCompare(e.getBlock().getType()), e);
                    return;
                }
            }
        }
    }

    public static void AddBlockBreakToPlayersList(PlayerTemplate p, Material m, BlockBreakEvent e) {
        p.minedAfterJoin++;
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
        // Check if user want save player after how many blocks in config.yml
        if (ConfigHandler.GetSavePlayerDataAfter() > 0) {
            if (p.minedBlocks % ConfigHandler.GetSavePlayerDataAfter() == 0) {
                for (int x = 0; x < Plugin.players.size(); x++) {
                    if (Plugin.players.get(x).equals(p) && p.minedAfterJoin > 0) {
                        if (Plugin.mysql.isConnected()) {
                            MySQLHandler.SavePlayerDataToMySQL(p, x);
                        } else {
                            PlayerProcessor.CreatePlayerFileAndSetValue(p, x);
                        }
                    }
                }
            }
        }

        RewardTemplate rw = MyFunc.WhatRewardPermissonTheyHave(e.getPlayer());
        // ActionBar
        if (rw != null) {
            if (ConfigHandler.GetProgressActionBar()) {
                progressState = ((double) p.minedBlocks % (double) rw.blockNeedToMine)
                        * (100.0 / (double) rw.blockNeedToMine);
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(
                                MyFunc.ReplacePlaceHolder(e, ConfigHandler.GetActionBarTemplate(), p)));
                p.progresstionState = progressState;
                if (progressState == 0.0f) {
                    if (!ConfigHandler.GetProgessReachSound().equalsIgnoreCase("none")) {
                        e.getPlayer().playSound(e.getPlayer().getLocation(),
                                Sound.valueOf(ConfigHandler.GetProgessReachSound()), 1, 1);
                    }
                    ExecuteCommand(e, rw.commands, rw.randomCommand, p);
                    return;
                }
            }
        } else {
            if (ConfigHandler.GetProgressActionBar()) {
                progressState = 0.0;
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(
                                MyFunc.ReplacePlaceHolder(e, ConfigHandler.GetActionBarTemplate(), p)));
            }
        }

        if (rw != null && !ConfigHandler.GetProgressActionBar()) {
            if (p.minedBlocks % rw.blockNeedToMine == 0) {
                ExecuteCommand(e, rw.commands, rw.randomCommand, p);
            }
        } else {
            return;
        }
    }

    public static void ExecuteCommand(BlockBreakEvent e, List<String> commands, boolean isRandom, PlayerTemplate pt) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Random rand = new Random();
        if (commands.size() == 1) {
            Bukkit.dispatchCommand(console,
                    MyFunc.ReplacePlaceHolder(e, MyFunc.RemoveForwardSlash(commands.get(0)), pt));

        } else if (commands.size() > 1) {
            if (isRandom) {
                Bukkit.dispatchCommand(console,
                        MyFunc.ReplacePlaceHolder(e,
                                MyFunc.RemoveForwardSlash(commands.get(rand.nextInt(commands.size()))), pt));
            } else {
                for (String cmd : commands) {
                    Bukkit.dispatchCommand(console,
                            MyFunc.ReplacePlaceHolder(e, MyFunc.RemoveForwardSlash(cmd), pt));
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
