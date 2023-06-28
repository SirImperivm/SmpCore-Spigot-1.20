package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.other.Strings;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class Modules {

    private static List<String> generatedGuildsId;
    private static List<String> generatedOwnersId;
    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();

    public Modules() {
        generatedGuildsId = data.getGuilds().getGeneratedGuildsID();
        generatedOwnersId = new ArrayList<>();
    }

    public void createGuild(Player p, String guildName, String guildTitle, int membersLimit) {
        String guildId = Strings.getRandomString(1);
        while (generatedGuildsId.contains(guildId)) {
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
        conf.getGuilds().set(confPath + guildId + ".settings.addOwner.command1.string", "luckperms:lp user %username% parent add caporosso");
        conf.getGuilds().set(confPath + guildId + ".settings.addOfficer.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.addOfficer.command1.string", "luckperms:lp user %username% parent add officerrosso");
        conf.getGuilds().set(confPath + guildId + ".settings.addMember.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.addMember.command1.string", "luckperms:lp user %username% parent add rosso");
        conf.getGuilds().set(confPath + guildId + ".settings.remOwner.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.remOwner.command1.string", "luckperms:lp user %username% parent remove caporosso");
        conf.getGuilds().set(confPath + guildId + ".settings.remOfficer.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.remOfficer.command1.string", "luckperms:lp user %username% parent remove officerrosso");
        conf.getGuilds().set(confPath + guildId + ".settings.remMember.command1.type", "command");
        conf.getGuilds().set(confPath + guildId + ".settings.remMember.command1.string", "luckperms:lp user %username% parent remove rosso");
        conf.getGuilds().set(confPath + guildId + ".bank.limit", 5000.0);
        conf.save(conf.getGuilds(), conf.getGuildsFile());
    }

    public static List<String> getGeneratedGuildsId() {
        return generatedGuildsId;
    }

    public static List<String> getGeneratedOwnersId() {
        return generatedOwnersId;
    }
}
