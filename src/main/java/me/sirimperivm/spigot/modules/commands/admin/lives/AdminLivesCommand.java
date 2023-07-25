package me.sirimperivm.spigot.modules.commands.admin.lives;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class AdminLivesCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    private void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.admin-commands.lives")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("la")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.lives.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    getUsage(s);
                } else if (a.length == 1) {
                    if (a[0].equalsIgnoreCase("setDeathZone")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.lives.setdeathzone"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;

                                mods.setDeathZone(p);
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("life-top")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.lives.top"))) {
                            return true;
                        } else {
                            mods.sendLivesTop(s);
                        }
                    } else {
                        getUsage(s);
                    }
                } else if (a.length == 2) {
                    if (a[0].equalsIgnoreCase("info")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.lives.info"))) {
                            return true;
                        } else {
                            Player t = Bukkit.getPlayerExact(a[1]);
                            if (t == null) {
                                s.sendMessage(Config.getTransl("settings", "messages.errors.players.not-found"));
                            } else {
                                mods.sendLivesInfo(s, t);
                            }
                        }
                    } else {
                        getUsage(s);
                    }
                }
                else if (a.length == 3) {
                    if (a[0].equalsIgnoreCase("give-life")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.lives.life.give"))) {
                            return true;
                        } else {
                            Player t = Bukkit.getPlayerExact(a[1]);
                            if (t == null) {
                                s.sendMessage(Config.getTransl("settings", "messages.errors.players.not-found"));
                            } else {
                                String stringToAdd = a[2];
                                boolean containsChars = false;
                                for (char ch : stringToAdd.toCharArray()) {
                                    if (!(ch >= '0' && ch <= '9')) {
                                        containsChars = true;
                                        break;
                                    }
                                }
                                if (!containsChars) {
                                    int toAdd = Integer.parseInt(stringToAdd);
                                    int getLives = data.getLives().getPlayerLives(t);
                                    int total = toAdd + getLives;

                                    data.getLives().updatePlayerLives(t,total);
                                    s.sendMessage(Config.getTransl("settings", "messages.info.lives.life.added.admin")
                                            .replace("$livesCount", String.valueOf(toAdd))
                                            .replace("$playerName", a[1])
                                    );
                                    t.sendMessage(Config.getTransl("settings", "messages.info.lives.life.added.user")
                                            .replace("$livesCount", String.valueOf(toAdd))
                                            .replace("$totalCount", String.valueOf(total))
                                    );

                                } else {
                                    s.sendMessage(Config.getTransl("settings", "messages.errors.lives.contains-chars"));
                                }
                            }
                        }
                    }
                    else if (a[0].equalsIgnoreCase("take-life")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.lives.life.take"))) {
                            return true;
                        } else {
                            Player t = Bukkit.getPlayerExact(a[1]);
                            if (t == null) {
                                s.sendMessage(Config.getTransl("settings", "messages.errors.players.not-found"));
                            } else {
                                String stringToRemove = a[2];
                                boolean containsChars = false;
                                for (char ch : stringToRemove.toCharArray()) {
                                    if (!(ch >= '0' && ch <= '9')) {
                                        containsChars = true;
                                        break;
                                    }
                                }
                                if (!containsChars) {
                                    int toRemove = Integer.parseInt(stringToRemove);
                                    int getLives = data.getLives().getPlayerLives(t);
                                    int total = getLives - toRemove;

                                    data.getLives().updatePlayerLives(t,total);
                                    s.sendMessage(Config.getTransl("settings", "messages.info.lives.life.taken.admin")
                                            .replace("$livesCount", String.valueOf(toRemove))
                                            .replace("$playerName", a[1])
                                    );
                                    t.sendMessage(Config.getTransl("settings", "messages.info.lives.life.taken.user")
                                            .replace("$livesCount", String.valueOf(toRemove))
                                            .replace("$totalCount", String.valueOf(total))
                                    );

                                } else {
                                    s.sendMessage(Config.getTransl("settings", "messages.errors.lives.contains-chars"));
                                }
                            }
                        }
                    }
                    else {
                        getUsage(s);
                    }
                }
                else {
                    getUsage(s);
                }
            }
        }
        return false;
    }
}
