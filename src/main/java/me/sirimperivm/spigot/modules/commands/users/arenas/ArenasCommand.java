package me.sirimperivm.spigot.modules.commands.users.arenas;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Gui;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class ArenasCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    private void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.user-commands.arena")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("arena")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.arena.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    getUsage(s);
                } else if (a.length == 1) {
                    if (a[0].equalsIgnoreCase("join")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.arena.join"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                if (conf.getArenaPvP().getString("locations.defaultSpawn.world").equalsIgnoreCase("null")) {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.pvpArena.spawn.not-set"));
                                } else {
                                    if (mods.getPvpArenaData().containsKey(p.getName())) {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.pvpArena.players.already-joined"));
                                    } else {
                                        Location loc = p.getLocation();
                                        if (!mods.getPvpArenaJoins().containsKey(p.getName())) {
                                            mods.getPvpArenaJoins().put(p.getName(), loc);
                                        }
                                        mods.getPvpArenaData().put(p.getName(), "null");
                                        mods.sendPlayerToArenaSpawn(p);
                                        p.sendMessage(Config.getTransl("settings", "messages.info.pvpArena.joined"));
                                    }
                                }
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("leave")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.arena.leave"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                if (conf.getArenaPvP().getString("locations.defaultSpawn.world").equalsIgnoreCase("null")) {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.pvpArena.spawn.not-set"));
                                } else {
                                    if (!mods.getPvpArenaData().containsKey(p.getName())) {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.pvpArena.not-joined"));
                                    } else {
                                        Location loc = mods.getPvpArenaJoins().get(p.getName());
                                        mods.getPvpArenaData().remove(p.getName());
                                        mods.getPvpArenaJoins().remove(p.getName());
                                        p.teleport(loc);
                                        p.sendMessage(Config.getTransl("settings", "messages.info.pvpArena.left"));
                                    }
                                }
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("gui")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.arena.gui"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                Gui gui = new Gui();
                                p.openInventory(gui.pvpGui());
                            }
                        }
                    } else {
                        getUsage(s);
                    }
                } else if (a.length == 3) {
                    if (a[0].equalsIgnoreCase("bet")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.arena.bet"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
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
