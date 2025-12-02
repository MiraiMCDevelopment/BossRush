# BossRush
## Commands
- `/bossrush info`
- `/bossrush start <event>`
- `/bossrush end`
- `/bossrush reload`
## Permissions
- `bossrush.admin`
## Events.yml
```yaml
events:
  ender_dragon:
    display_name: "Ender Dragon"
    entity_type: ENDER_DRAGON
    time_limit: 28800
    rewards:
      name: "&bMVP&c+"
      action:
        - "[Console] ogrant {winner} MVP+ 7d global Boss Rush Winner"
```