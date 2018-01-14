package com.josephcatrambone.debatesimulator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.josephcatrambone.debatesimulator.scenes.*;

import java.util.Stack;

public class GDXMain extends ApplicationAdapter {
	//public static final Stack<Scene> SCENE_STACK = new Stack<Scene>();
	// We are NOT using a scene stack this time because there are some cyclic transitions.
	public static Scene ACTIVE_SCENE = null;
	public static Intro INTRO_SCENE = null;
	public static MainMenu MAIN_MENU_SCENE = null;
	public static CharacterSelect CHARACTER_SELECT_SCENE = null;
	public static BriefingRoom BRIEFING_SCENE = null;
	public static Debate DEBATE_SCENE = null;
	public static ElectionResults ELECTON_SCENE = null;

	public static TextureAtlas TEXTURE_ATLAS = null; // Need to create this after OpenGL context.
	public static Preferences PREFERENCES = null;

	@Override
	public void create () {
		PREFERENCES = Gdx.app.getPreferences("DebateSimulator2016_Prefs");
		TEXTURE_ATLAS = new TextureAtlas(Gdx.files.internal("main.atlas"));

		// Set up basic preferences if unset.
		// To remove: PREFERENCES.flush();
		PREFERENCES.putInteger("SFX_VOLUME", 5);
		PREFERENCES.putInteger("MUSIC_VOLUME", 5);
		PREFERENCES.putInteger("TEXT_SPEED", 5);
		PREFERENCES.putBoolean("SHORT_GAME", true);

		// Front-load all of our scenes.
		// If this takes too much time we may have to run it async.
		INTRO_SCENE = new Intro();
		MAIN_MENU_SCENE = new MainMenu();
		CHARACTER_SELECT_SCENE = new CharacterSelect();
		BRIEFING_SCENE = new BriefingRoom();
		DEBATE_SCENE = new Debate();
		ELECTON_SCENE = new ElectionResults();

		// Start with the intro.
		ACTIVE_SCENE = INTRO_SCENE;
		//ACTIVE_SCENE = ELECTON_SCENE;
	}

	@Override
	public void render () {
		// Render
		ACTIVE_SCENE.render();

		// Logic
		float delta = Gdx.graphics.getDeltaTime();
		TweenManager.update(delta);
		ACTIVE_SCENE.update(delta);

		// If everything is dead, quit game.
		if(ACTIVE_SCENE == null) {
			dispose();
			Gdx.app.exit();
		}
	}
	
	@Override
	public void dispose () {
		INTRO_SCENE.dispose();
		MAIN_MENU_SCENE.dispose();

		TEXTURE_ATLAS.dispose();
	}
}
