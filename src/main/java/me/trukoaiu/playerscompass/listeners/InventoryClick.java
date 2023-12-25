package me.trukoaiu.playerscompass.listeners;

import me.trukoaiu.playerscompass.utility.CreateInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class InventoryClick implements Listener {

    public static Map<UUID, String[]> playerHeadsSave = new HashMap<>();
    public static Map<UUID, Boolean> doesWorkInNether = new HashMap<>();

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {

        if (e.getView().getTitle().startsWith("Current Players: Page ")) {
            Player p = (Player) e.getWhoClicked();

            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            } else if (e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                selectAPlayer(p, e);

            } else if (e.getCurrentItem().getType().equals(Material.LIME_WOOL)) {
                unSelectAPlayer(p, e);

            } else if (e.getCurrentItem().getType().equals(Material.GREEN_WOOL)) {
                acceptAndCreateCompass(p, e);

            } else if (e.getCurrentItem().getType().equals(Material.NETHERRACK)) {
                swapNetherSettings(p, e);

            } else if (e.getCurrentItem().getType().equals(Material.RED_WOOL)) {
                removeSelectedPlayers(p);

            } else if (e.getCurrentItem().getType().equals(Material.REPEATER)) {
                swapToPreviousPage(p, e);

            } else if (e.getCurrentItem().getType().equals(Material.COMPARATOR)) {
                swapToNextPage(p, e);

            }
        }
    }

    private void selectAPlayer(Player p, InventoryClickEvent e) {
        ItemStack getHead = e.getCurrentItem();
        ItemMeta getHeadMeta = getHead.getItemMeta();
        String headName = getHeadMeta.getDisplayName();

        ItemStack greenWool = new ItemStack(Material.LIME_WOOL, 1);
        ItemMeta greenWoolMeta = greenWool.getItemMeta();

        greenWoolMeta.setDisplayName(ChatColor.GREEN + p.getName());

        greenWool.setItemMeta(greenWoolMeta);

        putInHashMap(p, headName);

        e.setCurrentItem(greenWool);
    }

    private void unSelectAPlayer(Player p, InventoryClickEvent e) {
        ItemStack getWool = e.getCurrentItem();
        ItemMeta getWoolMeta = getWool.getItemMeta();
        String headName = getWoolMeta.getDisplayName();
        headName = ChatColor.stripColor(headName);
        Player targetPlayer = Bukkit.getPlayer(headName);

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        assert playerHeadMeta != null;
        ((SkullMeta) playerHeadMeta).setOwningPlayer(targetPlayer);
        playerHeadMeta.setDisplayName(ChatColor.GREEN + headName);
        playerHead.setItemMeta(playerHeadMeta);

        removeFromHashMap(p, headName);

        e.setCurrentItem(playerHead);
    }

    private void acceptAndCreateCompass(Player p, InventoryClickEvent e){
        if (!playerHeadsSave.containsKey(p.getUniqueId())) {
            return;
        }

        ItemStack compass = new ItemStack(Material.COMPASS, 1);
        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();

        String[] playersSavedHeads = playerHeadsSave.get(p.getUniqueId());

        compassMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        compassMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        compassMeta.setDisplayName(ChatColor.YELLOW + "Follow: " + ChatColor.GREEN + playersSavedHeads[0]);

        ArrayList<String> compassLore = new ArrayList<>();
        compassLore.add(ChatColor.LIGHT_PURPLE + "Left-click to change player:");
        compassLore.add(ChatColor.LIGHT_PURPLE + "Right-click to point to player:");

        if (doesWorkInNether.containsKey(p.getUniqueId())) {
            compassMeta.setLodestoneTracked(false);
            compassLore.add(ChatColor.RED + "Work in the NETHER");
        } else {
            compassLore.add(ChatColor.RED + "Does not work in the NETHER");
        }

        for (String pp : playersSavedHeads) {
            compassLore.add(ChatColor.WHITE + pp);
        }

        compassMeta.setLore(compassLore);
        compass.setItemMeta(compassMeta);

        p.getInventory().addItem(compass);
        playerHeadsSave.remove(p.getUniqueId());
        p.closeInventory();
    }

    private void swapNetherSettings(Player p, InventoryClickEvent e) {
        ItemStack netherrack = e.getCurrentItem();
        ItemMeta netherrackMeta = netherrack.getItemMeta();
        ArrayList<String> netherrackLore = new ArrayList<>();

        if (!doesWorkInNether.containsKey(p.getUniqueId())) {
            doesWorkInNether.put(p.getUniqueId(), true);

            netherrackMeta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
            netherrackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            netherrackLore.add(ChatColor.RED + "Yes, it works in the nether!");
        } else {
            doesWorkInNether.remove(p.getUniqueId());

            netherrackMeta.removeEnchant(Enchantment.FIRE_ASPECT);
            netherrackLore.add(ChatColor.RED + "No, it does not work in the nether!");
        }

            netherrackMeta.setLore(netherrackLore);
            netherrack.setItemMeta(netherrackMeta);
    }

    private void removeSelectedPlayers(Player p) {
        if (!playerHeadsSave.containsKey(p.getUniqueId())) {
            return;
        }

        playerHeadsSave.remove(p.getUniqueId());

        p.closeInventory();
        CreateInventory.inventoryCreate(p, 1, new String[0], InventoryClick.checkForNetherWorking(p));
    }

    private void swapToPreviousPage(Player p, InventoryClickEvent e) {
        Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        Integer amountOfOnlinePlayers = onlinePlayers.length;
        String nameOfInventory = e.getView().getTitle();
        Integer lastPage = (int) Math.ceil(amountOfOnlinePlayers / 45.0);

        Integer currentPage = Integer.valueOf(nameOfInventory.replace("Current Players: Page ", ""));

        p.closeInventory();
        if (currentPage - 1 < 1) {
            CreateInventory.inventoryCreate(p, lastPage, playerHeadsSave.get(p.getUniqueId()), InventoryClick.checkForNetherWorking(p));
        } else {
            CreateInventory.inventoryCreate(p, currentPage - 1, playerHeadsSave.get(p.getUniqueId()), InventoryClick.checkForNetherWorking(p));
        }
    }

    private void swapToNextPage(Player p, InventoryClickEvent e){
        Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        Integer amountOfOnlinePlayers = onlinePlayers.length;
        String nameOfInventory = e.getView().getTitle();
        Integer lastPage = (int) Math.ceil(amountOfOnlinePlayers / 45.0);

        Integer currentPage = Integer.valueOf(nameOfInventory.replace("Current Players: Page ", ""));

        p.closeInventory();
        if (currentPage + 1 > lastPage) {
            CreateInventory.inventoryCreate(p, 1, playerHeadsSave.get(p.getUniqueId()), InventoryClick.checkForNetherWorking(p));
        } else {
            CreateInventory.inventoryCreate(p, currentPage + 1, playerHeadsSave.get(p.getUniqueId()), InventoryClick.checkForNetherWorking(p));
        }
    }

    public void putInHashMap(Player p, String nameOfnewHead) {
        UUID playerUUID = p.getUniqueId();

        if (!playerHeadsSave.containsKey(playerUUID)) {
            playerHeadsSave.put(playerUUID, new String[0]);
        }
        String[] playersSavedHeads = playerHeadsSave.get(playerUUID);
        String[] newPlayersSavedHeads = new String[playersSavedHeads.length + 1];

        System.arraycopy(playersSavedHeads, 0, newPlayersSavedHeads, 0, playersSavedHeads.length);
        newPlayersSavedHeads[playersSavedHeads.length] = ChatColor.stripColor(nameOfnewHead);

        playerHeadsSave.put(playerUUID, newPlayersSavedHeads);
    }

    public void removeFromHashMap(Player p, String nameOfHeadToRemove) {
        UUID playerUUID = p.getUniqueId();

        if (!playerHeadsSave.containsKey(playerUUID)) {
            return;
        }
        if (playerHeadsSave.get(playerUUID).length <= 1) {
            playerHeadsSave.remove(playerUUID);
            return;
        }
        String[] playersSavedHeads = playerHeadsSave.get(playerUUID);
        String[] newPlayersSavedHeads = new String[playersSavedHeads.length - 1];

        Integer steps = 0;

        for (int i = 0; i < playersSavedHeads.length; i++) {
            if (!playersSavedHeads[i].equals(nameOfHeadToRemove)) {
                newPlayersSavedHeads[steps] = playersSavedHeads[i];
                steps++;
            }
        }

        playerHeadsSave.put(playerUUID, newPlayersSavedHeads);
    }

    public static boolean checkForNetherWorking(Player p) {
        return doesWorkInNether.getOrDefault(p.getUniqueId(), false);
    }
}
