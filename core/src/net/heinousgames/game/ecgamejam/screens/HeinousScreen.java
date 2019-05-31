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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import net.heinousgames.game.ecgamejam.Main;

public class HeinousScreen implements Screen, InputProcessor {

    public Animation<TextureRegion> characterWalking;
    public Array<Rectangle> tiles;
    public boolean goingUp, goingDown, goingLeft, goingRight, holdingSpace, bgAlphaIncreasing;
    private float bgAlpha, wallLayerAlpha, characterX, characterY, stateTime;
    private Image bg;
    private int worldWidth, worldHeight;
    public Main main;
    public OrthographicCamera cameraGamePlay;
    public OrthogonalTiledMapRenderer renderer;
    public Pool<Rectangle> rectPool;
    public Rectangle characterRect;
    public ShapeRenderer debugRenderer;
    public TextureRegion currentFrame;
    public TiledMap map;

    public HeinousScreen(Main main, String mapFileName) {
        this.main = main;
        bgAlpha = 0;
        bgAlphaIncreasing = true;
        wallLayerAlpha = 1;
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
                if (object.getName().equals("bg")) {
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

        characterX = 11;
        characterY = 4;

        characterRect = new Rectangle(characterX, characterY, 1, 1);

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
            } else {
                bgAlpha -= 0.005f;
                wallLayerAlpha += 0.005f;
            }

            if (bgAlpha >= 1) {
                bgAlphaIncreasing = false;
            } else if (bgAlpha <= 0) {
                bgAlphaIncreasing = true;
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

        if (goingLeft) {
            characterX -= 0.07f;
        }

        if (goingRight) {
            characterX += 0.07f;
        }

        if (goingDown) {
            characterY -= 0.07f;
        }

        if (goingUp) {
            characterY += 0.07f;
        }

        int startX, startY, endX, endY;
        startX = (int) characterRect.x;
        endX = (int) characterRect.x + 1;
        startY = (int) characterRect.y;
        endY = (int) characterRect.y + 1;
        getTiles(startX, startY, endX, endY, tiles, "walls", rectPool);
        for (Rectangle tile : tiles) {
            if (characterRect.overlaps(tile)) {
                if (goingUp) {
                    characterY = tile.y - 1;
                }

                if (goingDown) {
                    characterY = tile.y + 1;
                }

                if (goingRight) {
                    characterX = tile.x - 1;
                }

                if (goingLeft) {
                    characterX = tile.x + 1;
                }
                break;
            }
        }

        characterRect.x = characterX;
        characterRect.y = characterY;

        stateTime += delta;
        currentFrame = characterWalking.getKeyFrame(stateTime);
        main.batch.begin();
        main.batch.draw(currentFrame, characterRect.x, characterRect.y, 1, 1);
        main.batch.end();

        renderDebug();

        getTiles(startX, startY, endX, endY, tiles, "walls", rectPool);
        for (Rectangle tile : tiles) {
            debugRenderer.rect(tile.x, tile.y, tile.width, tile.height);
        }
        debugRenderer.end();
    }

    private void renderDebug() {
        debugRenderer.setProjectionMatrix(cameraGamePlay.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(characterRect.x, characterRect.y, characterRect.width, characterRect.height);

//        getTiles(0, 0, worldWidth, worldHeight, tiles, "walls", rectPool);
//        for (Rectangle tile : tiles) {
//            debugRenderer.rect(tile.x, tile.y, tile.width, tile.height);
//        }
//        debugRenderer.end();
    }

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
//                        if (layerName.equalsIgnoreCase("spikes") || layerName.equals("spikesRow2")) {
//                            Rectangle rect = rectPool.obtain();
//                            Rectangle rect2 = rectPool.obtain();
//                            Rectangle rect3 = rectPool.obtain();
//                            Rectangle rect4 = rectPool.obtain();
//                            Rectangle rect5 = rectPool.obtain();
//                            Rectangle rect6 = rectPool.obtain();
//                            Rectangle rect7 = rectPool.obtain();
//                            Rectangle rect8 = rectPool.obtain();
//
//                            String direction = cell.getTile().getProperties().get("direction", String.class);
//                            if (direction.equalsIgnoreCase("up")) {
//                                rect.set(x, y, 7/16f, 3/16f);
//                                rect2.set(x + 1/16f, y + 3/16f, 5/16f, 3/16f);
//                                rect3.set(x + 1/8f, y + 3/8f, 3/16f, 3/16f);
//                                rect4.set(x + 3/16f, y + 9/16f, 1/16f, 1/8f);
//
//                                rect5.set(x + 0.5f, y, 7/16f, 3/16f);
//                                rect6.set(x + 9/16f, y + 3/16f, 5/16f, 3/16f);
//                                rect7.set(x + 5/8f, y + 3/8f, 3/16f, 3/16f);
//                                rect8.set(x + 11/16f, y + 9/16f, 1/16f, 1/8f);
//                            } else if (direction.equalsIgnoreCase("down")) {
//                                rect.set(x, y + 13/16f, 7/16f, 3/16f);
//                                rect2.set(x + 1/16f, y + 10/16f, 5/16f, 3/16f);
//                                rect3.set(x + 1/8f, y + 7/16f, 3/16f, 3/16f);
//                                rect4.set(x + 3/16f, y + 5/16f, 1/16f, 1/8f);
//
//                                rect5.set(x + 0.5f, y + 13/16f, 7/16f, 3/16f);
//                                rect6.set(x + 9/16f, y + 10/16f, 5/16f, 3/16f);
//                                rect7.set(x + 5/8f, y + 7/16f, 3/16f, 3/16f);
//                                rect8.set(x + 11/16f, y + 5/16f, 1/16f, 1/8f);
//                            } else if (direction.equalsIgnoreCase("left")) {
//                                rect.set(x + 5/16f, y + 11/16f, 1/8f, 1/16f);
//                                rect2.set(x + 7/16f, y + 5/8f, 3/16f, 3/16f);
//                                rect3.set(x + 5/8f, y + 9/16f, 3/16f, 5/16f);
//                                rect4.set(x + 13/16f, y + 0.5f, 3/16f, 7/16f);
//
//                                rect5.set(x + 5/16f, y + 3/16f, 1/8f, 1/16f);
//                                rect6.set(x + 7/16f, y + 1/8f, 3/16f, 3/16f);
//                                rect7.set(x + 5/8f, y + 1/16f, 3/16f, 5/16f);
//                                rect8.set(x + 13/16f, y, 3/16f, 7/16f);
//                            } else if (direction.equalsIgnoreCase("right")) {
//                                rect.set(x + 9/16f, y + 11/16f, 1/8f, 1/16f);
//                                rect2.set(x + 3/8f, y + 5/8f, 3/16f, 3/16f);
//                                rect3.set(x + 3/16f, y + 9/16f, 3/16f, 5/16f);
//                                rect4.set(x, y + 0.5f, 3/16f, 7/16f);
//
//                                rect5.set(x + 9/16f, y + 3/16f, 1/8f, 1/16f);
//                                rect6.set(x + 3/8f, y + 1/8f, 3/16f, 3/16f);
//                                rect7.set(x + 3/16f, y + 1/16f, 3/16f, 5/16f);
//                                rect8.set(x, y, 3/16f, 7/16f);
//                            }
//
//                            tiles.add(rect);
//                            tiles.add(rect2);
//                            tiles.add(rect3);
//                            tiles.add(rect4);
//                            tiles.add(rect5);
//                            tiles.add(rect6);
//                            tiles.add(rect7);
//                            tiles.add(rect8);
//                        } else {
                            Rectangle rect = rectPool.obtain();
                            rect.set(x, y, 1, 1);
                            tiles.add(rect);
//                        }
                    }
                }
            }
        }
    }
}
