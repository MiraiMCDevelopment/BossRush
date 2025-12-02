package org.miraimc.bossRush.event.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushData;
import org.miraimc.bossRush.event.BossRushEvent;

public class BossRushListener implements Listener {

    private final BossRush plugin;

    public BossRushListener(BossRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        BossRushEvent bossRushEvent = plugin.getBossRushManager().getBossRushEvent();
        if (bossRushEvent == null || !bossRushEvent.isRunning()) {
            return;
        }

        BossRushData data = bossRushEvent.getBossRushData();

        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        if (event.getEntityType() == data.getEntityType()) {
            plugin.getBossRushManager().endEvent(killer);
        }
    }
}
