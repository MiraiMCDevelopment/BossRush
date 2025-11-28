package org.miraimc.bossRush.webhook;

import org.bukkit.Bukkit;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushEvent;
import org.miraimc.bossRush.util.TimeFormatUtil;
import org.miraimc.bossRush.util.WebhookUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BossRushWebhook {

    private final BossRush plugin;

    public BossRushWebhook(BossRush plugin) {
        this.plugin = plugin;
    }

    public void sendEventStartWebhook() {
        File file = WebhookUtil.loadFile("event_start.json");

        BossRushEvent event = plugin.getBossRushEventManager().getBossRushEvent();
        if (event == null || !event.isRunning() || !plugin.getConfigFile().getBoolean("webhook.enabled")) {
            return;
        }

        long endTimeStamp = (event.getStartedAt() / 1000) + event.getMaxTime();
        String eventMaxTime = "<t:" + endTimeStamp + ":R>";
        String eventObjective = getEventObjectiveName(event);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{event_objective}", eventObjective);
        placeholders.put("{event_max_time}", eventMaxTime);

        WebhookUtil.sendWebhook(file, plugin.getConfigFile().getString("webhook.url"), placeholders);
    }

    public void sendEventEndWebhook() {
        File file = WebhookUtil.loadFile("event_end.json");

        BossRushEvent event = plugin.getBossRushEventManager().getBossRushEvent();
        if (event == null || !plugin.getConfigFile().getBoolean("webhook.enabled")) {
            return;
        }

        String eventEndedAt = "<t:" + (event.getEndedAt() / 1000) + ":R>";
        String eventObjective = getEventObjectiveName(event);
        String winnerName = event.getWinnerUuid() != null ? Bukkit.getPlayer(event.getWinnerUuid()).getName() : "No One";
        String elapsedTime = TimeFormatUtil.formatMillis(event.getEndedAt() - event.getStartedAt());

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{event_objective}", eventObjective);
        placeholders.put("{event_max_time}", eventEndedAt);
        placeholders.put("{event_winner}", winnerName);
        placeholders.put("{event_winner_uuid}", event.getWinnerUuid().toString());
        placeholders.put("{event_elapsed_time}", elapsedTime);

        WebhookUtil.sendWebhook(file, plugin.getConfigFile().getString("webhook.url"), placeholders);
    }

    private String getEventObjectiveName(BossRushEvent event) {
        return Arrays.stream(event.getEntityType().name().split("_"))
                .map(s -> s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

}
