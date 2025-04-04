package nl.multitime.multiSite.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.multitime.multiSite.MultiSite;
import nl.multitime.multiSite.data.ServerStats;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class StatsHandler implements HttpHandler {

    private final MultiSite plugin;

    public StatsHandler(MultiSite plugin) {
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
        ServerStats stats = plugin.getDataManager().getServerStats();

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();

        JSONObject response = new JSONObject();

        JSONObject serverInfo = new JSONObject();
        serverInfo.put("name", Bukkit.getServer().getName());
        serverInfo.put("version", Bukkit.getServer().getVersion());
        serverInfo.put("bukkitVersion", Bukkit.getServer().getBukkitVersion());
        serverInfo.put("onlineMode", Bukkit.getServer().getOnlineMode());
        serverInfo.put("maxPlayers", Bukkit.getServer().getMaxPlayers());
        serverInfo.put("viewDistance", Bukkit.getServer().getViewDistance());
        serverInfo.put("ip", Bukkit.getServer().getIp());
        serverInfo.put("port", Bukkit.getServer().getPort());

        JSONObject performance = new JSONObject();
        performance.put("tps", stats.getTps());
        performance.put("uptime", stats.getUptime());

        JSONObject memory = new JSONObject();
        memory.put("heapUsed", heapMemory.getUsed() / 1024 / 1024); // MB
        memory.put("heapMax", heapMemory.getMax() / 1024 / 1024); // MB
        memory.put("heapCommitted", heapMemory.getCommitted() / 1024 / 1024); // MB

        JSONObject players = new JSONObject();
        players.put("online", Bukkit.getServer().getOnlinePlayers().size());
        players.put("unique", Bukkit.getServer().getOfflinePlayers().length);

        response.put("server", serverInfo);
        response.put("performance", performance);
        response.put("memory", memory);
        response.put("players", players);
        response.put("timestamp", System.currentTimeMillis());

        String responseText = response.toJSONString();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseText.length());

        OutputStream os = exchange.getResponseBody();
        os.write(responseText.getBytes());
        os.close();
    }
}
