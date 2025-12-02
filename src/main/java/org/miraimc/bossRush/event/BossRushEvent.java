package org.miraimc.bossRush.event;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BossRushEvent {

    private BossRushData bossRushData;
    private BossRushState state;
    private int duration;
    private UUID winner;
    private long startTime;
    private long endTime;

    public BossRushEvent() {
        reset();
    }

    public void reset() {
        this.bossRushData = null;
        this.state = BossRushState.WAITING;
        this.duration = 0;
        this.winner = null;
        this.startTime = 0L;
        this.endTime = 0L;
    }

    public void start(BossRushData data) {
        reset();
        this.bossRushData = data;
        this.state = BossRushState.RUNNING;
        this.startTime = System.currentTimeMillis();
    }

    public void end(UUID winner) {
        this.state = BossRushState.WAITING;
        this.endTime = System.currentTimeMillis();
        this.winner = winner;
    }

    public boolean isRunning() {
        return this.state == BossRushState.RUNNING;
    }

}
