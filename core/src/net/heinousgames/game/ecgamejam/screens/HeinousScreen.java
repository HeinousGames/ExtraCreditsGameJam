package net.heinousgames.game.ecgamejam.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
//import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import net.heinousgames.game.ecgamejam.CheckPoint;
import net.heinousgames.game.ecgamejam.Constants;
import net.heinousgames.game.ecgamejam.Main;
import net.heinousgames.game.ecgamejam.LevelFinishedStatusTable;

import java.util.ArrayList;

public class HeinousScreen implements InputProcessor, Screen {

    private static final float MOVEMENT_SPEED = .085f;
    private static final float PATH_BUFFER_DISTANCE = .6f;

    private Animation<TextureRegion> characterWalking;
    private Array<Rectangle> tiles;
    private boolean goingUp, goingDown, goingLeft, goingRight, holdingSpace, bgAlphaIncreasing, displayLevelCompleteWindow;
    private float bgAlpha, wallLayerAlpha, stateTime;
    private Image bg;
    private int level;
    public Main main;
    private OrthographicCamera cameraGamePlay, cameraDialogs;
    private OrthogonalTiledMapRenderer renderer;
    private Pool<Rectangle> rectPool;
    private Rectangle characterRect;
    private Sound gameWin, lightUp;
    private Stage stageDialogs;
    private TextureRegion nonMovingFrame;
    private TiledMap map;

    private int currentCheckpointIndex;
    private Array<CheckPoint> checkPoints;
    private ArrayList<float[]> foundPaths = new ArrayList<float[]>();
    private boolean win;

    public HeinousScreen(final Main main, int level) {
        this.main = main;
        this.level = level;
        gameWin = Gdx.audio.newSound(Gdx.files.internal("game_win.mp3"));
        lightUp = Gdx.audio.newSound(Gdx.files.internal("light_up.mp3"));
        bgAlpha = 0;
        bgAlphaIncreasing = true;
        wallLayerAlpha = 1;

        if (level == 1) {
            map = main.mapLoader.load("drawbridge.tmx");
            characterRect = new Rectangle(1, 32.5f, (7/16f), (7/16f));
        } else if (level == 2) {
            map = main.mapLoader.load("tiger.tmx");
            characterRect = new Rectangle(16.65f, 1, (7/16f), (7/16f));
        } else if (level == 3) {
            map = main.mapLoader.load("brain.tmx");
            characterRect = new Rectangle(10, 10f, (7/16f), (7/16f));
        }
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 32f);
        int worldWidth = map.getProperties().get("width", Integer.class);
        int worldHeight = map.getProperties().get("height", Integer.class);
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

