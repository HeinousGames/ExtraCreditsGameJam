package net.heinousgames.game.ecgamejam.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import net.heinousgames.game.ecgamejam.Constants;
import net.heinousgames.game.ecgamejam.Main;

public class SettingsScreen implements Screen {

    private boolean prefMusic, prefSfx;
    private Image imgMusic, imgSfx;
    private Main main;
    private Stage stageUI;
    private TextureRegion musicOn, musicOff, sfxOn, sfxOff;

    SettingsScreen(final Main main) {
        this.main = main;

        OrthographicCamera camera = new OrthographicCamera();

        stageUI = new Stage(new StretchViewport(960, 540, camera));
        Gdx.input.setInputProcessor(stageUI);

        prefMusic = main.prefs.getBoolean(Constants.MUSIC_OPTION, true);
        prefSfx = main.prefs.getBoolean(Constants.SFX_OPTION, true);

        musicOn = new TextureRegion(main.buttons, 128, 4432, 190, 190);
        musicOff = new TextureRegion(main.buttons, 743, 4432, 190, 190);
        sfxOn = new TextureRegion(main.buttons, 128, 4648, 190, 190);
        sfxOff = new TextureRegion(main.buttons, 743, 4648, 190, 190);

        imgMusic = new Image(musicOn);
        imgSfx = new Image(sfxOn);

        imgMusic.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                prefMusic = !prefMusic;
                setMusicDrawable(main.prefs);
            }
        });

        imgSfx.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                prefSfx = !prefSfx;
                setSoundDrawable(main.prefs);
            }
        });

        setMusicDrawable(main.prefs);
        setSoundDrawable(main.prefs);

        Table btnTable = new Table();
        btnTable.setSize(960, 540);
        btnTable.add(imgMusic).prefSize(190, 190).padRight(48);
        btnTable.add(imgSfx).prefSize(190, 190).padLeft(48);

        stageUI.addActor(btnTable);

        ImageButton exitButton = new ImageButton(main.styleExit);
        exitButton.setBounds(0, 480, 60, 60);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                        main.buttonClick.play();
                    }
                    dispose();
                    main.setScreen(new StartScreen(main));
                }
            }
        });
        stageUI.addActor(exitButton);

        main.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stageUI);
        if (main.prefs.getBoolean(Constants.MUSIC_OPTION)) {
            if (!main.bgMusic.isPlaying()) {
                main.bgMusic.play();
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        main.batch.begin();
        main.batch.draw(main.bg, 0, 0);
        main.batch.end();

        stageUI.act();
        stageUI.draw();
    }

    @Override
    public void resize(int width, int height) {
        stageUI.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stageUI.dispose();
    }

    private void setMusicDrawable(Preferences preferences) {
        if (prefMusic) {
            imgMusic.setDrawable(new TextureRegionDrawable(musicOn));
            if (!main.bgMusic.isPlaying()) {
                main.bgMusic.play();
            }
        } else {
            imgMusic.setDrawable(new TextureRegionDrawable(musicOff));
            if (main.bgMusic.isPlaying()) {
                main.bgMusic.stop();
            }
        }
        preferences.putBoolean(Constants.MUSIC_OPTION, prefMusic).flush();
    }

    private void setSoundDrawable(Preferences preferences) {
        if (prefSfx) {
            imgSfx.setDrawable(new TextureRegionDrawable(sfxOn));
        } else {
            imgSfx.setDrawable(new TextureRegionDrawable(sfxOff));
        }
        preferences.putBoolean(Constants.SFX_OPTION, prefSfx).flush();
    }
}
