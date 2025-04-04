package nl.multitime.multiSite.data;

import org.bukkit.configuration.ConfigurationSection;

public class ServerStats {

    private int onlinePlayers;
    private int maxPlayers;
    private double tps;
    private long uptime;
    private long lastUpdated;

    public ServerStats() {
        this.onlinePlayers = 0;
        this.maxPlayers = 0;
        this.tps = 20.0;
        this.uptime = 0;
        this.lastUpdated = System.currentTimeMillis();
    }

    public void loadFromConfig(ConfigurationSection section) {
        if (section == null) return;

        this.onlinePlayers = section.getInt("online_players", 0);
        this.maxPlayers = section.getInt("max_players", 0);
        this.tps = section.getDouble("tps", 20.0);
        this.uptime = section.getLong("uptime", 0);
        this.lastUpdated = section.getLong("last_updated", System.currentTimeMillis());
    }

    public void saveToConfig(ConfigurationSection section) {
        section.set("online_players", onlinePlayers);
        section.set("max_players", maxPlayers);
        section.set("tps", tps);
        section.set("uptime", uptime);
        section.set("last_updated", lastUpdated);
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
