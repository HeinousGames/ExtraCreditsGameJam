package net.heinousgames.game.ecgamejam.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import net.heinousgames.game.ecgamejam.Constants;
import net.heinousgames.game.ecgamejam.Main;

public class StartScreen implements Screen {

    private Main main;
    private Stage stageUI;
    private Texture gameTitle;

    StartScreen(final Main main) {
        this.main = main;

        gameTitle = new Texture("cool_logo.png");
        Image imgTitle = new Image(gameTitle);

        OrthographicCamera camera = new OrthographicCamera();

        stageUI = new Stage(new StretchViewport(960, 540, camera));
        Gdx.input.setInputProcessor(stageUI);

        ImageButton playButton = new ImageButton(main.stylePlay);
        ImageButton settingsButton = new ImageButton(main.styleSettings);
        ImageButton infoButton = new ImageButton(main.styleInfo);

        infoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                        main.buttonClick.play();
                    }
                    main.setScreen(new InfoScreen(main));
                    dispose();
                }
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                        main.buttonClick.play();
                    }
                    main.setScreen(new SettingsScreen(main));
                    dispose();
                }
            }
        });

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                        main.buttonClick.play();
                    }
                    main.setScreen(new LevelSelectScreen(main));
                    dispose();
                }
            }
        });

        Table btnTable = new Table();
        btnTable.setSize(960, 540);
        btnTable.add(imgTitle).colspan(3).row();
        btnTable.add(playButton).prefSize(190, 190).padRight(24);
        btnTable.add(settingsButton).prefSize(190, 190).padLeft(24).padRight(24);
        btnTable.add(infoButton).prefSize(190, 190).padLeft(24);

        stageUI.addActor(btnTable);

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
        gameTitle.dispose();
    }
}
