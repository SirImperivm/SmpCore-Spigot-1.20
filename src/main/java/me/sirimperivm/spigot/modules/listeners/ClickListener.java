package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

@SuppressWarnings("all")
public class ClickListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        int slot = e.getSlot();

        if (title.equalsIgnoreCase(Config.getTransl("guis", "guis.shopGui.title"))) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);

            if (Errors.noPermAction(p, conf.getSettings().getString("permissions.user-actions.guilds.shop.use"))) {
                return;
            } else {
                for (String item : conf.getGuis().getConfigurationSection("guis.shopGui.items").getKeys(false)) {
                    String itemsPath = "guis.shopGui.items." + item;
                    List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");
                    String actionType = conf.getGuis().getString(itemsPath + ".action");

                    if (slots.contains(slot)) {
                        if (actionType.equalsIgnoreCase("CLOSE_MENU")) {
                            p.closeInventory();
                        } else if (actionType.equalsIgnoreCase("BUY_GUILD")) {
                            String guildId = conf.getGuis().getString(itemsPath + ".settings.guildId");
                            String guildName;
                            String guildTitle;
                            if (!guildId.equalsIgnoreCase("null")) {
                                if (mods.getGeneratedGuilds().contains(guildId)) {
                                    guildName = data.getGuilds().getGuildName(guildId);
                                    guildTitle = Config.getTransl("guilds", "guilds." + guildId + ".guildTitle");

                                    double userBalance = mods.getUserBalance(p);
                                    double price = conf.getGuis().getDouble(itemsPath + ".settings.price");
                                    if (price <= userBalance) {
                                        List<String> generatedMembers = mods.getGeneratedMembers();
                                        boolean alreadyExists = false;
                                        for (String generated : generatedMembers) {
                                            String[] partGenerated = generated.split(";");
                                            String username = partGenerated[1];
                                            if (p.getName().equals(username)) {
                                                alreadyExists = true;
                                                break;
                                            }
                                        }

                                        if (!alreadyExists) {
                                            mods.takeMoney(p, price);
                                            p.sendMessage(Config.getTransl("settings", "messages.info.money.withdrawn")
                                                    .replace("$value", String.valueOf(price)));
                                            mods.createLeader(p, guildId);
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.members.alreadyHaveGuild"));
                                        }
                                    } else {
                                        p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.money.not-enought")
                                                .replace("$price", String.valueOf(price))
                                                .replace("$guildTitle", guildTitle));
                                    }
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.guildId.notExists"));
                                }
                            }
                            else {
                                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.guildId.isNull"));
                            }
                        }
                    }
                }
            }
        }
    }
}
