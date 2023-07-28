package me.sirimperivm.spigot.assets.managers.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.other.Strings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("all")
public class PapiExpansions extends PlaceholderExpansion {
    private final Main plugin;
    public PapiExpansions(Main plugin) {
        this.plugin = plugin;
    }

    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @Override
    public String getIdentifier() {
        return "smpc";
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

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "guilds.name"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                toReturn = data.getGuilds().getGuildName(mods.getGuildsData().get(playerName).get(0));
            } else {
                toReturn = "Nessuna";
            }
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "guilds.id"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                toReturn = mods.getGuildsData().get(playerName).get(0);
            } else {
                toReturn = "N/A";
            }
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "guilds.title"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                String guildName = data.getGuilds().getGuildName(mods.getGuildsData().get(playerName).get(0));
                toReturn = mods.getGuildTitle(guildName);
            }
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "guilds.bank.still"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                double value = data.getGuilds().getGuildBalance(mods.getGuildsData().get(playerName).get(0));
                toReturn = String.valueOf(value);
            }
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "guilds.bank.formatted"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                double value = data.getGuilds().getGuildBalance(mods.getGuildsData().get(playerName).get(0));
                toReturn = Strings.formatNumber(value);
            }
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "guilds.roles.guilders-count"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                String guildId = mods.getGuildsData().get(playerName).get(0);
                int value = data.getGuildMembers().getGuildersCount(guildId);
                toReturn = String.valueOf(value);
            }
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "guilds.roles.officers-count"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                String guildId = mods.getGuildsData().get(playerName).get(0);
                int value = data.getGuildMembers().getOfficersCount(guildId);
                toReturn = String.valueOf(value);
            }
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "guilds.members.count"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                String guildId = mods.getGuildsData().get(playerName).get(0);
                int value = data.getGuildMembers().getMembersCount(guildId);
                toReturn = String.valueOf(value);
            }
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "guilds.members.limit"))) {
            String playerName = p.getName();
            if (mods.getGuildsData().containsKey(playerName)) {
                String guildId = mods.getGuildsData().get(playerName).get(0);
                int value = mods.getMembersLimit(guildId);
                toReturn = String.valueOf(value) == "-1" ? "∞" : String.valueOf(value);
            }
        }

        if (param.startsWith("top_guild-bank_guild_name_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopBankList();
            String[] splitter = list.get(topPosition).split("£");

            toReturn = splitter[0];
        }

        if (param.startsWith("top_guild-bank_guild_title_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopBankList();
            String[] splitter = list.get(topPosition).split("£");

            String guildName = data.getGuilds().getGuildName(splitter[0]);
            toReturn = Config.getTransl("guilds", "guilds." + guildName + ".guildTitle");
        }

        if (param.startsWith("top_guild-bank_guild_value_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopBankList();
            String[] splitter = list.get(topPosition).split("£");
            String guildId = splitter[0];
            double value = Double.parseDouble(splitter[1]);

            toReturn = String.valueOf(value);
        }

        if (param.startsWith("top_guild-bank_guild_value-formatted_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopBankList();
            String[] splitter = list.get(topPosition).split("£");
            String guildId = splitter[0];
            double value = Double.parseDouble(splitter[1]);

            toReturn = Strings.formatNumber(value);
        }



        if (param.startsWith("top_guild-members_guild_name_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopMembersList();
            String[] splitter = list.get(topPosition).split("£");

            toReturn = splitter[0];
        }

        if (param.startsWith("top_guild-members_guild_title_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopMembersList();
            String[] splitter = list.get(topPosition).split("£");

            String guildName = data.getGuilds().getGuildName(splitter[0]);
            toReturn = Config.getTransl("guilds", "guilds." + guildName + ".guildTitle");
        }

        if (param.startsWith("top_guild-members_guild_value_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopMembersList();
            String[] splitter = list.get(topPosition).split("£");
            String guildId = splitter[0];
            int value = Integer.parseInt(splitter[1]);

            toReturn = String.valueOf(value);
        }

        if (param.startsWith("top_kills-member_name_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopKillsList();
            String[] splitter = list.get(topPosition).split("£");

            toReturn = splitter[0];
        }

        if (param.startsWith("top_kills-member_count_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopKillsList();
            String[] splitter = list.get(topPosition).split("£");

            toReturn = splitter[1];
        }

        if (param.startsWith("top_deaths-member_name_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopDeathsList();
            String[] splitter = list.get(topPosition).split("£");

            toReturn = splitter[0];
        }

        if (param.startsWith("top_deaths-member_count_")) {
            String parameter = param;
            int stringPosition = 0;
            do {
                stringPosition++;
            } while (parameter.charAt(stringPosition) != '#');

            stringPosition++;
            char charTopPosition = parameter.charAt(stringPosition);
            int topPosition = Integer.parseInt(String.valueOf(charTopPosition));

            topPosition -= 1;
            topPosition = topPosition > 9 ? 9 : topPosition;

            List<String> list = mods.getTopDeathsList();
            String[] splitter = list.get(topPosition).split("£");

            toReturn = splitter[1];
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "lives.count"))) {
            String getPlayerLives = String.valueOf(data.getLives().getPlayerLives((Player) p));
            toReturn = getPlayerLives;
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + ".lives.is_dead"))) {
            boolean isDead = data.getLives().isDead((Player) p);
            if (isDead) {
                toReturn = Config.getTransl("settings", "messages.others.deaths.placeholderFormats.dead");
            } else {
                toReturn = Config.getTransl("settings", "messages.others.deaths.placeholderFormats.live");
            }
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + ".deathsManager.deaths_count"))) {
            int deaths = data.getStats().getPlayerData((Player) p, "deaths");
            toReturn = String.valueOf(deaths);
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + ".deathsManager.deaths_count-formatted"))) {
            Double deaths = Double.valueOf(data.getStats().getPlayerData((Player) p, "deaths"));
            toReturn = Strings.formatNumber(deaths);
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + ".deathsManager.kills_count"))) {
            int kills = data.getStats().getPlayerData((Player) p, "kills");
            toReturn = String.valueOf(kills);
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + ".deathsManager.kills_count-formatted"))) {
            Double kills = Double.valueOf(data.getStats().getPlayerData((Player) p, "kills"));
            toReturn = Strings.formatNumber(kills);
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + ".bounties.value"))) {
            double bountyValue = data.getBounties().getBounty(p.getName());
            if (bountyValue > 0) {
                toReturn = Config.getTransl("settings", "messages.others.bounties.placeholderFormats.bounty")
                        .replace("$bountyValue", String.valueOf(bountyValue));
            }
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + ".bounties.value-formatted"))) {
            double bountyValue = data.getBounties().getBounty(p.getName());
            if (bountyValue > 0) {
                toReturn = Config.getTransl("settings", "messages.others.bounties.placeholderFormats.bounty")
                        .replace("$bountyValue", Strings.formatNumber(bountyValue));
            }
        }

        return toReturn;
    }
}
