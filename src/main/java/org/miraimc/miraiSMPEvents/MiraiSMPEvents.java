package org.miraimc.miraiSMPEvents;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.miraimc.miraiSMPEvents.event.SMPEventManager;
import org.miraimc.miraiSMPEvents.event.command.SMPEventCommand;
import org.miraimc.miraiSMPEvents.event.listener.SMPEventListener;
import org.miraimc.miraiSMPEvents.util.BasicConfigFile;

public final class MiraiSMPEvents extends JavaPlugin {

    @Getter
    private BasicConfigFile configFile, langFile, eventsFile;
    @Getter
    private SMPEventManager smpEventManager;

    @Override
    public void onEnable() {
        getLogger().info("Enabling MiraiSMPEvents");
        loadConfigFiles();
        registerManagers();
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling MiraiSMPEvents");
    }

    private void loadConfigFiles() {
        getLogger().info("Loading MiraiSMPEvents config files...");
        configFile = new BasicConfigFile(this, "config.yml");
        langFile = new BasicConfigFile(this, "lang.yml");
        eventsFile = new BasicConfigFile(this, "events.yml");
    }

    private void registerManagers() {
        getLogger().info("Registering MiraiSMPEvents managers");
        smpEventManager = new SMPEventManager(this);
    }

    private void registerListeners() {
        getLogger().info("Registering MiraiSMPEvents listeners");
        getServer().getPluginManager().registerEvents(new SMPEventListener(this), this);
    }

    private void registerCommands() {
        getLogger().info("Registering MiraiSMPEvents commands");
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new SMPEventCommand(this));
    }
}
