package me.sirimperivm.spigot.assets.managers;

import io.th0rgal.oraxen.api.OraxenItems;
import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import me.sirimperivm.spigot.assets.other.General;
import me.sirimperivm.spigot.assets.other.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.other.Enchants;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Modules {

    private static List<String> generatedGuilds;
    private static List<String> generatedMembers;
    private static List<String> guildMembers;
    private static HashMap<String, String> invites;
    private static HashMap<String, List<String>> guildsData;
    private static Main plugin = Main.getPlugin();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Vault vault = Main.getVault();

    public Modules() {
        invites = new HashMap<String, String>();
        guildMembers = new ArrayList<>();
        refreshSettings();
        executeTasksLoop();
    }

    void refreshSettings() {
        generatedGuilds = data.getGuilds().getGeneratedGuildsID();
        generatedMembers = data.getGuildMembers().getGeneratedMembersID();
        guildsData = data.getGuildMembers().guildsData();
    }

    public void deleteTask(int taskId) {
        data.getTasks().deleteTask(taskId);
    }

    public void executeTasksLoop() {
        BukkitScheduler schedule = Bukkit.getScheduler();
        schedule.runTaskTimer(plugin, () -> {
            String query = "SELECT * FROM " + data.getTasks().database;

            try {
                PreparedStatement state = data.conn.prepareStatement(query);
                ResultSet rs = state.executeQuery();
                while (rs.next()) {
                    int taskId = rs.getInt("taskId");
                    String taskType = rs.getString("taskType");
                    String taskValue = rs.getString("taskValue");
                    if (taskType.equalsIgnoreCase("expelGuildMember")) {
                        Player target = Bukkit.getPlayerExact(taskValue);
                        if (target != null) {
                            removeMember(target);
                            deleteTask(taskId);
                        }
                    }
                    if (taskType.equalsIgnoreCase("sendGuildersBroadcast")) {
                        String[] splitter = taskValue.split("£");
                        String username = splitter[0];
                        String message = splitter[1];
                        Player target = Bukkit.getPlayerExact(username);
                        if (target != null) {
                            target.sendMessage(Colors.text(message));
                            deleteTask(taskId);
                        }
                    }
                    if (taskType.equalsIgnoreCase("sendUserMessage")) {
                        String[] splitter = taskValue.split("£");
                        String username = splitter[0];
                        String message = splitter[1];
                        Player target = Bukkit.getPlayerExact(username);
                        if (target != null) {
                            target.sendMessage(Colors.text(message));
                            deleteTask(taskId);
                        }
                    }
                    if (taskType.equalsIgnoreCase("setOfficer")) {
                        Player target = Bukkit.getPlayerExact(taskValue);
                        if (target != null) {
                            setOfficer(target);
                            deleteTask(taskId);
                        }
                    }
                    if (taskType.equalsIgnoreCase("removeOfficer")) {
                        Player target = Bukkit.getPlayerExact(taskValue);
                        if (target != null) {
                            removeOfficer(target);
                            deleteTask(taskId);
                        }
                    }
                }
            } catch (SQLException e) {
                log.severe("Impossibile eseguire una task!");
                e.printStackTrace();
            }
        }, 20L, 20L);

       /* List<Integer> taskList = data.getTasks().returnAllTasks();
        BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTaskTimer(plugin, () -> {
            data.getTasks().executeTasks();
            refreshSettings();
        }, 20L, 20L); */
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
        conf.getGuilds().set(confPath + guildName + ".settings.addLeader.command1.type", "sendCommand");
        conf.getGuilds().set(confPath + guildName + ".settings.addLeader.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".settings.addOfficer.command1.type", "sendCommand");
        conf.getGuilds().set(confPath + guildName + ".settings.addOfficer.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".settings.addMember.command1.type", "sendCommand");
        conf.getGuilds().set(confPath + guildName + ".settings.addMember.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".settings.remLeader.command1.type", "sendCommand");
        conf.getGuilds().set(confPath + guildName + ".settings.remLeader.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".settings.remOfficer.command1.type", "sendCommand");
        conf.getGuilds().set(confPath + guildName + ".settings.remOfficer.command1.string", "");
        conf.getGuilds().set(confPath + guildName + ".settings.remMember.command1.type", "sendCommand");
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
            String gId = splitter[1];
            String username = splitter[0];
            if (gId.equalsIgnoreCase(guildId)) {
                data.getTasks().insertTask("expelGuildMember", username);
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

    public void inviteMember(Player target, String guildName) {
        String username = target.getName();
        target.sendMessage(Config.getTransl("settings", "messages.info.guild.members.invited.you")
                .replace("$guildName", guildName));
        sendGuildersBroadcast(data.getGuilds().getGuildId(guildName), Config.getTransl("settings", "messages.info.guild.members.invited.members")
                .replace("$playerName", username));
        invites.put(username, guildName);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (invites.containsKey(username)) {
                    target.sendMessage(Config.getTransl("settings", "messages.info.general.time.expired"));
                    invites.remove(username);
                }
            }
        }.runTaskLater(plugin, 20 * 15);
    }

    public void insertMember(Player p, String guildName, String guildRole) {
        String memberId = Strings.generateUuid();
        for (String generated : generatedMembers) {
            String[] splitter = generated.split(";");
            if (splitter[1].equalsIgnoreCase(memberId)) {
                memberId = Strings.generateUuid();
            }
        }

        String username = p.getName();
        String guildId = data.getGuilds().getGuildId(guildName);

        List<String> guildAndRole = new ArrayList<>();
        guildAndRole.add(guildId);
        guildAndRole.add(guildRole);

        if (guildsData.containsKey(username)) {
            guildsData.replace(username, guildAndRole);
        } else {
            guildsData.put(username, guildAndRole);
        }

        data.getGuildMembers().insertMemberData(username, memberId, guildId, guildRole);
        setters(p, guildId, "addMember");
        sendPlayerToGhome(p, guildName);
    }

    public void removeMember(Player p) {
        String playerName = p.getName();
        if (guildsData.containsKey(playerName)) {
            List<String> guildsAndRole = guildsData.get(playerName);
            String guildId = guildsAndRole.get(0);
            String guildRole = guildsAndRole.get(1);
            String setterType = null;
            switch (guildRole) {
                case "leader":
                    setterType = "remLeader";
                    break;
                case "officer":
                    setterType = "remOfficer";
                    break;
                default:
                    setterType = "remMember";
                    break;
            }

            data.getGuildMembers().removeMemberData(playerName);
            guildsData = data.getGuildMembers().guildsData();
            setters(p, guildId, setterType);
            sendPlayerToLobby(p);
            sendGuildersBroadcast(guildId, conf.getSettings().getString("messages.info.guild.members.kicked-others")
                    .replace("%sp", plugin.getSuccessPrefix())
                    .replace("%ip", plugin.getInfoPrefix())
                    .replace("%fp", plugin.getFailPrefix())
                    .replace("$player", playerName)
            );
            p.sendMessage(Config.getTransl("settings", "messages.info.guild.members.kicked"));
        }
    }

    public void setOfficer(Player p) {
        String playerName = p.getName();
        List<String> guildAndRole = guildsData.get(playerName);
        String guildId = guildAndRole.get(0);

        data.getGuildMembers().updateMemberData(playerName, "guildRole", "officer");
        guildsData = data.getGuildMembers().guildsData();
        setters(p, guildId, "remMember");
        setters(p, guildId, "addOfficer");
        p.sendMessage(Config.getTransl("settings", "messages.info.guild.members.officer.set"));
    }

    public void removeOfficer(Player p) {
        String playerName = p.getName();
        List<String> guildAndRole = guildsData.get(playerName);
        String guildId = guildAndRole.get(0);

        data.getGuildMembers().updateMemberData(playerName, "guildRole", "member");
        guildsData = data.getGuildMembers().guildsData();
        setters(p, guildId, "remOfficer");
        setters(p, guildId, "addMember");
        p.sendMessage(Config.getTransl("settings", "messages.info.guild.members.officer.remove"));
    }
    public void setLeadership(String guildId, Player p) {

    }

    public void leaveMember(Player p) {
        String playerName = p.getName();
        if (guildsData.containsKey(playerName)) {
            List<String> guildsAndRole = guildsData.get(playerName);
            String guildId = guildsAndRole.get(0);
            String guildRole = guildsAndRole.get(1);
            String guildName = data.getGuilds().getGuildName(guildId);
            String setterType = null;
            switch (guildRole) {
                case "leader":
                    p.sendMessage(Config.getTransl("settings", "messages.errors.members.leader.cant-leave"));
                    break;
                case "officer":
                    setterType = "remOfficer";
                    data.getGuildMembers().removeMemberData(playerName);
                    guildsData = data.getGuildMembers().guildsData();
                    setters(p, guildId, setterType);
                    sendPlayerToLobby(p);
                    p.sendMessage(Config.getTransl("settings", "messages.success.guilds.left")
                            .replace("$guildName", guildName));
                    sendGuildersBroadcast(guildId, conf.getSettings().getString("messages.info.guild.members.left")
                            .replace("%sp", plugin.getSuccessPrefix())
                            .replace("%ip", plugin.getInfoPrefix())
                            .replace("%fp", plugin.getFailPrefix())
                            .replace("$player", playerName)
                    );
                    sendPlayerToLobby(p);
                    break;
                default:
                    setterType = "remMember";
                    data.getGuildMembers().removeMemberData(playerName);
                    guildsData = data.getGuildMembers().guildsData();
                    setters(p, guildId, setterType);
                    sendPlayerToLobby(p);
                    p.sendMessage(Config.getTransl("settings", "messages.success.guilds.left")
                            .replace("$guildName", guildName));
                    sendGuildersBroadcast(guildId, conf.getSettings().getString("messages.info.guild.members.left")
                            .replace("%sp", plugin.getSuccessPrefix())
                            .replace("%ip", plugin.getInfoPrefix())
                            .replace("%fp", plugin.getFailPrefix())
                            .replace("$player", playerName)
                    );
                    sendPlayerToLobby(p);
                    break;
            }
        }
    }

    void setters(Player p, String guildId, String type) {
        String guildName = data.getGuilds().getGuildName(guildId);
        String username = p.getName();
        for (String setting : conf.getGuilds().getConfigurationSection("guilds." + guildName + ".settings." + type).getKeys(false)) {
            String settingsPath = "guilds." + guildName + ".settings." + type + "." + setting;
            String settingType = conf.getGuilds().getString(settingsPath + ".type");
            String groupName = null;
            switch (settingType) {
                case "sendCommand":
                    String command = conf.getGuilds().getString(settingsPath + ".string");
                    command = command.replace("%username%", username);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    break;
                case "giveItem":
                    String itemType = conf.getGuilds().getString(settingsPath + ".itemType");
                    switch (itemType) {
                        case "oraxen":
                            String oraxenId = conf.getGuilds().getString(settingsPath + ".oraxenId");
                            ItemStack oraxenItem = OraxenItems.getItemById(oraxenId).build();
                            p.getInventory().addItem(oraxenItem);
                            break;
                        default:
                            String materialName = conf.getGuilds().getString(settingsPath + ".material");
                            ItemStack item = new ItemStack(Material.getMaterial(materialName));
                            if (materialName.startsWith("LEATHER_")) {
                                String color = conf.getGuilds().getString(settingsPath + ".color");

                                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                                String[] colorSplitter = color.split(",");
                                int R = Integer.parseInt(colorSplitter[0]);
                                int G = Integer.parseInt(colorSplitter[1]);
                                int B = Integer.parseInt(colorSplitter[2]);
                                meta.setColor(Color.fromRGB(R, G, B));

                                String displayName = conf.getGuilds().getString(settingsPath + ".displayName");
                                if (!displayName.equalsIgnoreCase("null")) {
                                    meta.setDisplayName(Colors.text(displayName));
                                }

                                meta.setLore(General.lore(conf.getGuilds().getStringList(settingsPath + ".lore")));

                                meta.setCustomModelData(conf.getGuilds().getInt(settingsPath + ".model"));

                                for (String flag : conf.getGuilds().getStringList(settingsPath + ".flags")) {
                                    meta.addItemFlags(ItemFlag.valueOf(flag));
                                }
                                item.setItemMeta(meta);
                                for (String enchants : conf.getGuilds().getStringList(settingsPath + ".enchantments")) {
                                    String[] splitter = enchants.split(";");
                                    Enchantment enchantment = Enchants.getEnchant(splitter[0]);
                                    int enchLevel = Integer.parseInt(splitter[1]);
                                    item.addUnsafeEnchantment(enchantment, enchLevel);
                                }
                            } else {
                                ItemMeta meta = item.getItemMeta();

                                String displayName = conf.getGuilds().getString(settingsPath + ".displayName");
                                if (!displayName.equalsIgnoreCase("null")) {
                                    meta.setDisplayName(Colors.text(displayName));
                                }

                                meta.setLore(General.lore(conf.getGuilds().getStringList(settingsPath + ".lore")));

                                meta.setCustomModelData(conf.getGuilds().getInt(settingsPath + ".model"));

                                for (String flag : conf.getGuilds().getStringList(settingsPath + ".flags")) {
                                    meta.addItemFlags(ItemFlag.valueOf(flag));
                                }
                                item.setItemMeta(meta);
                                for (String enchants : conf.getGuilds().getStringList(settingsPath + ".enchantments")) {
                                    String[] splitter = enchants.split(";");
                                    Enchantment enchantment = Enchants.getEnchant(splitter[0]);
                                    int enchLevel = Integer.parseInt(splitter[1]);
                                    item.addUnsafeEnchantment(enchantment, enchLevel);
                                }
                            }
                            p.getInventory().addItem(item);
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
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

        data.getGuilds().updateBoughtGuild(guildId, 1);
        data.getGuildMembers().insertMemberData(username, memberId, guildId, "leader");
        String guildName = data.getGuilds().getGuildName(guildId);

        setters(p, guildId, "addLeader");
        sendPlayerToGhome(p, guildName);
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

    public void sendPlayerToGhome(Player p, String guildName) {
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

    public void sendGuildersBroadcast(String guildId, String message) {
        for (String key : guildsData.keySet()) {
            List<String> guildsAndRole = guildsData.get(key);
            String gId = guildsAndRole.get(0);
            String gRole = guildsAndRole.get(1);
            if (gId.equalsIgnoreCase(guildId)) {
                data.getTasks().insertTask("sendGuildersBroadcast", key + "£" + message);
            }
        }
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

    public static HashMap<String, String> getInvites() {
        return invites;
    }

    public static HashMap<String, List<String>> getGuildsData() {
        return guildsData;
    }

    public static Db getData() {
        return data;
    }
}
