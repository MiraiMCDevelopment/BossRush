package org.miraimc.bossRush.schedule;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.miraimc.bossRush.BossRush;
import org.miraimc.bossRush.event.BossRushEvent;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleManager {

    private final BossRush plugin;
    private final Map<String, BukkitTask> scheduledTasks = new HashMap<>();

    public ScheduleManager(BossRush plugin) {
        this.plugin = plugin;
    }

    public void loadSchedules() {
        cancelAllSchedules();

        List<String> schedules = plugin.getConfigFile().getStringList("event.schedules");
        if (schedules == null || schedules.isEmpty()) {
            plugin.getLogger().warning("No event schedules found!");
            return;
        }

        for (String scheduleStr : schedules) {
            try {
                parseAndSchedule(scheduleStr);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to parse schedule: " + scheduleStr);
                e.printStackTrace();
            }
        }
    }

    private void parseAndSchedule(String scheduleStr) {
        String[] parts = scheduleStr.split(";");

        if (parts.length != 3) {
            plugin.getLogger().warning("Invalid schedule: " + scheduleStr);
            return;
        }

        String dayOfWeek = parts[0];
        String time = parts[1];
        String eventName = parts[2];

        LocalTime targetTime = LocalTime.parse(time);

        if (dayOfWeek.equals("Daily")) {
            scheduleDailyEvent(targetTime, eventName, scheduleStr);
        } else {
            scheduleWeeklyEvent(dayOfWeek, targetTime, eventName, scheduleStr);
        }
    }

    private void scheduleDailyEvent(LocalTime targetTime, String eventName, String scheduleStr) {
        long delayTicks = calculateDelayTicks(targetTime);
        long periodTicks = 24 * 60 * 60 * 20L;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
           triggerEvent(eventName);
        }, delayTicks, periodTicks);

        scheduledTasks.put(scheduleStr, task);
        plugin.getLogger().info("Scheduled daily event: " + eventName + " at " + targetTime);
    }

    private void scheduleWeeklyEvent(String dayOfWeek, LocalTime targetTime, String eventName, String scheduleStr) {
        DayOfWeek targetDay;
        try {
            targetDay = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid day of week: " + dayOfWeek);
            return;
        }

        long delayTicks = calculateWeeklyDelayTicks(targetDay, targetTime);
        long periodTicks = 7 * 24 * 60 * 60 * 20L;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            triggerEvent(eventName);
        }, delayTicks, periodTicks);

        scheduledTasks.put(scheduleStr, task);
        plugin.getLogger().info("Scheduled weekly event: " + eventName + " on " + dayOfWeek + " at " + targetTime);
    }

    private long calculateDelayTicks(LocalTime targetTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(targetTime.getHour())
                .withMinute(targetTime.getMinute())
                .withSecond(0)
                .withNano(0);

        if (nextRun.isBefore(now) || nextRun.isEqual(now)) {
            nextRun = nextRun.plusDays(1);
        }

        long secondsUntil = ChronoUnit.SECONDS.between(now, nextRun);
        return secondsUntil * 20L;
    }

    private long calculateWeeklyDelayTicks(DayOfWeek targetDay, LocalTime targetTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(targetTime.getHour())
                .withMinute(targetTime.getMinute())
                .withSecond(0)
                .withNano(0);

        int currentDay = now.getDayOfWeek().getValue();
        int targetDayValue = targetDay.getValue();
        int daysUntil = targetDayValue - currentDay;

        if (daysUntil < 0 || (daysUntil == 0 && nextRun.isBefore(now))) {
            daysUntil += 7;
        }

        nextRun = nextRun.plusDays(daysUntil);

        long secondsUntil = ChronoUnit.SECONDS.between(now, nextRun);
        return secondsUntil * 20L;
    }

    private void triggerEvent(String eventName) {
        plugin.getLogger().info("Triggering scheduled event: " + eventName);
        plugin.getBossRushEventManager().loadEvent(eventName);
        BossRushEvent bossRushEvent = plugin.getBossRushEventManager().getBossRushEvent();
        if (bossRushEvent != null && !bossRushEvent.isRunning()) {
            plugin.getBossRushEventManager().startEvent(bossRushEvent);
        }
    }

    public void cancelAllSchedules() {
        for (BukkitTask task : scheduledTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        scheduledTasks.clear();
        plugin.getLogger().info("Cancelled all scheduled tasks");
    }

    public int getScheduleCount() {
        return scheduledTasks.size();
    }

}
