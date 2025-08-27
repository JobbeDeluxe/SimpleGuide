package dev.yourserver.simpleguide;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class SimpleGuidePlugin extends JavaPlugin implements Listener {

    Lang lang;
    SidebarService sidebar;
    NavigatorService navigator;
    SuggestionService suggestions;
    BookService books;
    AdvLocalization advLoc;

    @Override
    public void onEnable() {
        migrateConfig();
        saveDefaultConfig();
        saveResource("messages_en.yml", false);
        saveResource("messages_de.yml", false);
        saveResource("goals.yml", false);
        saveResource("translations/de_de.yml", false);
        

        advLoc = new AdvLocalization(this);
        lang = new Lang(this);
        sidebar = new SidebarService(this);
        navigator = new NavigatorService(this);
        suggestions = new SuggestionService(this);
        books = new BookService(this);

        getServer().getPluginManager().registerEvents(this, this);
        GuideCommand guide = new GuideCommand(this);
        Objects.requireNonNull(getCommand("guide")).setExecutor(guide);
        Objects.requireNonNull(getCommand("guide")).setTabCompleter(guide);

        getLogger().info("SimpleGuide enabled.");
    }

    

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        sidebar.startFor(p);
        if (getConfig().getBoolean("navigator.enabled_by_default", true)) {
            navigator.setEnabled(p.getUniqueId(), true, p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        sidebar.stopFor(e.getPlayer());
        navigator.clearBossbar(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent e) {
        sidebar.updateSoon(e.getPlayer());
    }

    private void migrateConfig() {
        try {
            getConfig().addDefault("configVersion", 0);
            int ver = getConfig().getInt("configVersion", 0);
            boolean changed = false;
            if (ver < 4) {
                // enforce new defaults
                getConfig().set("display.mode", "sidebar");
                getConfig().set("sidebar.show_usage_line", false);
                if (!getConfig().isSet("sidebar.update_interval_ticks"))
                    getConfig().set("sidebar.update_interval_ticks", 60);
                if (!getConfig().isSet("sidebar.force_sidebar"))
                    getConfig().set("sidebar.force_sidebar", true);
                getConfig().set("configVersion", 4);
                changed = true;
            }
            if (changed) {
                saveConfig();
                getLogger().info("[Config] Migrated to version " + getConfig().getInt("configVersion"));
            }
        } catch (Throwable t) {
            getLogger().warning("[Config] Migration failed: " + t.getMessage());
        }
    }
}
