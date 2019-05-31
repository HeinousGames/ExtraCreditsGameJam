package net.heinousgames.game.ecgamejam;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CheckPoint extends Image {

    public CheckPoint(TextureRegion textureRegion, float x, float y) {
        super(textureRegion);
        setBounds(x, y, 1, 1);
    }
}
