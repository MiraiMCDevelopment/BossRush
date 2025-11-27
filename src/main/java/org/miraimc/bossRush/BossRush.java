package org.miraimc.bossRush;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.miraimc.bossRush.event.BossRushEventManager;
import org.miraimc.bossRush.event.command.BossRushCommand;
import org.miraimc.bossRush.event.listener.BossRushListener;
import org.miraimc.bossRush.schedule.ScheduleManager;
import org.miraimc.bossRush.util.BasicConfigFile;

@Getter
public final class BossRush extends JavaPlugin {

    private BasicConfigFile configFile, langFile, eventsFile;
    private BossRushEventManager bossRushEventManager;
    private ScheduleManager scheduleManager;

    @Override
    public void onEnable() {
        getLogger().info("Enabling BossRush");
        loadConfigFiles();
        registerManagers();
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling BossRush");
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
        scheduleManager = new ScheduleManager(this);
        scheduleManager.loadSchedules();
    }

    private void registerListeners() {
        getLogger().info("Registering BossRush listeners");
        getServer().getPluginManager().registerEvents(new BossRushListener(this), this);
    }

    private void registerCommands() {
        getLogger().info("Registering BossRush commands");
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new BossRushCommand(this));
    }
}
