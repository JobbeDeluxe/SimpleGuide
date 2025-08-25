package dev.yourserver.simpleguide;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HudBossbarService {
    private final SimpleGuidePlugin plugin;

    private static class Bars {
        BossBar adv, c1, c2;
    }
    private final Map<UUID, Bars> map = new HashMap<>();

    public HudBossbarService(SimpleGuidePlugin plugin) {
        this.plugin = plugin;
        // periodic update
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                updateNow(p);
            }
        }, 40L, plugin.getConfig().getInt("sidebar.update_interval_ticks", 60));
    }

    public void startFor(Player p) {
        Bars b = map.get(p.getUniqueId());
        if (b == null) {
            b = new Bars();
            b.adv = BossBar.bossBar(Component.text(""), 1f, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_10);
            b.c1  = BossBar.bossBar(Component.text(""), 1f, BossBar.Color.YELLOW, BossBar.Overlay.NOTCHED_10);
            b.c2  = BossBar.bossBar(Component.text(""), 1f, BossBar.Color.YELLOW, BossBar.Overlay.NOTCHED_10);
            map.put(p.getUniqueId(), b);
            p.showBossBar(b.adv);
            p.showBossBar(b.c1);
            p.showBossBar(b.c2);
        }
        updateNow(p);
    }

    public void stopFor(Player p) {
        Bars b = map.remove(p.getUniqueId());
        if (b != null) {
            p.hideBossBar(b.adv);
            p.hideBossBar(b.c1);
            p.hideBossBar(b.c2);
        }
    }

    public void updateNow(Player p) {
        Bars b = map.get(p.getUniqueId());
        if (b == null) return;
        Suggestion adv = plugin.suggestions.nextAdvancementSuggestion(p);
        Suggestion ctx1 = plugin.suggestions.contextSuggestionPrimary(p);
        Suggestion ctx2 = plugin.suggestions.contextSuggestionSecondary(p);

        String t1 = adv != null ? adv.title : "";
        String t2 = ctx1 != null ? ctx1.title : "";
        String t3 = ctx2 != null ? ctx2.title : "";
        b.adv.name(Component.text(t1));
        b.c1.name(Component.text(t2));
        b.c2.name(Component.text(t3));
    }
}
