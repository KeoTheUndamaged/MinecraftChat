package com.keotheundamaged.minecraftchat.Commands;

import com.keotheundamaged.minecraftchat.Common.Config.DiscordConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Handles Discord-related commands for the Minecraft plugin.
 * Allows admin users to change Discord integration configuration such as Chat Channel ID, Banned Words log channel, and Discord Bot API token.
 */
public class DiscordCommands implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DiscordConfigManager discordConfigManager;

    /**
     * Constructs a DiscordCommands instance with the specified plugin and Discord configuration manager.
     *
     * @param plugin the JavaPlugin instance
     * @param discordConfigManager the manager for Discord configuration
     */
    public DiscordCommands(JavaPlugin plugin, DiscordConfigManager discordConfigManager) {
        this.plugin = plugin;
        this.discordConfigManager = discordConfigManager;
    }

    /**
     * Executes the given command, returning its success.
     * Allows admin users to change Discord integration configuration.
     *
     * @param sender the source of the command
     * @param command the command which was executed
     * @param label the alias of the command which was used
     * @param args the arguments passed to the command
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // TODO allow admin users to change discord integration configuration Chat Channel ID|Banned words log channel|Discord Bot API
        if (!(sender instanceof Player)) {
            return false;
        }
        return false;
    }
}