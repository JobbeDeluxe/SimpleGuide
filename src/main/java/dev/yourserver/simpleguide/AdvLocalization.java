package dev.yourserver.simpleguide;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class AdvLocalization {
    private final JavaPlugin plugin;
    private JSONObject de;

    public AdvLocalization(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        de = new JSONObject();
        // try external override first: plugins/SimpleGuide/translations/de_de.json
        try {
            File ext = new File(plugin.getDataFolder(), "translations/de_de.json");
            if (ext.exists()) {
                try (FileReader r = new FileReader(ext, StandardCharsets.UTF_8)) {
                    de = (JSONObject) new JSONParser().parse(r);
                    return;
                }
            }
        } catch (Throwable ignored) {}

        // fallback to internal resource
        try (InputStreamReader r = new InputStreamReader(plugin.getResource("translations/de_de.json"), StandardCharsets.UTF_8)) {
            de = (JSONObject) new JSONParser().parse(r);
        } catch (Throwable t) {
            de = new JSONObject();
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

    @Nullable
    public String title(Player p, String advKeyMinecraftLike) {
        if (!isGerman(p)) return null;
        String langKey = toLangKey(advKeyMinecraftLike, "title");
        Object v = de.get(langKey);
        return v != null ? v.toString() : null;
    }

    @Nullable
    public String description(Player p, String advKeyMinecraftLike) {
        if (!isGerman(p)) return null;
        String langKey = toLangKey(advKeyMinecraftLike, "description");
        Object v = de.get(langKey);
        return v != null ? v.toString() : null;
    }

    // "minecraft:story/enter_the_nether" -> "advancements.story.enter_the_nether.title|description"
    private String toLangKey(String mcKey, String suffix) {
        String path = mcKey.replace("minecraft:", "").replace('/', '.');
        return "advancements." + path + "." + suffix;
    }
}
