package nl.multitime.multiSite.config;

import nl.multitime.multiSite.MultiSite;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final MultiSite plugin;
    private FileConfiguration config;
    private File configFile;

    private FileConfiguration playersConfig;
    private File playersFile;

    public ConfigManager(MultiSite plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        plugin.saveDefaultConfig();

        loadPlayersConfig();
    }

    private void loadPlayersConfig() {
        playersFile = new File(plugin.getDataFolder(), "players.yml");
        if (!playersFile.exists()) {
            try {
                playersFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create players.yml!");
                e.printStackTrace();
            }
        }

        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    public FileConfiguration getPlayersConfig() {
        return playersConfig;
    }

    public void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save players.yml!");
            e.printStackTrace();
        }
    }

    public void reloadConfigs() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        loadPlayersConfig();
    }
}
