package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Gui;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import me.sirimperivm.spigot.assets.other.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class ChatListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();
        String message = e.getMessage();
        HashMap<String, List<String>> guildsData = mods.getGuildsData();
        String guildId = guildsData.get(playerName).get(0);
        Gui g = new Gui();

        boolean containsChars = false;
        if (mods.getDepositCooldown().contains(playerName)) {
            e.setCancelled(true);
            for (char c : message.toCharArray()) {
                if (!(c >= '0' && c <= '9')) {
                    containsChars = true;
                    break;
                }
            }

            if (!containsChars) {
                String guildName = data.getGuilds().getGuildName(guildId);
                double toDeposit = Double.parseDouble(message);
                double depositLimit = conf.getGuilds().getDouble("guilds." + guildName + ".bank.limit");
                double bankBalance = data.getGuilds().getGuildBalance(guildId);
                double userBalance = Vault.getEcon().getBalance(p);

                if (depositLimit == -1.0 || (toDeposit + bankBalance) <= depositLimit) {
                    if (userBalance >= toDeposit) {
                        Vault.getEcon().withdrawPlayer(p, toDeposit);
                        data.getGuilds().updateGuildBalance(guildId, String.valueOf(toDeposit + bankBalance));
                        p.sendMessage(Config.getTransl("settings", "messages.info.money.withdrawn")
                                .replace("$value", Strings.formatNumber(toDeposit)));
                        mods.sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.guild.bank.money.deposit")
                                .replace("$username", playerName)
                                .replace("$value", Strings.formatNumber(toDeposit)));
                    } else {
                        p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.deposit.not-enough-money"));
                    }
                } else {
                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.deposit.limit-reached")
                            .replace("$depositLimit", Strings.formatNumber(depositLimit)));
                }
            } else {
                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.chars-not-allowed"));
                p.openInventory(g.bankGui(p, guildId));
            }
            mods.getDepositCooldown().remove(playerName);
            return;
        }
        if (mods.getWithdrawCooldown().contains(playerName)) {
            e.setCancelled(true);
            for (char c : message.toCharArray()) {
                if (!(c >= '0' && c <= '9')) {
                    containsChars = true;
                    break;
                }
            }

            if (!containsChars) {
                String guildName = data.getGuilds().getGuildName(guildId);
                double toWithdraw = Double.parseDouble(message);
                double bankBalance = data.getGuilds().getGuildBalance(guildId);

                if (toWithdraw <= bankBalance) {
                    Vault.getEcon().depositPlayer(p, toWithdraw);
                    data.getGuilds().updateGuildBalance(guildId, String.valueOf(bankBalance - toWithdraw));
                    p.sendMessage(Config.getTransl("settings", "messages.info.money.deposit")
                            .replace("$value", String.valueOf(toWithdraw)));
                    mods.sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.guild.bank.money.taken")
                            .replace("$username", playerName)
                            .replace("$value", Strings.formatNumber(toWithdraw)));
                } else {
                    p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.withdraw.bank-not-enough"));
                }
            } else {
                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.chars-not-allowed"));
                p.openInventory(g.bankGui(p, guildId));
            }
            mods.getWithdrawCooldown().remove(playerName);
            return;
        }

        if (mods.getGuildsChat().containsKey(playerName)) {
            e.setCancelled(true);
            guildId = mods.getGuildsChat().get(playerName);
            for (Player all : Bukkit.getOnlinePlayers()) {
                String allName = all.getName();
                String allGuildId = mods.getGuildsChat().get(allName);
                if (allGuildId.equals(guildId)) {
                    all.sendMessage(Config.getTransl("settings", "messages.others.guilds.chat")
                            .replace("$playerName", playerName)
                            .replace("$message", message)
                    );
                }
            }
        }
    }
}
