package me.sirimperivm.spigot.modules.commands.users.pvp;

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
public class PvPGui implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    private void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.user-commands.pvpGui")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("pvpGui")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.pvpGui.main"))) {
                return true;
            } else {
                if (a.length != 0) {
                    getUsage(s);
                } else {
                    if (s instanceof Player) {
                        Player p = (Player) s;
                        Gui g = new Gui();
                        p.openInventory(g.pvpGui());
                    } else {
                        getUsage(s);
                    }
                }
            }
        }
        return false;
    }
}
