# Rail Signal
A comprehensive solution to tracking and managing your rail system, in real time.

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