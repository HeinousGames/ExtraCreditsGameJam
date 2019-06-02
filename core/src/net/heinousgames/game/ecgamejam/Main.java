package net.heinousgames.game.ecgamejam;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import net.heinousgames.game.ecgamejam.screens.StartScreen;

public class Main extends Game {

    public Music bgMusic;
	public Preferences prefs;
	public SpriteBatch batch;
	public Texture buttons, windows, bg;
	public TmxMapLoader mapLoader;

	@Override
	public void create () {
        batch = new SpriteBatch();
        mapLoader = new TmxMapLoader();
        prefs = Gdx.app.getPreferences("ExtraCreditsGameJamSave");
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("Ceci Beats - Beach Daze.mp3"));
        bgMusic.setLooping(true);
//        bgMusic.play();

        bg = new Texture("BG.png");
        buttons = new Texture("Buttons.png");
        windows = new Texture("Windows.png");

        setScreen(new StartScreen(this));
	}

	@Override
	public void render () {
	    super.render();
	}
	
	@Override
	public void dispose () {
	    batch.dispose();
	    bgMusic.dispose();

	    bg.dispose();
	    buttons.dispose();
	    windows.dispose();
	}
}
