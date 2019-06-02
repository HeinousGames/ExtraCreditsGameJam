package net.heinousgames.game.ecgamejam.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.heinousgames.game.ecgamejam.Main;

public class BaseWindow extends Window {

    public interface BaseWindowCallback {
        void buttonClick(String key);
    }

    private static final WindowStyle windowStyle;

    private Image imgX;
    protected Main game;
    public BaseWindowCallback callback;

    static {
        windowStyle = new WindowStyle(new BitmapFont(), Color.WHITE,
                new TextureRegionDrawable(new TextureRegion(new Texture("window.png"))));
    }

    BaseWindow(final BaseWindowCallback callback, Main game/*, WindowStyle windowStyle*/) {
        super("", windowStyle);
        this.callback = callback;

//        setTouchable(Touchable.disabled);

        this.game = game;

//        imgX = new Image(new Texture("cancel.png"));
//        imgX.setColor(Color.RED);
//        imgX.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                if (event.getType() == InputEvent.Type.touchUp) {
//                    remove();
//                    callback.buttonClick("close");
//                }
//            }
//        });
//        getTitleTable().padRight(24).padTop(80).add(imgX).size(74, 74);

        setClip(true);
        setFillParent(true);
        setMovable(false);
        setTransform(true);
//        setColor(Color.PINK);
//        setModal(true);
//        setVisible(true);
//        debug();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

//        setColor(main.red, main.green, main.blue, getColor().a);
//        imgX.setColor(main.red, main.green, main.blue, getColor().a);

//        if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
//            remove();
//            callback.buttonClick("close");
//        }
    }

}
