package net.heinousgames.game.ecgamejam;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.heinousgames.game.ecgamejam.screens.HGScreen;

public class Main extends Game {

    private static final float COLOR_FREQUENCY = 0.21f;

    public AssetManager assetManager;
    public float red, green, blue;
    private float colorCounter;
    public ImageButton.ImageButtonStyle stylePlay, styleExit;
    public Music bgMusic;
	public Preferences prefs;
    public ShapeRenderer shapeRenderer;
    public Sound buttonClick;
	public SpriteBatch batch;
	Texture buttons, windows;
	public Texture bg;
	public TmxMapLoader mapLoader;

	@Override
	public void create () {
        assetManager = new AssetManager();
        assetManager.load("Ceci Beats - Beach Daze.mp3", Music.class);
        batch = new SpriteBatch();
        mapLoader = new TmxMapLoader();
        prefs = Gdx.app.getPreferences("ExtraCreditsGameJamSave");
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("button_click.mp3"));

        bg = new Texture("BG.png");
        buttons = new Texture("Buttons.png");
        windows = new Texture("Windows.png");

        TextureRegion exitUp = new TextureRegion(buttons, 128, 4000, 190, 190);
        TextureRegion exitDown = new TextureRegion(buttons, 529, 4000, 190, 190);
        TextureRegion exitOver = new TextureRegion(buttons, 320, 4000, 190, 190);

        TextureRegion playUp = new TextureRegion(buttons, 128, 760, 190, 190);
        TextureRegion playDown = new TextureRegion(buttons, 529, 760, 190, 190);
        TextureRegion playOver = new TextureRegion(buttons, 320, 760, 190, 190);

        stylePlay = new ImageButton.ImageButtonStyle();
        stylePlay.up = new TextureRegionDrawable(new TextureRegion(playUp));
        stylePlay.down = new TextureRegionDrawable(new TextureRegion(playDown));
        stylePlay.over = new TextureRegionDrawable(new TextureRegion(playOver));

        styleExit = new ImageButton.ImageButtonStyle();
        styleExit.up = new TextureRegionDrawable(new TextureRegion(exitUp));
        styleExit.down = new TextureRegionDrawable(new TextureRegion(exitDown));
        styleExit.over = new TextureRegionDrawable(new TextureRegion(exitOver));

        shapeRenderer = new ShapeRenderer();
        colorCounter = 0f;
        red = green = blue = 1;

        setScreen(new HGScreen(this));
	}

	@Override
	public void render () {
	    super.render();
        red = (float) (Math.sin(COLOR_FREQUENCY * colorCounter + 0) * 127 + 128) / 255f;
        green = (float) (Math.sin(COLOR_FREQUENCY * colorCounter + 2) * 127 + 128) / 255f;
        blue = (float) (Math.sin(COLOR_FREQUENCY * colorCounter + 4) * 127 + 128) / 255f;
        colorCounter += Gdx.graphics.getDeltaTime() * 11f;
    }
	
	@Override
	public void dispose () {
	    assetManager.dispose();
	    batch.dispose();
	    buttonClick.dispose();
//	    bgMusic.dispose();

	    bg.dispose();
	    buttons.dispose();
	    windows.dispose();

	    shapeRenderer.dispose();
	}
}
