package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;

public class AnvilEvents implements Listener {
    private final DiscordHelper discord;
    public AnvilEvents() {
        this.discord = new DiscordHelper(false);
    }
    @EventHandler
    public void onItemRename(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        Player player = (Player) event.getWhoClicked();
        AnvilInventory inventory = (AnvilInventory) event.getInventory();
        String itemName = inventory.getRenameText();
        if (itemName == null) return;

        BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();

        String result = bannedWordsHelper.checkForBannedWords(itemName);
        if (result != null) {
            event.setCancelled(true);
            this.discord.sendReportMessage(player, itemName, "anvil", result);
        }
    }
}
