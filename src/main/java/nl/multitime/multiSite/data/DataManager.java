package nl.multitime.multiSite.data;

import nl.multitime.multiSite.MultiSite;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final MultiSite plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private ServerStats serverStats;

    public DataManager(MultiSite plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
        this.serverStats = new ServerStats();

        loadAllData();
    }

    public PlayerData getPlayerData(UUID playerUuid) {
        return playerDataMap.computeIfAbsent(playerUuid, uuid -> new PlayerData(uuid));
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public ServerStats getServerStats() {
        return serverStats;
    }

    public void loadAllData() {
        FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();

        if (playersConfig.contains("players")) {
            for (String uuidString : playersConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                PlayerData data = new PlayerData(uuid);
                data.loadFromConfig(playersConfig.getConfigurationSection("players." + uuidString));
                playerDataMap.put(uuid, data);
            }
        }

        if (playersConfig.contains("server_stats")) {
            serverStats.loadFromConfig(playersConfig.getConfigurationSection("server_stats"));
        }
    }

    public void saveAllData() {
        FileConfiguration playersConfig = plugin.getConfigManager().getPlayersConfig();

        for (Map.Entry<UUID, PlayerData> entry : playerDataMap.entrySet()) {
            String path = "players." + entry.getKey().toString();
            entry.getValue().saveToConfig(playersConfig.createSection(path));
        }

        serverStats.saveToConfig(playersConfig.createSection("server_stats"));

        plugin.getConfigManager().savePlayersConfig();
    }

    public void updateServerStats() {
        serverStats.setOnlinePlayers(plugin.getServer().getOnlinePlayers().size());
        serverStats.setMaxPlayers(plugin.getServer().getMaxPlayers());
        serverStats.setTps(calculateTps());
        serverStats.setLastUpdated(System.currentTimeMillis());
    }

    private double calculateTps() {
        try {
            return plugin.getServer().getTPS()[0];
        } catch (Exception e) {
            return 20.0;
        }
    }
}
