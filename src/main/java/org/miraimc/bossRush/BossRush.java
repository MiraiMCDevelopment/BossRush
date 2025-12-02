package org.miraimc.bossRush;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.miraimc.bossRush.bossbar.BossBarListener;
import org.miraimc.bossRush.bossbar.BossBarManager;
import org.miraimc.bossRush.command.BossRushCommand;
import org.miraimc.bossRush.event.BossRushEventManager;
import org.miraimc.bossRush.event.listener.BossRushListener;
import org.miraimc.bossRush.schedule.ScheduleManager;
import org.miraimc.bossRush.util.BasicConfigFile;
import org.miraimc.bossRush.webhook.BossRushWebhook;

@Getter
public final class BossRush extends JavaPlugin {

    @Getter private static BossRush instance;
    private BasicConfigFile configFile, langFile, eventsFile;
    private BossRushEventManager bossRushEventManager;
    private BossBarManager bossBarManager;
    private ScheduleManager scheduleManager;
    private BossRushWebhook bossRushWebhook;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Enabling BossRush");
        loadConfigFiles();
        registerManagers();
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling BossRush");
        bossBarManager.cleanup();
    }

    private void loadConfigFiles() {
        getLogger().info("Loading BossRush config files...");
        configFile = new BasicConfigFile(this, "config.yml");
        langFile = new BasicConfigFile(this, "lang.yml");
        eventsFile = new BasicConfigFile(this, "events.yml");
    }

    private void registerManagers() {
        getLogger().info("Registering BossRush managers");
        bossRushEventManager = new BossRushEventManager(this);
        bossBarManager = new BossBarManager(this);
        scheduleManager = new ScheduleManager(this);
        scheduleManager.loadSchedules();
        bossRushWebhook = new BossRushWebhook(this);
    }

    private void registerListeners() {
        getLogger().info("Registering BossRush listeners");
        getServer().getPluginManager().registerEvents(new BossRushListener(this), this);
        getServer().getPluginManager().registerEvents(new BossBarListener(this), this);
    }

    private void registerCommands() {
        getLogger().info("Registering BossRush commands");
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new BossRushCommand(this));
    }
}
