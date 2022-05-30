#!/usr/bin/env dub
/+ dub.sdl:
    dependency "dsh" version="~>1.6.1"
+/

/**
 * This script will build the Rail Signal Vue app, then bundle it into this
 * Spring project's files under src/main/resources/app/, and will then build
 * this project into a jar file.
 */
module build_system;

import dsh;
import std.stdio;
import std.string;

const DIST = "./src/main/resources/app";
const DIST_ORIGIN = "./quasar-app/dist/spa";
const APP_BUILD = "quasar build -m spa";
const API_BUILD = "mvn clean package spring-boot:repackage -DskipTests=true";

void main(string[] args) {
    print("Building RailSignalAPI");
    string baseDomain = "localhost:8080";
    bool useHttps = false;
    if (args.length >= 2) {
        baseDomain = args[1];
        if (args.length >= 3 && args[2] == "secure") {
            useHttps = true;
            print("Will configure web app to use secure connections.");
        }
    }
    string apiUrl = format!"%s://%s/api"(useHttps ? "https" : "http", baseDomain);
    string wsUrl = format!"%s://%s/api/ws/app"(useHttps ? "wss" : "ws", baseDomain);
    print("Building web app using API url %s and WS url %s", apiUrl, wsUrl);
    sleepSeconds(3);

    chdir("quasar-app");
    print("Building app...");
    setEnv("RAIL_SIGNAL_API_URL", apiUrl);
    setEnv("RAIL_SIGNAL_WS_URL", wsUrl);
    runOrQuit(APP_BUILD);
    print("Copying dist from %s to %s", DIST_ORIGIN, DIST);
    chdir("..");
    removeIfExists(DIST);
    mkdir(DIST);
    copyDir(DIST_ORIGIN, DIST);

    print("Building API...");
    runOrQuit(API_BUILD);
    print("Build complete!");
    string jarFile = findFile(".", "\\.jar", false);

    print("Generating run script...");
    auto scriptFile = File("target/run.sh", "w");
    scriptFile.write("#!/usr/bin/bash\n");
    scriptFile.write("RAIL_SIGNAL_API_URL=http://localhost:8080/api\n");
    scriptFile.write("RAIL_SIGNAL_WS_URL=ws://localhost:8080/api/ws/app\n");
    scriptFile.write("java -jar " ~ jarFile ~ "\n");
    scriptFile.close();
    print("Script file generated.");
}

