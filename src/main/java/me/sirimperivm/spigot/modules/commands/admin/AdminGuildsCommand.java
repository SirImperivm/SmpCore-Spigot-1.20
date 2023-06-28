package me.sirimperivm.spigot.modules.commands.admin;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("all")
public class AdminGuildsCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.admin-commands.guilds")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("ga")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.guilds.main"))) {
                return true;
            } else {
                if (Errors.noConsole(s)) {
                    return true;
                } else {
                    Player p = (Player) s;
                    if (a.length == 0) {
                        getUsage(p);
                    } else if (a.length == 1) {
                        getUsage(p);
                    } else if (a.length == 2) {
                        getUsage(p);
                    } else if (a.length == 3) {
                        getUsage(p);
                    } else if (a.length == 4) {
                        if (a[0].equalsIgnoreCase("createguild")) {
                            if (Errors.noPermCommand(p, conf.getSettings().getString("permissions.admin-commands.guilds.create"))) {
                                return true;
                            } else {
                                String newGuildName = a[1];
                                List<String> generatedGuilds = mods.getGeneratedGuilds();
                                boolean alreadyExists = false;
                                for (String generated : generatedGuilds) {
                                    String[] partGenerated = generated.split(";");
                                    String guildName = partGenerated[1];
                                    if (newGuildName.equals(guildName)) {
                                        alreadyExists = true;
                                        break;
                                    }
                                }
                                if (!alreadyExists) {
                                    boolean containsChars = false;
                                    for (char mLimitChar : a[3].toCharArray()) {
                                        if (!((mLimitChar >= '0') && (mLimitChar <= '9'))) {
                                            containsChars = true;
                                            break;
                                        }
                                    }
                                    if (!containsChars) {
                                        int memberLimit = Integer.parseInt(a[3]);
                                        if (memberLimit > 0) {
                                            mods.createGuild(p, newGuildName, a[2], memberLimit);
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.membersLimit.greaterThanZero"));
                                        }
                                    } else {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.membersLimit.containsChars"));
                                    }
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.alreadyExists"));
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
