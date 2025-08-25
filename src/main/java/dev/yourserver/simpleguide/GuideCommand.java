package dev.yourserver.simpleguide;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuideCommand implements TabExecutor {

    private final SimpleGuidePlugin plugin;
    public GuideCommand(SimpleGuidePlugin plugin) { this.plugin = plugin; }

    private boolean hasGuideBook(Player p) {
        org.bukkit.NamespacedKey key = plugin.books.key();
        for (org.bukkit.inventory.ItemStack it : p.getInventory().getContents()) {
            if (it == null) continue;
            if (!(it.getItemMeta() instanceof org.bukkit.inventory.meta.BookMeta)) continue;
            org.bukkit.inventory.meta.BookMeta bm = (org.bukkit.inventory.meta.BookMeta) it.getItemMeta();
            try {
                Integer tag = bm.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.INTEGER);
                if (tag != null && tag == 1) return true;
            } catch (Throwable ignored) {}
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage(plugin.lang.msg(null, "player_only")); return true; }
        Player p = (Player) sender;

        if (args.length == 0 || "gui".equalsIgnoreCase(args[0])) {
            GuideMenu.open(p, plugin);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "on":
                plugin.navigator.setEnabled(p.getUniqueId(), true, p);
                return true;
            case "off":
                plugin.navigator.setEnabled(p.getUniqueId(), false, p);
                return true;
            case "book":
                if (!hasGuideBook(p)) {
                    Suggestion adv = plugin.suggestions.nextAdvancementSuggestion(p);
                    Suggestion ctx1 = plugin.suggestions.contextSuggestionPrimary(p);
                    Suggestion ctx2 = plugin.suggestions.contextSuggestionSecondary(p);
                    p.getInventory().addItem(plugin.books.buildBook(p, adv, ctx1, ctx2));
                }
                p.sendMessage(plugin.lang.msg(p, "usage_hint"));
                return true;
            case "target":
                if (args.length >= 4) {
                    try {
                        double x = Double.parseDouble(args[1]);
                        double y = Double.parseDouble(args[2]);
                        double z = Double.parseDouble(args[3]);
                        Location loc = new Location(p.getWorld(), x, y, z);
                        plugin.navigator.setTarget(p, loc, "Target");
                    } catch (NumberFormatException e) {
                        p.sendMessage("Invalid coordinates.");
                    }
                    return true;
                } else {
                    p.sendMessage("Usage: /guide target <x> <y> <z>");
                    return true;
                }
            case "locate":
                if (args.length >= 2) {
                    String key = args[1].toLowerCase(Locale.ROOT);
                    plugin.navigator.locateStructure(p, key);
                    return true;
                } else {
                    p.sendMessage("Usage: /guide locate <structure>");
                    return true;
                }
            default:
                GuideMenu.open(p, plugin);
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            out.add("on"); out.add("off"); out.add("book"); out.add("target"); out.add("locate"); out.add("gui");
        } else if (args.length == 2 && "locate".equalsIgnoreCase(args[0])) {
            out.add("village"); out.add("fortress"); out.add("stronghold"); out.add("monument");
        }
        return out;
    }
}
