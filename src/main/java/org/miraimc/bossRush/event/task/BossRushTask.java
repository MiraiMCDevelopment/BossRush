package org.miraimc.bossRush.event.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushData;
import org.miraimc.bossRush.event.BossRushEvent;

public class BossRushTask extends BukkitRunnable {

    private final BossRush plugin;

    public BossRushTask(BossRush plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        BossRushEvent event = plugin.getBossRushManager().getBossRushEvent();
        if (event == null || !event.isRunning()) {
            cancel();
            return;
        }

        BossRushData data = event.getBossRushData();
        event.setDuration(event.getDuration() + 1);

        if (event.getDuration() >= data.getTimeLimit()) {
            plugin.getBossRushManager().endEvent(null);
            cancel();
            return;
        }

        plugin.getBossBarManager().updateAllProgress();
    }
}
