# Rail Signal
A comprehensive solution to tracking and managing your rail system, in real time.

## Setup
To set up your own Rail Signal, system, you will need to follow the following steps:
1. Download and run the Rail Signal API and web app. Go to the [releases](https://github.com/andrewlalis/RailSignalAPI/releases) page to download the latest release. Then, place the JAR file in your desired location, and run it with `java -jar rail-signal.jar`.
2. Open the web app (http://localhost:8080 by default) and create a new rail system, and add components and segments to build your network. More information about this will be given later.
3. Add components to your actual rail system, and install a driver script onto your device. For Minecraft rail systems using Immersive railroading and some computer mod, you can run `pastebin run jKyAiE8k` to run an installation script. *Please make an issue if you have a system for which there is not yet any available driver.*

## Development
To work on and develop Rail Signal, you will need to run both the Java/Spring-Boot backend API, and the Vue/Quasar frontend app.

To start up the API, the project directory in IntelliJ (or the IDE of your choice), and run the `RailSignalApiApplication` main method.

To start up the app, open a terminal in the `quasar-app` directory, and run `quasar dev`.

### Building
To build a complete API/app distributable JAR file, simply run the following:
```
./build_system.d
```
> Note: The build script requires the D language toolchain to be installed on your system. Also, you can compile `build_system.d` to a native executable to run the build script more efficiently.

This will produce a `rail-signal-api-XXX.jar` file in the `target` directory, which contains both the API, and the frontend app, packaged together so that the entire JAR can simply be run via `java -jar`.