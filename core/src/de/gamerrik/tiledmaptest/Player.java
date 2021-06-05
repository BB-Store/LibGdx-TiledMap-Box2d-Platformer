package de.gamerrik.tiledmaptest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Player {
    private static final float PLAYERSPEED = 2.5f;
    private final static Vector2 PLAYERSIZE = new Vector2(1f, 1f);
    private static final int FRAME_COLS = 9;
    private static final int FRAME_ROWS = 4;
    private final TextureRegion[] _walkLeft;
    private final TextureRegion[] _walkRight;
    private final TextureRegion[] _walkDown;
    private final TextureRegion[] _walkUp;
    private Animation<TextureRegion> _playerAnimation;
    private TextureRegion currentFrame;
    private float stateTime;
    private boolean _isPlayerMoving;
    private Sprite _sprite;
    private Body playerBody;


    public Player(Vector2 _position, String spriteTexture, World world) {
        Texture playerSprite = new Texture(spriteTexture);

        TextureRegion[][] tmp = TextureRegion.split(playerSprite,
                playerSprite.getWidth() / FRAME_COLS,
                playerSprite.getHeight() / FRAME_ROWS);

        _sprite = new Sprite();

        _sprite.setPosition(_position.x, _position.y);
        _sprite.setSize(PLAYERSIZE.x,PLAYERSIZE.y);
        _walkLeft = new TextureRegion[FRAME_COLS];
        _walkRight = new TextureRegion[FRAME_COLS];
        _walkDown = new TextureRegion[FRAME_COLS];
        _walkUp = new TextureRegion[FRAME_COLS];


        for (int i = 0; i < FRAME_ROWS; i++) {
            int index = 0;
            for (int j = 0; j < FRAME_COLS; j++) {
                switch (i) {
                    case 0:
                        _walkUp[index++] = tmp[i][j];
                        break;
                    case 1:
                        _walkLeft[index++] = tmp[i][j];
                        break;
                    case 2:
                        _walkDown[index++] = tmp[i][j];
                        break;
                    case 3:
                        _walkRight[index++] = tmp[i][j];
                        break;

                }

            }
        }

        _playerAnimation = new Animation<>(0.025f, _walkRight);
        currentFrame = _playerAnimation.getKeyFrame(stateTime, false);
        stateTime = 0;

        //Creat Physic Stuff
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(_sprite.getX()+1f, _sprite.getY()+1f);
        playerBody = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        playerBody.createFixture(fixtureDef);
        // Shape is the only disposable of the lot, so get rid of it
        shape.dispose();
    }


    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        if (_isPlayerMoving) currentFrame = _playerAnimation.getKeyFrame(stateTime, true);
        else currentFrame = _playerAnimation.getKeyFrame(0, false);

        //Set Sprite Position
        _sprite.setPosition(playerBody.getPosition().x, playerBody.getPosition().y);
        //Draw Sprites Current Frame
        batch.draw(currentFrame, _sprite.getX()-_sprite.getWidth()/2, _sprite.getY()-_sprite.getHeight()/2, _sprite.getWidth(), _sprite.getHeight());

    }


    public void handleInput(int movementId, float delta) {

            switch (movementId){
                case 1:

                    if(playerBody.getLinearVelocity().x >= -2.5f)playerBody.applyLinearImpulse(new Vector2(-PLAYERSPEED, 0), playerBody.getWorldCenter(), true);
                    playerMovement(_walkLeft);
                    break;
                case 2:
                    if(playerBody.getLinearVelocity().x <= 2.5f)playerBody.applyLinearImpulse(new Vector2(PLAYERSPEED, 0), playerBody.getWorldCenter(), true);
                    playerMovement(_walkRight);
                    break;
                case 3:
                    if(playerBody.getLinearVelocity().y <= 1.5f)playerBody.applyLinearImpulse(new Vector2(0, 1.5f), playerBody.getWorldCenter(), true);
                    playerMovement(_walkUp);
                    break;
            }
    }

    private void playerMovement(TextureRegion[] animation) {
        _isPlayerMoving = true;
        _playerAnimation = new Animation(0.055f, animation);
    }
}
