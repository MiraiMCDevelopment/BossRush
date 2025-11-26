package org.miraimc.bossRush.event.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushEvent;
import org.miraimc.bossRush.util.TextUtil;

@CommandAlias("bossrush")
@CommandPermission("bossrush.admin")
public class BossRushCommand extends BaseCommand {

    private final BossRush plugin;

    public BossRushCommand(BossRush plugin) {
        this.plugin = plugin;
    }

    @Subcommand("start")
    public void onStart(Player sender, String event) {
        if (!plugin.getBossRushEventManager().loadEvent(event)) {
            sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.unable_load")));
            return;
        }

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
