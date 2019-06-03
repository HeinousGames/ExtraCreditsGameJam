package net.heinousgames.game.ecgamejam;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.heinousgames.game.ecgamejam.Main;
import net.heinousgames.game.ecgamejam.screens.HeinousScreen;
import net.heinousgames.game.ecgamejam.screens.LevelSelectScreen;

public class LevelFinishedStatusTable extends Table {

    public LevelFinishedStatusTable(final Main main, final int level) {

        setBackground(new TextureRegionDrawable(new TextureRegion(
                main.windows, 1870, 2112, 770, 534)));

        ImageButton playButton = new ImageButton(main.stylePlay);
        ImageButton exitButton = new ImageButton(main.styleExit);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                    main.buttonClick.play();
                }
                remove();
                main.getScreen().dispose();
                main.setScreen(new HeinousScreen(main,level+1));
            }
        });

//        imgReplay.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//
//            }
//        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                    main.buttonClick.play();
                }
                remove();
                main.getScreen().dispose();
                main.setScreen(new LevelSelectScreen(main));
            }
        });

        add(exitButton).size(120, 120).padRight(20);
//        add(imgReplay).prefSize(128, 128);
        add(playButton).size(120, 120).padLeft(20).row();

//        pack();
//        debug();
    }

}
