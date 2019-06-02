package net.heinousgames.game.ecgamejam.windows;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.heinousgames.game.ecgamejam.Main;
import net.heinousgames.game.ecgamejam.screens.HeinousScreen;
import net.heinousgames.game.ecgamejam.screens.LevelSelectScreen;

public class LevelFinishedStatusWindow extends BaseWindow {

    public LevelFinishedStatusWindow(final BaseWindowCallback callback, final Main main, /*WindowStyle windowStyle,*/ final int level) {
        super(callback, main);

        TextureRegion exit = new TextureRegion(main.buttons, 128, 4000, 190, 190);
        TextureRegion play = new TextureRegion(main.buttons, 128, 761, 190, 190);

        Image imgContinue = new Image(play);
//        Image imgReplay = new Image(new Texture("replay.png"));
        Image imgExit = new Image(exit);

        imgContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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

        imgExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
                main.getScreen().dispose();
                main.setScreen(new LevelSelectScreen(main));
            }
        });

        add(imgExit).prefSize(128, 128);
//        add(imgReplay).prefSize(128, 128);
        add(imgContinue).prefSize(128, 128).row();

//        debug();
    }

}
