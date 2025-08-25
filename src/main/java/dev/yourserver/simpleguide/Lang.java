package dev.yourserver.simpleguide;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class Lang {
    private final SimpleGuidePlugin plugin;
    private YamlConfiguration en;
    private YamlConfiguration de;

    public Lang(SimpleGuidePlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        en = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages_en.yml"));
        de = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages_de.yml"));
    }

    private boolean isGerman(Player p) {
        try {
            java.util.Locale loc = p.locale();
            if (loc == null) return false;
            return loc.getLanguage().toLowerCase(java.util.Locale.ROOT).startsWith("de");
        } catch (Throwable t) {
            return false;
        }
    }

    public String msg(Player p, String key) {
        if (p != null && isGerman(p)) return color(de.getString(key, key));
        return color(en.getString(key, key));
    }

    public String msgf(Player p, String key, Object... args) {
        String raw = (p != null && isGerman(p)) ? de.getString(key, key) : en.getString(key, key);
        try { return color(String.format(raw, args)); } catch (Exception e) { return color(raw); }
    }

    private String color(String s) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', s == null ? "" : s);
    }
}
