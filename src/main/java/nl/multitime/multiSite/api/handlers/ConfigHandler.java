package nl.multitime.multiSite.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.multitime.multiSite.MultiSite;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigHandler implements HttpHandler {

    private final MultiSite plugin;

    public ConfigHandler(MultiSite plugin) {
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

        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            handleGetRequest(exchange);
        }
        else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            handlePostRequest(exchange);
        }
        else {
            String response = "Method not allowed";
            exchange.sendResponseHeaders(405, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        FileConfiguration config = plugin.getConfig();

        JSONObject response = new JSONObject();

        for (String key : config.getKeys(true)) {
            if (!config.isConfigurationSection(key)) {
                response.put(key, config.get(key));
            }
        }

        String responseText = response.toJSONString();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseText.length());

        OutputStream os = exchange.getResponseBody();
        os.write(responseText.getBytes());
        os.close();
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String apiKey = null;

        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals("key")) {
                    apiKey = keyValue[1];
                    break;
                }
            }
        }

        String configApiKey = plugin.getConfig().getString("api.key", "");
        if (configApiKey.isEmpty() || !configApiKey.equals(apiKey)) {
            String response = "Unauthorized: Invalid API key";
            exchange.sendResponseHeaders(401, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String requestBody = reader.lines().collect(Collectors.joining());

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonConfig = (JSONObject) parser.parse(requestBody);

            FileConfiguration config = plugin.getConfig();

            for (Object keyObj : jsonConfig.keySet()) {
                String key = (String) keyObj;
                Object value = jsonConfig.get(key);
                config.set(key, value);
            }

            plugin.saveConfig();

            String response = "{\"success\": true, \"message\": \"Configuration updated successfully\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        } catch (ParseException e) {
            String response = "{\"success\": false, \"message\": \"Invalid JSON format\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, response.length());

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
