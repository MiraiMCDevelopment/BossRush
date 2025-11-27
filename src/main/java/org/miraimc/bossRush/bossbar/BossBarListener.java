package org.miraimc.bossRush.bossbar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushEvent;

public class BossBarListener implements Listener {

    private final BossRush plugin;

    public BossBarListener(BossRush plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        BossRushEvent bossRushEvent = plugin.getBossRushEventManager().getBossRushEvent();
        if (bossRushEvent != null && bossRushEvent.isRunning()) {
            plugin.getBossBarManager().registerPlayerBossBar(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getBossBarManager().unregisterPlayerBossBar(player);
    }
}
