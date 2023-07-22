package me.sirimperivm.spigot.assets.managers.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.other.Strings;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class PapiExpansions extends PlaceholderExpansion {
    private Main plugin;
    public PapiExpansions(Main plugin) {
        this.plugin = plugin;
    }

    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @Override
    public String getIdentifier() {
        return "smpc_";
    }

    @Override
    public String getAuthor() {
        return "SirImperivm_";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, String param) {
        String path = "placeholders.";
        String toReturn = "";

        if (param.equals(conf.getSettings().getString(path + "guilds.name"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                toReturn = data.getGuilds().getGuildName(mods.getGuildsData().get(playerName).get(0));
            } else {
                toReturn = "Nessuna";
            }
        }

        if (param.equals(conf.getSettings().getString(path + "guilds.id"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                toReturn = mods.getGuildsData().get(playerName).get(0);
            } else {
                toReturn = "N/A";
            }
        }

        if (param.equals(conf.getSettings().getString(path + "guilds.title"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                String guildName = data.getGuilds().getGuildName(mods.getGuildsData().get(playerName).get(0));
                toReturn = mods.getGuildTitle(guildName);
            }
        }

        if (param.equals(conf.getSettings().get(path + "guilds.bank.still"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                double value = data.getGuilds().getGuildBalance(mods.getGuildsData().get(playerName).get(0));
                toReturn = String.valueOf(value);
            }
        }

        if (param.equals(conf.getSettings().get(path + "guilds.bank.formatted"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                double value = data.getGuilds().getGuildBalance(mods.getGuildsData().get(playerName).get(0));
                toReturn = Strings.formatNumber(value);
            }
        }

        if (param.equals(conf.getSettings().get(path + "top.guild-bank.guild_name"))) {
            char lastChar = param.charAt(param.length() -1);
            if (!(lastChar >= '0' && lastChar <= '9')) {
                toReturn = "Errore";
            } else {
                int position = Integer.parseInt(String.valueOf(lastChar));
                position =- 1;
                if (position > 9) {
                    position = 9;
                }
                HashMap<String, Double> balanceTop = mods.getGuildsBalanceTop();
                List<String> list = new ArrayList<String>();
                for (String key : balanceTop.keySet()) {
                    list.add(key + ";" + String.valueOf(balanceTop.get(key)));
                }
                String[] splitter = list.get(position).split(";");
                String guildId = splitter[0];
                double value = Double.parseDouble(splitter[1]);
                toReturn = data.getGuilds().getGuildName(guildId);
            }
        }

        if (param.equals(conf.getSettings().get(path + "top.guild-bank.guild_title"))) {
            char lastChar = param.charAt(param.length() -1);
            if (!(lastChar >= '0' && lastChar <= '9')) {
                toReturn = "Errore";
            } else {
                int position = Integer.parseInt(String.valueOf(lastChar));
                position =- 1;
                if (position > 9) {
                    position = 9;
                }
                HashMap<String, Double> balanceTop = mods.getGuildsBalanceTop();
                List<String> list = new ArrayList<String>();
                for (String key : balanceTop.keySet()) {
                    list.add(key + ";" + String.valueOf(balanceTop.get(key)));
                }
                String[] splitter = list.get(position).split(";");
                String guildId = splitter[0];
                double value = Double.parseDouble(splitter[1]);
                String guildName = data.getGuilds().getGuildName(guildId);
                toReturn = Config.getTransl("guilds", "guilds." + guildName + ".guildTitle");
            }
        }

        if (param.equals(conf.getSettings().get(path + "top.guild-bank.guild_value"))) {
            char lastChar = param.charAt(param.length() -1);
            if (!(lastChar >= '0' && lastChar <= '9')) {
                toReturn = "Errore";
            } else {
                int position = Integer.parseInt(String.valueOf(lastChar));
                position =- 1;
                if (position > 9) {
                    position = 9;
                }
                HashMap<String, Double> balanceTop = mods.getGuildsBalanceTop();
                List<String> list = new ArrayList<String>();
                for (String key : balanceTop.keySet()) {
                    list.add(key + ";" + String.valueOf(balanceTop.get(key)));
                }
                String[] splitter = list.get(position).split(";");
                String guildId = splitter[0];
                double value = Double.parseDouble(splitter[1]);
                toReturn = String.valueOf(value);
            }
        }

        if (param.equals(conf.getSettings().get(path + "top.guild-bank.guild_value_formatted"))) {
            char lastChar = param.charAt(param.length() -1);
            if (!(lastChar >= '0' && lastChar <= '9')) {
                toReturn = "Errore";
            } else {
                int position = Integer.parseInt(String.valueOf(lastChar));
                position =- 1;
                if (position > 9) {
                    position = 9;
                }
                HashMap<String, Double> balanceTop = mods.getGuildsBalanceTop();
                List<String> list = new ArrayList<String>();
                for (String key : balanceTop.keySet()) {
                    list.add(key + ";" + String.valueOf(balanceTop.get(key)));
                }
                String[] splitter = list.get(position).split(";");
                String guildId = splitter[0];
                double value = Double.parseDouble(splitter[1]);
                toReturn = Strings.formatNumber(value);
            }
        }

        return toReturn;
    }
}
