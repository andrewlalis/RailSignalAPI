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

const DIST = "./src/main/resources/app";
const DIST_ORIGIN = "./quasar-app/dist/spa";
const APP_BUILD = "quasar build -m spa";

void main(string[] args) {
    print("Building RailSignalAPI");
    chdir("quasar-app");
    print("Building app...");
    runOrQuit(APP_BUILD);
    print("Copying dist from %s to %s", DIST_ORIGIN, DIST);
    chdir("..");
    removeIfExists(DIST);
    mkdir(DIST);
    copyDir(DIST_ORIGIN, DIST);
    print("Building API...");
    runOrQuit("mvn clean package spring-boot:repackage -DskipTests=true");
    print("Build complete!");

    if (args.length > 1 && args[1] == "run") {
        string f = findFile("target", "^.+\\.jar$", false);
        if (f == null) {
            error("Could not find jar file!");
        } else {
            print("Running the program.");
            run("java -jar " ~ f);
        }
    }
}

