package me.sirimperivm.spigot.assets.managers;

import io.th0rgal.oraxen.api.OraxenItems;
import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import me.sirimperivm.spigot.assets.other.General;
import me.sirimperivm.spigot.assets.other.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.other.Enchants;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Modules {

    private static List<String> generatedGuilds;
    private static List<String> generatedMembers;
    private static List<String> guildMembers;
    private static HashMap<String, String> invites;
    private static HashMap<String, List<String>> guildsData;
    private static HashMap<String, String> guildsChat;
    private static HashMap<String, List<String>> guildsList;
    private static List<String> topBankList;
    private static List<String> topMembersList;
    private static List<String> guildBankList;
    private static List<String> depositCooldown;
    private static List<String> withdrawCooldown;
    private static List<String> spyChat;
    private static Main plugin = Main.getPlugin();
    private static Logger log = Logger.getLogger("SMPCore");
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Vault vault = Main.getVault();

    public Modules() {
        invites = new HashMap<String, String>();
        guildsChat = new HashMap<String, String>();
        guildsList = new HashMap<String, List<String>>();
        spyChat = new ArrayList<String>();
        guildMembers = new ArrayList<String>();
        depositCooldown = new ArrayList<String>();
        withdrawCooldown = new ArrayList<String>();
        topBankList = new ArrayList<String>();
        topMembersList = new ArrayList<String>();
        refreshSettings();
        executeTasksLoop();
        refreshBankTop();
        refreshMembersTop();
    }

    void refreshSettings() {
        BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTaskTimer(plugin, () -> {
            generatedGuilds = data.getGuilds().getGeneratedGuildsID();
            generatedMembers = data.getGuildMembers().getGeneratedMembersID();
            guildsData = data.getGuildMembers().guildsData();
        }, 20, 5 * 20);

    }

    void refreshBankTop() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(plugin, () -> {
            String query = "SELECT * FROM " + data.getGuilds().database;
            Map<String, Double> map = new HashMap<>();
            topBankList = new ArrayList<String>();

            try {
                PreparedStatement state = data.conn.prepareStatement(query);
                ResultSet rs = state.executeQuery();
                while (rs.next()) {
                    map.put(rs.getString("guildName"), Double.parseDouble(rs.getString("bankBalance")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            List<Map.Entry<String, Double>> sortedList = new ArrayList<>(map.entrySet());
            sortedList.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

            for (Map.Entry<String, Double> entry : sortedList) {
                topBankList.add(entry.getKey() + "£" + entry.getValue());
            }
        }, 20, 20 * 20);
    }

    void refreshMembersTop() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(plugin, () -> {
            String query = "SELECT * FROM " + data.getGuilds().database;
            Map<String, Integer> map = new HashMap<>();
            topMembersList = new ArrayList<String>();

            try {
                PreparedStatement state = data.conn.prepareStatement(query);
                ResultSet rs = state.executeQuery();
                while (rs.next()) {
                    String guildName = rs.getString("guildName");
                    int membersCount = getMembersCount(rs.getString("guildId"));
                    map.put(guildName, membersCount);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(map.entrySet());
            sortedList.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));

            for (Map.Entry<String, Integer> entry : sortedList) {
                topMembersList.add(entry.getKey() + "£" + entry.getValue());
            }
        }, 20, 20 * 20);
    }

    public void deleteTask(int taskId) {
        data.getTasks().deleteTask(taskId);
    }

    public void sendGuildInfo(Player p, String type, String guildName) {
        switch (type) {
            case "admin":
                for (String line : conf.getSettings().getStringList("messages.tabCompleters.guilds.guild-info.admin")) {
                    String guildId = data.getGuilds().getGuildId(guildName);
                    if (line.equalsIgnoreCase("$guildCopyId")) {
                        TextComponent component = new TextComponent(Colors.text("&a[Copia ID]"));
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, guildId));
                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Copia l'ID").create()));
                        p.spigot().sendMessage(component);
                    } else {
                        p.sendMessage(Colors.text(line
                                .replace("$guildTitle", Config.getTransl("guilds", "guilds." + guildName + ".guildTitle"))
                                .replace("$guildLeader", !data.getGuildMembers().guildLeader(guildId).equalsIgnoreCase("null") ? data.getGuildMembers().guildLeader(guildId) : "N/A")
                                .replace("$membersCount", String.valueOf(getMembersCount(guildId)))
                                .replace("$membersLimit", getMembersLimit(guildId) == -1 ? "∞" : String.valueOf(getMembersLimit(guildId)))
                                .replace("$guildLevel", String.valueOf(data.getGuilds().getGuildLevel(guildId)))
                                .replace("$guildId", guildId)
                                .replace("$officersOnline", data.getGuildMembers().getOnlineOfficers(guildId))
                                .replace("$guildersOnline", data.getGuildMembers().getOnlineGuilders(guildId))
                                .replace("$bankBalance", Strings.formatNumber(data.getGuilds().getGuildBalance(guildId)))
                                .replace("$officersCount", String.valueOf(data.getGuildMembers().getOfficersCount(guildId)))
                                .replace("$guildersCount", String.valueOf(data.getGuildMembers().getGuildersCount(guildId)))
                        ));
                    }
                }
                break;
            default:
                for (String line : conf.getSettings().getStringList("messages.tabCompleters.guilds.guild-info.user")) {
                    String guildId = data.getGuilds().getGuildId(guildName);
                    if (line.equalsIgnoreCase("$guildCopyId")) {
                        TextComponent component = new TextComponent(Colors.text("&a[Copia ID]"));
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, guildId));
                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Copia l'ID").create()));
                        p.spigot().sendMessage(component);
                    } else {
                        p.sendMessage(Colors.text(line
                                .replace("$guildTitle", Config.getTransl("guilds", "guilds." + guildName + ".guildTitle"))
                                .replace("$guildLeader", !data.getGuildMembers().guildLeader(guildId).equalsIgnoreCase("null") ? data.getGuildMembers().guildLeader(guildId) : "N/A")
                                .replace("$membersCount", String.valueOf(getMembersCount(guildId)))
                                .replace("$membersLimit", getMembersLimit(guildId) == -1 ? "∞" : String.valueOf(getMembersLimit(guildId)))
                                .replace("$guildLevel", String.valueOf(data.getGuilds().getGuildLevel(guildId)))
                                .replace("$guildId", guildId)
                                .replace("$officersOnline", data.getGuildMembers().getOnlineOfficers(guildId))
                                .replace("$guildersOnline", data.getGuildMembers().getOnlineGuilders(guildId))
                                .replace("$bankBalance", Strings.formatNumber(data.getGuilds().getGuildBalance(guildId)))
                                .replace("$officersCount", String.valueOf(data.getGuildMembers().getOfficersCount(guildId)))
                                .replace("$guildersCount", String.valueOf(data.getGuildMembers().getGuildersCount(guildId)))
                        ));
                    }
                }
                break;
        }
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
                    if (taskType.equalsIgnoreCase("setter")) {
                        String[] splitter = taskValue.split("£");
                        String username = splitter[0];
                        String guildId = splitter[1];
                        String setterType = splitter[2];
                        Player p = Bukkit.getPlayerExact(username);

                        if (p != null) {
                            setters(p, guildId, setterType);
                            deleteTask(taskId);
                        }
                    }
                }
            } catch (SQLException e) {
                log.severe("Impossibile eseguire una task!");
                e.printStackTrace();
            }
        }, 20L, 20L);
    }

    public void sendGuildTop(Player p, String type) {
        if (type.equals("bank")) {
            p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.bank.header"));
            p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.bank.title"));
            p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.bank.spacer"));
            p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.bank.lines.header"));

            int loop = 0;
            for (String line : topBankList) {
                String[] splitter = line.split("£");
                String guildName = splitter[0];
                Double value = Double.parseDouble(splitter[1]);

                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.bank.lines.line")
                        .replace("$guildTitle", Colors.text(getGuildTitle(guildName)))
                        .replace("$guildBalance", Strings.formatNumber(value))
                );
                if (loop == 8) {
                    break;
                }
                loop++;
            }

            p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.bank.footer"));
        } else if (type.equals("members")) {
            p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.members.header"));
            p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.members.title"));
            p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.members.spacer"));
            p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.members.lines.header"));

            int loop = 0;
            for (String line : topMembersList) {
                String[] splitter = line.split("£");
                String guildName = splitter[0];

                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.members.lines.line")
                        .replace("$guildTitle", Colors.text(getGuildTitle(guildName)))
                        .replace("$membersCount", splitter[1])
                );
                if (loop == 8) {
                    break;
                }
                loop++;
            }
            p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.top.members.footer"));
        }
    }

    public void sendGuildList(Player p, String type) {
        guildsList = data.getGuilds().getBoughtGuildList();

        switch (type) {
            case "admin":
                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.admin.header"));
                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.admin.title"));
                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.admin.spacer"));
                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.admin.lines.header"));
                for (String key : guildsList.keySet()) {
                    String guildId = guildsList.get(key).get(0);
                    int boughtStatus = Integer.parseInt(guildsList.get(key).get(1));

                    TextComponent component = new TextComponent(Colors.text("&a[Copia ID]"));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, guildId));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Copia l'ID").create()));
                    p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.admin.lines.line")
                            .replace("$guildName", data.getGuilds().getGuildName(guildId))
                            .replace("$guildId", guildId)
                            .replace("$boughtStats", boughtStatus == 1 ? "acquistata" : "non acquistata")
                            .replace("$guildMembers", Strings.formatNumber(getMembersCount(guildId)))
                    );
                    p.spigot().sendMessage(component);
                }
                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.admin.footer"));
                break;
            default:
                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.user.header"));
                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.user.title"));
                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.user.spacer"));
                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.user.lines.header"));
                for (String key : guildsList.keySet()) {
                    String guildId = guildsList.get(key).get(0);
                    int boughtStatus = Integer.parseInt(guildsList.get(key).get(1));

                    p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.user.lines.line")
                            .replace("$guildName", data.getGuilds().getGuildName(guildId))
                            .replace("$boughtStats", boughtStatus == 1 ? "acquistata" : "non acquistata")
                            .replace("$guildMembers", Strings.formatNumber(getMembersCount(guildId)))
                    );
                }
                p.sendMessage(Config.getTransl("settings", "messages.tabCompleters.guilds.list.user.footer"));
                break;
        }
    }

    public int getMembersCount(String guildId) {
        int members = 0;
        for (String key : guildsData.keySet()) {
            String gId = guildsData.get(key).get(0);
            if (gId.equalsIgnoreCase(guildId)) {
                members++;
            }
        }
        return members;
    }

    public int getMembersLimit(String guildId) {
        int limit = 0;
        String guildName = data.getGuilds().getGuildName(guildId);
        limit = conf.getGuilds().getInt("guilds." + guildName + ".membersLimit");
        return limit;
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

        TextComponent copyGID = new TextComponent(Colors.text("&a[Copia l'ID]"));
        copyGID.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, guildId));
        copyGID.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Clicca per copiare").create()));

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
        p.spigot().sendMessage(copyGID);
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
                Player guilders = Bukkit.getPlayerExact(username);
                if (guilders != null) {
                    String guildRole = getGuildsData().get(username).get(1);
                    if (guildRole.equalsIgnoreCase("leader")) {
                        setters(p, guildId, "remLeader");
                    } else if (guildRole.equalsIgnoreCase("officer")) {
                        setters(p, guildId, "remOfficer");
                    } else if (guildRole.equalsIgnoreCase("member")) {
                        setters(p, guildId, "remMember");
                    }
                } else {
                    String guildRole = getGuildsData().get(username).get(1);
                    if (guildRole.equalsIgnoreCase("leader")) {
                        data.getTasks().insertTask("setter", username + "£" + guildId + "remLeader");
                    } else if (guildRole.equalsIgnoreCase("officer")) {
                        data.getTasks().insertTask("setter", username + "£" + guildId + "remOfficer");
                    } else if (guildRole.equalsIgnoreCase("member")) {
                        data.getTasks().insertTask("setter", username + "£" + guildId + "remMember");
                    }
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
        String settingsPath = "zones.lobby";

        String worldName = p.getLocation().getWorld().getName();
        double posX = p.getLocation().getX();
        double posY = p.getLocation().getY();
        double posZ = p.getLocation().getZ();
        float rotYaw = p.getLocation().getYaw();
        float rotPitch = p.getLocation().getPitch();

        conf.getZones().set(settingsPath + ".world", worldName);
        conf.getZones().set(settingsPath + ".posX", posX);
        conf.getZones().set(settingsPath + ".posY", posY);
        conf.getZones().set(settingsPath + ".posZ", posZ);
        conf.getZones().set(settingsPath + ".rotYaw", rotYaw);
        conf.getZones().set(settingsPath + ".rotPitch", rotPitch);
        conf.save(conf.getZones(), conf.getZonesFile());
        p.sendMessage(Config.getTransl("settings", "messages.success.lobby.located"));
    }

    public void inviteMember(Player target, String guildName) {
        String username = target.getName();
        target.sendMessage(Config.getTransl("settings", "messages.info.guild.members.invited.you")
                .replace("$guildName", guildName));
        sendGuildersBroadcast(data.getGuilds().getGuildId(guildName), Config.getTransl("settings", "messages.info.guild.members.invited.members")
                .replace("$playerName", username), "null");
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
            sendGuildersBroadcast(guildId, conf.getSettings().getString("messages.info.guild.members.kicked-tabCompleters")
                    .replace("%sp", plugin.getSuccessPrefix())
                    .replace("%ip", plugin.getInfoPrefix())
                    .replace("%fp", plugin.getFailPrefix())
                    .replace("$player", playerName)
                    , "null");
            p.sendMessage(Config.getTransl("settings", "messages.info.guild.members.kicked"));
        }
    }

    public void newLeadership(Player p, Player t, String guildId) {
        String playerName = p.getName();
        String targetName = t.getName();

        data.getGuildMembers().updateMemberData(playerName, "guildRole", "officer");
        data.getGuildMembers().updateMemberData(targetName, "guildRole", "leader");
        guildsData = data.getGuildMembers().guildsData();
        setters(p, guildId, "remLeader");
        setters(p, guildId, "addOfficer");
        setters(t, guildId, "addLeader");
        if (guildsData.get(targetName).get(1).equalsIgnoreCase("member")) {
            setters(t, guildId, "remMember");
        } else if (guildsData.get(targetName).get(1).equalsIgnoreCase("officer")) {
            setters(t, guildId, "remOfficer");
        }
        sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.guild.members.leader.changed")
                .replace("$oldLeader", playerName)
                .replace("$newLeader", targetName)
        , "null");
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
                            , "null");
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
                            , "null");
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

    public void changeSpawn(Player p, String guildName) {
        Location loc = p.getLocation();
        String worldName = loc.getWorld().getName();
        double posX = loc.getX();
        double posY = loc.getY();
        double posZ = loc.getZ();
        float rotYaw = loc.getYaw();
        float rotPitch = loc.getPitch();
        conf.getGuilds().set("guilds." + guildName + ".mainHome.worldName", worldName);
        conf.getGuilds().set("guilds." + guildName + ".mainHome.posX", posX);
        conf.getGuilds().set("guilds." + guildName + ".mainHome.posY", posY);
        conf.getGuilds().set("guilds." + guildName + ".mainHome.posZ", posZ);
        conf.getGuilds().set("guilds." + guildName + ".mainHome.rotYaw", rotYaw);
        conf.getGuilds().set("guilds." + guildName + ".mainHome.rotPitch", rotPitch);
        conf.save(conf.getGuilds(), conf.getGuildsFile());

        p.sendMessage(Config.getTransl("settings", "messages.success.guilds.spawn.relocated")
                .replace("$guildName", guildName));
    }

    public void sendPlayerToLobby(Player p) {
        World locWorld = Bukkit.getWorld(conf.getZones().getString("zones.lobby.world"));
        double locX = conf.getZones().getDouble("zones.lobby.posX");
        double locY = conf.getZones().getDouble("zones.lobby.posY");
        double locZ = conf.getZones().getDouble("zones.lobby.posZ");
        float rotYaw = conf.getZones().getInt("zones.lobby.rotYaw");
        float rotPitch = conf.getZones().getInt("zones.lobby.rotPitch");

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

    public void sendGuildersBroadcast(String guildId, String message, String exceptedUsername) {
        for (String key : guildsData.keySet()) {
            List<String> guildsAndRole = guildsData.get(key);
            String gId = guildsAndRole.get(0);
            String gRole = guildsAndRole.get(1);
            if (gId.equalsIgnoreCase(guildId)) {
                if (!exceptedUsername.equalsIgnoreCase(key) || exceptedUsername.equals("null")) {
                    data.getTasks().insertTask("sendGuildersBroadcast", key + "£" + message);
                }
            }
        }
    }

    public void addGuildBalance(Player p, String guildId, double newBalance) {
        double bankBalance = data.getGuilds().getGuildBalance(guildId);
        double newValue = bankBalance + newBalance;
        data.getGuilds().updateGuildBalance(guildId, String.valueOf(newValue));
        p.sendMessage(Config.getTransl("settings", "messages.info.guild.money.admin.added-admin")
                .replace("$cost", Strings.formatNumber(newValue))
                .replace("$guildName", data.getGuilds().getGuildName(guildId))
                .replace("$guildBalance", Strings.formatNumber(bankBalance)
        ));
        String playerName = p.getName();
        sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.guild.money.admin.added.users")
                        .replace("$cost", Strings.formatNumber(newValue))
                        .replace("$guildBalance", Strings.formatNumber(bankBalance))
                , playerName);
    }

    public void takeGuildBalance(Player p, String guildId, double newBalance) {
        double bankBalance = data.getGuilds().getGuildBalance(guildId);
        double newValue = bankBalance - newBalance;
        data.getGuilds().updateGuildBalance(guildId, String.valueOf(newValue));
        p.sendMessage(Config.getTransl("settings", "messages.info.guild.money.admin.taken-admin")
                .replace("$cost", Strings.formatNumber(newValue))
                .replace("$guildName", data.getGuilds().getGuildName(guildId))
                .replace("$guildBalance", Strings.formatNumber(bankBalance)
                ));
        String playerName = p.getName();
        sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.guild.money.admin.taken.users")
                        .replace("$cost", Strings.formatNumber(newValue))
                        .replace("$guildBalance", Strings.formatNumber(bankBalance))
                , playerName);
    }

    public void setGuildBalance(Player p, String guildId, double newBalance) {
        data.getGuilds().updateGuildBalance(guildId, String.valueOf(newBalance));
        p.sendMessage(Config.getTransl("settings", "messages.info.guild.money.admin.setted-admin")
                .replace("$guildName", data.getGuilds().getGuildName(guildId))
                .replace("$guildBalance", Strings.formatNumber(data.getGuilds().getGuildBalance(guildId))
                ));
        String playerName = p.getName();
        sendGuildersBroadcast(guildId, Config.getTransl("settings", "messages.info.guild.money.admin.setted.users")
                        .replace("$guildBalance", Strings.formatNumber(data.getGuilds().getGuildBalance(guildId)))
                , playerName);
    }

    public boolean isLobbyLocated() {
        return !conf.getZones().getString("zones.lobby.world").equalsIgnoreCase("null");
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

    public String getGuildTitle(String guildName) {
        String guildTitle = conf.getGuilds().getString("guilds." + guildName + ".guildTitle");
        return guildTitle;
    }

    public double returnCost (String type) {
        switch (type) {
            case "per-invite":
                return conf.getSettings().getDouble("settings.guilds.costs.per-invite");
            default:
                return 0;
        }
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

    public static HashMap<String, List<String>> getGuildsList() {
        return guildsList;
    }

    public static HashMap<String, String> getGuildsChat() {
        return guildsChat;
    }

    public static List<String> getDepositCooldown() {
        return depositCooldown;
    }

    public static List<String> getSpyChat() {
        return spyChat;
    }

    public static List<String> getWithdrawCooldown() {
        return withdrawCooldown;
    }

    public static List<String> getTopBankList() {
        return topBankList;
    }

    public static List<String> getTopMembersList() {
        return topMembersList;
    }

    public static Db getData() {
        return data;
    }
}
