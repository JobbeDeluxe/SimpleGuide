package dev.yourserver.simpleguide;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Locale;

public class AdvLocalization {
    private final SimpleGuidePlugin plugin;
    private YamlConfiguration de;

    public AdvLocalization(SimpleGuidePlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        // external override: plugins/SimpleGuide/translations/de_de.yml
        File ext = new File(plugin.getDataFolder(), "translations/de_de.yml");
        if (ext.exists()) {
            de = YamlConfiguration.loadConfiguration(ext);
        } else {
            de = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "translations/de_de.yml"));
            try {
                plugin.saveResource("translations/de_de.yml", false);
                de = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "translations/de_de.yml"));
            } catch (Throwable ignored) {}
        }
    }

    private boolean isGerman(Player p) {
        try {
            Locale loc = p.locale();
            return loc != null && loc.getLanguage().toLowerCase(Locale.ROOT).startsWith("de");
        } catch (Throwable t) {
            return false;
        }
    }

    private String path(String advKey, String tail) {
        String path = advKey.replace("minecraft:", "").replace('/', '.');
        return "advancements." + path + "." + tail;
    }

    public String title(Player p, String advKey) {
        if (!isGerman(p)) return null;
        return de.getString(path(advKey, "title"));
    }

    public String description(Player p, String advKey) {
        if (!isGerman(p)) return null;
        return de.getString(path(advKey, "description"));
    }
}
