package me.trukoaiu.playerscompass;

import me.trukoaiu.playerscompass.commands.PlayerCompass;
import me.trukoaiu.playerscompass.listeners.CompassTarget;
import me.trukoaiu.playerscompass.listeners.InventoryClick;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayersCompass extends JavaPlugin {

    @Override
    public void onEnable() {

        getCommand("tracker").setExecutor(new PlayerCompass());
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new CompassTarget(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
