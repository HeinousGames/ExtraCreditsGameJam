package net.heinousgames.game.ecgamejam.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import net.heinousgames.game.ecgamejam.LevelSelectTable;
import net.heinousgames.game.ecgamejam.Main;

public class LevelSelectScreen implements Screen {

    private final Main main;
    private Stage stageUI;

    public LevelSelectScreen(final Main main) {
        this.main = main;

        OrthographicCamera camera = new OrthographicCamera();

        stageUI = new Stage(new StretchViewport(960, 540, camera));

        LevelSelectTable table1 = new LevelSelectTable(main, 1);
        LevelSelectTable table2 = new LevelSelectTable(main, 2);
        LevelSelectTable table3 = new LevelSelectTable(main, 3);
//        LevelSelectTable table4 = new LevelSelectTable(main, 4, "1_4", "1_3");
//        LevelSelectTable table5 = new LevelSelectTable(main, 5, "1_5", "1_4");
//        LevelSelectTable table6 = new LevelSelectTable(main, 6, "1_6", "1_5");
//        LevelSelectTable table7 = new LevelSelectTable(main, 7, "1_7", "1_6");
//        LevelSelectTable table8 = new LevelSelectTable(main, 8, "1_8", "1_7");
//        LevelSelectTable table9 = new LevelSelectTable(main, 9, "1_9", "1_8");
//        LevelSelectTable table10 = new LevelSelectTable(main, 10, "1_10", "1_9");

        Table btnTable = new Table();
        btnTable.setSize(960, 540);
        btnTable.add(table1);//.prefSize(128, 128);//.padRight(48);
        btnTable.add(table2);//.prefSize(128, 128);//.padLeft(48).row();
        btnTable.add(table3);
//        btnTable.add(table4);
//        btnTable.add(table5).row();
//
//        btnTable.add(table6);
//        btnTable.add(table7);
//        btnTable.add(table8);
//        btnTable.add(table9);
//        btnTable.add(table10);

        stageUI.addActor(btnTable);

        ImageButton exitButton = new ImageButton(main.styleExit);
        exitButton.setBounds(0, 480, 60, 60);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    main.buttonClick.play();
                    dispose();
                    main.setScreen(new StartScreen(main));
                }
            }
        });
        stageUI.addActor(exitButton);

        main.batch.setProjectionMatrix(camera.combined);

//        btnTable.debug();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stageUI);
        if (!main.bgMusic.isPlaying()) {
            main.bgMusic.play();
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
}
