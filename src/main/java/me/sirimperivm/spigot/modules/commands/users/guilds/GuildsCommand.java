package me.sirimperivm.spigot.modules.commands.users.guilds;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Gui;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class GuildsCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.user-commands.guilds")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("g")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    getUsage(s);
                } else if (a.length == 1) {
                    if (a[0].equalsIgnoreCase("shop")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.guilds.shop"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                Gui gm = new Gui();
                                p.openInventory(gm.shopGui());
                            }
                        }
                    } else {
                        getUsage(s);
                    }
                } else {
                    getUsage(s);
                }
            }
        }
        return false;
    }
}