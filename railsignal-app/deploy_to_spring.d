#!/usr/bin/env dub
/+ dub.sdl:
    dependency "dsh" version="~>1.6.1"
+/
import dsh;

const DEST = "../src/main/resources/static";

void main() {
    print("Deploying Vue app to Spring's /static directory.");
    runOrQuit("vite build --base=/app/");
    rmdirRecurse(DEST);
    copyDir("./dist", DEST);
}

