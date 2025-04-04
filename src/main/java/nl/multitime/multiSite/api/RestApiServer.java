package nl.multitime.multiSite.api;

import com.sun.net.httpserver.HttpServer;
import nl.multitime.multiSite.MultiSite;
import nl.multitime.multiSite.api.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class RestApiServer {

    private final MultiSite plugin;
    private final int port;
    private HttpServer server;
    private boolean running;

    public RestApiServer(MultiSite plugin, int port) {
        this.plugin = plugin;
        this.port = port;
        this.running = false;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);

            // Set up context handlers for different API endpoints
            server.createContext("/api/status", new ServerStatusHandler(plugin));
            server.createContext("/api/players", new PlayersHandler(plugin));
            server.createContext("/api/player", new PlayerHandler(plugin));
            server.createContext("/api/stats", new StatsHandler(plugin));
            server.createContext("/api/config", new ConfigHandler(plugin));

            server.createContext("/api", new RootHandler(plugin));

            server.setExecutor(Executors.newCachedThreadPool());

            server.start();
            running = true;

            plugin.getLogger().info("REST API server started on port " + port);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to start REST API server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            running = false;
            plugin.getLogger().info("REST API server stopped");
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }
}
