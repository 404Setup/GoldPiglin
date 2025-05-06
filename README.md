# GoldPiglin
> This project is not completely finished yet, so there are usually some missing features.
>
> GoldPiglin will not be ported to ModLoader, as there are already some mods with similar functionality there.

**When you wear armor with gold patterns, the effect is the same as wearing gold armor.**

## Feature
- Piglins will ignore you when wearing armor with gold patterns.
- Emulate vanilla behavior [in v24.11.1]
- Enhanced behaviors, such as eye tracking

## Adapter
> **It is recommended to use an adapter with NMS mode**
> - All adapters should behave exactly the same.
> - The Spigot adapter is only available in Spigot and its forks (not in Paper).
> - The Paper adapter is only available in Paper and its forks (not in Spigot).
> 
- Spigot (NMS Mode) 1.20.1-1.21.5
- Paper (NMS Mode) 1.20.1-1.21.5
- NBTAPI 2.13.2
- RTag 1.5.10

## Usage
### Config
```yaml
# Select according to your needs.
# Supported adapters: Spigot, Paper, NBTAPI, Rtag
# The Spigot adapter is compatible with 1.20.1-1.21.5, if Minecraft releases
# an update then you have to wait for the new GoldPiglin version
#  (Paper adapter does not need to wait for updates most of the time).
adapter: NBTAPI
debug: false
update-message: true
hatred:
  expiration-time: 20
  expiration-scanner-time: 40
  near:
    # Area-wide hatred, closer to vanilla behavior, but may take longer to calculate.
    enabled: false
    x: 6
    y: 6
    z: 6
  can-see:
    # Whether only Piglin within the player's sight will trigger hatred
    enabled: true
    # Use Spigot's own canSee API instead of GoldPiglin's line of sight calculation
    native: false
    # Inverted line of sight calculations to calculate entity line of sight instead of player line of sight
    reversal: false
```

### Permission
- goldpiglin.command.reload [default: OP]
- goldpiglin.command.version [default: OP]
- goldpiglin.update_message [default: OP]

### Command
- /goldpiglin:gpiglin <reload|version> [/gpiglin]

## License
Use [Apache-2.0](https://github.com/404Setup/GoldPiglin?tab=Apache-2.0-1-ov-file#readme) as the license.

Any redistribution is prohibited (including but not limited to: recreating pages about this plugin anywhere;
redistributing binary builds of this plugin; including this plugin in server modpacks distributed to others; etc.).

## Release
[Modrinth - GoldPiglin](https://modrinth.com/plugin/goldpiglin)

[Curseforge - GoldPiglin](https://www.curseforge.com/minecraft/bukkit-plugins/goldpiglin)

[SpigotForum - GoldPiglin](https://www.spigotmc.org/resources/goldpiglin.120819)

[Hangar - GoldPiglin](https://hangar.papermc.io/Goal/goldpiglin)

[Github - GoldPiglin](https://github.com/404Setup/GoldPiglin/releases)

## Depend
- Java17 at least (**Java 21 is recommended**)
- [NBT-API v2.13.2 (Optional)](https://www.spigotmc.org/resources/nbt-api.7939/)
- [RTag v1.5.10 (Optional)](https://modrinth.com/plugin/rtag)