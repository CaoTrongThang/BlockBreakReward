package com.blockbreakreward.EventsHandler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import com.blockbreakreward.Plugin;
import com.blockbreakreward.GUI.LeaderboardManager;

public class InventoryEventHandler implements Listener {
    Plugin plugin;

    public InventoryEventHandler(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {

        if (e.getInventory().equals(LeaderboardManager.inv)) {
            List<Integer> draggingSlot = new ArrayList<>(e.getRawSlots());
            if (draggingSlot.get(0) < LeaderboardManager.inv.getSize()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getAction() == InventoryAction.NOTHING) {
            return;
        }
        if (e.getClickedInventory().equals(LeaderboardManager.inv)) {
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
        }

    }
}