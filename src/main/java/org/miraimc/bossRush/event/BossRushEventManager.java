package org.miraimc.bossRush.event;

import lombok.Getter;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.task.BossRushTask;
import org.miraimc.bossRush.util.TextUtil;
import org.miraimc.bossRush.util.TimeFormatUtil;

import java.util.List;

public class BossRushEventManager {

    private final BossRush plugin;
    @Getter private BossRushEvent bossRushEvent;
    private BossRushTask bossRushTask;

    public BossRushEventManager(BossRush plugin) {
        this.plugin = plugin;
    }

    public boolean loadEvent(String name) {
        String path = getPath(name);
        ConfigurationSection section = plugin.getEventsFile().getConfigurationSection(path);
        if (section == null) {
            return false;
        }

        String entityStr = section.getString("entity");
        EntityType entity = EntityType.fromName(entityStr);
        int maxTime = section.getInt("max_time");

        bossRushEvent = new BossRushEvent(name, entity, maxTime);
        return true;
    }

    public void startEvent(BossRushEvent bossRushEvent) {
        bossRushEvent.setStartedAt(System.currentTimeMillis());
        bossRushEvent.setRunning(true);

        String path = getPath(bossRushEvent.getName());
        ConfigurationSection section = plugin.getEventsFile().getConfigurationSection(path + "started");

        String titleStr = section.getString("title");
        String subtitleStr = section.getString("subtitle");
        List<String> messages = section.getStringList("messages");

        Bukkit.getOnlinePlayers().forEach(player -> {
            Title title = Title.title(
                    TextUtil.translate(titleStr),
                    TextUtil.translate(subtitleStr)
            );
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.0F, 1.0F);
            messages.forEach(message -> player.sendMessage(TextUtil.translate(message)));
        });

        if (bossRushTask == null || bossRushTask.isCancelled()) {
            bossRushTask = new BossRushTask(plugin);
            bossRushTask.runTaskTimer(plugin, 0L, 20L);
        }

        plugin.getBossBarManager().registerAllPlayersBossBar();
    }

    public void endEvent(BossRushEvent bossRushEvent) {
        endEvent(bossRushEvent, null);
    }

    public void endEvent(BossRushEvent bossRushEvent, Player winner) {
        bossRushEvent.setEndedAt(System.currentTimeMillis());
        bossRushEvent.setRunning(false);

        String path = getPath(bossRushEvent.getName());
        ConfigurationSection section = plugin.getEventsFile().getConfigurationSection(path + "ended");

        String titleStr = section.getString("title").replace("{winner}", winner.getName());
        String subtitleStr = section.getString("subtitle").replace("{winner}", winner.getName());
        List<String> messages = section.getStringList("messages");

        if (winner == null) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage(plugin.getLangFile().getString("event.force_ended"));
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
            });
            return;
        }

        bossRushEvent.setWinnerUuid(winner.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(player -> {
            Title title = Title.title(
                    TextUtil.translate(titleStr),
                    TextUtil.translate(subtitleStr)
            );
            player.showTitle(title);

            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);

            String formattedTime = TimeFormatUtil.formatMillis(bossRushEvent.getEndedAt() - bossRushEvent.getStartedAt());
            messages.forEach(message -> player.sendMessage(TextUtil.translate(message
                    .replace("{winner}", winner.getName())
                    .replace("{time}", titleStr)
                    .replace("{formatted_time}", formattedTime)
            )));
            plugin.getBossBarManager().unregisterPlayerBossBar(player);
        });

        giveRewards(bossRushEvent);
        bossRushTask.cancel();
    }

    private String getPath(String eventName) {
        return "events." + eventName + ".";
    }

    private void giveRewards(BossRushEvent bossRushEvent) {
        Player winner = Bukkit.getPlayer(bossRushEvent.getWinnerUuid());
        List<String> rewards = plugin.getEventsFile().getStringList("events." + bossRushEvent.getName() + ".rewards");
        for (String reward : rewards) {
            reward = reward.replace("{winner}", winner.getName());
            if (reward.startsWith("[Console] ")) {
                reward = reward.replace("[Console] ", "");
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), reward);
            }
        }
    }
}
