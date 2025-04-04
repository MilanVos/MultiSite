package nl.multitime.multiSite.commands;

import nl.multitime.multiSite.MultiSite;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final MultiSite plugin;
    private final List<String> subCommands = Arrays.asList("reload", "stats", "api", "help");

    public AdminCommand(MultiSite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("multisite.admin.reload")) {
                    sender.sendMessage(ChatColor.RED + "Je hebt geen toestemming om dit commando uit te voeren.");
                    return true;
                }
                plugin.getConfigManager().reloadConfigs();
                sender.sendMessage(ChatColor.GREEN + "MultiSite configuratie is herladen!");
                break;

            case "stats":
                if (!sender.hasPermission("multisite.admin.stats")) {
                    sender.sendMessage(ChatColor.RED + "Je hebt geen toestemming om dit commando uit te voeren.");
                    return true;
                }
                showStats(sender);
                break;

            case "api":
                if (!sender.hasPermission("multisite.admin.api")) {
                    sender.sendMessage(ChatColor.RED + "Je hebt geen toestemming om dit commando uit te voeren.");
                    return true;
                }
                if (args.length > 1 && args[1].equalsIgnoreCase("restart")) {
                    restartApi(sender);
                } else {
                    showApiStatus(sender);
                }
                break;

            case "help":
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "==== MultiSite Help ====");
        sender.sendMessage(ChatColor.YELLOW + "/multisite reload " + ChatColor.WHITE + "- Herlaad de configuratie");
        sender.sendMessage(ChatColor.YELLOW + "/multisite stats " + ChatColor.WHITE + "- Toon server statistieken");
        sender.sendMessage(ChatColor.YELLOW + "/multisite api " + ChatColor.WHITE + "- Toon API status");
        sender.sendMessage(ChatColor.YELLOW + "/multisite api restart " + ChatColor.WHITE + "- Herstart de API server");
        sender.sendMessage(ChatColor.YELLOW + "/multisite help " + ChatColor.WHITE + "- Toon dit help menu");
    }

    private void showStats(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "==== Server Statistieken ====");
        sender.sendMessage(ChatColor.YELLOW + "Online spelers: " + ChatColor.WHITE +
                plugin.getServer().getOnlinePlayers().size() + "/" + plugin.getServer().getMaxPlayers());
        sender.sendMessage(ChatColor.YELLOW + "TPS: " + ChatColor.WHITE +
                String.format("%.2f", plugin.getDataManager().getServerStats().getTps()));
        sender.sendMessage(ChatColor.YELLOW + "Uptime: " + ChatColor.WHITE +
                formatTime(plugin.getDataManager().getServerStats().getUptime()));
        sender.sendMessage(ChatColor.YELLOW + "Laatste update: " + ChatColor.WHITE +
                new java.util.Date(plugin.getDataManager().getServerStats().getLastUpdated()));
    }

    private void showApiStatus(CommandSender sender) {
        boolean isRunning = plugin.getApiServer().isRunning();
        int port = plugin.getApiServer().getPort();

        sender.sendMessage(ChatColor.GOLD + "==== API Status ====");
        sender.sendMessage(ChatColor.YELLOW + "Status: " +
                (isRunning ? ChatColor.GREEN + "Actief" : ChatColor.RED + "Inactief"));
        sender.sendMessage(ChatColor.YELLOW + "Poort: " + ChatColor.WHITE + port);
        sender.sendMessage(ChatColor.YELLOW + "Endpoint: " + ChatColor.WHITE +
                "http://localhost:" + port + "/api");
    }

    private void restartApi(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "API server wordt herstart...");
        plugin.getApiServer().stop();
        plugin.getApiServer().start();
        sender.sendMessage(ChatColor.GREEN + "API server is herstart!");
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        return String.format("%d dagen, %d uren, %d minuten",
                days, hours % 24, minutes % 60);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("api")) {
            List<String> apiSubCommands = new ArrayList<>();
            apiSubCommands.add("restart");
            return apiSubCommands.stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
