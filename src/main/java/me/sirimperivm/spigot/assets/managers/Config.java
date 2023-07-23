package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.utils.Colors;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Config {

    private static Main plugin = Main.getPlugin();
    private static Logger log = Logger.getLogger("SMPCore");
    private File folder = plugin.getDataFolder();
    private File settingsFile, guildsFile, helpsFile, guisFile, lobbyFile;
    private FileConfiguration settings, guilds, helps, guis, lobby;

    public Config() {
        settingsFile = new File(folder, "settings.yml");
        settings = new YamlConfiguration();
        guildsFile = new File(folder, "guilds.yml");
        guilds = new YamlConfiguration();
        helpsFile = new File(folder, "helps.yml");
        helps = new YamlConfiguration();
        guisFile = new File(folder, "guis.yml");
        guis = new YamlConfiguration();
        lobbyFile = new File(folder, "lobby.yml");
        lobby = new YamlConfiguration();

        if (!folder.exists()) {
            folder.mkdir();
        }

        if (!settingsFile.exists()) {
            create(settings, settingsFile);
        }

        if (!guildsFile.exists()) {
            create(guilds, guildsFile);
        }

        if (!helpsFile.exists()) {
            create(helps, helpsFile);
        }

        if (!guisFile.exists()) {
            create(guis, guisFile);
        }

        if (!lobbyFile.exists()) {
            create(lobby, lobbyFile);
        }
    }

    private void create(FileConfiguration c, File f) {
        String n = f.getName();
        try {
            Files.copy(plugin.getResource(n), f.toPath(), new CopyOption[0]);
            load(c, f);
        } catch (IOException e) {
            log.severe("Impossibile creare il file " + n + "!");
            e.printStackTrace();
        }
    }

    public void save(FileConfiguration c, File f) {
        String n = f.getName();
        try {
            c.save(f);
        } catch (IOException e) {
            log.severe("Impossibile salvare il file " + n + "!");
            e.printStackTrace();
        }
    }

    public void load(FileConfiguration c, File f) {
        String n = f.getName();
        try {
            c.load(f);
        } catch (IOException | InvalidConfigurationException e) {
            log.severe("Impossibile caricare il file " + n + "!");
            e.printStackTrace();
        }
    }

    public void saveAll() {
        save(settings, settingsFile);
        save(guilds, guildsFile);
        save(helps, helpsFile);
        save(guis, guisFile);
        save(lobby, lobbyFile);
    }

    public void loadAll() {
        load(settings, settingsFile);
        load(guilds, guildsFile);
        load(helps, helpsFile);
        load(guis, guisFile);
        load(lobby, lobbyFile);
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public File getGuildsFile() {
        return guildsFile;
    }

    public File getHelpsFile() {
        return helpsFile;
    }

    public File getGuisFile() {
        return guisFile;
    }

    public File getLobbyFile() {
        return lobbyFile;
    }

    public FileConfiguration getSettings() {
        return settings;
    }

    public FileConfiguration getGuilds() {
        return guilds;
    }

    public FileConfiguration getHelps() {
        return helps;
    }

    public FileConfiguration getGuis() {
        return guis;
    }

    public FileConfiguration getLobby() {
        return lobby;
    }

    public static String getTransl(String type, String key) {
        switch (type) {
            case "guilds":
                return Colors.text(Main.getConf().getGuilds().getString(key)
                        .replace("%sp", Main.getSuccessPrefix())
                        .replace("%ip", Main.getInfoPrefix())
                        .replace("%fp", Main.getFailPrefix())
                );
            case "helps":
                return Colors.text(Main.getConf().getHelps().getString(key)
                        .replace("%sp", Main.getSuccessPrefix())
                        .replace("%ip", Main.getInfoPrefix())
                        .replace("%fp", Main.getFailPrefix())
                );
            case "guis":
                return Colors.text(Main.getConf().getGuis().getString(key)
                        .replace("%sp", Main.getSuccessPrefix())
                        .replace("%ip", Main.getInfoPrefix())
                        .replace("%fp", Main.getFailPrefix())
                );
            case "lobby":
                return Colors.text(Main.getConf().getLobby().getString(key)
                        .replace("%sp", Main.getSuccessPrefix())
                        .replace("%ip", Main.getInfoPrefix())
                        .replace("%fp", Main.getFailPrefix())
                );
            default:
                return Colors.text(Main.getConf().getSettings().getString(key)
                        .replace("%sp", Main.getSuccessPrefix())
                        .replace("%ip", Main.getInfoPrefix())
                        .replace("%fp", Main.getFailPrefix())
                );
        }
    }
}
