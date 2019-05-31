package net.heinousgames.game.ecgamejam;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import net.heinousgames.game.ecgamejam.screens.HeinousScreen;

public class Main extends Game {

    public Music bgMusic;
	public Preferences preferences;
	public SpriteBatch batch;
	public TmxMapLoader mapLoader;

	@Override
	public void create () {
        batch = new SpriteBatch();
        mapLoader = new TmxMapLoader();
        preferences = Gdx.app.getPreferences("ExtraCreditsGameJamSave");
        setScreen(new HeinousScreen(this, "tiger.tmx"));
		batch = new SpriteBatch();
	}

	@Override
	public void render () {
	    super.render();
	}
	
	@Override
	public void dispose () {
	    batch.dispose();
	    bgMusic.dispose();
	}
}
