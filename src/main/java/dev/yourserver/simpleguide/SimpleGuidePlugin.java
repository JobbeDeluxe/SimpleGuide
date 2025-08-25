package dev.yourserver.simpleguide;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SimpleGuidePlugin extends JavaPlugin implements Listener {

    Lang lang;
    SidebarService sidebar;
    HudBossbarService hud;
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

        lang = new Lang(this);
        sidebar = new SidebarService(this);
        hud = new HudBossbarService(this);
        navigator = new NavigatorService(this);
        suggestions = new SuggestionService(this);
        books = new BookService(this);
        advLoc = new AdvLocalization(this);

        getServer().getPluginManager().registerEvents(this, this);
        GuideCommand guide = new GuideCommand(this);
        Objects.requireNonNull(getCommand("guide")).setExecutor(guide);
        Objects.requireNonNull(getCommand("guide")).setTabCompleter(guide);

        getLogger().info("SimpleGuide enabled.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (getConfig().getString("display.mode","bossbar").equalsIgnoreCase("sidebar")) if (getConfig().getString("display.mode","sidebar").equalsIgnoreCase("sidebar")) sidebar.startFor(p); else if (hud!=null) hud.startFor(p); else hud.startFor(p);
        if (getConfig().getBoolean("navigator.enabled_by_default", true)) {
            navigator.setEnabled(p.getUniqueId(), true, p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        sidebar.stopFor(e.getPlayer()); if (hud!=null) hud.stopFor(e.getPlayer());
        navigator.clearBossbar(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent e) {
        if (getConfig().getString("display.mode","bossbar").equalsIgnoreCase("sidebar")) sidebar.updateSoon(e.getPlayer()); else if (hud!=null) hud.updateNow(e.getPlayer());
    }
}
