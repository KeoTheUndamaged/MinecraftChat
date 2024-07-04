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

public class BannedWordsCommands implements CommandExecutor {
    private final BannedWordsHelper bannedWordsHelper;

    public BannedWordsCommands() {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("MinecraftChat");
        this.bannedWordsHelper = BannedWordsHelper.getInstance(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (!player.hasPermission("minecraftchat.admin")) return true;

        if (args.length == 0) {
            player.sendMessage(String.format("%s missing arguments", ChatColor.YELLOW));
        }

        String action = args[0];

        if (action.equalsIgnoreCase("list")) {
            player.sendMessage("Not implemented yet");
            return true;
        }

        try {
            String word = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            String bannedWordType = args[1];

            if (action.equalsIgnoreCase("add")) {
                if (bannedWordType.equalsIgnoreCase("exact")) {
                    bannedWordsHelper.addToExactBannedWords(word);
                }
                if (bannedWordType.equalsIgnoreCase("wildcard")) {
                    bannedWordsHelper.addToWildcardBannedWords(word);
                }
            }
            if (action.equalsIgnoreCase("remove")) {
                if (bannedWordType.equalsIgnoreCase("exact")) {
                    bannedWordsHelper.removeFromExactBannedWords(word);
                }
                if (bannedWordType.equalsIgnoreCase("wildcard")) {
                    bannedWordsHelper.removeFromWildcardBannedWords(word);
                }
            }
        } catch (Exception e) {
            Bukkit.getServer().getLogger().warning(String.format("%s Failed to execute command: %s", ChatColor.RED, e.getMessage()));
            player.sendMessage(e.getMessage());
        }
        return false;
    }
}
