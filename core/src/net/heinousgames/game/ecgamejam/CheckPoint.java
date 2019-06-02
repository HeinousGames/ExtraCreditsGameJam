package net.heinousgames.game.ecgamejam;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CheckPoint extends Image {

    public boolean connectedHigher = false;
    public boolean connectedLower = false;
    public float originX;
    public float originY;
    public Main main;
    public Vector2 originVec;
    public Vector2 furthestPointTowardsHigherCheckpoint;
    public Vector2 furthestPointTowardsLowerCheckpoint;

    public CheckPoint(Main main, TextureRegion textureRegion, float x, float y) {
//        super(textureRegion);
        this.main = main;
        setBounds(x, y, 1, 1);
        originY = (y + y + this.getHeight())/2f;
        originX = (x + x + this.getWidth())/2f;
        originVec = new Vector2(originX, originY);
        furthestPointTowardsLowerCheckpoint = new Vector2(originX, originY);
        furthestPointTowardsHigherCheckpoint = new Vector2(originX, originY);
    }

    public void draw (Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.end();

        main.shapeRenderer.setColor(main.red, main.green, main.blue, 1);
        main.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        main.shapeRenderer.circle(originX, originY, 0.25f, 8);//getX() + 0.5f, getY() + 0.5f, 0.25f);
        main.shapeRenderer.end();

        batch.begin();
    }

}
