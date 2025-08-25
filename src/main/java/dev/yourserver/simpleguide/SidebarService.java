package dev.yourserver.simpleguide;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SidebarService {

    private final SimpleGuidePlugin plugin;
    private final Map<java.util.UUID, Scoreboard> boards = new ConcurrentHashMap<>();
    private final Map<java.util.UUID, BukkitTask> tasks = new ConcurrentHashMap<>();

    public SidebarService(SimpleGuidePlugin plugin) { this.plugin = plugin; }

    public void startFor(Player p) {
        stopFor(p);
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        if (sm == null) return;
        Scoreboard sb = sm.getNewScoreboard();
        boards.put(p.getUniqueId(), sb);
        p.setScoreboard(sb);
        scheduleUpdate(p);
        updateNow(p);
    }

    public void stopFor(Player p) {
        java.util.UUID id = p.getUniqueId();
        BukkitTask t = tasks.remove(id);
        if (t != null) t.cancel();
        boards.remove(id);
    }

    public void updateSoon(Player p) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> updateNow(p), 10L);
    }

    private Objective ensureObjective(Player p, Scoreboard sb) {
        boolean de = p.locale() != null && p.locale().getLanguage().toLowerCase(java.util.Locale.ROOT).startsWith("de");
        String title = plugin.getConfig().getString(de ? "sidebar.title_de" : "sidebar.title_en", "&bSimpleGuide");
        String colored = org.bukkit.ChatColor.translateAlternateColorCodes('&', title);
        Objective obj = sb.getObjective("simpleguide");
        if (obj == null) {
            obj = sb.registerNewObjective("simpleguide", "dummy", colored);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            obj.setDisplayName(colored);
        }
        return obj;
    }

    private void clearScores(Scoreboard sb) {
        for (String e : new HashSet<>(sb.getEntries())) sb.resetScores(e);
    }

    public void updateNow(Player p) {
        Scoreboard sb = boards.get(p.getUniqueId());
        if (sb == null) return;
        Objective obj = ensureObjective(p, sb);
        clearScores(sb);

        Suggestion adv = plugin.suggestions.nextAdvancementSuggestion(p);
        Suggestion ctx1 = plugin.suggestions.contextSuggestionPrimary(p);
        Suggestion ctx2 = plugin.suggestions.contextSuggestionSecondary(p);

        String usage = plugin.lang.msg(p, "sidebar_usage");

        int line = 4;
        if (adv != null) obj.getScore(ChatColor.GREEN + adv.title).setScore(line--);
        if (ctx1 != null) obj.getScore(ChatColor.YELLOW + ctx1.title).setScore(line--);
        if (ctx2 != null) obj.getScore(ChatColor.YELLOW + ctx2.title).setScore(line--);
        if (plugin.getConfig().getBoolean("sidebar.show_usage_line", true)) {
            obj.getScore(ChatColor.GRAY + usage).setScore(line--);
        }
    }

    private void scheduleUpdate(Player p) {
        int ticks = Math.max(20, plugin.getConfig().getInt("sidebar.update_interval_ticks", 60));
        BukkitTask t = Bukkit.getScheduler().runTaskTimer(plugin, () -> updateNow(p), ticks, ticks);
        tasks.put(p.getUniqueId(), t);
    }
}
