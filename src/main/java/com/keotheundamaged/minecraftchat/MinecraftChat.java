package com.keotheundamaged.minecraftchat;

import com.keotheundamaged.minecraftchat.Commands.BannedWordsCommands;
import com.keotheundamaged.minecraftchat.Events.AnvilEvents;
import com.keotheundamaged.minecraftchat.Events.BookEvents;
import com.keotheundamaged.minecraftchat.Events.ChatEvents;
import com.keotheundamaged.minecraftchat.Events.SignEvents;
import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the MinecraftChat plugin.
 * Handles plugin enable and disable events.
 */
public final class MinecraftChat extends JavaPlugin {
    private DiscordHelper discord;
    private BannedWordsHelper bannedWordsHelper;

    /**
     * Called when the plugin is enabled.
     * Initializes DiscordHelper and BannedWordsHelper instances, sends a startup message to Discord,
     * registers commands and event listeners.
     */
    @Override
    public void onEnable() {
        this.discord = DiscordHelper.getInstance(this);
        this.bannedWordsHelper = BannedWordsHelper.getInstance(this);

        // Send startup message to Discord
        this.discord.sendChatMessage(":green_circle: | Starting server");

        // Register command executor for the blacklist command
        getCommand("blacklist").setExecutor(new BannedWordsCommands());

        // Register event listeners
        getServer().getPluginManager().registerEvents(new ChatEvents(), this);
        getServer().getPluginManager().registerEvents(new SignEvents(), this);
        getServer().getPluginManager().registerEvents(new BookEvents(), this);
        getServer().getPluginManager().registerEvents(new AnvilEvents(), this);
    }

    /**
     * Called when the plugin is disabled.
     * Saves data and sends a shutdown message to Discord.
     */
    @Override
    public void onDisable() {
        // Save banned words data
        this.bannedWordsHelper.saveData();

        // Send shutdown message to Discord
        this.discord.sendChatMessage(":red_circle: | Stopping server");
        this.discord.saveData();
    }
}