package me.sirimperivm.spigot.assets.other;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("all")
public class Strings {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();

    public static String generateUuid() {
        String generated = UUID.randomUUID().toString();
        generated = generated.replaceAll("-", "");
        return generated;
    }

    public static String formatNumber(double number) {
        String toReturn = String.valueOf(number);
        String valueCurrency = "";
        int formatSize = conf.getSettings().getInt("settings.strings.valueFormatSize");
        List<String> references = conf.getSettings().getStringList("settings.strings.valueFormatting");
        StringBuilder sb = new StringBuilder(formatSize);

        for (String reference : references) {
            String[] splitter = reference.split("-");
            double referenceValue = Double.parseDouble(splitter[0]);
            if (number >= referenceValue) {
                toReturn = String.valueOf(number/referenceValue);
                valueCurrency = splitter[1];
            }
        }

        if (toReturn.length() >= formatSize) {
            for (int i = 0; i < formatSize; i++) {
                if (toReturn.charAt(i) == '.') {
                    break;
                }
                sb.append(toReturn.charAt(i));
            }

            toReturn = sb.toString();
        }

        if (toReturn.contains(".0")) {
            toReturn = toReturn.replace(".0", "");
        }

        toReturn = toReturn+valueCurrency;

        return toReturn;
    }
}
