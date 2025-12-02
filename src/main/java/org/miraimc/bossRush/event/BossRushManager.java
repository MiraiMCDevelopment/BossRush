package org.miraimc.bossRush.event;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.task.BossRushTask;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class BossRushManager {

    private final BossRush plugin;
    private BossRushEvent bossRushEvent;
    private final Map<String, BossRushData> bossRushDataMap = new HashMap<>();
    private BossRushTask bossRushTask;

    public BossRushManager(BossRush plugin) {
        this.plugin = plugin;
        bossRushEvent = new BossRushEvent();
    }

    public void loadData(String eventId) {
        bossRushDataMap.remove(eventId);

        String path = "events." + eventId;
        ConfigurationSection section = plugin.getEventsFile().getConfigurationSection(path);
        if (section == null) {
            plugin.getLogger().warning("Missing section for event: " + path);
            return;
        }

        String displayName = section.getString("display_name");
        String entityStr = section.getString("entity_type");
        if (displayName == null || entityStr == null) {
            plugin.getLogger().warning("Invalid event config for " + eventId);
            return;
        }

        EntityType entityType = EntityType.fromName(entityStr);
        if (entityType == null) {
            plugin.getLogger().warning("Unknown entity type " + entityStr + "for event " + eventId);
            return;
        }

        int timeLimit = section.getInt("time_limit", 1800);
        String rewardsName = section.getString("rewards.name", "Default Rewards");
        List<String> rewardsAction = section.getStringList("rewards.action");

        BossRushData data = new BossRushData(eventId, displayName, entityType, timeLimit, rewardsName, rewardsAction);
        bossRushDataMap.put(eventId, data);
        plugin.getLogger().info("BossRushData loaded: " + eventId);
    }

    public void loadAllData() {
        bossRushDataMap.clear();
        ConfigurationSection section = plugin.getEventsFile().getConfigurationSection("events");
        if (section == null) {
            plugin.getLogger().warning("There is no events.");
            return;
        }
        for (String key : section.getKeys(false)) {
            loadData(key);
        }
    }

    public boolean startEvent(String eventId) {
        BossRushData data = bossRushDataMap.get(eventId);
        if (data == null) {
            plugin.getLogger().warning("Unknown event " + eventId);
            return false;
        }
        if (bossRushEvent.isRunning()) {
            plugin.getLogger().warning("Event " + eventId + " is already running");
            return false;
        }

        bossRushEvent.start(data);
        plugin.getBossRushHandler().displayStartEvent();

        if (bossRushTask != null) {
            bossRushTask.cancel();
            bossRushTask = null;
        }
        bossRushTask = new BossRushTask(plugin);
        bossRushTask.runTaskTimer(plugin, 0L, 20L);

        plugin.getBossRushWebhook().sendEventStartWebhook();
        plugin.getLogger().info("BossRushEvent started: " + eventId);
        return true;
    }

    public boolean endEvent(@Nullable Player winner) {
        if (!bossRushEvent.isRunning()) {
            plugin.getLogger().warning("No running event to end.");
            return false;
        }

        bossRushEvent.end(winner != null ? winner.getUniqueId() : null);
        plugin.getBossRushHandler().displayEndEvent();

        if (winner != null) {
            plugin.getRewardManager().giveRewards(
                    bossRushEvent.getBossRushData(),
                    bossRushEvent.getWinner()
            );
        }

        if (bossRushTask != null) {
            bossRushTask.cancel();
            bossRushTask = null;
        }

        plugin.getLogger().info("BossRushEvent ended: " + bossRushEvent.getBossRushData().getId());
        return true;
    }
}
