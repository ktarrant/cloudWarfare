package com.ktarrant.cloudWarfare.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;

/**
 * Created by ktarrant1 on 12/27/15.
 */
public class PlayerManager implements GestureDetector.GestureListener, ContactListener, Disposable {
    public static final Vector2 DEFAULT_START_POS = new Vector2(0.0f, 10.0f);

    protected World world;
    protected Camera camera;
    protected Vector2 startPos;
    protected Array<Player> players;
    protected Player activePlayer;

    public PlayerManager(World world, Camera camera) {
        this.world = world;
        this.camera = camera;
        this.players = new Array<Player>();
        this.startPos = DEFAULT_START_POS;

        // Set us as the contact listener so we can catch player contacts
        world.setContactListener(this);
    }

    public Player addNewPlayer() {
        Player newPlayer = new Player();
        players.add(newPlayer);

        // First we create a body definition
        newPlayer.bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        newPlayer.bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our body's starting position in the world
        newPlayer.bodyDef.position.set(startPos);

        // Create a circle shape and set its radius to 6
        newPlayer.circleShape = new CircleShape();
        newPlayer.circleShape.setRadius(0.6f);

        // Create a fixture definition to apply our shape to
        newPlayer.fixtureDef = new FixtureDef();
        newPlayer.fixtureDef.shape = newPlayer.circleShape;
        newPlayer.fixtureDef.density = 0.2f;
        newPlayer.fixtureDef.friction = 1.0f;
        // rv.newPlayerFixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our body in the world using our body definition
        newPlayer.body = world.createBody(newPlayer.bodyDef);

        // Create our fixture and attach it to the body
        newPlayer.fixture = newPlayer.body.createFixture(newPlayer.fixtureDef);

        // Set up the player
        newPlayer.contactBodies = new ArrayList<Body>();
        newPlayer.setState(Player.PlayerState.AIR_ACTIVE);

        return newPlayer;
    }

    public void addMainPlayer() {
        Player mainPlayer = addNewPlayer();
        activePlayer = mainPlayer;
    }

    public void checkBounds(float mapMinX, float mapMinY, float mapMaxX, float mapMaxY) {
        for (Player player : players) {
            Vector2 pos = player.body.getPosition();
            if ((pos.x < mapMinX) || (pos.x > mapMaxX) ||
                    (pos.y < mapMinY) || (pos.y > mapMaxY)) {

                // Reset the player and put them back in the start position
                player.body.setAngularVelocity(0.0f);
                player.body.setLinearVelocity(0.0f, 0.0f);
                player.body.setTransform(startPos, 0.0f);
            }
        }
    }

    public void draw(PlayerRenderer renderer) {
        if (activePlayer != null) {
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin();
            renderer.drawPlayerControlHelp(activePlayer);
            renderer.end();
            renderer.drawEnvironmentData(activePlayer);
        }
    }

    public Vector2 getActivePosition() {
        if (activePlayer == null) {
            return null;
        } else {
            return activePlayer.body.getPosition();
        }
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (activePlayer == null) {
            return false;
        }

        Vector3 worldCoor = camera.unproject(new Vector3(x, y, 0.0f));
        Vector2 playerPos = activePlayer.body.getPosition();
        Vector2 cursorVec = new Vector2(worldCoor.x - playerPos.x, worldCoor.y - playerPos.y);
        float curAng = MathUtils.atan2(cursorVec.y, cursorVec.x);
        Vector2 impulse = new Vector2(MathUtils.cos(curAng), MathUtils.sin(curAng));
        switch (activePlayer.state) {
            case AIR_ACTIVE:
            case AIR_PUFF:
                if (worldCoor.y > playerPos.y) { // Going upwards, puff up!
                    activePlayer.setState(Player.PlayerState.AIR_PUFF);
                } else { // Dodging to the side or diving down
                    activePlayer.setState(Player.PlayerState.AIR_ACTIVE);
                }
                break;
            case FOOT_ACTIVE:
                activePlayer.body.setTransform(activePlayer.body.getPosition(),
                        (impulse.x > 0) ? 0.0f : MathUtils.PI);

                break;
            default:
                return false;
        }

        if (activePlayer.state.horizDeadZoneCos > 0) {
            if (impulse.x > activePlayer.state.horizDeadZoneCos) {
                impulse.set(1.0f, 0.0f);
            }
            if (impulse.x < -activePlayer.state.horizDeadZoneCos) {
                impulse.set(-1.0f, 0.0f);
            }
        }
        if (activePlayer.state.vertDeadZoneCos > 0) {
            if (impulse.y > activePlayer.state.vertDeadZoneCos) {
                impulse.set(0.0f, 1.0f);
            }
            if (impulse.y < -activePlayer.state.vertDeadZoneCos) {
                impulse.set(0.0f, -1.0f);
            }
        }

        activePlayer.body.applyLinearImpulse(
                impulse.scl(activePlayer.state.jumpPower),
                activePlayer.body.getPosition(),
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
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (fixA.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixB) {
                    player.contactBodies.add(fixA.getBody());
                    player.setState(Player.PlayerState.FOOT_ACTIVE);
                    return;
                }
            }
        } else if (fixB.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixA) {
                    player.contactBodies.add(fixA.getBody());
                    player.setState(Player.PlayerState.FOOT_ACTIVE);
                    return;
                }
            }
        } // else do nothing
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (fixA.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixB) {
                    player.contactBodies.remove(fixA.getBody());
                    if (player.contactBodies.isEmpty()) {
                        player.setState(Player.PlayerState.AIR_ACTIVE);
                    }
                    return;
                }
            }
        } else if (fixB.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixA) {
                    player.contactBodies.remove(fixB.getBody());
                    if (player.contactBodies.isEmpty()) {
                        player.setState(Player.PlayerState.AIR_ACTIVE);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public void dispose() {
        for (Player player : this.players) {
            if (player.circleShape != null) {
                player.circleShape.dispose();
            }
        }
    }
}
