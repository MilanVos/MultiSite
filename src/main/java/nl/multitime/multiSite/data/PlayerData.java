package nl.multitime.multiSite.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private String lastKnownName;
    private long playTime;
    private int deaths;
    private int kills;
    private long lastLogin;
    private long lastLogout;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.playTime = 0;
        this.deaths = 0;
        this.kills = 0;
        this.lastLogin = 0;
        this.lastLogout = 0;
    }

    public void loadFromConfig(ConfigurationSection section) {
        if (section == null) return;

        this.lastKnownName = section.getString("name", "");
        this.playTime = section.getLong("playtime", 0);
        this.deaths = section.getInt("deaths", 0);
        this.kills = section.getInt("kills", 0);
        this.lastLogin = section.getLong("last_login", 0);
        this.lastLogout = section.getLong("last_logout", 0);
    }

    public void saveToConfig(ConfigurationSection section) {
        section.set("name", lastKnownName);
        section.set("playtime", playTime);
        section.set("deaths", deaths);
        section.set("kills", kills);
        section.set("last_login", lastLogin);
        section.set("last_logout", lastLogout);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void addPlayTime(long additionalTime) {
        this.playTime += additionalTime;
    }

    public int getDeaths() {
        return deaths;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public int getKills() {
        return kills;
    }

    public void incrementKills() {
        this.kills++;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(long lastLogout) {
        this.lastLogout = lastLogout;
    }
}
