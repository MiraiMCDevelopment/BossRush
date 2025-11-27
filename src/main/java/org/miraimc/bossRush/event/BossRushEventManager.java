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

import java.util.Collection;
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
        if (entityStr == null) {
            return false;
        }

        EntityType entity = EntityType.fromName(entityStr);
        if (entity == null) {
            return false;
        }

        int maxTime = section.getInt("max_time", 1800);

        bossRushEvent = new BossRushEvent(name, entity, maxTime);
        return true;
    }

    public void startEvent(BossRushEvent bossRushEvent) {
        bossRushEvent.setStartedAt(System.currentTimeMillis());
        bossRushEvent.setRunning(true);

        String path = getPath(bossRushEvent.getName());
        ConfigurationSection section = plugin.getEventsFile().getConfigurationSection(path + "started");
        if (section == null) {
            return;
        }

        String titleStr = section.getString("title");
        String subtitleStr = section.getString("subtitle");
        List<String> messages = section.getStringList("messages");

        Title title = Title.title(
                TextUtil.translate(titleStr),
                TextUtil.translate(subtitleStr)
        );

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.0F, 1.0F);
            messages.forEach(message -> player.sendMessage(TextUtil.translate(message)));
            plugin.getBossBarManager().registerPlayerBossBar(player);
        }

        if (bossRushTask == null || bossRushTask.isCancelled()) {
            bossRushTask = new BossRushTask(plugin);
            bossRushTask.runTaskTimer(plugin, 0L, 20L);
        }
    }

    public void endEvent(BossRushEvent bossRushEvent) {
        endEvent(bossRushEvent, null);
    }

    public void endEvent(BossRushEvent bossRushEvent, Player winner) {
        if (!bossRushEvent.isRunning()) {
            return;
        }

        bossRushEvent.setEndedAt(System.currentTimeMillis());
        bossRushEvent.setRunning(false);

        String path = getPath(bossRushEvent.getName());
        ConfigurationSection section = plugin.getEventsFile().getConfigurationSection(path + "ended");

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        if (winner == null) {
            String forceEndedMessage = plugin.getLangFile().getString("event.force_ended");
            for (Player player : players) {
                player.sendMessage(forceEndedMessage);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
                plugin.getBossBarManager().unregisterPlayerBossBar(player);
            }

            if (bossRushTask != null) {
                bossRushTask.cancel();
            }
            return;
        }

        if (section == null) {
            return;
        }

        String winnerName = winner.getName();
        bossRushEvent.setWinnerUuid(winner.getUniqueId());

        String titleStr = section.getString("title").replace("{winner}", winner.getName());
        String subtitleStr = section.getString("subtitle").replace("{winner}", winner.getName());
        List<String> messages = section.getStringList("messages");

        Title title = Title.title(
                TextUtil.translate(titleStr),
                TextUtil.translate(subtitleStr)
        );

        String formattedTime = TimeFormatUtil.formatMillis(
                bossRushEvent.getEndedAt() - bossRushEvent.getStartedAt()
        );

        for (Player player : players) {
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
            messages.forEach(message -> player.sendMessage(TextUtil.translate(message
                    .replace("{winner}", winnerName)
                    .replace("{time}", formattedTime)
            )));

            plugin.getBossBarManager().unregisterPlayerBossBar(player);
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
           giveRewards(bossRushEvent);
        });

        if (bossRushTask != null) {
            bossRushTask.cancel();
        }
    }

    private String getPath(String eventName) {
        return "events." + eventName + ".";
    }

    private void giveRewards(BossRushEvent bossRushEvent) {
        Player winner = Bukkit.getPlayer(bossRushEvent.getWinnerUuid());

        if (winner == null || !winner.isOnline()) {
            return;
        }

        String winnerName = winner.getName();
        List<String> rewards = plugin.getEventsFile().getStringList("events." + bossRushEvent.getName() + ".rewards");

        for (String reward : rewards) {
            String processedReward = reward.replace("{winner}", winnerName);

            if (processedReward.startsWith("[Console] ")) {
                String command = processedReward.replace("[Console] ", "");

                Bukkit.getScheduler().runTask(plugin, () -> {
                   try {
                       plugin.getServer().dispatchCommand(
                               plugin.getServer().getConsoleSender(),
                               command
                       );
                   } catch (Exception e) {
                       plugin.getLogger().warning(e.getMessage());
                   }
                });
            }
        }
    }
}
