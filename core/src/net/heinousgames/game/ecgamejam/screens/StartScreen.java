package net.heinousgames.game.ecgamejam.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import net.heinousgames.game.ecgamejam.Main;

public class StartScreen implements Screen {

    private Main main;
//    private Music bgMusic;
    private Stage stageUI;

    public StartScreen(final Main main) {
        this.main = main;

//        ImageButton exitBtn = new ImageButton();

//        TextureRegion
//        Window.WindowStyle windowStyle = new Window.WindowStyle(new BitmapFont(), Color.WHITE,
//                new TextureRegionDrawable(new TextureRegion(new Texture("window.png"))));

        OrthographicCamera camera = new OrthographicCamera();

        stageUI = new Stage(new StretchViewport(960, 540, camera));
        Gdx.input.setInputProcessor(stageUI);

        TextureRegion exit = new TextureRegion(main.buttons, 128, 4000, 190, 190);
        TextureRegion play = new TextureRegion(main.buttons, 128, 761, 190, 190);

        Image imgExit = new Image(exit);
        Image imgPlay = new Image(play);

        imgExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    Gdx.app.exit();
                }
            }
        });

        imgPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    main.setScreen(new LevelSelectScreen(main));
                    dispose();
                }
            }
        });

        Table btnTable = new Table();
        btnTable.setSize(960, 540);
        btnTable.add(imgExit).prefSize(190, 190).padRight(48);
        btnTable.add(imgPlay).prefSize(190, 190).padLeft(48).row();

        stageUI.addActor(btnTable);

//        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("sfx/title_theme.mp3"));
//        bgMusic.setLooping(true);
//        bgMusic.play();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stageUI);
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
//        bgMusic.dispose();
    }
}
