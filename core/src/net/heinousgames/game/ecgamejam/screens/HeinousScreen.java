package net.heinousgames.game.ecgamejam.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import net.heinousgames.game.ecgamejam.CheckPoint;
import net.heinousgames.game.ecgamejam.Main;

import java.util.ArrayList;

public class HeinousScreen implements Screen, InputProcessor {

    private static final float COLOR_FREQUENCY = 0.21f;
    private static final float MOVEMENT_SPEED = .07f;
    private static final float PATH_BUFFER_DISTANCE = .5f;

    public Animation<TextureRegion> characterWalking;
    public Array<Rectangle> tiles;
    public boolean goingUp, goingDown, goingLeft, goingRight, holdingSpace, bgAlphaIncreasing,
            upMemory, downMemory, leftMemory, rightMemory;
    private float bgAlpha, wallLayerAlpha, stateTime;
    private float red, green, blue, colorCounter;
    private Image bg;
    private int worldWidth, worldHeight;
    public Main main;
    public OrthographicCamera cameraGamePlay;
    public OrthogonalTiledMapRenderer renderer;
    public Pool<Rectangle> rectPool;
    public Rectangle characterRect;
    //public Circle characterCircle;
    public ShapeRenderer debugRenderer;
    public TextureRegion currentFrame, nonMovingFrame;
    public TiledMap map;

    public int currentCheckpointIndex;
    public Array<CheckPoint> checkPoints;
    public ArrayList<float[]> foundPaths = new ArrayList();

    public float percentOfCurrentPath = 0;

    public HeinousScreen(Main main, String mapFileName) {
        this.main = main;
        bgAlpha = 0;
        bgAlphaIncreasing = true;
        wallLayerAlpha = 1;
        colorCounter = 0f;
        red = green = blue = 1;
        debugRenderer = new ShapeRenderer();
        map = main.mapLoader.load(mapFileName);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 32f);
        worldWidth = map.getProperties().get("width", Integer.class);
        worldHeight = map.getProperties().get("height", Integer.class);
        cameraGamePlay = new OrthographicCamera();
        cameraGamePlay.setToOrtho(false, worldWidth, worldHeight);
        cameraGamePlay.position.set(worldWidth / 2f, worldHeight / 2f, 0);
        main.batch.setProjectionMatrix(cameraGamePlay.combined);

        for (MapLayer layer : map.getLayers()) {
            for (MapObject object : layer.getObjects()) {
                if (object.getName() != null && object.getName().equals("bg")) {
                    bg = new Image(((TiledMapTileMapObject) object).getTile().getTextureRegion());
                    bg.setSize(worldWidth, worldHeight);
                }
            }
        }

        Texture characterSheet = new Texture("character.png");
        TextureRegion[][] regions = TextureRegion.split(characterSheet, 32, 32);
        characterWalking = new Animation<TextureRegion>(0.1f, regions[0][0], regions[0][1],
                regions[0][2], regions[0][3], regions[0][4], regions[0][5], regions[0][6], regions[0][7]);
        characterWalking.setPlayMode(Animation.PlayMode.LOOP);
        nonMovingFrame = regions[0][0];

        //characterX = 11;
        //characterY = 4;

        //characterRect = new Rectangle(characterX, characterY, 1, 1);
        characterRect = new Rectangle(1, 32.5f, (7/16f), (7/16f));
        //characterCircle = new Circle(11, 4, (5/16f));

        rectPool = new Pool<Rectangle>() {
            @Override
            protected Rectangle newObject () {
                return new Rectangle();
            }
        };
        tiles = new Array<Rectangle>();

        main.bgMusic = Gdx.audio.newMusic(Gdx.files.internal("Ceci Beats - Beach Daze.mp3"));
        main.bgMusic.setLooping(true);

        Gdx.input.setInputProcessor(this);

        currentCheckpointIndex = 0;

        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().equals("checkpoints")) {
                int count = layer.getObjects().getCount();
                checkPoints = new Array<CheckPoint>(count);
                checkPoints.size = count;
                for (MapObject mapObject : layer.getObjects()) {
                    TextureRegion textureRegion = ((TiledMapTileMapObject) mapObject).getTextureRegion();
                    float x = ((TiledMapTileMapObject) mapObject).getX();
                    float y = ((TiledMapTileMapObject) mapObject).getY();
                    int position = mapObject.getProperties().get("position", Integer.class);
                    checkPoints.set(position, new CheckPoint(textureRegion, x/32f, y/32f));
                }
            }
        }

