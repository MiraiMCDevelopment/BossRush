package org.miraimc.bossRush.event.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushEvent;

public class BossRushTask extends BukkitRunnable {

    private final BossRush plugin;

    public BossRushTask(BossRush plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        BossRushEvent bossRushEvent = plugin.getBossRushEventManager().getBossRushEvent();
        if (bossRushEvent == null || !bossRushEvent.isRunning()) {
            cancel();
            return;
        }

        long elapsedMillis = System.currentTimeMillis() - bossRushEvent.getStartedAt();
        int elapsedSeconds = (int) (elapsedMillis / 1000);

        bossRushEvent.setTime(elapsedSeconds);

        if (elapsedSeconds >= bossRushEvent.getMaxTime()) {
            plugin.getBossRushEventManager().endEvent(bossRushEvent);
            cancel();
            return;
        }

        plugin.getBossBarManager().updateAllProgress();
    }
}
