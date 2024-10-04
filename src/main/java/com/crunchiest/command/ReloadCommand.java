package com.crunchiest.command;

import com.crunchiest.CrunchiestFishingPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final CrunchiestFishingPlugin plugin;

    public ReloadCommand(CrunchiestFishingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        // Reload all configurations
        plugin.reloadAllConfigs();
        sender.sendMessage("All configurations have been reloaded successfully!");
        return true;
    }
}