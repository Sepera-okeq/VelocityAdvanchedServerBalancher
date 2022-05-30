# VelocityAdvanchedServerBalancher
Advanced Velocity server balancer. With MultiPaper support.

Example config:
```json
{
  "lobby": ["lobby_1", "lobby_2", "lobby_3"],
  "auth": ["auth_1", "auth_2"],
  "main": ["cluster_1", "cluster_2", "cluster_3", "cluster_4" ]
}
```

When you connect to `#lobby`, you will be teleported to the smallest number of players, thereby balancing between these servers (`lobby_1`, `lobby_2` and `lobby_3`)
