package com.josephcatrambone.debatesimulator.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.josephcatrambone.debatesimulator.GDXMain;

// TODO: Delete this before release.
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class DesktopLauncher {
	public static void main (String[] arg) {
		System.out.println("DEBUG: Packing textures into atlas.  Remove this before release.");
		packTextures();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640*2;
		config.height = 480*2;
		config.title = "Debate Simulator 2016";
		new LwjglApplication(new GDXMain(), config);
	}

	public static void packTextures() {
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.combineSubdirectories = true;
		settings.maxWidth=2048;
		settings.maxHeight=2048;
		TexturePacker.process(settings, "../../unpacked", "./", "main");
	}
}
