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
}
