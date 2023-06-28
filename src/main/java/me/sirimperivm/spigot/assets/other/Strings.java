package me.sirimperivm.spigot.assets.other;

import java.util.UUID;

@SuppressWarnings("all")
public class Strings {

    public static String generateUuid() {
        String generated = UUID.randomUUID().toString();
        generated = generated.replaceAll("-", "");
        return generated;
    }
}
