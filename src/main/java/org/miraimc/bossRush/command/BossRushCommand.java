package org.miraimc.bossRush.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
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

    @Subcommand("debug")
    public void onDebug(Player sender) {
        BossRushEvent event = plugin.getBossRushManager().getBossRushEvent();

        sender.sendMessage(TextUtil.translate(LINE));
        sender.sendMessage(TextUtil.translate("&b&lBoss Rush"));

        if (event == null) {
            sender.sendMessage(TextUtil.translate("&fEvent is null."));
            sender.sendMessage(TextUtil.translate(LINE));
            return;
        }

        if (event.getBossRushData() == null) {
            sender.sendMessage(TextUtil.translate("&fBossRushData is null."));
            sender.sendMessage(TextUtil.translate(LINE));
            return;
        }

        sender.sendMessage(TextUtil.translate("&fEvent Name: &b" + event.getBossRushData().getDisplayName()));
        sender.sendMessage(TextUtil.translate("&fEvent Time: &b" + event.getDuration()));
        sender.sendMessage(TextUtil.translate("&fEvent Time Limit: &b" + event.getBossRushData().getTimeLimit()));
        sender.sendMessage(TextUtil.translate("&fEvent Running: &b" + event.isRunning()));
        sender.sendMessage(TextUtil.translate("&bEvent Rewards:"));
        List<String> rewardsAction = plugin.getEventsFile().getRawStringList("events." + event.getBossRushData().getDisplayName() + ".rewards.action");
        for (String reward : rewardsAction) {
            sender.sendMessage(ColorParser.of(" " + reward).papi(sender).legacy().build());
        }
        sender.sendMessage(TextUtil.translate(LINE));
    }

    @Subcommand("load")
    public void onLoad(Player sender, String eventId) {
        try {
            plugin.getBossRushManager().loadData(eventId);
            sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.loaded")
                .replace("{event}", eventId)));
        } catch (Exception e) {
            sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.unable_load")));
        }
    }


    @Subcommand("start")
    public void onStart(Player sender, String eventId) {
        BossRushEvent event = plugin.getBossRushManager().getBossRushEvent();
        if (event == null || event.isRunning()) {
            sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.unable_start")));
            return;
        }

        plugin.getBossRushManager().startEvent(eventId);
    }

    @Subcommand("end")
    public void onEnd(Player sender) {
        BossRushEvent event = plugin.getBossRushManager().getBossRushEvent();
        if (event == null || !event.isRunning()) {
            sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.unable_end")));
            return;
        }
        plugin.getBossRushManager().endEvent(null);
    }

    @Subcommand("reload")
    public void onReload(Player sender) {
        plugin.getConfigFile().reload();
        plugin.getLangFile().reload();
        plugin.getEventsFile().reload();
        plugin.getBossRushManager().loadAllData();
        plugin.getScheduleManager().loadSchedules();

        sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("reload")));
    }
}
