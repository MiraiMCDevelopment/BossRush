package org.miraimc.miraiSMPEvents.event.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import org.miraimc.miraiSMPEvents.MiraiSMPEvents;
import org.miraimc.miraiSMPEvents.event.SMPEvent;
import org.miraimc.miraiSMPEvents.util.TextUtil;

@CommandAlias("miraismpevent|smpevent|event")
@CommandPermission("miraismpevent.admin")
public class SMPEventCommand extends BaseCommand {

    private final MiraiSMPEvents plugin;

    public SMPEventCommand(MiraiSMPEvents plugin) {
        this.plugin = plugin;
    }

    @Subcommand("start")
    public void onStart(Player sender, String event) {
        if (!plugin.getSmpEventManager().loadEvent(event)) {
            sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.unable_load")));
            return;
        }

        SMPEvent smpEvent = plugin.getSmpEventManager().getSmpEvent();
        if (smpEvent == null) {
            sender.sendMessage(TextUtil.translate(plugin.getLangFile().getString("event.unable_start")));
            return;
        }

        plugin.getSmpEventManager().startEvent(smpEvent);
    }

    @Subcommand("end")
    public void onEnd(Player sender) {
        SMPEvent smpEvent = plugin.getSmpEventManager().getSmpEvent();
        plugin.getSmpEventManager().endEvent(smpEvent);
    }
}
