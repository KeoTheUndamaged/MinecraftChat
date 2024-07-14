package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Event listener for sign change events in Minecraft.
 */
public class SignEvents implements Listener {
    private final DiscordHelper discord;
    private final BannedWordsHelper bannedWordsHelper;

    /**
     * Constructor for SignEvents.
     * Initializes the DiscordHelper and BannedWordsHelper instances.
     */
    public SignEvents() {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("MinecraftChat");
        this.discord = DiscordHelper.getInstance(plugin);
        this.bannedWordsHelper = BannedWordsHelper.getInstance(plugin);
    }

    /**
     * Event handler for SignChangeEvent.
     * Checks the content of the sign for banned words and cancels the event if any are found.
     *
     * @param event The SignChangeEvent.
     */
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        String signContent = String.join(" ", event.getLines()); // Get content of sign being changed

        String result = bannedWordsHelper.checkForBannedWords(signContent);

        if (result != null) {
            event.setCancelled(true); // Cancel the event if a banned word is found
            this.discord.sendReportMessage(player, signContent, "sign", result); // Send a report to Discord
        }
    }
}