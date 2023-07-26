package me.sirimperivm.spigot.assets.other;

import me.sirimperivm.spigot.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@SuppressWarnings("all")
public enum Configs {
    settings(Main.getConf().getSettings(), Main.getConf().getSettingsFile()),
    guilds(Main.getConf().getGuilds(), Main.getConf().getGuildsFile()),
    helps(Main.getConf().getHelps(), Main.getConf().getHelpsFile()),
    guis(Main.getConf().getGuis(), Main.getConf().getGuisFile()),
    zones(Main.getConf().getZones(), Main.getConf().getZonesFile()),
    arenaPvP(Main.getConf().getArenaPvP(), Main.getConf().getArenaPvPFile());

    private FileConfiguration c;
    private File f;

    Configs(FileConfiguration c, File f) {
        this.c = c;
        this.f = f;
    }

    public FileConfiguration getC() {
        return c;
    }

    public File getF() {
        return f;
    }
}
