package dev.yourserver.simpleguide;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BookService {
    private final SimpleGuidePlugin plugin;
    public BookService(SimpleGuidePlugin plugin) { this.plugin = plugin; }

    public NamespacedKey key() { return new NamespacedKey(plugin, "guidebook"); }

    public ItemStack buildBook(Player p, Suggestion adv, Suggestion ctx1, Suggestion ctx2) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(plugin.lang.msg(p, "book_title"));
        meta.setAuthor(plugin.lang.msg(p, "book_author"));

        Component page1 = Component.text(plugin.lang.msg(p, "book_section_adv")).color(NamedTextColor.AQUA)
                .append(Component.newline())
                .append(Component.text("1) " + (adv != null ? adv.title : "—")))
                .append(Component.newline())
                .append(Component.text(adv != null ? adv.hint : ""))
                .append(Component.newline()).append(Component.newline())
                .append(Component.text(plugin.lang.msg(p, "book_section_ctx")).color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("2) " + (ctx1 != null ? ctx1.title : "—")))
                .append(Component.newline()).append(Component.text(ctx1 != null ? ctx1.hint : ""))
                .append(Component.newline())
                .append(Component.text("3) " + (ctx2 != null ? ctx2.title : "—")))
                .append(Component.newline()).append(Component.text(ctx2 != null ? ctx2.hint : ""));
        meta.addPages(page1);

        Component page2 = Component.text(plugin.lang.msg(p, "book_section_nav")).color(NamedTextColor.BLUE)
                .append(Component.newline())
                .append(Component.text("» ON").clickEvent(ClickEvent.runCommand("/guide on")))
                .append(Component.newline())
                .append(Component.text("» OFF").clickEvent(ClickEvent.runCommand("/guide off")));
        meta.addPages(page2);

        Component page3 = Component.text("GUI").color(NamedTextColor.BLUE)
                .append(Component.newline())
                .append(Component.text("» Open GUI").clickEvent(ClickEvent.runCommand("/guide gui")));
        meta.addPages(page3);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key(), PersistentDataType.INTEGER, 1);

        book.setItemMeta(meta);
        return book;
    }
}
