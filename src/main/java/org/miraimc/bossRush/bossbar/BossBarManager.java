package org.miraimc.bossRush.bossbar;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class BossBarManager {

    private final BossRush plugin;
    private final Map<UUID, BossBar> bossBars = new ConcurrentHashMap<>();

    public BossBarManager(BossRush plugin) {
        this.plugin = plugin;
    }

    public void registerAllPlayersBossBar() {
        Bukkit.getOnlinePlayers().forEach(this::registerPlayerBossBar);
    }

    public void registerPlayerBossBar(Player player) {
        BossRushEvent bossRushEvent = plugin.getBossRushEventManager().getBossRushEvent();
        if (bossRushEvent == null || !bossRushEvent.isRunning()) {
            return;
        }

        UUID uuid = player.getUniqueId();

        BossBar bar = bossBars.get(uuid);
        if (bar == null) {
            bar = Bukkit.createBossBar(plugin.getLangFile().getString("boss_bar.title"), BarColor.PURPLE, BarStyle.SOLID);
            bar.addPlayer(player);
            bossBars.put(uuid, bar);
        }

        updateProgress(player);
    }

    public void updateProgress(Player player) {
        BossBar bar = bossBars.get(player.getUniqueId());
        if (bar == null) {
            return;
        }

        BossRushEvent bossRushEvent = plugin.getBossRushEventManager().getBossRushEvent();
        if (bossRushEvent == null || !bossRushEvent.isRunning()) {
            return;
        }

        int eventTime = bossRushEvent.getTime();
        int eventMaxTime = bossRushEvent.getMaxTime();

        bar.setProgress((double) (eventMaxTime - eventTime) / eventMaxTime);
    }
}
