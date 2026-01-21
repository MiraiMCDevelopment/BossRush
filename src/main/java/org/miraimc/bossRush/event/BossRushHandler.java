package org.miraimc.bossRush.event;

import io.github.milkdrinkers.colorparser.paper.ColorParser;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.util.TimeFormatUtil;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BossRushHandler {

    private final BossRush plugin;

    public BossRushHandler(BossRush plugin) {
        this.plugin = plugin;
    }

    public void displayStartEvent() {
        String langPath = "event.started.";

        BossRushData data = plugin.getBossRushManager().getBossRushEvent().getBossRushData();

        Title title = Title.title(
                ColorParser.of(plugin.getLangFile().getString(langPath + "title"))
                        .with("event", data.getId())
                        .with("event_display_name", data.getDisplayName())
                        .legacy()
                        .build(),
                ColorParser.of(plugin.getLangFile().getString(langPath + "subtitle"))
                        .with("event", data.getDisplayName())
                        .with("event_display_name", data.getDisplayName())
                        .legacy()
                        .build());

        List<String> messages = plugin.getLangFile().getRawStringList(langPath + "messages");

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.0F, 1.0F);
            messages.forEach(message -> player.sendMessage(ColorParser.of(message)
                    .with("event", data.getId())
                    .with("event_display_name", data.getDisplayName())
                    .with("rewards", data.getRewardsName())
                    .papi(player)
                    .legacy()
                    .build()
            ));
            plugin.getBossBarManager().registerPlayerBossBar(player);
        }

        plugin.getBossRushWebhook().sendEventStartWebhook();
    }

    public void displayEndEvent() {
        String langPath = "event.ended.";

        BossRushEvent event = plugin.getBossRushManager().getBossRushEvent();
        BossRushData data = event.getBossRushData();

        UUID winnerId = event.getWinner();
        final String winnerName = (winnerId != null)
                ? Optional.ofNullable(Bukkit.getOfflinePlayer(winnerId).getName()).orElse("Unknown")
                : "No One";
        String time = TimeFormatUtil.formatMillis(event.getEndTime() - event.getStartTime());

        Title title = Title.title(
                ColorParser.of(plugin.getLangFile().getString(langPath + "title"))
                        .with("event", data.getId())
                        .with("event_display_name", data.getDisplayName())
                        .with("winner", winnerName)
                        .with("rewards", data.getRewardsName())
                        .with("time", time)
                        .legacy()
                        .build()
                ,
                ColorParser.of(plugin.getLangFile().getString(langPath + "subtitle"))
                        .with("event", data.getId())
                        .with("event_display_name", data.getDisplayName())
                        .with("winner", winnerName)
                        .with("rewards", data.getRewardsName())
                        .with("time", time)
                        .legacy()
                        .build());

        List<String> messages = plugin.getLangFile().getRawStringList(langPath + "messages");

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
            messages.forEach(message -> player.sendMessage(ColorParser.of(message)
                    .with("event", data.getId())
                    .with("event_display_name", data.getDisplayName())
                    .with("winner", winnerName)
                    .with("rewards", data.getRewardsName())
                    .with("time", time)
                    .papi(player)
                    .legacy()
                    .build()
            ));
            plugin.getBossBarManager().unregisterPlayerBossBar(player);
        }
        plugin.getBossRushWebhook().sendEventEndWebhook();
    }
}
