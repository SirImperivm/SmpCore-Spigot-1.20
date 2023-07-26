package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.values.Vault;
import me.sirimperivm.spigot.assets.other.General;
import me.sirimperivm.spigot.assets.other.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
        String path = "guis.shopGui.";
        String title = Config.getTransl("guis", path + "title");
        int size = 9 * conf.getGuis().getInt(path + "rows");

        Inventory inv = Bukkit.createInventory(null, size, title);

        for (String item : conf.getGuis().getConfigurationSection("guis.shopGui.items").getKeys(false)) {
            String itemsPath = "guis.shopGui.items." + item;
            String actionType = conf.getGuis().getString(itemsPath + ".action");
            List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");

            ItemStack material = new ItemStack(Material.getMaterial(conf.getGuis().getString(itemsPath + ".material")));
            ItemMeta meta = material.getItemMeta();
            String displayName = conf.getGuis().getString(itemsPath + ".displayName");
            boolean glowing = conf.getGuis().getBoolean(itemsPath + ".glowing");

            if (!displayName.equalsIgnoreCase("null")) {
                meta.setDisplayName(Colors.text(displayName));
            }
            if (actionType.equalsIgnoreCase("BUY_GUILD")) {
                String guildName = conf.getGuis().getString(itemsPath + ".settings.guildName");
                if (!guildName.equals("null")) {
                    boolean boughtStatus = data.getGuilds().boughtStatus("guildName", guildName);
                    if (!boughtStatus) {
                        List<String> lore = new ArrayList<String>();
                        for (String line : conf.getGuis().getStringList(itemsPath + ".lore")) {
                            lore.add(line
                                    .replace("$guildCost", Strings.formatNumber(conf.getGuis().getDouble(itemsPath + ".settings.price")))
                            );
                        }

                        meta.setLore(General.lore(lore));
                    } else {
                        List<String> lore = new ArrayList<String>();
                        for (String line : conf.getGuis().getStringList(itemsPath + ".boughtLore")) {
                            lore.add(line
                                    .replace("$guildCost", Strings.formatNumber(conf.getGuis().getDouble(itemsPath + ".settings.price")))
                            );
                        }

                        meta.setLore(General.lore(lore));
                    }
                } else {
                    slots.clear();
                }
            } else {
                meta.setLore(General.lore(conf.getGuis().getStringList(itemsPath + ".lore")));
            }
            meta.setCustomModelData(conf.getGuis().getInt(itemsPath + ".model"));
            if (glowing) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            for (String flag : conf.getGuis().getStringList(itemsPath + ".itemFlags")) {
                meta.addItemFlags(ItemFlag.valueOf(flag));
            }
            material.setItemMeta(meta);

            for (Integer i : slots) {
                inv.setItem(i, material);
            }
        }
        return inv;
    }

    public Inventory bankGui(Player p, String guildId) {
        String guildName = data.getGuilds().getGuildName(guildId);
        String guildTitle = Config.getTransl("guilds", "guilds." + guildName + ".guildTitle");
        String guiTitle = Config.getTransl("guis", "guis.bankGui.title")
                .replace("${guildTitle}", guildTitle);
        int size = conf.getGuis().getInt("guis.bankGui.rows") * 9;

        Inventory inv = Bukkit.createInventory(null, size, guiTitle);

        for (String item : conf.getGuis().getConfigurationSection("guis.bankGui.items").getKeys(false)) {
            String itemsPath = "guis.bankGui.items." + item;
            List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");

            ItemStack material = new ItemStack(Material.getMaterial(conf.getGuis().getString(itemsPath + ".material")));
            ItemMeta meta = material.getItemMeta();
            String displayName = conf.getGuis().getString(itemsPath + ".displayName");
            if (!displayName.equalsIgnoreCase("null")) {
                meta.setDisplayName(Colors.text(displayName));
            }
            List<String> lore = new ArrayList<String>();
            for (String line : conf.getGuis().getStringList(itemsPath + ".lore")) {
                lore.add(line
                        .replace("${guildBalance}", Strings.formatNumber(data.getGuilds().getGuildBalance(guildId)))
                        .replace("${userBalance}", Strings.formatNumber(Vault.getEcon().getBalance(p)))
                );
            }
            meta.setLore(General.lore(lore));
            meta.setCustomModelData(conf.getGuis().getInt(itemsPath + ".model"));
            if (conf.getGuis().getBoolean(itemsPath + ".glowing")) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            for (String flag : conf.getGuis().getStringList(itemsPath + ".itemFlags")) {
                meta.addItemFlags(ItemFlag.valueOf(flag));
            }
            material.setItemMeta(meta);
            for (Integer i : slots) {
                inv.setItem(i, material);
            }
        }

        return inv;
    }

    public Inventory upgradesGui(String guildId) {
        String guildName = data.getGuilds().getGuildName(guildId);
        String guildTitle = Config.getTransl("guilds", "guilds." + guildName + ".guildTitle");
        String guiTitle = Config.getTransl("guis", "guis.upgradesGui.title")
                .replace("${guildTitle}", guildTitle);
        int size = conf.getGuis().getInt("guis.upgradesGui.rows") * 9;

        Inventory inv = Bukkit.createInventory(null, size, guiTitle);

        for (String item : conf.getGuis().getConfigurationSection("guis.upgradesGui.items").getKeys(false)) {
            String itemsPath = "guis.upgradesGui.items." + item;
            String actionType = conf.getGuis().getString(itemsPath + ".action");
            List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");

            ItemStack material = new ItemStack(Material.getMaterial(conf.getGuis().getString(itemsPath + ".material")), conf.getGuis().getInt(itemsPath + ".amount"));
            ItemMeta meta = material.getItemMeta();
            String displayName = conf.getGuis().getString(itemsPath + ".displayName");
            if (!displayName.equalsIgnoreCase("null")) {
                meta.setDisplayName(Colors.text(displayName));
            }

            if (actionType.equalsIgnoreCase("BUY_LEVEL")) {
                double levelCost = 0.0;
                int levelRequired = 0;

                for (String requirement : conf.getGuis().getConfigurationSection(itemsPath + ".settings.requirements").getKeys(false)) {
                    double oldLevelCost = levelCost;
                    int oldLevelRequired = levelRequired;

                    String requirementsPath = "guis.upgradesGui.items." + item + ".settings.requirements." + requirement;
                    String requirementType = conf.getGuis().getString(requirementsPath + ".type");
                    if (requirementType.equalsIgnoreCase("money")) {
                        double newLevelCost = conf.getGuis().getDouble(requirementsPath + ".value");
                        if (newLevelCost >= oldLevelCost) {
                            levelCost = newLevelCost;
                        }
                    } else if (requirementType.equalsIgnoreCase("level")) {
                        int newLevelRequired = conf.getGuis().getInt(requirementsPath + ".value");
                        if (newLevelRequired >= oldLevelRequired) {
                            levelRequired = newLevelRequired;
                        }
                    }
                }

                List<String> lore = new ArrayList<String>();
                for (String line : conf.getGuis().getStringList(itemsPath + ".lore")) {
                    lore.add(line
                            .replace("$levelCost", Strings.formatNumber(levelCost))
                            .replace("$levelRequired", String.valueOf(levelRequired))
                    );
                }
                meta.setLore(General.lore(lore));
            } else {
                meta.setLore(General.lore(conf.getGuis().getStringList(itemsPath + ".lore")));
            }

            meta.setCustomModelData(conf.getGuis().getInt(itemsPath + ".model"));
            if (conf.getGuis().getBoolean(itemsPath + ".glowing")) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            for (String flag : conf.getGuis().getStringList(itemsPath + ".itemFlags")) {
                meta.addItemFlags(ItemFlag.valueOf(flag));
            }
            material.setItemMeta(meta);

            for (Integer i : slots) {
                inv.setItem(i, material);
            }
        }
        return inv;
    }

    public Inventory pvpGui() {
        String guiTitle = Config.getTransl("arenaPvP", "gui.title");
        int guiSize = 9 * conf.getArenaPvP().getInt("gui.rows");

        Inventory inv = Bukkit.createInventory(null, guiSize, guiTitle);

        for (String item : conf.getArenaPvP().getConfigurationSection("gui.items").getKeys(false)) {
            String itemsPath = "gui.items." + item;
            List<Integer> slots = conf.getArenaPvP().getIntegerList(itemsPath + ".slots");

            ItemStack material = new ItemStack(Material.getMaterial(conf.getArenaPvP().getString(itemsPath + ".material")));
            ItemMeta meta = material.getItemMeta();
            String displayName = conf.getArenaPvP().getString(itemsPath + ".displayName");
            if (!displayName.equalsIgnoreCase("null")) {
                meta.setDisplayName(Colors.text(displayName));
            }
            meta.setLore(General.lore(conf.getArenaPvP().getStringList(itemsPath + ".lore")));
            meta.setCustomModelData(conf.getArenaPvP().getInt(itemsPath + ".model"));
            if (conf.getArenaPvP().getBoolean(itemsPath + ".glowing")) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            for (String flag : conf.getArenaPvP().getStringList(itemsPath + ".itemFlags")) {
                meta.addItemFlags(ItemFlag.valueOf(flag));
            }
            material.setItemMeta(meta);

            for (Integer i : slots) {
                inv.setItem(i, material);
            }
        }

        return inv;
    }
}
