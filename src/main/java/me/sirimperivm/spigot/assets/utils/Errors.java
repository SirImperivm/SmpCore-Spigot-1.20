package me.sirimperivm.spigot.assets.utils;

import me.sirimperivm.spigot.assets.managers.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class Errors {

    public static boolean noPermCommand(CommandSender s, String node) {
        if (s.hasPermission(node))
            return false;
        s.sendMessage(Config.getTransl("settings", "messages.errors.no-perm.command")
                .replace("$node", node));
        return true;
    }

    public static boolean noPermAction(Player p, String node) {
        if (p.hasPermission(node))
            return false;
        p.sendMessage(Config.getTransl("settings", "messages.errors.no-perm.action")
                .replace("$node", node));
        return true;
    }

    public static boolean noConsole(CommandSender s) {
        if (s instanceof Player)
            return false;
        s.sendMessage(Config.getTransl("settings", "messages.errors.no-console"));
        return true;
    }
}
