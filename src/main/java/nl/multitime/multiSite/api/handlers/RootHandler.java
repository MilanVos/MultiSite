package nl.multitime.multiSite.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.multitime.multiSite.MultiSite;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class RootHandler implements HttpHandler {

    private final MultiSite plugin;

    public RootHandler(MultiSite plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        JSONObject response = new JSONObject();
        response.put("name", "MultiSite API");
        response.put("version", plugin.getDescription().getVersion());
        response.put("endpoints", new String[] {
            "/api/status",
            "/api/players",
            "/api/player",
            "/api/stats",
            "/api/config"
        });

        String responseText = response.toJSONString();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseText.length());

        OutputStream os = exchange.getResponseBody();
        os.write(responseText.getBytes());
        os.close();
    }
}
