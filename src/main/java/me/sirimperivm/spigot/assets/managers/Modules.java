package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.other.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class Modules {

    private static List<String> generatedGuilds;
    private static List<String> generatedOwners;
    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();

    public Modules() {
        generatedGuilds = data.getGuilds().getGeneratedGuildsID();
        generatedOwners = new ArrayList<>();
    }

    public void createGuild(Player p, String guildName, String guildTitle, int membersLimit) {
        String guildId = Strings.getRandomString(1);
        while (generatedGuilds.contains(guildId)) {
            guildId = Strings.getRandomString(1);
        }

        String confPath = "guilds.";
        Location loc = p.getLocation();
        String locWorld = loc.getWorld().getName();
        double locX = loc.getX();
        double locY = loc.getY();
        double locZ = loc.getZ();
        float locYaw = loc.getYaw();
        float locPitch = loc.getPitch();

        String startingBalance = String.valueOf(conf.getSettings().getDouble("settings.guilds.bank.startingBalance"));

        data.getGuilds().insertGuildData(guildName, guildId);
        data.getGuilds().updateGuildBalance(guildId, startingBalance);

        conf.getGuilds().set(confPath + guildId + ".guildName", guildName);
        conf.getGuilds().set(confPath + guildId + ".guildTitle", guildTitle);
        conf.getGuilds().set(confPath + guildId + ".membersLimit", membersLimit);
        conf.getGuilds().set(confPath + guildId + ".mainHome.worldName", locWorld);
        conf.getGuilds().set(confPath + guildId + ".mainHome.posX", locX);
        conf.getGuilds().set(confPath + guildId + ".mainHome.posY", locY);
        conf.getGuilds().set(confPath + guildId + ".mainHome.posZ", locZ);
        conf.getGuilds().set(confPath + guildId + ".mainHome.rotYaw", locYaw);
        conf.getGuilds().set(confPath + guildId + ".mainHome.rotPitch", locPitch);
        conf.getGuilds().set(confPath + guildId + ".settings.addOwner.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.addOwner.command1.string", "");
        conf.getGuilds().set(confPath + guildId + ".settings.addOfficer.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.addOfficer.command1.string", "");
        conf.getGuilds().set(confPath + guildId + ".settings.addMember.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.addMember.command1.string", "");
        conf.getGuilds().set(confPath + guildId + ".settings.remOwner.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.remOwner.command1.string", "");
        conf.getGuilds().set(confPath + guildId + ".settings.remOfficer.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.remOfficer.command1.string", "");
        conf.getGuilds().set(confPath + guildId + ".settings.remMember.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.remMember.command1.string", "");
        conf.getGuilds().set(confPath + guildId + ".bank.limit", conf.getSettings().getDouble("settings.guilds.bank.defaultBankLimit"));
        conf.save(conf.getGuilds(), conf.getGuildsFile());

        for (String send : conf.getSettings().getStringList("messages.success.guilds.created")) {
            p.sendMessage(Colors.text(send
                    .replace("$guildId", guildId)
                    .replace("$guildName", guildName)
                    .replace("$guildTitle", Colors.text(guildTitle))
                    .replace("$position", Config.getTransl("settings", "messages.success.guilds.positionFormat")
                            .replace("$worldName", locWorld)
                            .replace("$posX", String.valueOf(locX))
                            .replace("$posY", String.valueOf(locY))
                            .replace("$posZ", String.valueOf(locZ))
                    )
                    .replace("$membersLimit", String.valueOf(membersLimit))
                    .replace("$bankLimit", String.valueOf(conf.getSettings().getDouble("settings.guilds.bank.defaultBankLimit")))
            ));
        }
    }

    public static List<String> getGeneratedGuilds() {
        return generatedGuilds;
    }

    public static List<String> getGeneratedOwners() {
        return generatedOwners;
    }
}
