package nl.multitime.multiSite.listeners;

import nl.multitime.multiSite.MultiSite;
import nl.multitime.multiSite.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final MultiSite plugin;

    public PlayerListener(MultiSite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getDataManager().getPlayerData(player);

        playerData.setLastKnownName(player.getName());
        playerData.setLastLogin(System.currentTimeMillis());

        plugin.getDataManager().updateServerStats();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getDataManager().getPlayerData(player);

        long sessionTime = System.currentTimeMillis() - playerData.getLastLogin();
        playerData.addPlayTime(sessionTime);

        playerData.setLastLogout(System.currentTimeMillis());

        plugin.getDataManager().updateServerStats();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData playerData = plugin.getDataManager().getPlayerData(player);

        playerData.incrementDeaths();

        Player killer = player.getKiller();
        if (killer != null) {
            PlayerData killerData = plugin.getDataManager().getPlayerData(killer);
            killerData.incrementKills();
        }
    }
}
