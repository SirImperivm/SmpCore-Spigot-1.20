package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;

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
        generatedGuildsId = new ArrayList<>();
        generatedOwnersId = new ArrayList<>();
    }

    public static List<String> getGeneratedGuildsId() {
        return generatedGuildsId;
    }

    public static List<String> getGeneratedOwnersId() {
        return generatedOwnersId;
    }
}
