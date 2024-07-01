package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Common.Config.BannedWordsConfigManager;
import com.keotheundamaged.minecraftchat.Common.Config.DiscordConfigManager;
import com.keotheundamaged.minecraftchat.Common.Connectors.DiscordConnector;
import com.keotheundamaged.minecraftchat.Common.Helpers.BannedWordsHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.plugin.java.JavaPlugin;


public class AnvilEvents implements Listener {
    private final BannedWordsConfigManager bannedWordConfigManger;
    private final JavaPlugin plugin;
    private final DiscordConnector discord;
    private final String chatChannelId;
    private final String reportChannelId;

    /**
     * Constructs a AnvilEvents instance with the specified plugin and banned words configuration manager.
     *
     * @param plugin                  the JavaPlugin instance
     * @param bannedWordConfigManger  the manager for banned words configuration
     */
    public AnvilEvents(
            JavaPlugin plugin,
            BannedWordsConfigManager bannedWordConfigManger,
            DiscordConfigManager discordConfigManager,
            DiscordConnector discordConnector
    ) {
        this.plugin = plugin;
        this.bannedWordConfigManger = bannedWordConfigManger;
        this.discord = discordConnector;
        this.chatChannelId = discordConfigManager.getConfig().getString("chatChannel");
        this.reportChannelId = discordConfigManager.getConfig().getString("reportChannel");
    }

    /**
     * Handles the InventoryClickEvent, specifically for InventoryType of ANVIL, to check for banned words in
     * items being renamed by an anvil.
     *
     * @param event  the inventory click event
     */
    @EventHandler
    public void onItemRename(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {return;} // if entity is not a player. Ignore
        if (event.getInventory().getType() != InventoryType.ANVIL) {return;} // if inventory is not an anvil. Ignore
        AnvilInventory inventory = (AnvilInventory) event.getInventory(); // get inventory of the anvil.
        String newItemName = inventory.getRenameText(); // get name of the item being renamed in an anvil
        Player player = (Player) event.getWhoClicked(); // get player who is changing item name

        String bannedWordRegex = bannedWordConfigManger.getBannedWordsRegex();
        BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();
        String result = bannedWordsHelper.checkForBannedWords(newItemName, bannedWordRegex);

        // if result is not null, it means a banned word was detected
        if (result != null) {
            event.setCancelled(true);
            plugin.getLogger().info(String.format("%s [%s] used a banned word (%s) by renaming an item to %s",
                    player.getName(), player.getUniqueId(), result, newItemName));
            discord.sendModLogToChannel(
                    reportChannelId,
                    player,
                    "anvil",
                    result,
                    newItemName,
                    chatChannelId);
        }
    }
}