        rectPool = new Pool<Rectangle>() {
            @Override
            protected Rectangle newObject () {
                return new Rectangle();
            }
        };
        tiles = new Array<Rectangle>();

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
                    int pos = mapObject.getProperties().get("position", Integer.class);
                    checkPoints.set(pos, new CheckPoint(main, textureRegion, x/32f, y/32f));
                }
            }
        }

        cameraDialogs = new OrthographicCamera(854, 480);
        stageDialogs = new Stage(new StretchViewport(854, 480, cameraDialogs));

        ImageButton exitButton = new ImageButton(main.styleExit);
        exitButton.setBounds(2, 456, 20, 20);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                        main.buttonClick.play();
                    }
                    dispose();
                    main.setScreen(new LevelSelectScreen(main));
                }
            }
        });
        stageDialogs.addActor(exitButton);
    }

    @Override
    public void show() {
        if (main.prefs.getBoolean(Constants.MUSIC_OPTION)) {
            if (!main.bgMusic.isPlaying()) {
                main.bgMusic.play();
            }
        }
        InputMultiplexer multiplexer = new InputMultiplexer(stageDialogs, this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (holdingSpace) {
            if (bgAlphaIncreasing) {
                bgAlpha += 0.0075f;
                wallLayerAlpha -= 0.0075f;
//            } else {
//                bgAlpha -= 0.005f;
//                wallLayerAlpha += 0.005f;
            }

            if (bgAlpha >= 1) {
//                bgAlphaIncreasing = false;
//            } else if (bgAlpha <= 0) {
//                bgAlphaIncreasing = true;
                bgAlpha = 1;
                if (!displayLevelCompleteWindow) {
                    displayLevelCompleteWindow = true;
                    stageDialogs.addAction(Actions.sequence(Actions.delay(2),
                            new Action() {
                        @Override
                        public boolean act(float delta) {
                            LevelFinishedStatusTable window = new LevelFinishedStatusTable(main, level);
                            window.setBounds(cameraDialogs.viewportWidth/2 - cameraDialogs.viewportWidth/4,
                                    cameraDialogs.viewportHeight/2 - cameraDialogs.viewportHeight/4,
                                    cameraDialogs.viewportWidth/2, cameraDialogs.viewportHeight/2);
                            stageDialogs.addActor(window);
                            if (main.prefs.getBoolean(Constants.MUSIC_OPTION)) {
                                main.bgMusic.play();
                            }
                            return false;
                        }
                    }));
                }
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
        cameraDialogs.update();

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
        getTiles(startX, startY, endX, endY, tiles, rectPool);
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
//        float velocityYplaceHolder = velocity.y;

        // check regular walls
        getTiles(startX, startY, endX, endY, tiles, rectPool);
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


        checkCheckPoints(characterRect, checkPoints);

        characterRect.x += velocity.x;
        characterRect.y += velocity.y;

        addToPaths(characterRect, checkPoints);


        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        main.shapeRenderer.setProjectionMatrix(cameraGamePlay.combined);
        main.shapeRenderer.setColor(main.red, main.green, main.blue, wallLayerAlpha);
        main.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        /* goes with the sequential mode *
        for (int i = 0; i < currentCheckpointIndex; i++) {
            if (i != checkPoints.size - 1) {
                float checkPoint1X = (checkPoints.get(i).getX() + checkPoints.get(i).getX() + checkPoints.get(i).getWidth()) / 2f;
                float checkPoint1Y = (checkPoints.get(i).getY() + checkPoints.get(i).getY() + checkPoints.get(i).getHeight()) / 2f;
                float checkPoint2X = (checkPoints.get(i + 1).getX() + checkPoints.get(i + 1).getX() + checkPoints.get(i + 1).getWidth()) / 2f;
                float checkPoint2Y = (checkPoints.get(i + 1).getY() + checkPoints.get(i + 1).getY() + checkPoints.get(i + 1).getHeight()) / 2f;

                main.shapeRenderer.rectLine(new Vector2(checkPoint1X, checkPoint1Y), new Vector2(checkPoint2X, checkPoint2Y), 8 / 32f);
            }
        }

        if (currentCheckpointIndex == checkPoints.size) {
            int size = checkPoints.size;
            float checkPoint1X = (checkPoints.get(0).getX() + checkPoints.get(0).getX() + checkPoints.get(0).getWidth())/2f;
            float checkPoint1Y = (checkPoints.get(0).getY() + checkPoints.get(0).getY() + checkPoints.get(0).getHeight())/2f;
            float checkPoint2X = (checkPoints.get(size-1).getX() + checkPoints.get(size-1).getX() + checkPoints.get(size-1).getWidth())/2f;
            float checkPoint2Y = (checkPoints.get(size-1).getY() + checkPoints.get(size-1).getY() + checkPoints.get(size-1).getHeight())/2f;

            main.shapeRenderer.rectLine(new Vector2(checkPoint1X,checkPoint1Y),new Vector2(checkPoint2X,checkPoint2Y),8/32f);
        }*/

        /* goes with the more liberal option where you can gather path sections out of sequence */
        for (float[] positions: foundPaths) {
            main.shapeRenderer.rectLine(new Vector2(positions[0],positions[1]),new Vector2(positions[2],positions[3]),8/32f);
        }
        for (CheckPoint cp: checkPoints) {
            if(cp.originVec.x != cp.furthestPointTowardsLowerCheckpoint.x || cp.originVec.y != cp.furthestPointTowardsLowerCheckpoint.y){
                main.shapeRenderer.rectLine(cp.originVec,cp.furthestPointTowardsLowerCheckpoint,8/32f);
            }

            if(cp.originVec.x != cp.furthestPointTowardsHigherCheckpoint.x || cp.originVec.y != cp.furthestPointTowardsHigherCheckpoint.y){
                main.shapeRenderer.rectLine(cp.originVec,cp.furthestPointTowardsHigherCheckpoint,8/32f);
            }

            cp.draw();
        }

        main.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        stateTime += delta;
        TextureRegion currentFrame = characterWalking.getKeyFrame(stateTime);
        main.batch.setColor(main.batch.getColor().r, main.batch.getColor().g, main.batch.getColor().b, 1);
        main.batch.begin();

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

        if (foundPaths.size() == checkPoints.size && !win) {
            win = true;
            if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                main.bgMusic.pause();
                gameWin.play();
            }
            holdingSpace = true;
            main.prefs.putBoolean("level".concat(String.valueOf(level)), true).flush();
        }

        stageDialogs.act();
        stageDialogs.draw();

        //renderDebug();
    }

//    private void renderDebug() {
//        main.shapeRenderer.setProjectionMatrix(cameraGamePlay.combined);
//        main.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//
//        main.shapeRenderer.setColor(Color.RED);
//        main.shapeRenderer.rect(characterRect.x, characterRect.y, characterRect.width, characterRect.height);
//
//        getTiles(0, 0, worldWidth, worldHeight, tiles, rectPool);
//        for (Rectangle tile : tiles) {
//            main.shapeRenderer.rect(tile.x, tile.y, tile.width, tile.height);
//        }
//
//        for (int i = 0; i < currentCheckpointIndex; i++) {
//            float checkPoint1X = (checkPoints.get(i).getX() + checkPoints.get(i).getX() + checkPoints.get(i).getWidth())/2f ;
//            float checkPoint1Y = (checkPoints.get(i).getY() + checkPoints.get(i).getY() + checkPoints.get(i).getHeight())/2f;
//            float checkPoint2X = (checkPoints.get(i+1).getX() + checkPoints.get(i+1).getX() + checkPoints.get(i+1).getWidth())/2f ;
//            float checkPoint2Y = (checkPoints.get(i+1).getY() + checkPoints.get(i+1).getY() + checkPoints.get(i+1).getHeight())/2f;
//
//            main.shapeRenderer.line(checkPoint1X, checkPoint1Y, checkPoint2X, checkPoint2Y);
//        }
//
//        main.shapeRenderer.end();
//    }

/* Original proof of concept - changes the index to whatever the last checkPoint you collide with, regardless of your most recent checkPoint
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

/* Game mode/Option one, Must be sequential *

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

        for (int i = 0; i < checkpoints.size; i++) {
            float checkPointX = (checkpoints.get(i).getX() + checkpoints.get(i).getX() + checkpoints.get(i).getWidth())/2f;
            float checkPointY = (checkpoints.get(i).getY() + checkpoints.get(i).getY() + checkpoints.get(i).getHeight())/2f;
            float distance = (float) Math.sqrt(((checkPointX - originX) * (checkPointX - originX)) + ((checkPointY - originY) * (checkPointY - originY)));
            if (distance <= PATH_BUFFER_DISTANCE) {
                currentCheckpointIndex = i;
            }else{
                if(checkpoints.get(i).furthestPointTowardsHigherCheckpoint != null){
                    checkPointX = checkpoints.get(i).furthestPointTowardsHigherCheckpoint.x;
                    checkPointY = checkpoints.get(i).furthestPointTowardsHigherCheckpoint.y;
                    distance = (float) Math.sqrt(((checkPointX - originX) * (checkPointX - originX)) + ((checkPointY - originY) * (checkPointY - originY)));
                    if (distance <= PATH_BUFFER_DISTANCE) {
                        currentCheckpointIndex = i;
                    }
                }
                if(checkpoints.get(i).furthestPointTowardsLowerCheckpoint != null){
                    checkPointX = checkpoints.get(i).furthestPointTowardsLowerCheckpoint.x;
                    checkPointY = checkpoints.get(i).furthestPointTowardsLowerCheckpoint.y;
                    distance = (float) Math.sqrt(((checkPointX - originX) * (checkPointX - originX)) + ((checkPointY - originY) * (checkPointY - originY)));
                    if (distance <= PATH_BUFFER_DISTANCE) {
                        currentCheckpointIndex = i;
                    }
                }
            }
        }
    }


    /* Game mode/Option two, doesn't need to be sequential *

    public void checkCheckPoints(Rectangle rect, Array<CheckPoint> checkpoints){

        float originX = (rect.x + rect.width + rect.x)/2f;
        float originY = (rect.y + rect.height + rect.y)/2f;

        if(currentCheckpointIndex != 0){
            float distance = distance(checkpoints.get(currentCheckpointIndex - 1).originVec, new Vector2(originX, originY));
            if (distance <= PATH_BUFFER_DISTANCE) {
                float[] tempPositions = {checkpoints.get(currentCheckpointIndex).originX, (checkpoints.get(currentCheckpointIndex).originY), checkpoints.get(currentCheckpointIndex - 1).originX, checkpoints.get(currentCheckpointIndex - 1).originY};
                if(!foundPaths.contains(tempPositions)){
                    foundPaths.add(tempPositions);
                }
            }
        }

        if(currentCheckpointIndex < checkpoints.size - 1){
            float checkPointX = (checkpoints.get(currentCheckpointIndex + 1).getX() + checkpoints.get(currentCheckpointIndex + 1).getX() + checkpoints.get(currentCheckpointIndex + 1).getWidth())/2f;
            float checkPointY = (checkpoints.get(currentCheckpointIndex + 1).getY() + checkpoints.get(currentCheckpointIndex + 1).getY() + checkpoints.get(currentCheckpointIndex + 1).getHeight())/2f;
            float distance = distance(checkPointX, checkPointY, originX, originY);
            if (distance <= PATH_BUFFER_DISTANCE) {
                float[] tempPositions = {(checkpoints.get(currentCheckpointIndex).getX() + checkpoints.get(currentCheckpointIndex).getX() + checkpoints.get(currentCheckpointIndex).getWidth())/2f, (checkpoints.get(currentCheckpointIndex).getY() + checkpoints.get(currentCheckpointIndex).getY() + checkpoints.get(currentCheckpointIndex).getHeight())/2f, checkPointX, checkPointY};
                if(!foundPaths.contains(tempPositions)){
                    foundPaths.add(tempPositions);
                }
            }
        }

        if (currentCheckpointIndex == checkpoints.size - 1) {
            float checkPointX = (checkpoints.get(0).getX() + checkpoints.get(0).getX() + checkpoints.get(0).getWidth())/2f;
            float checkPointY = (checkpoints.get(0).getY() + checkpoints.get(0).getY() + checkpoints.get(0).getHeight())/2f;
            float distance = distance(checkPointX, checkPointY, originX, originY);
            if (distance <= PATH_BUFFER_DISTANCE) {
                float[] tempPositions = {(checkpoints.get(currentCheckpointIndex).getX() + checkpoints.get(currentCheckpointIndex).getX() + checkpoints.get(currentCheckpointIndex).getWidth())/2f, (checkpoints.get(currentCheckpointIndex).getY() + checkpoints.get(currentCheckpointIndex).getY() + checkpoints.get(currentCheckpointIndex).getHeight())/2f, checkPointX, checkPointY};
                if(!foundPaths.contains(tempPositions)){
                    foundPaths.add(tempPositions);
                }
            }
        }

        for (int i = 0; i < checkpoints.size; i++) {
            float checkPointX = (checkpoints.get(i).getX() + checkpoints.get(i).getX() + checkpoints.get(i).getWidth())/2f;
            float checkPointY = (checkpoints.get(i).getY() + checkpoints.get(i).getY() + checkpoints.get(i).getHeight())/2f;
            float distance = distance(checkPointX, checkPointY, originX,originY);
            if (distance <= PATH_BUFFER_DISTANCE) {
                currentCheckpointIndex = i;
            }else{
                if(checkpoints.get(i).furthestPointTowardsHigherCheckpoint != null){
                    checkPointX = checkpoints.get(i).furthestPointTowardsHigherCheckpoint.x;
                    checkPointY = checkpoints.get(i).furthestPointTowardsHigherCheckpoint.y;
                    distance = distance(checkPointX, checkPointY, originX, originY);
                    if (distance <= PATH_BUFFER_DISTANCE) {
                        currentCheckpointIndex = i;
                    }
                }
                if(checkpoints.get(i).furthestPointTowardsLowerCheckpoint != null){
                    checkPointX = checkpoints.get(i).furthestPointTowardsLowerCheckpoint.x;
                    checkPointY = checkpoints.get(i).furthestPointTowardsLowerCheckpoint.y;
                    distance = distance(checkPointX, checkPointY, originX, originY);
                    if (distance <= PATH_BUFFER_DISTANCE) {
                        currentCheckpointIndex = i;
                    }
                }
            }
        }
    }
*/
    /* Game mode/Option two, doesn't need to be sequential Now with path meeting technology*/

    private void checkCheckPoints(Rectangle rect, Array<CheckPoint> checkpoints){

        float originX = (rect.x + rect.width + rect.x)/2f;
        float originY = (rect.y + rect.height + rect.y)/2f;
//        int lastIndex = (currentCheckpointIndex == 0) ? checkpoints.size - 1  : currentCheckpointIndex - 1;
//        int nextIndex = (currentCheckpointIndex == checkpoints.size -1) ? 0 : currentCheckpointIndex + 1;
/*
            float distance = distance(checkpoints.get(lastIndex).originVec, new Vector2(originX, originY));
            if (distance <= PATH_BUFFER_DISTANCE && !checkpoints.get(lastIndex).connectedHigher) {
                float[] tempPositions = {checkpoints.get(currentCheckpointIndex).originX, (checkpoints.get(currentCheckpointIndex).originY), checkpoints.get(lastIndex).originX, checkpoints.get(lastIndex).originY};
                foundPaths.add(tempPositions);
                checkpoints.get(lastIndex).connectedHigher = true;
                checkpoints.get(currentCheckpointIndex).connectedLower = true;
            }

            distance = distance(checkpoints.get(nextIndex).originVec, new Vector2(originX, originY));
            if (distance <= PATH_BUFFER_DISTANCE && !checkpoints.get(nextIndex).connectedLower) {
                float[] tempPositions = {checkpoints.get(currentCheckpointIndex).originX, (checkpoints.get(currentCheckpointIndex).originY), checkpoints.get(nextIndex).originX, checkpoints.get(nextIndex).originY};
                foundPaths.add(tempPositions);
                checkpoints.get(nextIndex).connectedLower = true;
                checkpoints.get(currentCheckpointIndex).connectedHigher = true;
            }
*/
        for (int i = 0; i < checkpoints.size; i++) {
            float checkPointX = (checkpoints.get(i).getX() + checkpoints.get(i).getX() + checkpoints.get(i).getWidth())/2f;
            float checkPointY = (checkpoints.get(i).getY() + checkpoints.get(i).getY() + checkpoints.get(i).getHeight())/2f;
            float distance = distance(checkPointX, checkPointY, originX,originY);
            if (distance <= PATH_BUFFER_DISTANCE) {
                currentCheckpointIndex = i;
            }else{
                if(!checkpoints.get(i).connectedHigher){// furthestPointTowardsHigherCheckpoint != null){
                    checkPointX = checkpoints.get(i).furthestPointTowardsHigherCheckpoint.x;
                    checkPointY = checkpoints.get(i).furthestPointTowardsHigherCheckpoint.y;
                    distance = distance(checkPointX, checkPointY, originX, originY);
                    if (distance <= PATH_BUFFER_DISTANCE) {
                        currentCheckpointIndex = i;
                    }
                }
                if(!checkpoints.get(i).connectedLower){//.furthestPointTowardsLowerCheckpoint != null){
                    checkPointX = checkpoints.get(i).furthestPointTowardsLowerCheckpoint.x;
                    checkPointY = checkpoints.get(i).furthestPointTowardsLowerCheckpoint.y;
                    distance = distance(checkPointX, checkPointY, originX, originY);
                    if (distance <= PATH_BUFFER_DISTANCE) {
                        currentCheckpointIndex = i;
                    }
                }
            }
        }
    }


    private float distance(Vector2 point1, Vector2 point2){
        return (float)Math.sqrt(Math.pow(point2.x - point1.x,2) + Math.pow(point2.y - point1.y,2));
    }

    private float distance(float x1, float y1, float x2, float y2){
        return (float)Math.sqrt(Math.pow(x2 - x1,2) + Math.pow(y2- y1,2));
    }

    private void addToPaths(Rectangle characterRect, Array<CheckPoint> checkPoints){

        float originX = (characterRect.x + characterRect.width + characterRect.x)/2f;
        float originY = (characterRect.y + characterRect.height + characterRect.y)/2f;

        float distance = distance(checkPoints.get(currentCheckpointIndex).furthestPointTowardsLowerCheckpoint,new Vector2(originX,originY));
        if(distance <= PATH_BUFFER_DISTANCE && !checkPoints.get(currentCheckpointIndex).connectedLower){
            int lastIndex = currentCheckpointIndex - 1;
            if(currentCheckpointIndex == 0){
                lastIndex = checkPoints.size -1;
            }
            float deltaY = (checkPoints.get(currentCheckpointIndex).originX < checkPoints.get(lastIndex).originX) ?  checkPoints.get(lastIndex).originY - checkPoints.get(currentCheckpointIndex).originY :  checkPoints.get(currentCheckpointIndex).originY - checkPoints.get(lastIndex).originY;

            float deltaC = distance(checkPoints.get(currentCheckpointIndex).originX, checkPoints.get(currentCheckpointIndex).originY,checkPoints.get(lastIndex).originX,checkPoints.get(lastIndex).originY);
            float angleY = (float)(Math.asin((deltaY/deltaC)) * 180 / Math.PI);
            float playerPerpendicularAngle1 = (float)((angleY - 90) * Math.PI)/180f;
            float playerPerpendicularAngle2 = (float)((angleY + 90) * Math.PI)/180f;
            Vector2 playerRadiusPoint1 = new Vector2(originX + (PATH_BUFFER_DISTANCE * (float)Math.cos(playerPerpendicularAngle1)),originY + (PATH_BUFFER_DISTANCE * (float)Math.sin(playerPerpendicularAngle1)));
            Vector2 playerRadiusPoint2 = new Vector2(originX + (PATH_BUFFER_DISTANCE * (float)Math.cos(playerPerpendicularAngle2)),originY + (PATH_BUFFER_DISTANCE * (float)Math.sin(playerPerpendicularAngle2)));


            Vector2 intersectPoint = new Vector2();
            Intersector.intersectSegments(playerRadiusPoint1, playerRadiusPoint2,checkPoints.get(currentCheckpointIndex).originVec,checkPoints.get(lastIndex).originVec,intersectPoint);

            float intersectDistance = distance(intersectPoint,checkPoints.get(lastIndex).originVec);
            float furthestPointDistance = distance(checkPoints.get(currentCheckpointIndex).furthestPointTowardsLowerCheckpoint,checkPoints.get(lastIndex).originVec);
            if(intersectDistance < furthestPointDistance){
                if(intersectPoint.x != 0 || intersectPoint.y != 0) {
                    checkPoints.get(currentCheckpointIndex).furthestPointTowardsLowerCheckpoint.set(intersectPoint);
                    distance = distance(checkPoints.get(currentCheckpointIndex).furthestPointTowardsLowerCheckpoint, checkPoints.get(lastIndex).furthestPointTowardsHigherCheckpoint);
                    if(distance <= PATH_BUFFER_DISTANCE * .55f + MOVEMENT_SPEED){
                        checkPoints.get(currentCheckpointIndex).connectedLower = true;
                        checkPoints.get(lastIndex).connectedHigher = true;
                        float[] tempPositions = {checkPoints.get(currentCheckpointIndex).originX, (checkPoints.get(currentCheckpointIndex).originY), checkPoints.get(lastIndex).originX, checkPoints.get(lastIndex).originY};
                        foundPaths.add(tempPositions);
                        if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                            lightUp.play(0.1f);
                        }
                    }
                }
            }
        }

        distance = distance(checkPoints.get(currentCheckpointIndex).furthestPointTowardsHigherCheckpoint,new Vector2(originX,originY));
        if(distance <= PATH_BUFFER_DISTANCE && !checkPoints.get(currentCheckpointIndex).connectedHigher){
            int nextIndex = currentCheckpointIndex + 1;
            if(currentCheckpointIndex == checkPoints.size -1){
                nextIndex = 0;
            }

            float deltaY = (checkPoints.get(currentCheckpointIndex).originX < checkPoints.get(nextIndex).originX) ?  checkPoints.get(nextIndex).originY - checkPoints.get(currentCheckpointIndex).originY :  checkPoints.get(currentCheckpointIndex).originY - checkPoints.get(nextIndex).originY;
            float deltaC = distance(checkPoints.get(currentCheckpointIndex).originVec, checkPoints.get(nextIndex).originVec);
            float angleY = (float)(Math.asin((deltaY/deltaC)) * 180 / Math.PI);
            float playerPerpendicularAngle1 = (float)((angleY - 90) * Math.PI)/180f;
            float playerPerpendicularAngle2 = (float)((angleY + 90) * Math.PI)/180f;
            Vector2 playerRadiusPoint1 = new Vector2(originX + (PATH_BUFFER_DISTANCE * (float)Math.cos(playerPerpendicularAngle1)),originY + (PATH_BUFFER_DISTANCE * (float)Math.sin(playerPerpendicularAngle1)));
            Vector2 playerRadiusPoint2 = new Vector2(originX + (PATH_BUFFER_DISTANCE * (float)Math.cos(playerPerpendicularAngle2)),originY + (PATH_BUFFER_DISTANCE * (float)Math.sin(playerPerpendicularAngle2)));

            Vector2 intersectPoint = new Vector2();
            Intersector.intersectSegments(playerRadiusPoint1, playerRadiusPoint2,checkPoints.get(currentCheckpointIndex).originVec,checkPoints.get(nextIndex).originVec,intersectPoint);
            float intersectDistance = distance(intersectPoint,checkPoints.get(nextIndex).originVec);
            float furthestPointDistance = distance(checkPoints.get(currentCheckpointIndex).furthestPointTowardsHigherCheckpoint,checkPoints.get(nextIndex).originVec);
            if(intersectDistance < furthestPointDistance){
                if(intersectPoint.x != 0 || intersectPoint.y != 0) {
                    checkPoints.get(currentCheckpointIndex).furthestPointTowardsHigherCheckpoint.set(intersectPoint);

                    distance = distance(checkPoints.get(currentCheckpointIndex).furthestPointTowardsHigherCheckpoint, checkPoints.get(nextIndex).furthestPointTowardsLowerCheckpoint);
                    if(distance <= PATH_BUFFER_DISTANCE * .55f + MOVEMENT_SPEED) {
                        checkPoints.get(currentCheckpointIndex).connectedHigher = true;
                        checkPoints.get(nextIndex).connectedLower = true;
                        float[] tempPositions = {checkPoints.get(currentCheckpointIndex).originX, (checkPoints.get(currentCheckpointIndex).originY), checkPoints.get(nextIndex).originX, checkPoints.get(nextIndex).originY};
                        foundPaths.add(tempPositions);
                        if (main.prefs.getBoolean(Constants.SFX_OPTION)) {
                            lightUp.play(0.1f);
                        }
                    }
                }
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        stageDialogs.getViewport().update(width, height, true);
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
        gameWin.dispose();
        lightUp.dispose();
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

    private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles, Pool<Rectangle> rectPool) {
        rectPool.freeAll(tiles);
        tiles.clear();
        if (map.getLayers().get("walls") != null) {
            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
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
