package nl.multitime.multiSite;

import nl.multitime.multiSite.api.RestApiServer;
import nl.multitime.multiSite.commands.AdminCommand;
import nl.multitime.multiSite.config.ConfigManager;
import nl.multitime.multiSite.data.DataManager;
import nl.multitime.multiSite.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class MultiSite extends JavaPlugin {

    private ConfigManager configManager;
    private DataManager dataManager;
    private RestApiServer apiServer;
    private BukkitTask statsTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getCommand("multisite").setExecutor(new AdminCommand(this));

        int port = getConfig().getInt("api.port", 8080);
        this.apiServer = new RestApiServer(this, port);
        apiServer.start();

        int updateInterval = getConfig().getInt("data.update-interval", 60) * 20; // Convert to ticks
        this.statsTask = getServer().getScheduler().runTaskTimerAsynchronously(
                this,
                () -> dataManager.updateServerStats(),
                100L,
                updateInterval
        );

        getLogger().info("MultiSite has been enabled!");
    }

    @Override
    public void onDisable() {
        if (statsTask != null) {
            statsTask.cancel();
        }

        if (apiServer != null) {
            apiServer.stop();
        }

        if (dataManager != null) {
            dataManager.saveAllData();
        }

        getLogger().info("MultiSite has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public RestApiServer getApiServer() {
        return apiServer;
    }
}
