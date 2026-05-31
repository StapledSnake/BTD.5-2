package com.mygame;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Launcher {
    public static void main (String[] args) {
        MyGame game = new MyGame();
        Lwjgl3Application launcher = new Lwjgl3Application(game, getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Procedural Tower Defense");
        configuration.setWindowedMode(800, 500);
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);
        return configuration;
    }
}