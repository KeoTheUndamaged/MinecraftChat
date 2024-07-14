package com.keotheundamaged.minecraftchat.Commands;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Command executor for managing banned words in Minecraft chat.
 */
public class BannedWordsCommands implements CommandExecutor {
    private final BannedWordsHelper bannedWordsHelper;

    /**
     * Constructor for BannedWordsCommands.
     * Initializes the BannedWordsHelper instance.
     */
    public BannedWordsCommands() {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("MinecraftChat");
        this.bannedWordsHelper = BannedWordsHelper.getInstance(plugin);
    }

    /**
     * Handles commands for managing banned words.
     *
     * @param sender  The sender of the command.
     * @param command The command that was executed.
     * @param label   The alias of the command that was used.
     * @param args    The arguments passed with the command.
     * @return true if the command was executed successfully, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true; // Only allow players to use the command
        Player player = (Player) sender;
        if (!player.hasPermission("minecraftchat.admin")) return true; // Only allow players with the correct permission to use the command

        if (args.length == 0) {
            player.sendMessage(String.format("%s missing arguments", ChatColor.YELLOW));
            return true;
        }

        String action = args[0];

        if (action.equalsIgnoreCase("list")) {
            // List all exact and wildcard banned words
            String exactWords = bannedWordsHelper.getExactBannedWords();
            String wildcardWords = bannedWordsHelper.getWildcardBannedWords();

            player.sendMessage("Banned words:");
            player.sendMessage("---------------------");
            player.sendMessage(String.format("%s Exact: %s", ChatColor.YELLOW, exactWords));
            player.sendMessage(" ");
            player.sendMessage(String.format("%s Wildcard: %s", ChatColor.YELLOW, wildcardWords));
            player.sendMessage("---------------------");

            return true;
        }

        try {
            String word = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            String bannedWordType = args[1];

            if (action.equalsIgnoreCase("add")) {
                // Add a word to the exact or wildcard banned words list
                if (bannedWordType.equalsIgnoreCase("exact")) {
                    bannedWordsHelper.addToExactBannedWords(word);
                } else if (bannedWordType.equalsIgnoreCase("wildcard")) {
                    bannedWordsHelper.addToWildcardBannedWords(word);
                }
                player.sendMessage(String.format("%s Added %s to %s banned words", ChatColor.GREEN, word, bannedWordType));
            } else if (action.equalsIgnoreCase("remove")) {
                // Remove a word from the exact or wildcard banned words list
                if (bannedWordType.equalsIgnoreCase("exact")) {
                    bannedWordsHelper.removeFromExactBannedWords(word);
                } else if (bannedWordType.equalsIgnoreCase("wildcard")) {
                    bannedWordsHelper.removeFromWildcardBannedWords(word);
                }
                player.sendMessage(String.format("%s Removed %s from %s banned words", ChatColor.GREEN, word, bannedWordType));
            } else {
                player.sendMessage(String.format("%s Unknown action: %s", ChatColor.RED, action));
            }
        } catch (Exception e) {
            Bukkit.getServer().getLogger().warning(String.format("%s Failed to execute command: %s", ChatColor.RED, e.getMessage()));
            player.sendMessage(String.format("%s Failed to execute command: %s", ChatColor.RED, e.getMessage()));
        }
        return true;
    }
}