package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import me.sirimperivm.spigot.assets.other.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class Modules {

    private static List<String> generatedGuilds;
    private static List<String> generatedMembers;
    private static List<String> guildMembers;
    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Vault vault = Main.getVault();

    public Modules() {
        generatedGuilds = data.getGuilds().getGeneratedGuildsID();
        generatedMembers = data.getGuildMembers().getGeneratedMembersID();
        guildMembers = new ArrayList<>();
    }

    public void createGuild(Player p, String guildName, String guildTitle, int membersLimit) {
        String guildId = Strings.generateUuid();
        for (String generated : generatedGuilds) {
            String[] partGenerated = generated.split(";");
            if (partGenerated[0].equals(guildId)) {
                guildId = Strings.generateUuid();
            }
        }
        generatedGuilds.add(guildId + ";" + guildName);

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

        conf.getGuilds().set(confPath + guildName + ".guildId", guildId);
        conf.getGuilds().set(confPath + guildName + ".guildTitle", guildTitle);
        conf.getGuilds().set(confPath + guildName + ".membersLimit", membersLimit);
        conf.getGuilds().set(confPath + guildName + ".mainHome.worldName", locWorld);
        conf.getGuilds().set(confPath + guildName + ".mainHome.posX", locX);
        conf.getGuilds().set(confPath + guildName + ".mainHome.posY", locY);
        conf.getGuilds().set(confPath + guildName + ".mainHome.posZ", locZ);
        conf.getGuilds().set(confPath + guildName + ".mainHome.rotYaw", locYaw);
        conf.getGuilds().set(confPath + guildName + ".mainHome.rotPitch", locPitch);
        conf.getGuilds().set(confPath + guildName + ".settings.addOwner.command1.type", "command");
        conf.getGuilds().set(confPath + guildName + ".settings.addOwner.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".settings.addOfficer.command1.type", "command");
        conf.getGuilds().set(confPath + guildName + ".settings.addOfficer.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".settings.addMember.command1.type", "command");
        conf.getGuilds().set(confPath + guildName + ".settings.addMember.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".settings.remOwner.command1.type", "command");
        conf.getGuilds().set(confPath + guildName + ".settings.remOwner.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".settings.remOfficer.command1.type", "command");
        conf.getGuilds().set(confPath + guildName + ".settings.remOfficer.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".settings.remMember.command1.type", "command");
        conf.getGuilds().set(confPath + guildName + ".settings.remMember.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".bank.limit", conf.getSettings().getDouble("settings.guilds.bank.defaultBankLimit"));
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

    public void deleteGuild(Player p, String guildName) {
        String guildId = data.getGuilds().getGuildId(guildName);
        data.getGuilds().deleteGuildData(guildId);

        guildMembers = data.getGuildMembers().getGuildMembers();
        for (String members : guildMembers) {
            String[] splitter = members.split(";");
            String gName = splitter[1];
            String username = splitter[0];
            if (gName.equalsIgnoreCase(guildName)) {
                Player spawnedPlayer = Bukkit.getPlayer(username);
                if (spawnedPlayer != null) {
                    sendPlayerToLobby(spawnedPlayer);
                    spawnedPlayer.sendMessage(Config.getTransl("settings", "messages.info.guild.deleted.sendToPlayer"));
                }
            }
        }

        for (String key : conf.getGuilds().getConfigurationSection("guilds").getKeys(false)) {
            if (key.equals(guildName)) {
                conf.getGuilds().set("guilds." + key, null);
            }
        }
        conf.save(conf.getGuilds(), conf.getGuildsFile());

        generatedGuilds = data.getGuilds().getGeneratedGuildsID();
        p.sendMessage(Config.getTransl("settings", "messages.success.guilds.deleted")
                .replace("$guildName", guildName));
    }

    public void setLobby(Player p) {
        String settingsPath = "settings.lobby.location";

        String worldName = p.getLocation().getWorld().getName();
        double posX = p.getLocation().getX();
        double posY = p.getLocation().getY();
        double posZ = p.getLocation().getZ();
        float rotYaw = p.getLocation().getYaw();
        float rotPitch = p.getLocation().getPitch();

        conf.getSettings().set(settingsPath + ".world", worldName);
        conf.getSettings().set(settingsPath + ".posX", posX);
        conf.getSettings().set(settingsPath + ".posY", posY);
        conf.getSettings().set(settingsPath + ".posZ", posZ);
        conf.getSettings().set(settingsPath + ".rotYaw", rotYaw);
        conf.getSettings().set(settingsPath + ".rotPitch", rotPitch);
        conf.save(conf.getSettings(), conf.getSettingsFile());
        p.sendMessage(Config.getTransl("settings", "messages.success.lobby.located"));
    }

    public void createLeader(Player p, String guildId) {
        String username = p.getName();
        String memberId = Strings.generateUuid();
        for (String generated : generatedMembers) {
            String[] partGenerated = generated.split(";");
            if (partGenerated[0].equals(memberId)) {
                memberId = Strings.generateUuid();
            }
        }
        generatedMembers.add(memberId + ";" + username);

        data.getGuildMembers().insertMemberData(username, memberId, guildId, "leader");
        String guildName = data.getGuilds().getGuildName(guildId);

        for (String setting : conf.getGuilds().getConfigurationSection("guilds." + guildName + ".settings.addOwner").getKeys(false)) {
            String settingsPath = "guilds." + guildName + ".settings.addOwner." + setting;
            String settingType = conf.getGuilds().getString(settingsPath + ".type");
            if (settingType.equalsIgnoreCase("command")) {
                String command = conf.getGuilds().getString(settingsPath + ".string");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                        .replace("%username%", p.getName())
                );
            }
        }

        String locationPath = "guilds." + guildName + ".mainHome";
        World homeWorld = Bukkit.getWorld(conf.getGuilds().getString(locationPath + ".worldName"));
        double posX = conf.getGuilds().getDouble(locationPath + ".posX");
        double posY = conf.getGuilds().getDouble(locationPath + ".posY");
        double posZ = conf.getGuilds().getDouble(locationPath + ".posZ");
        float rotYaw = conf.getGuilds().getInt(locationPath + ".rotYaw");
        float rotPitch = conf.getGuilds().getInt(locationPath + ".rotPitch");
        Location home = new Location(homeWorld, posX, posY, posZ, rotYaw, rotPitch);

        p.teleport(home);
    }

    public void sendPlayerToLobby(Player p) {
        World locWorld = Bukkit.getWorld(conf.getSettings().getString("settings.lobby.location.world"));
        double locX = conf.getSettings().getDouble("settings.lobby.location.posX");
        double locY = conf.getSettings().getDouble("settings.lobby.location.posY");
        double locZ = conf.getSettings().getDouble("settings.lobby.location.posZ");
        float rotYaw = conf.getSettings().getInt("settings.lobby.locations.rotYaw");
        float rotPitch = conf.getSettings().getInt("settings.lobby.locations.rotPitch");

        Location spawn = new Location(locWorld, locX, locY, locZ, rotYaw, rotPitch);
        p.teleport(spawn);
    }

    public boolean isLobbyLocated() {
        return !conf.getSettings().getString("settings.lobby.location.world").equalsIgnoreCase("null");
    }

    public double getUserBalance(Player p) {
        return vault.getEcon().getBalance(p);
    }
    public void takeMoney(Player p, double value) {
        vault.getEcon().withdrawPlayer(p, value);
    }
    public void addMoney(Player p, double value) {
        vault.getEcon().depositPlayer(p, value);
    }

    public static List<String> getGeneratedGuilds() {
        return generatedGuilds;
    }

    public static List<String> getGeneratedMembers() {
        return generatedMembers;
    }

    public static List<String> getGuildMembers() {
        return guildMembers;
    }
}
