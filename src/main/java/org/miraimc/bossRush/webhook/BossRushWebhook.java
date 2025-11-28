package org.miraimc.bossRush.webhook;

import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushEvent;
import org.miraimc.bossRush.util.TimeFormatUtil;
import org.miraimc.bossRush.util.WebhookUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

        String eventMaxTime = "<t:" + System.currentTimeMillis() + event.getMaxTime() + ":R>";
        String eventObjective = event.getEntityType().name();

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{event_objective}", eventObjective);
        placeholders.put("{event_max_time}", eventMaxTime);

        WebhookUtil.sendWebhook(file, plugin.getConfigFile().getString("webhook.url"), placeholders);
    }

    public void sendEventEndWebhook() {
        File file = WebhookUtil.loadFile("event_end.json");

        BossRushEvent event = plugin.getBossRushEventManager().getBossRushEvent();
        if (event == null || !event.isRunning() || !plugin.getConfigFile().getBoolean("webhook.enabled")) {
            return;
        }

        String eventEndedAt = "<t:" + event.getEndedAt() + ":R>";
        String eventObjective = event.getEntityType().name();
        String winnerName = event.getWinnerUuid() != null ? event.getWinnerUuid().toString() : "No One";
        String elapsedTime = TimeFormatUtil.formatMillis(event.getEndedAt() - event.getStartedAt());

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{event_objective}", eventObjective);
        placeholders.put("{event_max_time}", eventEndedAt);
        placeholders.put("{event_winner}", winnerName);
        placeholders.put("{event_winner_uuid}", winnerName);
        placeholders.put("{event_elapsed_time}", elapsedTime);


        WebhookUtil.sendWebhook(file, plugin.getConfigFile().getString("webhook.url"), placeholders);
    }
}
