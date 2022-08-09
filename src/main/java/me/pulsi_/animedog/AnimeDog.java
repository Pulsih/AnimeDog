package me.pulsi_.animedog;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AnimeDog extends JavaPlugin {

    private static AnimeDog instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.createConfigs();

        getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    @Override
    public void onDisable() {
        instance = this;
    }

    public static AnimeDog getInstance() {
        return instance;
    }

    public FileConfiguration getPlayerData() {
        return configManager.getPlayerData();
    }

    public void savePlayerData() {
        configManager.savePlayerData();
    }
}