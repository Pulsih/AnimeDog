package me.pulsi_.animedog;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final AnimeDog plugin;
    private File playerData;
    private FileConfiguration playerDataConfig;

    public ConfigManager(AnimeDog plugin) {
        this.plugin = plugin;
    }

    public void createConfigs() {
        playerData = new File(plugin.getDataFolder(), "playerData.yml");
        if (!playerData.exists()) plugin.saveResource("playerData.yml", false);

        playerDataConfig = new YamlConfiguration();
        YamlConfiguration.loadConfiguration(playerData);
    }

    public FileConfiguration getPlayerData() {
        return playerDataConfig;
    }

    public void reloadPlayerData() {
        playerDataConfig = YamlConfiguration.loadConfiguration(playerData);
    }

    public void savePlayerData() {
        try {
            playerDataConfig.save(playerData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}