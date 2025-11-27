package org.miraimc.bossRush.event.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushEvent;
import org.miraimc.bossRush.util.TextUtil;

import java.util.List;

@CommandAlias("bossrush")
@CommandPermission("bossrush.admin")
public class BossRushCommand extends BaseCommand {

    private final BossRush plugin;
    private static final String LINE = "&7&m----------------------------------------";

    public BossRushCommand(BossRush plugin) {
        this.plugin = plugin;
    }

    @Subcommand("info")
    public void onInfo(Player sender) {
        BossRushEvent event = plugin.getBossRushEventManager().getBossRushEvent();

        sender.sendMessage(TextUtil.translate(LINE));
        sender.sendMessage(TextUtil.translate("&b&lBoss Rush"));

        if (event == null) {
            sender.sendMessage(TextUtil.translate("&fEvent is not loaded."));
            sender.sendMessage(TextUtil.translate(LINE));
            return;
        }

        sender.sendMessage(TextUtil.translate("&fEvent Name: &b" + event.getName()));
        sender.sendMessage(TextUtil.translate("&fEvent Time: &b" + event.getTime()));
        sender.sendMessage(TextUtil.translate("&fEvent Time Limit: &b" + event.getMaxTime()));
        sender.sendMessage(TextUtil.translate("&fEvent Running: &b" + event.isRunning()));
        sender.sendMessage(TextUtil.translate("&bEvent Rewards:"));
        List<String> rewards = plugin.getEventsFile().getStringList("events." + event.getName() + ".rewards");
        for (String reward : rewards) {
            sender.sendMessage(TextUtil.translate(" " + reward));
        }
        sender.sendMessage(TextUtil.translate(LINE));
    }

    @Subcommand("load")
    public void onLoad(Player sender, String event) {
        if (!plugin.getBossRushEventManager().loadEvent(event)) {
            sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.unable_load")));
            return;
        }

        sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.loaded")
                .replace("{event}", event)));
    }

    @Subcommand("start")
    public void onStart(Player sender) {
        BossRushEvent bossRushEvent = plugin.getBossRushEventManager().getBossRushEvent();
        if (bossRushEvent == null || bossRushEvent.isRunning()) {
            sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.unable_start")));
            return;
        }

        plugin.getBossRushEventManager().startEvent(bossRushEvent);
    }

    @Subcommand("end")
    public void onEnd(Player sender) {
        BossRushEvent bossRushEvent = plugin.getBossRushEventManager().getBossRushEvent();
        if (bossRushEvent == null || !bossRushEvent.isRunning()) {
            sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.unable_end")));
            return;
        }
        plugin.getBossRushEventManager().endEvent(bossRushEvent);
    }

    @Subcommand("reload")
    public void onReload(Player sender) {
        plugin.getConfigFile().reload();
        plugin.getLangFile().reload();
        plugin.getEventsFile().reload();

        sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("reload")));
    }
}
