package org.miraimc.bossRush.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;

import java.util.UUID;

@Getter
@Setter
public class BossRushEvent {

    private String name;
    private EntityType entityType;
    private int time;
    private int maxTime;
    private boolean running;
    private UUID winnerUuid;
    private long startedAt;
    private long endedAt;

    public BossRushEvent(String name, EntityType entityType, int maxTime) {
        this.name = name;
        this.entityType = entityType;
        this.maxTime = maxTime;
        this.running = false;
        this.winnerUuid = null;
    }
}
