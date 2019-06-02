package net.heinousgames.game.ecgamejam;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.heinousgames.game.ecgamejam.screens.HeinousScreen;

public class LevelSelectTable extends Table {

    public LevelSelectTable(final Main main, final int levelToLoad) {

        TextureRegion play = new TextureRegion(main.buttons, 128, 761, 190, 190);
        TextureRegion playLocked = new TextureRegion(main.buttons, 1660, 5296, 190, 190);

        Image image;

        if (main.prefs.getBoolean("level".concat(String.valueOf(levelToLoad)))) {
            if (levelToLoad == 1) {
                image = new Image(new Texture("drawbridge1024.png"));
            } else if (levelToLoad == 2) {
                image = new Image(new Texture("tiger.png"));
            } else {
                image = new Image(play);
            }
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getType() == InputEvent.Type.touchUp) {
                        main.getScreen().dispose();
                        main.setScreen(new HeinousScreen(main, levelToLoad));
                    }
                }
            });
        } else {
            if (main.prefs.getBoolean("level".concat(String.valueOf(levelToLoad-1))) || levelToLoad == 1) {
                image = new Image(play);
                addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (event.getType() == InputEvent.Type.touchUp) {
                            main.getScreen().dispose();
                            main.setScreen(new HeinousScreen(main, levelToLoad));
                        }
                    }
                });
            } else {
                image = new Image(playLocked);
            }
        }

        add(image).prefSize(190, 190).row();

//        debug();
    }

}
