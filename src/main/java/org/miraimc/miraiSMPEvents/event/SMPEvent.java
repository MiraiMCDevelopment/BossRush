package org.miraimc.miraiSMPEvents.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;

import java.util.UUID;

@Getter
@Setter
public class SMPEvent {

    private String name;
    private EntityType entityType;
    private int time;
    private boolean running;
    private UUID winnerUuid;
    private long startedAt;
    private long endedAt;

    public SMPEvent(String name, EntityType entityType, int time) {
        this.name = name;
        this.entityType = entityType;
        this.time = time;
        this.running = false;
        this.winnerUuid = null;
    }
}
