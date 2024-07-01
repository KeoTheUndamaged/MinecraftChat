package com.keotheundamaged.minecraftchat;

import com.keotheundamaged.minecraftchat.Commands.BannedWordsCommand;
import com.keotheundamaged.minecraftchat.Commands.DiscordCommands;
import com.keotheundamaged.minecraftchat.Common.Config.BannedWordsConfigManager;
import com.keotheundamaged.minecraftchat.Common.Config.DiscordConfigManager;
import com.keotheundamaged.minecraftchat.Common.Connectors.DiscordConnector;
import com.keotheundamaged.minecraftchat.Events.AnvilEvents;
import com.keotheundamaged.minecraftchat.Events.BookEvents;
import com.keotheundamaged.minecraftchat.Events.ChatEvents;
import com.keotheundamaged.minecraftchat.Events.SignEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Main class for the Minecraft Chat plugin.
 * This class handles the initialization and shutdown of the plugin, including loading commands and events.
 * It also manages the connection to Discord.
 *
 * @version 1.0
 * @since 2024-07-01
 *
 * <p>
 * This plugin enables the integration of Minecraft server chat with Discord,
 * as well as managing banned words and handling various in-game events.
 * </p>
 *
 * @see JavaPlugin
 * @see DiscordConnector
 * @see BannedWordsConfigManager
 * @see DiscordConfigManager
 */
public final class MinecraftChat extends JavaPlugin {
    private final DiscordConnector discordConnector;
    private final BannedWordsConfigManager bannedWordsConfigManager;
    private final DiscordConfigManager discordConfigManager;
    private final String chatChannel;

    /**
     * Constructor for MinecraftChat.
     * Initializes the configuration managers and the Discord connector.
     */
    public MinecraftChat() {
        bannedWordsConfigManager = new BannedWordsConfigManager(this);
        discordConfigManager = new DiscordConfigManager(this);
        String token = discordConfigManager.getConfig().getString("token");
        chatChannel = discordConfigManager.getConfig().getString("chatChannel");
        discordConnector = new DiscordConnector(token, chatChannel);
    }

    /**
     * Called when the plugin is enabled.
     * Sets up commands, registers event listeners, and sends a startup message to Discord.
     */
    @Override
    public void onEnable() {
        getLogger().info("Starting Minecraft Chat...");
        discordConnector.sendMessageToChannel(chatChannel, "Server starting");

        getLogger().info("Loading commands...");
        Objects.requireNonNull(getCommand("discord")).setExecutor(new DiscordCommands(this, discordConfigManager));
        Objects.requireNonNull(getCommand("blacklist")).setExecutor(new BannedWordsCommand(this, bannedWordsConfigManager));

        getLogger().info("Loading events...");
        getServer().getPluginManager().registerEvents(
                new ChatEvents(this, bannedWordsConfigManager, discordConfigManager, discordConnector), this);

        getServer().getPluginManager().registerEvents(
                new SignEvents(this, bannedWordsConfigManager, discordConfigManager, discordConnector), this);

        getServer().getPluginManager().registerEvents(
                new AnvilEvents(this, bannedWordsConfigManager, discordConfigManager, discordConnector), this);

        getServer().getPluginManager().registerEvents(
                new BookEvents(this, bannedWordsConfigManager, discordConfigManager, discordConnector), this);
        getLogger().info("Minecraft Chat enabled!");
    }

    /**
     * Called when the plugin is disabled.
     * Sends a shutdown message to Discord and saves configuration data.
     */
    @Override
    public void onDisable() {
        discordConnector.sendMessageToChannel(chatChannel, "Server shutting down");
        discordConnector.shutdown();
        discordConfigManager.saveData();
        bannedWordsConfigManager.saveData(false);
    }
}