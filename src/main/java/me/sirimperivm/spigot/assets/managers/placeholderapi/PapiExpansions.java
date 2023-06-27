package me.sirimperivm.spigot.assets.managers.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sirimperivm.spigot.Main;

@SuppressWarnings("all")
public class PapiExpansions extends PlaceholderExpansion {
    private Main plugin;
    public PapiExpansions(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "smpc_";
    }

    @Override
    public String getAuthor() {
        return "SirImperivm_";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
