package me.sirimperivm.spigot.assets.other;

import java.util.Random;

@SuppressWarnings("all")
public class Strings {

    static String charSet = "0123456789ABCDEF";

    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i=0; i<length; i++) {
            int randomIndex = random.nextInt(charSet.length());
            char randomChar = charSet.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
