package com.ktarrant.cloudWarfare;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by ktarrant1 on 12/20/15.
 */
public class Player implements GestureDetector.GestureListener {
    public float jumpPower = 10.0f;
    public final float playerBodyRadius = 0.6f; // TODO: Make player size configurable
    public final float playerLimbRadius = 0.8f;

    protected OrthographicCamera camera;
    protected BodyDef playerBodyDef = null;
    protected Body playerBody = null;
    protected CircleShape playerCircleShape = null;
    protected FixtureDef playerFixtureDef = null;
    protected Fixture playerFixture = null;

    public Player(World world, OrthographicCamera camera, Vector2 startPos) {
        // We will use the camera to unproject our screen locations
        this.camera = camera;

        // First we create a body definition
        this.playerBodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        this.playerBodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our body's starting position in the world
        this.playerBodyDef.position.set(startPos);

        // Create a circle shape and set its radius to 6
        this.playerCircleShape = new CircleShape();
        this.playerCircleShape.setRadius(playerBodyRadius);

        // Create a fixture definition to apply our shape to
        this.playerFixtureDef = new FixtureDef();
        this.playerFixtureDef.shape = this.playerCircleShape;
        this.playerFixtureDef.density = 0.5f;
        this.playerFixtureDef.friction = 0.1f;
        // rv.playerFixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our body in the world using our body definition
        this.playerBody = world.createBody(this.playerBodyDef);

        // Create our fixture and attach it to the body
        this.playerFixture = this.playerBody.createFixture(this.playerFixtureDef);

        // TODO: Create a definition for limbs
    }

    public Body getPlayerBody() {
        return this.playerBody;
    }

    public void dispose() {
        this.playerCircleShape.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Vector3 worldCoor = camera.unproject(new Vector3(x, y, 0.0f));
        Vector2 playerPos = this.playerBody.getPosition();
        float curAng = MathUtils.atan2(worldCoor.y - playerPos.y, worldCoor.x - playerPos.x);
        this.playerBody.applyLinearImpulse(
                this.jumpPower * MathUtils.cos(curAng),
                this.jumpPower * MathUtils.sin(curAng),
                this.playerBody.getPosition().x,
                this.playerBody.getPosition().y,
                true); // wake the player body
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}
