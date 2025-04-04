package nl.multitime.multiSite.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.multitime.multiSite.MultiSite;
import nl.multitime.multiSite.data.ServerStats;
import org.bukkit.Server;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class ServerStatusHandler implements HttpHandler {

    private final MultiSite plugin;

    public ServerStatusHandler(MultiSite plugin) {
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

        plugin.getDataManager().updateServerStats();

        Server server = plugin.getServer();
        ServerStats stats = plugin.getDataManager().getServerStats();

        JSONObject response = new JSONObject();
        response.put("online", true);
        response.put("players", server.getOnlinePlayers().size());
        response.put("maxPlayers", server.getMaxPlayers());
        response.put("tps", stats.getTps());
        response.put("version", server.getVersion());
        response.put("bukkitVersion", server.getBukkitVersion());
        response.put("uptime", stats.getUptime());
        response.put("lastUpdated", stats.getLastUpdated());

        String responseText = response.toJSONString();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseText.length());

        OutputStream os = exchange.getResponseBody();
        os.write(responseText.getBytes());
        os.close();
    }
}
