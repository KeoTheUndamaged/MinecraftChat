package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Event listener for player chat events in Minecraft.
 */
public class ChatEvents implements Listener {
    private final DiscordHelper discord;
    private final BannedWordsHelper bannedWordsHelper;

    /**
     * Constructor for ChatEvents.
     * Initializes the DiscordHelper and BannedWordsHelper instances.
     */
    public ChatEvents() {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("MinecraftChat");
        this.discord = DiscordHelper.getInstance(plugin);
        this.bannedWordsHelper = BannedWordsHelper.getInstance(plugin);
    }

    /**
     * Event handler for AsyncPlayerChatEvent.
     * Checks the chat message for banned words and cancels the event if any are found.
     * Sends a report to Discord if a banned word is found.
     * Sends the chat message to a Discord channel if no banned words are found.
     *
     * @param event The AsyncPlayerChatEvent.
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Check for banned words in the chat message
        String result = bannedWordsHelper.checkForBannedWords(message);
        if (result != null) {
            event.setCancelled(true); // Cancel the event if a banned word is found
            discord.sendReportMessage(player, message, "chat", result); // Send a report to Discord
            return;
        }

        // Send the chat message to the Discord channel
        discord.sendChatMessage(String.format("%s >> %s", player.getName(), message));
    }
}