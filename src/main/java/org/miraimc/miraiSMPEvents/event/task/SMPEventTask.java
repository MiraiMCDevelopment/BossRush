package org.miraimc.miraiSMPEvents.event.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.miraimc.miraiSMPEvents.MiraiSMPEvents;
import org.miraimc.miraiSMPEvents.event.SMPEvent;

public class SMPEventTask extends BukkitRunnable {

    private final MiraiSMPEvents plugin;

    public SMPEventTask(MiraiSMPEvents plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        SMPEvent smpEvent = plugin.getSmpEventManager().getSmpEvent();
        smpEvent.setTime(smpEvent.getTime() + 1);

        if (smpEvent.getTime() >= smpEvent.getMaxTime()) {
            plugin.getSmpEventManager().endEvent(smpEvent);
            cancel();
        }

    }
}
