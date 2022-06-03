#!/usr/bin/env dub
/+ dub.sdl:
    dependency "dsh" version="~>1.6.1"
    dependency "dxml" version="~>0.4.3"
    dependency "requests" version="~>2.0.8"
    dependency "d-properties" version="~>1.0.4"
+/

/**
 * This script will build the Rail Signal Vue app, then bundle it into this
 * Spring project's files under src/main/resources/app/, and will then build
 * this project into a jar file.
 */
module build_system;

import dsh;
import dxml.dom;
import dxml.util;

import std.stdio;
import std.string;
import std.algorithm;
import std.uni;

const DIST = "./src/main/resources/app";
const DIST_ORIGIN = "./quasar-app/dist/spa";
const APP_DIR = "./quasar-app";
const APP_BUILD = "quasar build -m spa";
const API_BUILD = "mvn clean package spring-boot:repackage -DskipTests=true";

const LOG_DIR = "./log";
const API_LOG = LOG_DIR ~ "/api_build.txt";
const APP_LOG = LOG_DIR ~ "/app_build.txt";

int main(string[] args) {
    string ver = getVersion();
    if (ver is null) {
        error("Could not determine version.");
        return 1;
    }
    removeIfExists(LOG_DIR);
    mkdir(LOG_DIR);
    print("Building Rail Signal v%s", ver);

    if (args.length >= 2) {
        string command = args[1].strip.toLower;
        if (command == "app") {
            buildApp();
        } else if (command == "api") {
            buildApi(ver);
        } else if (command == "all") {
            buildApp();
            buildApi(ver);
            if (args.length >= 3 && args[2].strip.toLower == "release") {
                print("Are you sure you want to create a GitHub release for version %s?", ver);
                string response = readln().strip.toLower;
                if (response == "yes" || response == "y") {
                    print("Please enter a short description for this release.");
                    string description = readln().strip;
                    createRelease(ver, description);
                }
            }
        }
    } else {
        buildApp();
        buildApi(ver);
    }

    return 0;
}

/** 
 * Builds the production version of the frontend app and injects it into the
 * API's resources to serve statically.
 */
void buildApp() {
    chdir(APP_DIR);
    print("Building app...");
    runOrQuit(APP_BUILD, "." ~ APP_LOG); // Use an extra dot because we moved into app dir.
    print("Copying dist from %s to %s", DIST_ORIGIN, DIST);
    chdir("..");
    removeIfExists(DIST);
    mkdir(DIST);
    copyDir(DIST_ORIGIN, DIST);
}

/** 
 * Builds the production version of the backend API.
 */
void buildApi(string ver) {
    print("Building API...");
    runOrQuit(API_BUILD, API_LOG);
    string[] jars = findFilesByExtension("target", ".jar", false);
    string jarFile = jars[0];
    string finalJarFile = "./target/rail-signal-" ~ ver ~ ".jar";
    // Clean up the jar file name.
    copy(jarFile, finalJarFile);
    print("Build complete. Created %s", finalJarFile);
}

/** 
 * Parses the version of the system from the pom file.
 * Returns: The version string, or null if it couldn't be found.
 */
string getVersion() {
    auto data = parseDOM!simpleXML(readText("pom.xml"));
    auto root = data.children[0];
    foreach (child; root.children) {
        if (child.name == "version") {
            return child.children[0].text;
        }
    }
    return null;
}

/** 
 * Creates a new GitHub release using the specified version, and uploads the
 * JAR file to the release.
 * Params:
 *   ver = The version.
 */
void createRelease(string ver, string description) {
    import d_properties;
    import requests;
    import std.json;

    print("Creating release...");

    JSONValue data = [
        "tag_name": "v" ~ ver,
        "name": "Rail Signal v" ~ ver,
        "body": description
    ];
    data.object["prerelease"] = JSONValue(false);
    data.object["generate_release_notes"] = JSONValue(false);

    auto rq = Request();
    auto props = Properties("github_token.properties");
    string username = props["username"];
    string token = props["token"];
    rq.authenticator = new BasicAuthentication(username, token);
    auto response = rq.post(
        "https://api.github.com/repos/andrewlalis/RailSignalAPI/releases",
        data.toString,
        "application/json"
    );
    if (response.code == 201) {
        string responseBody = cast(string) response.responseBody;
        JSONValue responseData = parseJSON(responseBody);
        print("Created release %s", responseData["url"].str);
        // Use the "upload-asset.sh" script to upload the asset, since internal requests api is broken.
        string command = format!"./upload-asset.sh github_api_token=%s owner=andrewlalis repo=RailSignalAPI tag=v%s filename=%s"(
            token,
            ver,
            "./target/rail-signal-" ~ ver ~ ".jar"
        );
        runOrQuit(command);
    } else {
        error("An error occurred while creating the release.");
        writeln(response.responseBody);
    }
}
    
