package com.keotheundamaged.minecraftchat;

import com.keotheundamaged.minecraftchat.Commands.BannedWordsCommands;
import com.keotheundamaged.minecraftchat.Events.AnvilEvents;
import com.keotheundamaged.minecraftchat.Events.BookEvents;
import com.keotheundamaged.minecraftchat.Events.ChatEvents;
import com.keotheundamaged.minecraftchat.Events.SignEvents;
import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftChat extends JavaPlugin {
    private DiscordHelper discord;
    private BannedWordsHelper bannedWordsHelper;

    @Override
    public void onEnable() {
        this.bannedWordsHelper = new BannedWordsHelper();
        this.discord = new DiscordHelper(true);

        this.discord.sendChatMessage(":green_circle: | Starting server");

        getCommand("blacklist").setExecutor(new BannedWordsCommands());

        getServer().getPluginManager().registerEvents(new ChatEvents(), this);
        getServer().getPluginManager().registerEvents(new SignEvents(), this);
        getServer().getPluginManager().registerEvents(new BookEvents(), this);
        getServer().getPluginManager().registerEvents(new AnvilEvents(), this);
    }

    @Override
    public void onDisable() {
        this.bannedWordsHelper.saveData(false);

        this.discord.sendChatMessage(":red_circle: | Stopping server");
        this.discord.saveData(false);
    }
}
