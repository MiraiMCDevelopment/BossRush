package org.miraimc.bossRush.event.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushEvent;

public class BossRushListener implements Listener {

    private final BossRush plugin;

    public BossRushListener(BossRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        BossRushEvent bossRushEvent = plugin.getBossRushEventManager().getBossRushEvent();
        if (bossRushEvent == null || !bossRushEvent.isRunning()) {
            return;
        }

        if (event.getEntity().getKiller() == null) {
            return;
        }

        if (event.getEntityType() == bossRushEvent.getEntityType()) {
            Player killer = event.getEntity().getKiller();
            plugin.getBossRushEventManager().endEvent(bossRushEvent, killer);
        }
    }
}
