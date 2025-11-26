package org.miraimc.miraiSMPEvents.event.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.miraimc.miraiSMPEvents.MiraiSMPEvents;
import org.miraimc.miraiSMPEvents.event.SMPEvent;

public class SMPEventListener implements Listener {

    private final MiraiSMPEvents plugin;

    public SMPEventListener(MiraiSMPEvents plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        SMPEvent smpEvent = plugin.getSmpEventManager().getSmpEvent();
        if (smpEvent == null || !smpEvent.isRunning()) {
            return;
        }

        if (event.getEntity().getKiller() == null) {
            return;
        }

        if (event.getEntityType() == smpEvent.getEntityType()) {
            Player killer = event.getEntity().getKiller();
            plugin.getSmpEventManager().endEvent(smpEvent, killer);
        }
    }
}
