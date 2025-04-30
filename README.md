# BeaconLabsCore

A comprehensive Spigot plugin for Minecraft 1.21 that offers a suite of server administration and utility features, including teleportation, chat management, player management, weather & time control, and customizable messages.

## Features

- Customizable join, leave, and death messages
- Global chat formatting and mute support (LuckPerms integration)
- Tab nametag colors via LuckPerms metadata
- Unknown command handler for mistyped commands
- TPS and server performance stats
- Random safe teleportation within world bounds
- Warp system with SQLite database storage
- Portable workbench and ender chest
- Inventory viewing and clearing
- Chat and command sudo
- Server-wide broadcast
- Weather and time control commands
- Player management: heal, gamemode, fly, vanish, spectate, clear lag

## Requirements

- Java 8 (or higher)
- Spigot API 1.21 (provided scope)
- LuckPerms API 5.4 (provided scope)

## Building

```powershell
mvn clean package
```

The shaded JAR will be located in `target/BeaconLabsCore-<version>-shaded.jar`.

## Installation

1. Copy the shaded JAR to your server's `plugins/` directory.
2. Start or reload the server to generate the default `config.yml`.
3. Configure messages and settings in `plugins/BeaconLabsCore/config.yml`.

## Configuration

The plugin generates a default `config.yml` on first run. Key settings include:

```yaml
plugin-prefix: "&6BeaconLabs &8» "
join-messages:
  enabled: true
  custom: "&7[&a+&7] &9{player}"
leave-messages:
  enabled: true
  custom: "&7[&c-&7] &9{player}"
death-messages:
  enabled: true
  custom: "&c{player} was killed by {other}"
warp-delay: 3
```

- Color codes: `&` syntax (e.g. `&c` for red)
- Placeholders: `{player}`, `{other}`
- Warp delay: Time in seconds that players must wait before being teleported to a warp (bypass with permission)

### Database

The plugin uses SQLite for storing warp locations:
- Warps are stored in `plugins/BeaconLabsCore/warps.db`
- No additional configuration is needed for the database

## Commands

### General

| Command     | Description                          | Usage        | Aliases                       |
| ----------- | ------------------------------------ | ------------ | ----------------------------- |
| /core       | Show plugin info                     | `/core`      | `beaconlabscore`, `labscore`  |
| /btps       | Display server TPS and entity counts | `/btps`      |                               |
| /globalmute | Toggle global chat mute              | `/globalmute`| `gmute`                       |

### Teleportation

| Command                | Description                        | Usage                           | Aliases            |
| ---------------------- | ---------------------------------- | ------------------------------- | ------------------ |
| /tp `<player>` [dest]  | Teleport to or teleport players    | `/tp <player> [to_player]`      | `teleport`         |
| /tphere `<player>`     | Bring player to you                | `/tphere <player>`              | `tph`              |
| /tpa `<player>`        | Send teleport request              | `/tpa <player>`                 |                    |
| /tpaccept `<player>`   | Accept teleport request            | `/tpaccept <player>`            |                    |
| /tpdeny [player]       | Deny teleport request              | `/tpdeny [player]`              |                    |
| /randomteleport        | Teleport to a random safe location | `/randomteleport`               | `rtp`              |
| /teleportcoordinates   | Teleport by coordinates            | `/tpc [player] <x> <y> <z>`     | `tpc`, `teleportc` |
| /warp `<name>`         | Teleport to a saved warp location  | `/warp <name>`                  |                    |
| /setwarp `<name>`      | Create a new warp                  | `/setwarp <name>`               |                    |
| /delwarp `<name>`      | Delete an existing warp            | `/delwarp <name>`               |                    |
| /warps                 | List all available warps           | `/warps`                        |                    |

### Chat & Server

| Command                     | Description                         | Usage                                | Aliases |
| --------------------------- | ----------------------------------- | ------------------------------------ | ------- |
| /chatsudo `<player>` <msg>  | Send chat as another player         | `/chatsudo <player> <message>`       | `csudo` |
| /sudo `<player>` <cmd>      | Execute command as another player   | `/sudo <player> <command>`           |         |
| /serverbroadcast <message>  | Broadcast server-wide message       | `/serverbroadcast <message>`         | `sbc`   |

### Weather & Time

| Command   | Description                  | Usage                             | Aliases |
| --------- | ---------------------------- | --------------------------------- | ------- |
| /weather  | Change weather               | `/weather <clear|rain|storm>`     | `w`     |
| /clear    | Clear weather                | `/clear`                          |         |
| /rain     | Set rain                     | `/rain`                           |         |
| /storm    | Set storm                    | `/storm`                          |         |
| /time     | Change time                  | `/time <value|day|night|noon|midnight>` |         |
| /day      | Set time to day              | `/day`                            |         |
| /night    | Set time to night            | `/night`                          |         |
| /noon     | Set time to noon             | `/noon`                           |         |
| /midnight | Set time to midnight         | `/midnight`                       |         |

### Inventory & Utilities

| Command                 | Description                         | Usage                           | Aliases               |
| ----------------------- | ----------------------------------- | ------------------------------- | --------------------- |
| /invsee `<player>`      | View another player's inventory     | `/invsee <player>`              |                       |
| /workbench              | Open portable workbench             | `/workbench`                    | `wb`                  |
| /enderchest             | Open portable ender chest           | `/enderchest`                   | `ec`                  |
| /clearinventory [player]| Clear player inventory              | `/clearinventory [player]`      | `clearinv`, `cinv`    |

### Player Management

| Command                   | Description                            | Usage                             | Aliases     |
| ------------------------- | -------------------------------------- | --------------------------------- | ----------- |
| /heal [player] [nonotify] | Heal self or another player            | `/heal [player] [nonotify]`       |             |
| /gamemode                 | Change game mode                       | `/gamemode <mode> [player] [nonotify]` | `gm`, `mode` |
| /fly [nonotify]           | Toggle flight                          | `/fly [nonotify]`                 |             |
| /vanish [player]          | Toggle invisibility                    | `/vanish [player]`                | `v`         |
| /spectate `<player>`      | Spectate a player                      | `/spectate <player>`              | `spec`      |
| /endspectate              | End spectator mode                     | `/endspectate`                    | `espec`     |

## Permissions

Permission nodes are defined in `src/main/resources/plugin.yml`. Some examples:

- `beaconlabs.core.vanish.self` — Allows a player to vanish themselves (default: OP)
- `beaconlabs.core.spectate` — Allows spectating other players (default: OP) 
- `beaconlabs.core.tpa` — Allows sending teleport requests (default: non-OP)
- `beaconlabs.core.warp` — Allows using warp locations (default: non-OP)
- `beaconlabs.core.warps` — Allows viewing the list of warps (default: non-OP)
- `beaconlabs.core.setwarp` — Allows creating warp points (default: OP)
- `beaconlabs.core.delwarp` — Allows deleting warp points (default: OP)
- etc.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes and open a pull request
