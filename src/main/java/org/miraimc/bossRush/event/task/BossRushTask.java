package org.miraimc.bossRush.event.task;

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
        bossRushEvent.setTime(bossRushEvent.getTime() + 1);

        if (bossRushEvent.getTime() >= bossRushEvent.getMaxTime()) {
            plugin.getBossRushEventManager().endEvent(bossRushEvent);
            cancel();
        }

    }
}
