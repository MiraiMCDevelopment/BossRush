package org.miraimc.miraiSMPEvents.event;

import lombok.Getter;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.miraimc.miraiSMPEvents.MiraiSMPEvents;
import org.miraimc.miraiSMPEvents.util.TextUtil;

import java.util.List;

public class SMPEventManager {

    private final MiraiSMPEvents plugin;
    @Getter private SMPEvent smpEvent;

    public SMPEventManager(MiraiSMPEvents plugin) {
        this.plugin = plugin;
    }

    public boolean loadEvent(String name) {
        String path = "events." + name + ".";
        ConfigurationSection section = plugin.getEventsFile().getConfigurationSection(path);
        if (section == null) {
            return false;
        }

        String entityStr = section.getString("entity");
        EntityType entity = EntityType.fromName(entityStr);
        int time = section.getInt("time");

        smpEvent = new SMPEvent(name, entity, time);
        return true;
    }

    public void startEvent(SMPEvent smpEvent) {
        smpEvent.setStartedAt(System.currentTimeMillis());
        smpEvent.setRunning(true);

        String path = "events." + smpEvent.getName() + ".";
        ConfigurationSection section = plugin.getEventsFile().getConfigurationSection(path + "started");

        String titleStr = section.getString("title");
        String subtitleStr = section.getString("subtitle");
        List<String> messages = section.getStringList("messages");

        Bukkit.getOnlinePlayers().forEach(player -> {
            Title title = Title.title(
                    TextUtil.translate(titleStr),
                    TextUtil.translate(subtitleStr)
            );
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.0F, 1.0F);
            messages.forEach(player::sendMessage);
        });
    }

    public void endEvent(SMPEvent smpEvent) {
        endEvent(smpEvent, null);
    }

    public void endEvent(SMPEvent smpEvent, Player winner) {
        smpEvent.setEndedAt(System.currentTimeMillis());
        smpEvent.setRunning(false);

        String path = "events." + smpEvent.getName() + ".";
        ConfigurationSection section = plugin.getEventsFile().getConfigurationSection(path + "ended");

        String titleStr = section.getString("title");
        String subtitleStr = section.getString("subtitle");
        List<String> messages = section.getStringList("messages");

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (winner == null) {
                player.sendMessage(plugin.getLangFile().getString("event.force_ended"));
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
                return;
            }

            Title title = Title.title(
                    TextUtil.translate(titleStr.replace("{winner}", winner.getName())),
                    TextUtil.translate(subtitleStr.replace("{winner}", winner.getName()))
            );
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
            messages.forEach(player::sendMessage);
        });
    }
}
