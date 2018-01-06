package com.josephcatrambone.debatesimulator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.josephcatrambone.debatesimulator.scenes.Intro;
import com.josephcatrambone.debatesimulator.scenes.Scene;

import java.util.Stack;

public class GDXMain extends ApplicationAdapter {
	public static final Stack<Scene> SCENE_STACK = new Stack<Scene>();
	public static TextureAtlas TEXTURE_ATLAS = null; // Need to create this after OpenGL context.
	public static Preferences PREFERENCES = Gdx.app.getPreferences("DebateSimulator2016_Prefs");

	@Override
	public void create () {
		TEXTURE_ATLAS = new TextureAtlas(Gdx.files.internal("main.atlas"));

		// Set up basic preferences if unset.
		// To remove: PREFERENCES.flush();
		PREFERENCES.putInteger("SFX_VOLUME", 5);
		PREFERENCES.putInteger("MUSIC_VOLUME", 5);
		PREFERENCES.putInteger("TEXT_SPEED", 5);
		PREFERENCES.putBoolean("SHORT_GAME", true);

		SCENE_STACK.push(new Intro());
	}

	@Override
	public void render () {
		SCENE_STACK.peek().render();
		SCENE_STACK.peek().update(Gdx.graphics.getDeltaTime());

		// If everything is dead, quit game.
		if(SCENE_STACK.isEmpty()) {
			dispose();
			Gdx.app.exit();
		}
	}
	
	@Override
	public void dispose () {
		while(!SCENE_STACK.isEmpty()) {
			SCENE_STACK.pop().dispose();
		}
		TEXTURE_ATLAS.dispose();
	}
}
