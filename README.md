# BossRush
## Commands
- `/bossrush info`
- `/bossrush load <event>`
- `/bossrush start`
- `/bossrush end`
- `/bossrush reload`
## Permissions
- `bossrush.admin`
## Events.yml
```yaml
events:
  ender_dragon:
    entity: ENDER_DRAGON
    max_time: 1800
    started:
      title: "&b&lBoss Rush Started"
      subtitle: "&fObjective: Kill Ender Dragon"
      messages:
        - "&aEvent has been started!"
    ended:
      title: "&c&lBoss Rush Ended"
      subtitle: "&fWinner: &b{winner}"
      messages:
        - "&aEvent has been ended!"
        - "&fWinner: &b{winner}"
        - "&fTime: &b{time_ms}"
    rewards:
      - "[Console] say {winner} is winner!"
```