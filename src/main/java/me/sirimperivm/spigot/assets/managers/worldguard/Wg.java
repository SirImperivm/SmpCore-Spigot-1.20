package me.sirimperivm.spigot.assets.managers.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import java.util.logging.Logger;

@SuppressWarnings("all")
public class Wg {

    Logger log = Logger.getLogger("SMPCore");
    private static WorldGuard wg;
    public static StateFlag smpcGuilds;
    public static StringFlag smpcGuildId;

    public Wg() {
        wg = WorldGuard.getInstance();
        FlagRegistry register = wg.getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("smpc-guilds", false);
            register.register(flag);
            smpcGuilds = flag;
            log.info("E' stata registrata con successo la flag \"smpc-guilds\"!");
        } catch (FlagConflictException e) {
            Flag<?> existing = register.get("smpc-guilds");
            if (existing instanceof StateFlag) {
                smpcGuilds = (StateFlag) existing;
            } else {
                log.severe("Non è stato possibile creare la flag \"smpc-guilds\", magari un altro plugin usa questo nome.");
            }
        }

        try {
            Flag<String> flag = new StringFlag("smpc-guild-id");
            register.register(flag);
            smpcGuildId = (StringFlag) flag;
            log.info("E' stata registrata con successo la flag \"smpc-guild-id\"!");
        } catch (FlagConflictException e) {
            Flag<?> existing = register.get("smpc-guild-id");
            if (existing instanceof StringFlag) {
                smpcGuildId = (StringFlag) existing;
            } else {
                log.severe("Non è stato possibile creare la flag \"smpc-guild-id\", magari un altro plugin usa questo nome.");
            }
        }
    }

    public static WorldGuard getWg() {
        return wg;
    }
}
