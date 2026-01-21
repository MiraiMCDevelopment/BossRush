package org.miraimc.bossRush.webhook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushData;
import org.miraimc.bossRush.event.BossRushEvent;
import org.miraimc.bossRush.util.TimeFormatUtil;
import org.miraimc.bossRush.util.WebhookUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BossRushWebhook {

    private final BossRush plugin;

    public BossRushWebhook(BossRush plugin) {
        this.plugin = plugin;
    }

    public void sendEventStartWebhook() {
        File file = WebhookUtil.loadFile("event_start.json");

        BossRushEvent event = plugin.getBossRushManager().getBossRushEvent();
        if (event == null || !event.isRunning() || !plugin.getConfigFile().getBoolean("webhook.enabled")) {
            return;
        }

        BossRushData data = event.getBossRushData();

        long endTimeStamp = (event.getStartTime() / 1000) + data.getTimeLimit();
        String eventMaxTime = "<t:" + endTimeStamp + ":R>";
        String eventObjective = getEventEntityName(data);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{event_objective}", eventObjective);
        placeholders.put("{event_max_time}", eventMaxTime);

        WebhookUtil.sendWebhook(file, plugin.getConfigFile().getRawString("webhook.url"), placeholders);
    }

    public void sendEventEndWebhook() {
        File file = WebhookUtil.loadFile("event_end.json");

        BossRushEvent event = plugin.getBossRushManager().getBossRushEvent();
        if (event == null || !plugin.getConfigFile().getBoolean("webhook.enabled")) {
            return;
        }

        BossRushData data = event.getBossRushData();

        UUID winnerId = event.getWinner();
        String winnerName = "No One";
        String winnerIdString = "None";

        if (winnerId != null) {
            Player winner = Bukkit.getPlayer(winnerId);
            if (winner != null) {
                winnerName = winner.getName();
            } else {
                winnerName = "Unknown";
            }
            winnerIdString = winnerId.toString();
        }

        String eventEndedAt = "<t:" + (event.getStartTime() / 1000) + ":R>";
        String eventObjective = getEventEntityName(data);
        String elapsedTime = TimeFormatUtil.formatMillis(event.getEndTime() - event.getStartTime());

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{event_objective}", eventObjective);
        placeholders.put("{event_max_time}", eventEndedAt);
        placeholders.put("{event_winner}", winnerName);
        placeholders.put("{event_winner_uuid}", winnerIdString);
        placeholders.put("{event_elapsed_time}", elapsedTime);

        WebhookUtil.sendWebhook(file, plugin.getConfigFile().getRawString("webhook.url"), placeholders);
    }

    private String getEventEntityName(BossRushData data) {
        return Arrays.stream(data.getEntityType().name().split("_"))
                .map(s -> s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
