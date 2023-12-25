package me.trukoaiu.playerscompass.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;


public class CompassTarget implements Listener {

    @EventHandler
    public void onCompassRightClick(PlayerInteractEvent e) {

        if (e.getAction().toString().contains("RIGHT")) {
            Player p = e.getPlayer();

            ItemStack mainHandItem = p.getInventory().getItemInMainHand();
            ItemStack offHandItem = p.getInventory().getItemInOffHand();

            if (mainHandItem.getType() == Material.COMPASS) {
                compassFunctionality(mainHandItem, p);
            } else if (offHandItem.getType() == Material.COMPASS) {
                compassFunctionality(offHandItem, p);
            }
        }

        if (e.getAction().toString().contains("LEFT")) {
            Player p = e.getPlayer();

            ItemStack mainHandItem = p.getInventory().getItemInMainHand();

            if (mainHandItem.getType() == Material.COMPASS) {
                compassSwapTarget(mainHandItem, p);
            }
        }
    }

    public void compassFunctionality(ItemStack hand, Player p) {
        if (hand.getItemMeta() == null) {
            return;
        }
        String compassName = hand.getItemMeta().getDisplayName();

        if (compassName.contains(ChatColor.YELLOW + "Follow: ")) {
            String playerName = compassName.replace("Follow: ", "");
            playerName = ChatColor.stripColor(playerName);
            Player target = Bukkit.getPlayer(playerName);

            if (target == null) {
                p.sendMessage(ChatColor.RED + "Target can not be found!");
            } else {
                if (p.getLocation().getWorld() != target.getLocation().getWorld()) {
                    p.sendMessage(ChatColor.RED + "Target is in other world");
                    p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f);
                } else {
                    setLocationInCompass(hand, p, target);
                    p.sendMessage(ChatColor.GREEN + playerName + " location has been updated!");
                }
            }
        }
    }

    private void setLocationInCompass(ItemStack inHandItem, Player p, Player target) {
        CompassMeta compassItemMeta = (CompassMeta) inHandItem.getItemMeta();
        List<String> compassLore = compassItemMeta.getLore();
        String[] loreArray = compassLore.toArray(new String[0]);

        if (loreArray[2].equals(ChatColor.RED + "Work in the NETHER")){
            compassItemMeta.setLodestone(target.getLocation());
            inHandItem.setItemMeta(compassItemMeta);
        } else {
            p.setCompassTarget(target.getLocation());
        }
    }

    public void compassSwapTarget(ItemStack mainHandItem, Player p) {
        if (mainHandItem.getItemMeta() == null) {
            return;
        }
        String compassName = mainHandItem.getItemMeta().getDisplayName();

        if (compassName.contains(ChatColor.YELLOW + "Follow: ")) {
            String currentPlayerName = compassName.replace("Follow: ", "");
            currentPlayerName = ChatColor.stripColor(currentPlayerName);

            ItemMeta compassItemMeta = mainHandItem.getItemMeta();
            List<String> compassLore = compassItemMeta.getLore();
            String[] loreArray = compassLore.toArray(new String[0]);

            Integer maxTargetsLength = loreArray.length - 3;
            String[] targetsNames = new String[maxTargetsLength];

            Integer currentPlayerIndex = 0;

            for (int i = 0; i < maxTargetsLength; i++) {
                targetsNames[i] = ChatColor.stripColor(loreArray[i + 3]);
                if (targetsNames[i].equals(currentPlayerName)) {
                    currentPlayerIndex = i;
                }
            }

            String nextTarget;

            if (currentPlayerIndex + 1 >= maxTargetsLength) {
                nextTarget = targetsNames[0];
            } else {
                nextTarget = targetsNames[currentPlayerIndex + 1];
            }

            ItemMeta mainHandItemMeta = mainHandItem.getItemMeta();
            mainHandItemMeta.setDisplayName(ChatColor.YELLOW + "Follow: " + ChatColor.GREEN + nextTarget);
            mainHandItem.setItemMeta(mainHandItemMeta);

            p.sendMessage(ChatColor.YELLOW + "Your target was swapped to: " + nextTarget);
        }
    }
}
