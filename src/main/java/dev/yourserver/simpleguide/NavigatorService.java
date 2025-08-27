package dev.yourserver.simpleguide;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NavigatorService {

    private final SimpleGuidePlugin plugin;

    private final Map<UUID, Boolean> enabled = new ConcurrentHashMap<>();
    private final Map<UUID, Location> targets = new ConcurrentHashMap<>();
    private final Map<UUID, BossBar> bars = new ConcurrentHashMap<>();

    public NavigatorService(SimpleGuidePlugin plugin) {
        this.plugin = plugin;
        // periodic updater for bossbar direction & distance
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (UUID id : targets.keySet()) {
                Player p = Bukkit.getPlayer(id);
                if (p == null) continue;
                if (!isEnabled(id)) continue;
                Location loc = targets.get(id);
                if (loc == null || !p.getWorld().equals(loc.getWorld())) continue;
                updateBossbar(p, directionArrow(p, loc) + " " + loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ(), loc);
            }
        }, 20L, 20L);
    }

    public void setEnabled(UUID id, boolean on, Player context) {
        enabled.put(id, on);
        if (context != null) {
            if (on) context.sendMessage(plugin.lang.msg(context, "navigator_on"));
            else context.sendMessage(plugin.lang.msg(context, "navigator_off"));
            if (!on) clearBossbar(id);
        }
    }
    public boolean isEnabled(UUID id) { return enabled.getOrDefault(id, false); }

    public void setTarget(Player p, Location loc, String title) {
        if (loc == null) {
            targets.remove(p.getUniqueId());
            clearBossbar(p.getUniqueId());
            p.sendMessage(plugin.lang.msg(p, "navigator_target_cleared"));
            return;
        }
        targets.put(p.getUniqueId(), loc);
        if (plugin.getConfig().getBoolean("navigator.use_compass", true)) {
            p.setCompassTarget(loc);
        }
        if (plugin.getConfig().getBoolean("navigator.use_bossbar", true)) {
            updateBossbar(p, title, loc);
            // immediate refresh with dynamic arrow
            updateBossbar(p, directionArrow(p, loc) + " " + loc.getBlockX()+"," + loc.getBlockY()+","+loc.getBlockZ(), loc);
        }
        p.sendMessage(plugin.lang.msgf(p, "navigator_target_set", title));
    }

    private String directionArrow(Player p, Location target) {
        Location pl = p.getLocation();
        double dx = target.getX() - pl.getX();
        double dz = target.getZ() - pl.getZ();
        double angleToTarget = Math.toDegrees(Math.atan2(-dx, dz)); // Minecraft yaw 0 = -Z
        double yaw = p.getLocation().getYaw();
        double diff = (angleToTarget - yaw);
        while (diff < -180) diff += 360;
        while (diff > 180) diff -= 360;
        double a = Math.abs(diff);
        if (a < 22.5) return "↑";
        if (a < 67.5) return diff > 0 ? "↖" : "↗";
        if (a < 112.5) return diff > 0 ? "←" : "→";
        if (a < 157.5) return diff > 0 ? "↙" : "↘";
        return "↓";
    }

    public void updateBossbar(Player p, String title, Location loc) {
        int dist = (int) p.getLocation().distance(loc);
        Component name = Component.text(String.format("%s – %dm", title, dist));
        BossBar bar = bars.get(p.getUniqueId());
        if (bar == null) {
            bar = BossBar.bossBar(name, 1.0f, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10);
            bars.put(p.getUniqueId(), bar);
            p.showBossBar(bar);
        }
        bar.name(name);
    }

    public void clearBossbar(UUID id) {
        BossBar bar = bars.remove(id);
        if (bar != null) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) p.hideBossBar(bar);
        }
    }

    // Real locate via reflection to support different Bukkit versions
    private static class StructureResolver {
        static Class<?> structureClass;
        static Object resolveType(String key) throws Exception {
            String name = key.toUpperCase(Locale.ROOT);
            try {
                structureClass = Class.forName("org.bukkit.StructureType");
            } catch (ClassNotFoundException e) {
                structureClass = Class.forName("org.bukkit.generator.structure.StructureType");
            }
            try {
                java.lang.reflect.Field f = structureClass.getField(name);
                return f.get(null);
            } catch (NoSuchFieldException ex) {
                return null;
            }
        }
    }

    public void locateStructure(Player p, String key) {
        if (p == null || p.getWorld() == null) return;
        p.sendMessage(plugin.lang.msgf(p, "navigator_locating", key));
        int radius = Math.max(256, plugin.getConfig().getInt("navigator.locate_radius", 2048));
        boolean unexplored = plugin.getConfig().getBoolean("navigator.locate_unexplored", false);

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Object type = StructureResolver.resolveType(key);
                if (type == null) { p.sendMessage(plugin.lang.msgf(p, "navigator_locate_failed", key)); return; }
                java.lang.reflect.Method locate = p.getWorld().getClass().getMethod("locateNearestStructure", Location.class, StructureResolver.structureClass, int.class, boolean.class);
                Location found = (Location) locate.invoke(p.getWorld(), p.getLocation(), type, radius, unexplored);
                if (found != null) {
                    org.bukkit.World w = p.getWorld();
                    int y = w.getHighestBlockYAt(found);
                    Location surface = new Location(w, found.getX(), Math.max(64, y + 1), found.getZ());
                    setTarget(p, surface, key);
                } else {
                    p.sendMessage(plugin.lang.msgf(p, "navigator_locate_failed", key));
                }
            } catch (Throwable t) {
                p.sendMessage(plugin.lang.msgf(p, "navigator_locate_failed", key));
            }
        });
    }
}
