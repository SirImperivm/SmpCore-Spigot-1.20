package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Gui;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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

        if (mods.getWithdrawCooldown().contains(playerName) || mods.getDepositCooldown().contains(playerName)) {
            List<String> guildAndRole = mods.getGuildsData().get(playerName);
            String guildId = guildAndRole.get(0);
            Gui gm = new Gui();

            boolean containsCharacters = false;
            for (char c : message.toCharArray()) {
                if (!(c >= '0' && c <= '9')) {
                    containsCharacters = true;
                    break;
                }
            }

            if (!containsCharacters) {
                double value = Double.parseDouble(message);
                if (mods.getDepositCooldown().contains(playerName)) {
                    double toDeposit = value;
                    double bankBalance = data.getGuilds().getGuildBalance(guildId);
                    double userBalance = Main.getVault().getEcon().getBalance(p);

                    String guildName = data.getGuilds().getGuildName(guildId);
                    double depositLimit = conf.getGuilds().getDouble("guilds." + guildName + ".bank.limit");

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
                    }
                }

                if (mods.getWithdrawCooldown().contains(playerName)) {
                    double toWithdraw = value;
                    double bankBalance = data.getGuilds().getGuildBalance(guildId);
                    double userBalance = Main.getVault().getEcon().getBalance(p);

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
                }
            } else {
                p.sendMessage(Config.getTransl("settings", "messages.errors.guilds.bank.chars-not-allowed"));
                p.openInventory(gm.bankGui(guildId));
            }
            return;
        }

        if (mods.getGuildsChat().containsKey(playerName)) {
            e.setCancelled(true);
            String guildId = mods.getGuildsChat().get(playerName);
            for (Player all : Bukkit.getOnlinePlayers()) {
                String allName = all.getName();
                if (mods.getGuildsChat().containsKey(allName)) {
                    String allGuildId = mods.getGuildsChat().get(allName);
                    if (allGuildId.equals(guildId)) {
                        all.sendMessage(Config.getTransl("settings", "messages.others.guilds.chat")
                                .replace("$playerName", playerName)
                                .replace("$message", message));
                    }
                }
            }
        }
    }
}