//        for (int i = 0; i < checkPoints.size; i++) {
//            System.out.println("Position: " + i + "\nX: " + checkPoints.get(i).getX() + "\nY: " + checkPoints.get(i).getY());
//        }
    }

    @Override
    public void show() {
//        main.bgMusic.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (holdingSpace) {
            if (bgAlphaIncreasing) {
                bgAlpha += 0.005f;
                wallLayerAlpha -= 0.005f;
//            } else {
//                bgAlpha -= 0.005f;
//                wallLayerAlpha += 0.005f;
            }

            if (bgAlpha >= 1) {
//                bgAlphaIncreasing = false;
//            } else if (bgAlpha <= 0) {
//                bgAlphaIncreasing = true;
                bgAlpha = 1;
            }

            if (wallLayerAlpha <= 0) {
                wallLayerAlpha = 0;
            }
        } else {
            bgAlpha = 0;
            wallLayerAlpha = 1;
            bgAlphaIncreasing = true;
        }

        main.batch.begin();
        bg.draw(main.batch, bgAlpha);
        main.batch.end();

        cameraGamePlay.update();

        renderer.setView(cameraGamePlay);
        renderer.getMap().getLayers().get("walls").setOpacity(wallLayerAlpha);
        renderer.render();

        Vector2 velocity = new Vector2();

        if (goingLeft) {
            velocity.x -= MOVEMENT_SPEED;
        }

        if (goingRight) {
            velocity.x += MOVEMENT_SPEED;
        }

        if (goingDown) {
            velocity.y -= MOVEMENT_SPEED;
        }

        if (goingUp) {
            velocity.y += MOVEMENT_SPEED;
        }

        Rectangle huskyRect = new Rectangle(characterRect);
        int startX, startY, endX, endY;
        if (velocity.x > 0) {
            startX = endX = (int) (huskyRect.x + huskyRect.getWidth() + velocity.x);
        } else {
            startX = endX = (int) (huskyRect.x + velocity.x);
        }
        startY = (int) (huskyRect.y);
        endY = (int) (huskyRect.y + huskyRect.getHeight());
        huskyRect.x += velocity.x;
        float velocityXplaceHolder = velocity.x;

        // check regular walls
        getTiles(startX, startY, endX, endY, tiles, "walls", rectPool);
        for (Rectangle tile : tiles) {
            if (huskyRect.overlaps(tile)) {
                velocity.x = 0;
                break;
            }
        }


        if (velocity.x == 0) {
            huskyRect.x -= velocityXplaceHolder;
        }

        // if the dog is moving upwards, check the tiles to the top of its
        // top bounding box edge, otherwise check the ones to the bottom
        if (velocity.y > 0) {
            startY = endY = (int) (huskyRect.y + huskyRect.getHeight() + velocity.y);
        } else {
            startY = endY = (int) (huskyRect.y + velocity.y);
        }
        startX = (int) (huskyRect.x);
        endX = (int) (huskyRect.x + huskyRect.getWidth());
        huskyRect.y += velocity.y;
        float velocityYplaceHolder = velocity.y;

        // check regular walls
        getTiles(startX, startY, endX, endY, tiles, "walls", rectPool);
        for (Rectangle tile : tiles) {
            if (huskyRect.overlaps(tile)) {
                // we actually reset the husky y-position here
                // so it is just below/above the tile we collided with
                // this removes bouncing :)
                if (velocity.y > 0) {
                    characterRect.y = tile.y - huskyRect.getHeight();
                } else {
                    characterRect.y = tile.y + tile.height;
                }
                velocity.y = 0;
                break;
            }
        }


        characterRect.x += velocity.x;
        characterRect.y += velocity.y;

        checkCheckPoints(characterRect, checkPoints);

        stateTime += delta;
        currentFrame = characterWalking.getKeyFrame(stateTime);
        main.batch.setColor(main.batch.getColor().r, main.batch.getColor().g, main.batch.getColor().b, 1);
        main.batch.begin();

        // uncomment to draw checkpoints
        for (CheckPoint checkPoint : checkPoints) {
            checkPoint.draw(main.batch, 1);
        }

        if (goingUp) {
            main.batch.draw(currentFrame, characterRect.x, characterRect.y,
                    characterRect.width / 2f, characterRect.height / 2f,
                    characterRect.width, characterRect.height, 1, 1, 0);
        } else if (goingDown) {
            main.batch.draw(currentFrame, characterRect.x, characterRect.y,
                    characterRect.width / 2f, characterRect.height / 2f,
                    characterRect.width, characterRect.height, 1, 1, 180);
        } else if (goingLeft) {
            main.batch.draw(currentFrame, characterRect.x, characterRect.y,
                    characterRect.width / 2f, characterRect.height / 2f,
                    characterRect.width, characterRect.height, 1, 1, 270);
        } else if (goingRight) {
            main.batch.draw(currentFrame, characterRect.x, characterRect.y,
                    characterRect.width / 2f, characterRect.height / 2f,
                    characterRect.width, characterRect.height, 1, 1, 90);
        } else {
            main.batch.draw(nonMovingFrame, characterRect.x, characterRect.y,
                    characterRect.width / 2f, characterRect.height / 2f,
                    characterRect.width, characterRect.height, 1, 1, 0);
        }
        main.batch.end();

        debugRenderer.setProjectionMatrix(cameraGamePlay.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);

        red = (float) (Math.sin(COLOR_FREQUENCY * colorCounter + 0) * 127 + 128) / 255f;
        green = (float) (Math.sin(COLOR_FREQUENCY * colorCounter + 2) * 127 + 128) / 255f;
        blue = (float) (Math.sin(COLOR_FREQUENCY * colorCounter + 4) * 127 + 128) / 255f;

        colorCounter += delta * 11f;
        debugRenderer.setColor(red, green, blue, 1);

        /*** goes with the sequential mode ***/
        for (int i = 0; i < currentCheckpointIndex; i++) {
            if (i != checkPoints.size - 1) {
                float checkPoint1X = (checkPoints.get(i).getX() + checkPoints.get(i).getX() + checkPoints.get(i).getWidth()) / 2f;
                float checkPoint1Y = (checkPoints.get(i).getY() + checkPoints.get(i).getY() + checkPoints.get(i).getHeight()) / 2f;
                float checkPoint2X = (checkPoints.get(i + 1).getX() + checkPoints.get(i + 1).getX() + checkPoints.get(i + 1).getWidth()) / 2f;
                float checkPoint2Y = (checkPoints.get(i + 1).getY() + checkPoints.get(i + 1).getY() + checkPoints.get(i + 1).getHeight()) / 2f;

                debugRenderer.rectLine(new Vector2(checkPoint1X, checkPoint1Y), new Vector2(checkPoint2X, checkPoint2Y), 8 / 32f);
            }
         }

        if (currentCheckpointIndex == checkPoints.size) {
            int size = checkPoints.size;
            float checkPoint1X = (checkPoints.get(0).getX() + checkPoints.get(0).getX() + checkPoints.get(0).getWidth())/2f;
            float checkPoint1Y = (checkPoints.get(0).getY() + checkPoints.get(0).getY() + checkPoints.get(0).getHeight())/2f;
            float checkPoint2X = (checkPoints.get(size-1).getX() + checkPoints.get(size-1).getX() + checkPoints.get(size-1).getWidth())/2f;
            float checkPoint2Y = (checkPoints.get(size-1).getY() + checkPoints.get(size-1).getY() + checkPoints.get(size-1).getHeight())/2f;

            debugRenderer.rectLine(new Vector2(checkPoint1X,checkPoint1Y),new Vector2(checkPoint2X,checkPoint2Y),8/32f);
        }

        /*** goes with the more liberal option where you can gather path sections out of sequence ***/
