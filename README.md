# Reservations
Simple ticket system that allows users to get a number and/or schedule a time to get help.


**Reservations** is a [Minecraft](http://minecraft.net) server plugin for [Bukkit](http://http://bukkit.org) / [Spigot](http://spigotmc.org) / [Paper](http://papermc.io) that allows players to take a number to get help. Alternately they can type in a time to ask for help at a specific time.

### Installation

Drop the jar in your plugins folder and start the server.

## Usage
The root command is `reservations` but
- `reservations` The root command for the plugin. It can be changed via config.yml
- Sub-commands:
  - `make` Take a number or set a time to request help. Example uses:
    - `reservations make` If run by a player, take a number for this player. Username can be appended to give a number to a specific user. The user has to have logged into the server at least one time.
    - `reservations make Magnum1997 [time]` Make a timed appointment for Magnum1997. Time can be in format of [hh:mm]a [hh:mm]p [HH:mm] Example: 3:00am, 3:00pm 03:00 or 15:00
  - `list` or `view` - Shows a list of current players waiting and appointments that have been made.
  - `clear <number>` Remove a player from the waiting list.
  - `cancel <Player>` Cancel a timed appointment for <Player>
  - `wipe` Clear the entire numbered waiting list. Does not affect timed reservations.
  - `help` Super secret surprise :heavy-sarcasm:

For settings see [config.yml](https://github.com/Magnum97/Reservations/blob/master/src/main/resources/config.yml)\
I tried to put all options in there. It is commented to help guide you.\

### Contributing
This is a pretty basic plugin. If you have any thoughts, good or bad, I would like them or any suggestions for features or improvements.\
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[GNU GPLv3](https://tldrlegal.com/license/gnu-general-public-license-v3-(gpl-3))
