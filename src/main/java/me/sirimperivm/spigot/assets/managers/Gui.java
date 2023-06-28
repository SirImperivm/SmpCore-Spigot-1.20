package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.other.General;
import me.sirimperivm.spigot.assets.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class Gui {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    public Inventory shopGui() {
        String confPath = "guis.shopGui.";
        String title = Config.getTransl("guis", confPath + "title");
        int size = conf.getGuis().getInt(confPath + "rows") * 9;

        Inventory inv = Bukkit.createInventory(null, size, title);

        for (String item : conf.getGuis().getConfigurationSection(confPath + "items").getKeys(false)) {
            String itemsPath = "guis.shopGui.items." + item;
            List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");
            String actionType = conf.getGuis().getString(itemsPath + ".action");

            Material mat = Material.getMaterial(conf.getGuis().getString(itemsPath + ".material"));
            ItemStack is = new ItemStack(mat);
            ItemMeta meta = is.getItemMeta();
            String displayName = conf.getGuis().getString(itemsPath + ".displayName");
            if (!displayName.equalsIgnoreCase("null")) {
                meta.setDisplayName(Colors.text(displayName));
            }
            meta.setLore(General.lore(conf.getGuis().getStringList(itemsPath + ".lore")));
            if (actionType.equalsIgnoreCase("BUY_GUILD")) {
                String guildId = conf.getGuis().getString(itemsPath + ".settings.guildId");
                if (!guildId.equals(null)) {
                    boolean boughtStatus = data.getGuilds().boughtStatus(guildId);
                    if (boughtStatus) {
                        meta.setLore(General.lore(conf.getGuis().getStringList(itemsPath + ".boughtLore")));
                    }
                } else {
                    slots = new ArrayList<>();
                }
            }
            meta.setCustomModelData(conf.getGuis().getInt(itemsPath + ".model"));
            if (conf.getGuis().getBoolean(itemsPath + ".glowing")) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            for (String flag : conf.getGuis().getStringList(itemsPath + ".itemFlags")) {
                meta.addItemFlags(ItemFlag.valueOf(flag));
            }
            is.setItemMeta(meta);
            for (Integer i : slots) {
                inv.setItem(i, is);
            }
        }
        return inv;
    }
}
