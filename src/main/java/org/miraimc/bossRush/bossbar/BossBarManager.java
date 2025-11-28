package org.miraimc.bossRush.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushEvent;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager {

    private final BossRush plugin;
    private final Map<UUID, BossBar> bossBars = new ConcurrentHashMap<>();

    private BarColor cachedColor;
    private BarStyle cachedStyle;

    public BossBarManager(BossRush plugin) {
        this.plugin = plugin;
        loadBossBarSettings();
    }

    private void loadBossBarSettings() {
        String colorStr = plugin.getConfigFile().getString("boss_bar.color");
        String styleStr = plugin.getConfigFile().getString("boss_bar.style");

        try {
            cachedColor = BarColor.valueOf(colorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            cachedColor = BarColor.PURPLE;
            plugin.getLogger().warning("Invalid boss bar color: " + colorStr + ", using PURPLE");
        }

        try {
            cachedStyle = BarStyle.valueOf(styleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            cachedStyle = BarStyle.SOLID;
            plugin.getLogger().warning("Invalid boss bar style: " + styleStr + ", using SOLID");
        }
    }

    public void registerAllPlayersBossBar() {
        BossRushEvent event = plugin.getBossRushEventManager().getBossRushEvent();
        if (event == null || !event.isRunning()) {
            return;
        }

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            registerPlayerBossBar(player);
        }
    }

    public void registerPlayerBossBar(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        BossRushEvent event = plugin.getBossRushEventManager().getBossRushEvent();
        if (event == null || !event.isRunning()) {
            return;
        }

        UUID uuid = player.getUniqueId();

        BossBar existingBar = bossBars.get(uuid);
        if (existingBar != null) {
            if (!existingBar.getPlayers().contains(player)) {
                existingBar.addPlayer(player);
            }
            updateProgress(player);
            return;
        }

        BossBar bar = Bukkit.createBossBar(event.getBossBar(), cachedColor, cachedStyle);
        bar.setVisible(true);
        bar.addPlayer(player);
        bossBars.put(uuid, bar);

        updateProgress(player);
    }

    public void updateProgress(Player player) {
        if (player == null) {
            return;
        }

        BossBar bar = bossBars.get(player.getUniqueId());
        if (bar == null) {
            return;
        }

        BossRushEvent event = plugin.getBossRushEventManager().getBossRushEvent();
        if (event == null || !event.isRunning()) {
            return;
        }

        int eventTime = event.getTime();
        int eventMaxTime = event.getMaxTime();

        if (eventMaxTime <= 0) {
            bar.setProgress(1.0);
            return;
        }

        int remainingTime = eventMaxTime - eventTime;
        double progress = (double) remainingTime / eventMaxTime;
        progress = Math.max(0.0, Math.min(1.0, progress));

        bar.setProgress(progress);
    }

    public void updateAllProgress() {
        BossRushEvent event = plugin.getBossRushEventManager().getBossRushEvent();
        if (event == null || !event.isRunning()) {
            return;
        }

        int eventTime = event.getTime();
        int eventMaxTime = event.getMaxTime();

        if (eventMaxTime <= 0) {
            return;
        }

        int remainingTime = eventMaxTime - eventTime;
        double progress = Math.max(0.0, Math.min(1.0, (double) remainingTime / eventMaxTime));

        for (BossBar bar : bossBars.values()) {
            bar.setProgress(progress);
        }
    }

    public void unregisterPlayerBossBar(Player player) {
        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        BossBar bar = bossBars.remove(uuid);

        if (bar != null) {
            bar.removePlayer(player);
            bar.removeAll();
        }
    }

    public void unregisterAllPlayersBossBar() {
        for (Map.Entry<UUID, BossBar> entry : bossBars.entrySet()) {
            BossBar bar = entry.getValue();
            if (bar != null) {
                bar.removeAll();
            }
        }
        bossBars.clear();
    }

    public int getActiveBossBars() {
        return bossBars.size();
    }

    public boolean hasBossBar(Player player) {
        return player != null && bossBars.containsKey(player.getUniqueId());
    }

    public void cleanup() {
        unregisterAllPlayersBossBar();
    }
}
