package net.heinousgames.game.ecgamejam;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CheckPoint extends Image {

    public boolean connectedHigher = false;
    public boolean connectedLower = false;
    public Vector2 furthestPointTowardsHigherCheckpoint = new Vector2();
    public Vector2 furthestPointTowardsLowerCheckpoint = new Vector2();
    public float originX;
    public float originY;
    public Vector2 originVec;

    public CheckPoint(TextureRegion textureRegion, float x, float y) {
        super(textureRegion);
        setBounds(x, y, 1, 1);
        originY = (y + y + this.getHeight())/2f;
        originX = (x + x + this.getWidth())/2f;
        originVec = new Vector2(originX, originY);
        furthestPointTowardsLowerCheckpoint.set(originX, originY);
        furthestPointTowardsHigherCheckpoint.set(originX,originY);
    }

}
