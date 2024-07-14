package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Event listener for anvil inventory events in Minecraft.
 */
public class AnvilEvents implements Listener {
    private final DiscordHelper discord;
    private final BannedWordsHelper bannedWordsHelper;

    /**
     * Constructor for AnvilEvents.
     * Initializes the DiscordHelper and BannedWordsHelper instances.
     */
    public AnvilEvents() {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("MinecraftChat");
        this.discord = DiscordHelper.getInstance(plugin);
        this.bannedWordsHelper = BannedWordsHelper.getInstance(plugin);
    }

    /**
     * Event handler for InventoryClickEvent when renaming an item in an anvil.
     * Checks the new item name for banned words and cancels the event if any are found.
     * Sends a report to Discord if a banned word is found.
     *
     * @param event The InventoryClickEvent.
     */
    @EventHandler
    public void onItemRename(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return; // Ensure the event involves a player
        if (event.getInventory().getType() != InventoryType.ANVIL) return; // Ensure the event involves an anvil inventory

        Player player = (Player) event.getWhoClicked();
        AnvilInventory inventory = (AnvilInventory) event.getInventory();
        String itemName = inventory.getRenameText();
        if (itemName == null) return; // Exit if no new name is provided

        // Check for banned words in the new item name
        String result = bannedWordsHelper.checkForBannedWords(itemName);
        if (result != null) {
            event.setCancelled(true); // Cancel the event if a banned word is found
            this.discord.sendReportMessage(player, itemName, "anvil", result); // Send a report to Discord
        }
    }
}