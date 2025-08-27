package dev.yourserver.simpleguide;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Statistic;

import java.io.File;
import java.util.Iterator;

public class SuggestionService {
    private final SimpleGuidePlugin plugin;
    private final YamlConfiguration goals;
    private final AdvLocalization advLoc;

    public SuggestionService(SimpleGuidePlugin plugin) {
        this.plugin = plugin;
        this.goals = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "goals.yml"));
        this.advLoc = plugin.advLoc;
    }

    public Suggestion nextAdvancementSuggestion(Player p) {
        Advancement best = null;
        int bestLeft = Integer.MAX_VALUE;
        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext();) {
            Advancement adv = it.next();
            if (!"minecraft".equals(adv.getKey().getNamespace())) continue;
            AdvancementProgress pr = p.getAdvancementProgress(adv);
            if (pr.isDone()) continue;
            int left = pr.getRemainingCriteria().size();
            if (left < bestLeft) {
                bestLeft = left;
                best = adv;
            }
        }
        if (best == null) return null;
        String key = best.getKey().toString();
        boolean de = p.locale() != null && p.locale().getLanguage().toLowerCase(java.util.Locale.ROOT).startsWith("de");

        String title = de ? advLoc.title(p, key) : null;
        if (title == null) title = goals.getString("advancements." + key + "." + (de ? "title_de" : "title_en"));
        if ((title == null || title.isEmpty()) && best.getDisplay() != null && best.getDisplay().title() != null) {
            Component c = best.getDisplay().title();
            title = PlainTextComponentSerializer.plainText().serialize(c);
        }
        if (title == null) title = key;

        String hint = de ? advLoc.description(p, key) : null;
        if ((hint == null || hint.isEmpty()) && best.getDisplay() != null && best.getDisplay().description() != null) {
            java.util.Locale loc = p.locale() != null ? p.locale() : java.util.Locale.ENGLISH;
            Component d = GlobalTranslator.render(best.getDisplay().description(), loc);
            hint = PlainTextComponentSerializer.plainText().serialize(d);
        }
        if (hint == null || hint.isEmpty()) hint = goals.getString("advancements." + key + "." + (de ? "suggest_de" : "suggest_en"), "");

        String structure = goals.getString("advancements." + key + ".structure", null);
        return new Suggestion(title, hint, structure);
    }

    public Suggestion contextSuggestionPrimary(Player p) {
        boolean de = p.locale() != null && p.locale().getLanguage().toLowerCase(java.util.Locale.ROOT).startsWith("de");
        if (p.getFoodLevel() <= plugin.getConfig().getInt("context.hunger_threshold", 6)) {
            return new Suggestion(de ? "Essen besorgen" : "Get food",
                    de ? "Weizen farmen, Brot backen, Tiere züchten." : "Farm wheat, bake bread, breed animals.", null);
        }
        int deaths = p.getStatistic(Statistic.DEATHS);
        if (deaths >= plugin.getConfig().getInt("context.death_threshold", 3)) {
            return new Suggestion(de ? "Besser schützen" : "Improve defense",
                    de ? "Schild + Rüstung craften, Bett setzen." : "Craft shield & armor, set a bed spawn.", null);
        }
        return new Suggestion(de ? "Dorf finden" : "Find a village",
                de ? "Betten, Handel, Essen." : "Beds, trades, food.", "village");
    }

    public Suggestion contextSuggestionSecondary(Player p) {
        boolean de = p.locale() != null && p.locale().getLanguage().toLowerCase(java.util.Locale.ROOT).startsWith("de");
        return new Suggestion(de ? "Verzauberungen" : "Enchanting",
                de ? "Baue einen Verzauberungstisch (4 Obsi, 2 Dia, 1 Buch)." : "Build an enchanting table (4 obsidian, 2 diamond, 1 book).",
                null);
    }

    public String structureDisplay(Player p, String key) {
        boolean de = p.locale() != null && p.locale().getLanguage().toLowerCase(java.util.Locale.ROOT).startsWith("de");
        String title = goals.getString("structures." + key + "." + (de ? "title_de" : "title_en"));
        if (title == null) {
            if ("village".equalsIgnoreCase(key)) return de ? "Dorf" : "Village";
            if ("fortress".equalsIgnoreCase(key)) return de ? "Netherfestung" : "Fortress";
            if ("stronghold".equalsIgnoreCase(key)) return "Stronghold";
            if ("monument".equalsIgnoreCase(key)) return de ? "Ozeanmonument" : "Ocean Monument";
            return key;
        }
        return title;
    }
}
