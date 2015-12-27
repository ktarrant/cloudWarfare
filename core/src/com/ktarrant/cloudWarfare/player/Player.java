package com.ktarrant.cloudWarfare.player;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

/**
 * Created by ktarrant1 on 12/20/15.
 */
public class Player implements GestureDetector.GestureListener, ContactListener {
    public final static float defaultControlDeadZone = MathUtils.PI / 6.0f;
    public final static float playerBodyRadius = 0.6f; // TODO: Make player size configurable

    protected OrthographicCamera camera;
    protected BodyDef playerBodyDef = null;
    protected Body playerBody = null;
    protected CircleShape playerCircleShape = null;
    protected FixtureDef playerFixtureDef = null;
    protected Fixture playerFixture = null;

    protected PlayerState state;
    protected ArrayList<Body> contactBodies;

    public enum PlayerState {
        AIR_ACTIVE(0.5f, 0.5f, 0, defaultControlDeadZone, 'A'),
        AIR_PUFF(0.5f, 0.5f, 0, defaultControlDeadZone, 'P'),
        FOOT_ACTIVE(2.0f, 0.5f, defaultControlDeadZone, defaultControlDeadZone, 'F');

        public float jumpPower;
        public float runPower;
        public float horizDeadZoneCos;
        public float vertDeadZoneCos;
        public float horizDeadZoneAng;
        public float vertDeadZoneAng;
        public final char stateIcon;

        private PlayerState(float jumpPower, float runPower, float horizDeadZoneAng, float vertDeadZoneAng, char stateIcon) {
            this.jumpPower = jumpPower;
            this.runPower = runPower;
            this.stateIcon = stateIcon;
            this.horizDeadZoneAng = horizDeadZoneAng;
            this.vertDeadZoneAng = vertDeadZoneAng;
            this.horizDeadZoneCos = MathUtils.cos(horizDeadZoneAng / 2.0f);
            this.vertDeadZoneCos = MathUtils.cos(vertDeadZoneAng / 2.0f);
        }
    }

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
        this.playerFixtureDef.density = 0.2f;
        this.playerFixtureDef.friction = 1.0f;
        // rv.playerFixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our body in the world using our body definition
        this.playerBody = world.createBody(this.playerBodyDef);
        world.setContactListener(this);

        // Create our fixture and attach it to the body
        this.playerFixture = this.playerBody.createFixture(this.playerFixtureDef);

        // TODO: Create a definition for limbs

        this.state = PlayerState.AIR_ACTIVE;
        this.contactBodies = new ArrayList<Body>();
    }

    public Body getPlayerBody() {
        return this.playerBody;
    }

    public void dispose() {
        this.playerCircleShape.dispose();
    }

    public void setState(PlayerState newState) {
        if (newState == this.state) {
            return;
        }

        switch (newState) {
            case FOOT_ACTIVE:
                //TODO: What happens when we are on-foot?
                this.getPlayerBody().setFixedRotation(true);
                this.getPlayerBody().setAngularVelocity(0.0f);
                this.getPlayerBody().setLinearVelocity(0.0f, 0.0f);
                break;
            case AIR_ACTIVE:
            case AIR_PUFF:
                this.getPlayerBody().setFixedRotation(false);
                break;
        }

        System.out.print("New state: " + newState.toString() + "\n");

        state = newState;
    }

    private void clampImpulse(Vector2 impulse) {
        if (this.state.horizDeadZoneCos > 0) {
            if (impulse.x > this.state.horizDeadZoneCos) {
                impulse.set(1.0f, 0.0f);
            }
            if (impulse.x < -this.state.horizDeadZoneCos) {
                impulse.set(-1.0f, 0.0f);
            }
        }
        if (this.state.vertDeadZoneCos > 0) {
            if (impulse.y > this.state.vertDeadZoneCos) {
                impulse.set(0.0f, 1.0f);
            }
            if (impulse.y < -this.state.vertDeadZoneCos) {
                impulse.set(0.0f, -1.0f);
            }
        }
    }

    public PlayerState getState() {
        return state;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Vector3 worldCoor = camera.unproject(new Vector3(x, y, 0.0f));
        Vector2 playerPos = this.playerBody.getPosition();
        Vector2 cursorVec = new Vector2(worldCoor.x - playerPos.x, worldCoor.y - playerPos.y);
        float curAng = MathUtils.atan2(cursorVec.y, cursorVec.x);
        Vector2 impulse = new Vector2(MathUtils.cos(curAng), MathUtils.sin(curAng));
        switch (this.state) {
            case AIR_ACTIVE:
            case AIR_PUFF:
                if (worldCoor.y > playerPos.y) { // Going upwards, puff up!
                    setState(PlayerState.AIR_PUFF);
                } else { // Dodging to the side or diving down
                    setState(PlayerState.AIR_ACTIVE);
                }
                break;
            case FOOT_ACTIVE:
                this.playerBody.setTransform(this.playerBody.getPosition(), (impulse.x > 0) ? 0.0f : MathUtils.PI);

                break;
            default:
                return false;
        }

        clampImpulse(impulse);

        this.playerBody.applyLinearImpulse(
                impulse.scl(this.state.jumpPower),
                this.playerBody.getPosition(),
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

    @Override
    public void beginContact(Contact contact) {
        Body contactBody = contact.getFixtureA().getBody();
        if (contactBody.getType() == BodyDef.BodyType.StaticBody) {
            contactBodies.add(contactBody);
            setState(PlayerState.FOOT_ACTIVE);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Body contactBody = contact.getFixtureA().getBody();
        if (contactBodies.remove(contactBody) && contactBodies.isEmpty()) {
            setState(PlayerState.AIR_ACTIVE);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
