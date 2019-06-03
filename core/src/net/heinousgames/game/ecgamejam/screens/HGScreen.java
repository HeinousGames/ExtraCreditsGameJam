package net.heinousgames.game.ecgamejam.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;

import net.heinousgames.game.ecgamejam.Constants;
import net.heinousgames.game.ecgamejam.Main;

/**
 * Created by Steve on 1/17/2016
 */
public class HGScreen implements Screen {

    private boolean timerSet;
    private final Main main;
    private OrthographicCamera camera;
    private Texture logo;

    public HGScreen(final Main main) {
        this.main = main;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 512);
        camera.position.x = 512;
        camera.position.y = 256;

        main.batch.setProjectionMatrix(camera.combined);

        logo = new Texture("hgLogo.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        Gdx.gl.glClearColor(0.1451f, 0.102f, 0.2f, 1);
        camera.update();

        main.batch.begin();
        main.batch.draw(logo, 0, 0);
        //, 256, 128, main.hgLogo.getWidth()/2, main.hgLogo.getHeight()/2);
        main.batch.end();

        if (main.assetManager.update()) {
            if (!timerSet) {
                timerSet = true;
                main.bgMusic = main.assetManager.get("Ceci Beats - Beach Daze.mp3", Music.class);
                main.bgMusic.setLooping(true);
                if (main.prefs.getBoolean(Constants.MUSIC_OPTION)) {
                    main.bgMusic.play();
                }
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        main.setScreen(new StartScreen(main));
                        dispose();
                    }
                }, 1);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        logo.dispose();
    }

}