# RailSignalAPI
A simple API for tracking rail traffic in signalled blocks.

You can download the program via [releases](https://github.com/andrewlalis/RailSignalAPI/releases).

Once you download the JAR file, you can simply run it with `java -jar <jarfile>`. Note that this program requires Java 17.

Once it's started up, navigate to http://localhost:8080 to view the RailSignal web interface, where you can make changes to your systems. You should start by creating a new rail system.

Once you've done that, you can go ahead and create some signals for your system.

## Immersive Railroading and ComputerCraft
To begin controlling your signals from within the game, you can set up a signal controller computer with a two detector augments on the rail (one for redstone, one for the computer connection) and two monitors. Make sure all perhipherals are connected to the network, and then run this command:
```
pastebin run Z72QhG7G
```
This will run an installation script that will guide you through setting up your signal's configuration.
