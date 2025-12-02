package org.miraimc.bossRush.event.reward;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.miraimc.bossRush.event.BossRushData;

import java.util.List;
import java.util.UUID;

public class RewardManager {

    public void giveRewards(BossRushData data, UUID winnerId) {
        Player winner = Bukkit.getPlayer(winnerId);
        if (winner == null || !winner.isOnline()) return;

        String winnerName = winner.getName();
        List<String> rewards = data.getRewardsAction();
        for (String reward : rewards) {
            String rewardAction = reward.replace("{winner}", winnerName);

            if (rewardAction.startsWith("[Console] ")) {
                String command = rewardAction.replace("[Console] ", "");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }
}
