package net.heinousgames.game.ecgamejam;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.heinousgames.game.ecgamejam.screens.HeinousScreen;

public class LevelSelectTable extends Table {

    public LevelSelectTable(final Main main, final int levelToLoad) {

        ImageButton button;

        if (main.prefs.getBoolean("level".concat(String.valueOf(levelToLoad)))) {
            if (levelToLoad == 1) {
                button = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("drawbridge1024.png"))));
            } else if (levelToLoad == 2) {
                button = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("tiger.png"))));
            } else if (levelToLoad == 3) {
                button = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("BrainBG.png"))));
            } else {
                button = new ImageButton(main.stylePlay);
            }
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getType() == InputEvent.Type.touchUp) {
                        main.buttonClick.play();
                        main.getScreen().dispose();
                        main.setScreen(new HeinousScreen(main, levelToLoad));
                    }
                }
            });
        } else {
            if (main.prefs.getBoolean("level".concat(String.valueOf(levelToLoad-1))) || levelToLoad == 1) {
                button = new ImageButton(main.stylePlay);
                addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (event.getType() == InputEvent.Type.touchUp) {
                            main.buttonClick.play();
                            main.getScreen().dispose();
                            main.setScreen(new HeinousScreen(main, levelToLoad));
                        }
                    }
                });
            } else {
                button = new ImageButton(new TextureRegionDrawable(new TextureRegion(main.buttons,
                        1660, 5296, 190, 190)));
            }
        }

        add(button).size(190, 190).row();

//        debug();
    }

}
