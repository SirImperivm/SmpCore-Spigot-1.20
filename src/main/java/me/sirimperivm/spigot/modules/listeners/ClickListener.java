package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
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
        ClickType cType = e.getClick();
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
                            String guildName = conf.getGuis().getString(itemsPath + ".settings.guildName");
                            String guildId;
                            String guildTitle;
                            if (!guildName.equalsIgnoreCase("null")) {
                                boolean contained = false;
                                List<String> generatedGuilds = mods.getGeneratedGuilds();
                                for (String generated : generatedGuilds) {
                                    String[] partGenerated = generated.split(";");
                                    String containedGuildName = partGenerated[1];
                                    if (guildName.equalsIgnoreCase(containedGuildName)) {
                                        contained = true;
                                        break;
                                    }
                                }
                                if (contained) {
                                    guildId = conf.getGuilds().getString("guilds." + guildName + ".guildId");
                                    guildTitle = Config.getTransl("guilds", "guilds." + guildName + ".guildTitle");

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

        if (title.equalsIgnoreCase(Config.getTransl("guis", "guis.bankGui.title"))) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);

            if (Errors.noPermAction(p, conf.getSettings().getString("permissions.user-actions.guilds.bank.use"))) {
                return;
            } else {
                for (String item : conf.getGuis().getConfigurationSection("guis.bankGui.items").getKeys(false)) {
                    String itemsPath = "guis.bankGui.items." + item;
                    String actionType = conf.getGuis().getString(itemsPath + ".action");
                    List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");
                    if (slots.contains(slot)) {
                        HashMap<String, List<String>> guildsData = mods.getGuildsData();
                        String playerName = p.getName();
                        if (guildsData.containsKey(playerName)) {
                            List<String> guildAndRole = guildsData.get(playerName);
                            String guildId = guildAndRole.get(0);
                            double bankBalance = data.getGuilds().getGuildBalance(guildId);
                            double userBalance = Main.getVault().getEcon().getBalance(p);

                            String guildName = data.getGuilds().getGuildName(guildId);
                            double depositLimit = conf.getGuilds().getDouble("guilds." + guildName + ".bank.limit");

                            if (actionType.equalsIgnoreCase("DEPOSIT")) {
                                if (Errors.noPermAction(p, conf.getSettings().getString("permissions.user-commands.guilds.bank.deposit"))) {
                                    return;
                                } else {
                                    if (cType == ClickType.LEFT) {
                                        double toDeposit = 100.0;
                                        if (depositLimit != -1.0 && (toDeposit + bankBalance) <= depositLimit) {
                                            if (userBalance >= toDeposit) {
                                                Vault.getEcon().withdrawPlayer(p, toDeposit);
                                                data.getGuilds().updateGuildBalance(guildId, String.valueOf(toDeposit + bankBalance));
                                                p.sendMessage(Config.getTransl("settings", "messages.info.money.withdrawn")
                                                        .replace("$value", String.valueOf(toDeposit)));
                                                mods.sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.bank.money.deposit")
                                                        .replace("$username", playerName)
                                                        .replace("$value", String.valueOf(toDeposit)));
                                            } else {
                                                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.deposit.not-enough-money"));
                                            }
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.deposit.limit-reached")
                                                    .replace("$depositLimit", String.valueOf(depositLimit)));
                                        }
                                    } else if (cType == ClickType.RIGHT) {
                                        double toDeposit = 1000.0;
                                        if (depositLimit != -1.0 && (toDeposit + bankBalance) <= depositLimit) {
                                            if (userBalance >= toDeposit) {
                                                Vault.getEcon().withdrawPlayer(p, toDeposit);
                                                data.getGuilds().updateGuildBalance(guildId, String.valueOf(toDeposit + bankBalance));
                                                p.sendMessage(Config.getTransl("settings", "messages.info.money.withdrawn")
                                                        .replace("$value", String.valueOf(toDeposit)));
                                                mods.sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.bank.money.deposit")
                                                        .replace("$username", playerName)
                                                        .replace("$value", String.valueOf(toDeposit)));
                                            } else {
                                                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.deposit.not-enough-money"));
                                            }
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.deposit.limit-reached")
                                                    .replace("$depositLimit", String.valueOf(depositLimit)));
                                        }
                                    } else if (cType == ClickType.MIDDLE) {
                                        if (mods.getWithdrawCooldown().contains(playerName)) {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.deposit.already-withdrawing"));
                                            return;
                                        }

                                        if (!mods.getDepositCooldown().contains(playerName)) {
                                            mods.getDepositCooldown().add(playerName);
                                            p.sendMessage(Config.getTransl("settings", "messages.info.guild.bank.depositCooldown.startMessage"));

                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    if (mods.getDepositCooldown().contains(playerName)) {
                                                        p.sendMessage(Config.getTransl("settings", "messages.info.general.time.expired"));
                                                        mods.getDepositCooldown().remove(playerName);
                                                    }
                                                }
                                            }.runTaskLater(plugin, 20 * 15);
                                        }
                                    }
                                }
                            }

                            if (actionType.equalsIgnoreCase("WITHDRAW")) {
                                if (Errors.noPermAction(p, conf.getSettings().getString("permissions.user-commands.guilds.bank.withdraw"))) {
                                    return;
                                } else {
                                    if (cType == ClickType.LEFT) {
                                        double toWithdraw = 100.0;
                                        if (toWithdraw <= bankBalance) {
                                            Vault.getEcon().depositPlayer(p, toWithdraw);
                                            data.getGuilds().updateGuildBalance(guildId, String.valueOf(bankBalance - toWithdraw));
                                            p.sendMessage(Config.getTransl("settings", "messages.info.money.deposit")
                                                    .replace("$value", String.valueOf(toWithdraw)));
                                            mods.sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.bank.money.taken")
                                                    .replace("$username", playerName)
                                                    .replace("$value", String.valueOf(toWithdraw)));
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.withdraw.bank-not-enough"));
                                        }
                                    } else if (cType == ClickType.RIGHT) {
                                        double toWithdraw = 1000.0;
                                        if (toWithdraw <= bankBalance) {
                                            Vault.getEcon().depositPlayer(p, toWithdraw);
                                            data.getGuilds().updateGuildBalance(guildId, String.valueOf(bankBalance - toWithdraw));
                                            p.sendMessage(Config.getTransl("settings", "messages.info.money.deposit")
                                                    .replace("$value", String.valueOf(toWithdraw)));
                                            mods.sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.bank.money.taken")
                                                    .replace("$username", playerName)
                                                    .replace("$value", String.valueOf(toWithdraw)));
                                        } else {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.withdraw.bank-not-enough"));
                                        }
                                    } else if (cType == ClickType.MIDDLE) {
                                        if (mods.getDepositCooldown().contains(playerName)) {
                                            p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.withdraw.already-depositing"));
                                            return;
                                        }

                                        if (!mods.getWithdrawCooldown().contains(playerName)) {
                                            mods.getWithdrawCooldown().add(playerName);
                                            p.sendMessage(Config.getTransl("settings", "messages.info.guild.bank.withdrawCooldown.startMessage"));

                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    if (mods.getWithdrawCooldown().contains(playerName)) {
                                                        p.sendMessage(Config.getTransl("settings", "messages.info.general.time.expired"));
                                                        mods.getWithdrawCooldown().remove(playerName);
                                                    }
                                                }
                                            }.runTaskLater(plugin, 20 * 15);
                                        }
                                    }
                                }
                            }
                        } else {
                            p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.dont-have"));
                        }
                    }
                }
            }
        }
    }
}
