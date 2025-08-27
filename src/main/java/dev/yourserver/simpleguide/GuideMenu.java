package dev.yourserver.simpleguide;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuideMenu implements InventoryHolder, Listener {

    private final SimpleGuidePlugin plugin;
    private final Player viewer;
    private final Inventory inv;

    public GuideMenu(Player viewer, SimpleGuidePlugin plugin) {
        this.viewer = viewer;
        this.plugin = plugin;
        String title = plugin.lang.msg(viewer, "gui_title");
        this.inv = Bukkit.createInventory(this, 27, org.bukkit.ChatColor.translateAlternateColorCodes('&', title));
        fill();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void open(Player p, SimpleGuidePlugin plugin) {
        GuideMenu m = new GuideMenu(p, plugin);
        p.openInventory(m.inv);
    }

    @Override public Inventory getInventory() { return inv; }

    private void setItem(int slot, Material mat, String name, String... lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', name));
        java.util.List<String> L = new java.util.ArrayList<>();
        for (String s : lore) L.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', s));
        m.setLore(L);
        it.setItemMeta(m);
        inv.setItem(slot, it);
    }

    private void fill() {
        boolean on = plugin.navigator.isEnabled(viewer.getUniqueId());
        setItem(10, on ? Material.LIME_DYE : Material.RED_DYE,
                on ? plugin.lang.msg(viewer, "gui_toggle_on") : plugin.lang.msg(viewer, "gui_toggle_off"),
                "&7Left-Click to toggle");

        setItem(12, Material.WRITTEN_BOOK, plugin.lang.msg(viewer, "gui_book"),
                "&7Left-Click to receive");

        String sStrong = plugin.suggestions.structureDisplay(viewer, "stronghold");
        String sFort   = plugin.suggestions.structureDisplay(viewer, "fortress");
        String sVillage= plugin.suggestions.structureDisplay(viewer, "village");

        setItem(14, Material.ENDER_EYE, String.format(plugin.lang.msg(viewer, "gui_locate"), sStrong),
                "&7Left-Click to locate");
        setItem(15, Material.NETHER_BRICK, String.format(plugin.lang.msg(viewer, "gui_locate"), sFort),
                "&7Left-Click to locate");
        setItem(16, Material.BELL, String.format(plugin.lang.msg(viewer, "gui_locate"), sVillage),
                "&7Left-Click to locate");

        setItem(22, Material.BARRIER, plugin.lang.msg(viewer, "gui_clear"), "&7Left-Click to clear target");
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        if (slot < 0 || slot >= inv.getSize()) return;

        switch (slot) {
            case 10:
                plugin.navigator.setEnabled(p.getUniqueId(), !plugin.navigator.isEnabled(p.getUniqueId()), p);
                fill();
                break;
            case 12:
                if (!hasGuideBook(p)) {
                    Suggestion adv = plugin.suggestions.nextAdvancementSuggestion(p);
                    Suggestion ctx1 = plugin.suggestions.contextSuggestionPrimary(p);
                    Suggestion ctx2 = plugin.suggestions.contextSuggestionSecondary(p);
                    p.getInventory().addItem(plugin.books.buildBook(p, adv, ctx1, ctx2));
                }
                break;
            case 14:
                plugin.navigator.locateStructure(p, "stronghold");
                break;
            case 15:
                plugin.navigator.locateStructure(p, "fortress");
                break;
            case 16:
                plugin.navigator.locateStructure(p, "village");
                break;
            case 22:
                plugin.navigator.setTarget(p, null, "");
                break;
            default:
                break;
        }
    }

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

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() != this) return;
        InventoryCloseEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
    }
}
