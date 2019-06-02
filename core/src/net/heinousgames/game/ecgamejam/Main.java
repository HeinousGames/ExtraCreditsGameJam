package net.heinousgames.game.ecgamejam;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.heinousgames.game.ecgamejam.screens.StartScreen;

public class Main extends Game {

    public ImageButton.ImageButtonStyle stylePlay, styleExit;
    public Music bgMusic;
	public Preferences prefs;
	public Sound buttonClick;
	public SpriteBatch batch;
	Texture buttons;
	public Texture bg;
	public Texture windows;
	public TmxMapLoader mapLoader;

	@Override
	public void create () {
        batch = new SpriteBatch();
        mapLoader = new TmxMapLoader();
        prefs = Gdx.app.getPreferences("ExtraCreditsGameJamSave");
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("button_click.mp3"));
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("Ceci Beats - Beach Daze.mp3"));
        bgMusic.setLooping(true);

        bg = new Texture("BG.png");
        buttons = new Texture("Buttons.png");
        windows = new Texture("Windows.png");

        TextureRegion exitUp = new TextureRegion(buttons, 128, 4000, 190, 190);
        TextureRegion exitDown = new TextureRegion(buttons, 534, 4000, 190, 190);
        TextureRegion exitOver = new TextureRegion(buttons, 324, 4000, 190, 190);

        TextureRegion playUp = new TextureRegion(buttons, 128, 760, 190, 190);
        TextureRegion playDown = new TextureRegion(buttons, 534, 760, 190, 190);
        TextureRegion playOver = new TextureRegion(buttons, 324, 760, 190, 190);

        stylePlay = new ImageButton.ImageButtonStyle();
        stylePlay.up = new TextureRegionDrawable(new TextureRegion(playUp));
        stylePlay.down = new TextureRegionDrawable(new TextureRegion(playDown));
        stylePlay.over = new TextureRegionDrawable(new TextureRegion(playOver));

        styleExit = new ImageButton.ImageButtonStyle();
        styleExit.up = new TextureRegionDrawable(new TextureRegion(exitUp));
        styleExit.down = new TextureRegionDrawable(new TextureRegion(exitDown));
        styleExit.over = new TextureRegionDrawable(new TextureRegion(exitOver));

        setScreen(new StartScreen(this));
	}

	@Override
	public void render () {
	    super.render();
	}
	
	@Override
	public void dispose () {
	    batch.dispose();
	    buttonClick.dispose();
	    bgMusic.dispose();

	    bg.dispose();
	    buttons.dispose();
	    windows.dispose();
	}
}
