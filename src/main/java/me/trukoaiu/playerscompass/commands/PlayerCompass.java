package me.trukoaiu.playerscompass.commands;

import me.trukoaiu.playerscompass.listeners.InventoryClick;
import me.trukoaiu.playerscompass.utility.CreateInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCompass implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player p) {
            if (InventoryClick.playerHeadsSave.containsKey(p.getUniqueId())) {
                CreateInventory.inventoryCreate(p, 1, InventoryClick.playerHeadsSave.get(p.getUniqueId()), InventoryClick.checkForNetherWorking(p));
            } else {
                CreateInventory.inventoryCreate(p, 1, new String[0], InventoryClick.checkForNetherWorking(p));
            }
        }

        return true;
    }
}
