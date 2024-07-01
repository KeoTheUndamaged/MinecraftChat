package com.keotheundamaged.minecraftchat.Commands;

import com.keotheundamaged.minecraftchat.Common.Config.BannedWordsConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Handles the banned words command for the Minecraft plugin.
 */
public class BannedWordsCommand  implements CommandExecutor {
    private final BannedWordsConfigManager bannedWordsConfigManager;
    private final JavaPlugin plugin;

    /**
     * Constructs a BannedWordsCommand with the specified banned words configuration manager.
     *
     * @param bannedWordConfigManger the manager for banned words configuration
     */
    public BannedWordsCommand(JavaPlugin plugin, BannedWordsConfigManager bannedWordConfigManger) {
        this.bannedWordsConfigManager = bannedWordConfigManger;
        this.plugin = plugin;
    }

    /**
     * Executes the command for managing banned words.
     *
     * @param sender  the sender of the command
     * @param command the command that was executed
     * @param label   the alias of the command that was used
     * @param args    the arguments passed with the command
     * @return true if the command was successful, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {return false;}
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(String.format("%s invalid command: blacklist add|remove|list [word to add/remove]", ChatColor.RED));
        }
        String action = args[0];

        if (action.equals("list")) {
            player.sendMessage(String.format("%s Banned Words: %s",
                    ChatColor.BLUE,
                    bannedWordsConfigManager.getConfig().getStringList("bannedWords")
            ));
        }

        if (action.equals("add") || action.equals("remove")) {
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            String word = String.join(" ", newArgs);

            try {
                addOrRemoveWordToBannedList(action, word);
                player.sendMessage(String.format("%s successfully updated banned word list",
                        ChatColor.GREEN)
                );
                plugin.getLogger().info(String.format("%s [%s] updated banned word list: %s %s",
                        player.getName(),
                        player.getUniqueId(),
                        action,
                        word
                ));
            } catch (Exception e) {
                player.sendMessage(String.format("%s failed to %s word: %s",
                        ChatColor.RED,
                        action,
                        e.getMessage()
                ));
                return false;
            }
        }
        return true;
    }

    /**
     * Adds or removes a word to/from the banned words list.
     *
     * @param action the action to perform ("add" or "remove")
     * @param word   the word to add or remove
     * @throws Exception if the word is already in the list (for "add") or not in the list (for "remove")
     */
    private void addOrRemoveWordToBannedList(@NotNull String action, @NotNull String word) throws Exception {
        FileConfiguration config = bannedWordsConfigManager.getConfig();
        List<String> bannedWords = config.getStringList("bannedWords");
        boolean wordExists = bannedWords.contains(word);
        if (action.equals("add")) {
            if (!wordExists) {
                bannedWords.add(word);
            } else {
                throw new Exception(String.format("The word '%s' is already in banned list", word));
            }
        }
        if (action.equals("remove")) {
            if (wordExists) {
                bannedWords.remove(word);
            } else {
                throw new Exception(String.format("The word '%s' is not in banned list", word));
            }
        }
        bannedWordsConfigManager.getConfig().set("bannedWords", bannedWords);
        bannedWordsConfigManager.saveData();
    }
}