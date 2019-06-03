package net.heinousgames.game.ecgamejam.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import net.heinousgames.game.ecgamejam.Constants;
import net.heinousgames.game.ecgamejam.Main;

public class InfoScreen implements Screen {

    private BitmapFont font;
    private GlyphLayout layout;
    private Main main;
    private OrthographicCamera camera, fontCamera;
    private Stage stageUI;

    InfoScreen(final Main main) {
        this.main = main;

        font = new BitmapFont();

        fontCamera = new OrthographicCamera(854, 480);
        fontCamera.position.set(427, 240, 0);
        camera = new OrthographicCamera();
        stageUI = new Stage(new StretchViewport(960, 540, camera));
        Gdx.input.setInputProcessor(stageUI);

        TextureRegion music = new TextureRegion(main.buttons, 128, 4432, 190, 190);
        TextureRegion musicDown = new TextureRegion(main.buttons, 529, 4432, 190, 190);
        TextureRegion musicOver = new TextureRegion(main.buttons, 320, 4432, 190, 190);

        TextureRegion coding = new TextureRegion(main.buttons, 1040, 4432, 190, 190);
        TextureRegion codingDown = new TextureRegion(main.buttons, 1442, 4432, 190, 190);
        TextureRegion codingOver = new TextureRegion(main.buttons, 1233, 4432, 190, 190);

        ImageButton.ImageButtonStyle  styleMusic = new ImageButton.ImageButtonStyle();
        styleMusic.up = new TextureRegionDrawable(music);
        styleMusic.down = new TextureRegionDrawable(musicDown);
        styleMusic.over = new TextureRegionDrawable(musicOver);

        ImageButton.ImageButtonStyle  styleCoding = new ImageButton.ImageButtonStyle();
        styleCoding.up = new TextureRegionDrawable(coding);
        styleCoding.down = new TextureRegionDrawable(codingDown);
        styleCoding.over = new TextureRegionDrawable(codingOver);

        ImageButton buttonMusic = new ImageButton(styleMusic);
        buttonMusic.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                        main.buttonClick.play();
                    }
                    Gdx.net.openURI("https://soundcloud.com/cecibeats");
                }
            }
        });

        ImageButton buttonCoding = new ImageButton(styleCoding);
        buttonCoding.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                        main.buttonClick.play();
                    }
                    Gdx.net.openURI("https://www.heinousgames.net");
                }
            }
        });

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

        layout = new GlyphLayout();
        layout.setText(font, "Connect the Dots was made by Heinous Games LLC (MI) as a part of the Extra Credits Game Jam.\n" +
                "Coding was handled by Steve Hanus and Ross King\n" +
                "Music produced by Ceci Beats.", Color.CYAN,//83.5f/255f, 94.5f/255f, 100f/255f, 1),
                854, Align.center, true);

        Table btnTable = new Table();
        btnTable.setSize(960, 540);
        btnTable.add(buttonCoding).size(100, 100).padRight(24);
        btnTable.add(buttonMusic).size(100, 100).padLeft(24).row();

        stageUI.addActor(btnTable);
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

        camera.update();
        fontCamera.update();

        main.batch.setProjectionMatrix(camera.combined);
        main.batch.begin();
        main.batch.draw(main.bg, 0, 0);
        main.batch.end();

        main.batch.setProjectionMatrix(fontCamera.combined);
        main.batch.begin();
        font.draw(main.batch, layout, 0, 360 + layout.height/2);
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
        font.dispose();
        stageUI.dispose();
    }
}
