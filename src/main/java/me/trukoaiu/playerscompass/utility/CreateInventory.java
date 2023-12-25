package me.trukoaiu.playerscompass.utility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class CreateInventory {

    //Class for creating GUI for setting a compass tracker
    public static void inventoryCreate(Player p, Integer page, String[] playerEnchantName, Boolean doesWorkInNether) {
        //Gets all the players
        Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);

        //Create inventory that holds heads
        Inventory inventory = Bukkit.createInventory(p, 54, "Current Players: Page " + page);
        Integer playersAmmonut = onlinePlayers.length;

        inventoryBottomMenu(inventory, playersAmmonut, doesWorkInNether);

        Integer startingPoint = page * 45;

        //Create selected page - loop over all players to generate their buttons
        for (int i = startingPoint - 45; i < startingPoint; i++) {
            if (onlinePlayers.length <= i) {
                break;
            }
            Player player = onlinePlayers[i];

            ItemStack playerHead;
            ItemMeta playerHeadMeta;

            Boolean shouldSelect = false;

            //Loop over selected players to check if the player should be displayed as selected
            for (String pp : playerEnchantName){
                if (pp.equalsIgnoreCase(player.getName())){
                    shouldSelect = true;
                }
            }
            if (shouldSelect) {
                playerHead = new ItemStack(Material.LIME_WOOL, 1);
                playerHeadMeta = playerHead.getItemMeta();
                assert playerHeadMeta != null;
            } else {
                playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
                assert playerHeadMeta != null;
                ((SkullMeta) playerHeadMeta).setOwningPlayer(player);

            }
            playerHeadMeta.setDisplayName(ChatColor.GREEN + player.getName());
            playerHead.setItemMeta(playerHeadMeta);

            inventory.addItem(playerHead);
        }

        p.openInventory(inventory);
    }

    //Bottom row of the inventory - menu of GUI. No functionality, just items.
    public static void inventoryBottomMenu(Inventory inventory, Integer totalNumberOfPlayers, Boolean doesWorkInNether) {
        //Empty spaces
        ItemStack placeholderGlassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta placeholderGlassPaneMeta = placeholderGlassPane.getItemMeta();
        placeholderGlassPaneMeta.setDisplayName(" ");
        placeholderGlassPane.setItemMeta(placeholderGlassPaneMeta);

        //Confirm - button
        ItemStack agreeBox = new ItemStack(Material.GREEN_WOOL, 1);
        ItemMeta agreeBoxMeta = agreeBox.getItemMeta();
        agreeBoxMeta.setDisplayName(ChatColor.GREEN + "Locate Selected Players");
        agreeBox.setItemMeta(agreeBoxMeta);

        //Remove all selected players - button
        ItemStack removeSelectedBox = new ItemStack(Material.RED_WOOL, 1);
        ItemMeta removeSelectedBoxMeta = removeSelectedBox.getItemMeta();
        removeSelectedBoxMeta.setDisplayName(ChatColor.RED + "Remove Selected Players");
        removeSelectedBox.setItemMeta(removeSelectedBoxMeta);

        //Selecting if work in nether - button
        ItemStack shouldWorkInNether = new ItemStack(Material.NETHERRACK, 1);
        ItemMeta shouldWorkInNetherMeta = removeSelectedBox.getItemMeta();
        shouldWorkInNetherMeta.setDisplayName(ChatColor.RED + "Should Work in Nether?");
        if (doesWorkInNether){
            ArrayList<String> netherrackLore = new ArrayList<>();
            netherrackLore.add(ChatColor.RED + "Yes, it works in the nether!");
            shouldWorkInNetherMeta.setLore(netherrackLore);

            shouldWorkInNetherMeta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
        } else {
            if (shouldWorkInNetherMeta.hasEnchant(Enchantment.FIRE_ASPECT)){
                shouldWorkInNetherMeta.removeEnchant(Enchantment.FIRE_ASPECT);
            }

            ArrayList<String> netherrackLore = new ArrayList<>();
            netherrackLore.add(ChatColor.RED + "No, it does not work in the nether!");
            shouldWorkInNetherMeta.setLore(netherrackLore);
        }
        shouldWorkInNether.setItemMeta(shouldWorkInNetherMeta);

        //Previous page - button
        ItemStack previous = new ItemStack(Material.REPEATER, 1);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.setDisplayName(ChatColor.YELLOW + "Previous Player's page");
        previous.setItemMeta(previousMeta);

        //Next page - button
        ItemStack next = new ItemStack(Material.COMPARATOR, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.YELLOW + "Next Player's page");
        next.setItemMeta(nextMeta);


        //Order of items in last row
        if (totalNumberOfPlayers > 45) {
            inventory.setItem(45, previous);
            inventory.setItem(53, next);
        } else {
            inventory.setItem(45, placeholderGlassPane);
            inventory.setItem(53, placeholderGlassPane);
        }
        inventory.setItem(46, placeholderGlassPane);
        inventory.setItem(47, removeSelectedBox);
        inventory.setItem(48, placeholderGlassPane);
        inventory.setItem(49, agreeBox);
        inventory.setItem(50, placeholderGlassPane);
        inventory.setItem(51, shouldWorkInNether);
        inventory.setItem(52, placeholderGlassPane);
    }
}
