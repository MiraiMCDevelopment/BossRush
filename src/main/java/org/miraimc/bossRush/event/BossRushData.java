package org.miraimc.bossRush.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class BossRushData {

    private String id;
    private String displayName;
    private EntityType entityType;
    private int timeLimit;
    private String rewardsName;
    private List<String> rewardsAction;

    public BossRushData(String eventId, String displayName, EntityType entityType, int timeLimit, String rewardsName, List<String> rewardsAction) {
        if (eventId == null || eventId.isEmpty()) {
            throw new IllegalArgumentException("Event ID cannot be null or empty");
        }
        if (displayName == null || displayName.isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be null or empty");
        }
        if (entityType == null) {
            throw new IllegalArgumentException("Entity type cannot be null");
        }

        this.id = eventId;
        this.displayName = displayName;
        this.entityType = entityType;
        this.timeLimit = timeLimit > 0 ? timeLimit : 60 * 60 * 8;
        this.rewardsName = rewardsName != null ? rewardsName : "";
        this.rewardsAction = rewardsAction !=null ? rewardsAction : Collections.emptyList();
    }
}
