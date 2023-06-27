package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.other.Strings;
import org.bukkit.Location;

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

    public void createGuild(Location loc, String guildName, int membersLimit) {
        String guildId = Strings.getRandomString(1);
        while (generatedGuildsId.contains(guildId)) {
            guildId = Strings.getRandomString(1);
        }

        String confPath = "guilds";
        String locWorld = loc.getWorld().getName();
        double locX = loc.getX();
        double locY = loc.getY();
        double locZ = loc.getZ();
        float locYaw = loc.getYaw();
        float locPitch = loc.getPitch();


    }

    public static List<String> getGeneratedGuildsId() {
        return generatedGuildsId;
    }

    public static List<String> getGeneratedOwnersId() {
        return generatedOwnersId;
    }
}