//        for (float[] positions: foundPaths) {
//            debugRenderer.rectLine(new Vector2(positions[0],positions[1]),new Vector2(positions[2],positions[3]),8/32f);
//        }

        debugRenderer.end();

        if (currentCheckpointIndex == checkPoints.size) {
            holdingSpace = true;
//            dispose();
//            main.setScreen(new HeinousScreen(main, "tiger.tmx"));
        }

        //renderDebug();
    }

    private void renderDebug() {
        debugRenderer.setProjectionMatrix(cameraGamePlay.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(characterRect.x, characterRect.y, characterRect.width, characterRect.height);

        getTiles(0, 0, worldWidth, worldHeight, tiles, "walls", rectPool);
        for (Rectangle tile : tiles) {
            debugRenderer.rect(tile.x, tile.y, tile.width, tile.height);
        }

        for (int i = 0; i < currentCheckpointIndex; i++) {
            float checkPoint1X = (checkPoints.get(i).getX() + checkPoints.get(i).getX() + checkPoints.get(i).getWidth())/2f ;
            float checkPoint1Y = (checkPoints.get(i).getY() + checkPoints.get(i).getY() + checkPoints.get(i).getHeight())/2f;
            float checkPoint2X = (checkPoints.get(i+1).getX() + checkPoints.get(i+1).getX() + checkPoints.get(i+1).getWidth())/2f ;
            float checkPoint2Y = (checkPoints.get(i+1).getY() + checkPoints.get(i+1).getY() + checkPoints.get(i+1).getHeight())/2f;

            debugRenderer.line(checkPoint1X, checkPoint1Y, checkPoint2X, checkPoint2Y);
        }

        debugRenderer.end();
    }

/****** Original proof of concept - changes the index to whatever the last checkPoint you collide with, regardless of your most recent checkPoint
 *
    public void checkCheckPoints(Rectangle rect, Array<CheckPoint> checkpoints){
        for (int i = 0; i < checkpoints.size; i++) {
            float originX = (rect.x + rect.width + rect.x)/2f;
            float originY = (rect.y + rect.height + rect.y)/2f;
            float checkPointX = (checkpoints.get(i).getX() + checkpoints.get(i).getX() + checkpoints.get(i).getWidth())/2f;
            float checkPointY = (checkpoints.get(i).getY() + checkpoints.get(i).getY() + checkpoints.get(i).getHeight())/2f;
            float distance = (float) Math.sqrt(
                    ((checkPointX - originX) * (checkPointX - originX)) +
                            ((checkPointY - originY) * (checkPointY - originY)));
            if (distance <= PATH_BUFFER_DISTANCE) {
                currentCheckpointIndex = i;
            }
        }
    }

*/

/*** Game mode/Option one, Must be sequential ***/

    public void checkCheckPoints(Rectangle rect, Array<CheckPoint> checkpoints) {
        float originX = (rect.x + rect.width + rect.x)/2f;
        float originY = (rect.y + rect.height + rect.y)/2f;
        if (currentCheckpointIndex < checkpoints.size - 1) {
            float checkPointX = (checkpoints.get(currentCheckpointIndex + 1).getX() + checkpoints.get(currentCheckpointIndex + 1).getX() + checkpoints.get(currentCheckpointIndex + 1).getWidth())/2f;
            float checkPointY = (checkpoints.get(currentCheckpointIndex + 1).getY() + checkpoints.get(currentCheckpointIndex + 1).getY() + checkpoints.get(currentCheckpointIndex + 1).getHeight())/2f;
            float distance = (float) Math.sqrt(((checkPointX - originX) * (checkPointX - originX)) + ((checkPointY - originY) * (checkPointY - originY)));
            if (distance <= PATH_BUFFER_DISTANCE) {
                currentCheckpointIndex++;
            }
        } else if (currentCheckpointIndex == checkpoints.size - 1) {
            float checkPointX = (checkpoints.get(0).getX() + checkpoints.get(0).getX() + checkpoints.get(0).getWidth())/2f;
            float checkPointY = (checkpoints.get(0).getY() + checkpoints.get(0).getY() + checkpoints.get(0).getHeight())/2f;
            float distance = (float) Math.sqrt(((checkPointX - originX) * (checkPointX - originX)) + ((checkPointY - originY) * (checkPointY - originY)));
            if (distance <= PATH_BUFFER_DISTANCE) {
                currentCheckpointIndex++;
            }
        }
    }


/*** Game mode/Option two, doesn't need to be sequential **

public void checkCheckPoints(Rectangle rect, Array<CheckPoint> checkpoints){

    if(currentCheckpointIndex != 0){
        float originX = (rect.x + rect.width + rect.x)/2f;
        float originY = (rect.y + rect.height + rect.y)/2f;
        float checkPointX = (checkpoints.get(currentCheckpointIndex - 1).getX() + checkpoints.get(currentCheckpointIndex - 1).getX() + checkpoints.get(currentCheckpointIndex -1).getWidth())/2f;
        float checkPointY = (checkpoints.get(currentCheckpointIndex - 1).getY() + checkpoints.get(currentCheckpointIndex - 1).getY() + checkpoints.get(currentCheckpointIndex - 1).getHeight())/2f;
        float distance = (float) Math.sqrt(((checkPointX - originX) * (checkPointX - originX)) + ((checkPointY - originY) * (checkPointY - originY)));
        if (distance <= PATH_BUFFER_DISTANCE) {
            float[] tempPositions = {(checkpoints.get(currentCheckpointIndex).getX() + checkpoints.get(currentCheckpointIndex).getX() + checkpoints.get(currentCheckpointIndex).getWidth())/2f, (checkpoints.get(currentCheckpointIndex).getY() + checkpoints.get(currentCheckpointIndex).getY() + checkpoints.get(currentCheckpointIndex).getHeight())/2f, checkPointX, checkPointY};
            if(!foundPaths.contains(tempPositions)){
                foundPaths.add(tempPositions);
            }
        }
    }

    if(currentCheckpointIndex < checkpoints.size - 1){
        float originX = (rect.x + rect.width + rect.x)/2f;
        float originY = (rect.y + rect.height + rect.y)/2f;
        float checkPointX = (checkpoints.get(currentCheckpointIndex + 1).getX() + checkpoints.get(currentCheckpointIndex + 1).getX() + checkpoints.get(currentCheckpointIndex + 1).getWidth())/2f;
        float checkPointY = (checkpoints.get(currentCheckpointIndex + 1).getY() + checkpoints.get(currentCheckpointIndex + 1).getY() + checkpoints.get(currentCheckpointIndex + 1).getHeight())/2f;
        float distance = (float) Math.sqrt(((checkPointX - originX) * (checkPointX - originX)) + ((checkPointY - originY) * (checkPointY - originY)));
        if (distance <= PATH_BUFFER_DISTANCE) {
            float[] tempPositions = {(checkpoints.get(currentCheckpointIndex).getX() + checkpoints.get(currentCheckpointIndex).getX() + checkpoints.get(currentCheckpointIndex).getWidth())/2f, (checkpoints.get(currentCheckpointIndex).getY() + checkpoints.get(currentCheckpointIndex).getY() + checkpoints.get(currentCheckpointIndex).getHeight())/2f, checkPointX, checkPointY};
            if(!foundPaths.contains(tempPositions)){
                foundPaths.add(tempPositions);
            }
        }
    }

    for (int i = 0; i < checkpoints.size; i++) {
        float originX = (rect.x + rect.width + rect.x)/2f;
        float originY = (rect.y + rect.height + rect.y)/2f;
        float checkPointX = (checkpoints.get(i).getX() + checkpoints.get(i).getX() + checkpoints.get(i).getWidth())/2f;
        float checkPointY = (checkpoints.get(i).getY() + checkpoints.get(i).getY() + checkpoints.get(i).getHeight())/2f;
        float distance = (float) Math.sqrt(
                ((checkPointX - originX) * (checkPointX - originX)) +
                        ((checkPointY - originY) * (checkPointY - originY)));
        if (distance <= PATH_BUFFER_DISTANCE) {
            currentCheckpointIndex = i;
        }
    }
}*/


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
        main.bgMusic.pause();
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT) {
            goingLeft = true;
        }

        if (keycode == Input.Keys.RIGHT) {
            goingRight = true;
        }

        if (keycode == Input.Keys.DOWN) {
            goingDown = true;
        }

        if (keycode == Input.Keys.UP) {
            goingUp = true;
        }

        if (keycode == Input.Keys.SPACE) {
            holdingSpace = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.LEFT) {
            goingLeft = false;
        }

        if (keycode == Input.Keys.RIGHT) {
            goingRight = false;
        }

        if (keycode == Input.Keys.DOWN) {
            goingDown = false;
        }

        if (keycode == Input.Keys.UP) {
            goingUp = false;
        }

        if (keycode == Input.Keys.SPACE) {
            holdingSpace = false;
            bgAlpha = 0;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles,
                         String layerName, Pool<Rectangle> rectPool) {
        rectPool.freeAll(tiles);
        tiles.clear();
        if (map.getLayers().get(layerName) != null) {
            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
            for (int y = startY; y <= endY; y++) {
                for (int x = startX; x <= endX; x++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null) {
                        Rectangle rect = rectPool.obtain();
                        rect.set(x, y, 1, 1);
                        tiles.add(rect);
                    }
                }
            }
        }
    }
}
