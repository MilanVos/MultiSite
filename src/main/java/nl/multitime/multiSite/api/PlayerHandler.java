package nl.multitime.multiSite.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.multitime.multiSite.MultiSite;
import nl.multitime.multiSite.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class PlayerHandler implements HttpHandler {

    private final MultiSite plugin;

    public PlayerHandler(MultiSite plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            String response = "Method not allowed";
            exchange.sendResponseHeaders(405, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        String playerIdentifier = null;

        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    if (keyValue[0].equals("uuid") || keyValue[0].equals("name")) {
                        playerIdentifier = keyValue[1];
                        break;
                    }
                }
            }
        }

        if (playerIdentifier == null) {
            String response = "Missing player identifier (uuid or name)";
            exchange.sendResponseHeaders(400, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        OfflinePlayer offlinePlayer = null;

        try {
            UUID uuid = UUID.fromString(playerIdentifier);
            offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException e) {
            offlinePlayer = Bukkit.getOfflinePlayer(playerIdentifier);
        }

        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
            String response = "Player not found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        PlayerData playerData = plugin.getDataManager().getPlayerData(offlinePlayer.getUniqueId());

        JSONObject response = new JSONObject();
        response.put("uuid", offlinePlayer.getUniqueId().toString());
        response.put("name", offlinePlayer.getName());
        response.put("online", offlinePlayer.isOnline());
        response.put("firstPlayed", offlinePlayer.getFirstPlayed());
        response.put("lastPlayed", offlinePlayer.getLastPlayed());
        response.put("playTime", playerData.getPlayTime());
        response.put("kills", playerData.getKills());
        response.put("deaths", playerData.getDeaths());
        response.put("lastLogin", playerData.getLastLogin());
        response.put("lastLogout", playerData.getLastLogout());

        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            response.put("health", player.getHealth());
            response.put("level", player.getLevel());
            response.put("exp", player.getExp());
            response.put("gameMode", player.getGameMode().toString());
            response.put("world", player.getWorld().getName());
        }

        String responseText = response.toJSONString();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseText.length());

        OutputStream os = exchange.getResponseBody();
        os.write(responseText.getBytes());
        os.close();
    }
}
