package com.ktarrant.cloudWarfare.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.ktarrant.cloudWarfare.action.Action;
import com.ktarrant.cloudWarfare.action.ActionDef;
import com.ktarrant.cloudWarfare.action.ActionDefLoader;
import com.ktarrant.cloudWarfare.action.ActionModifier;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ktarrant1 on 12/27/15.
 */
public class PlayerManager implements GestureDetector.GestureListener, ContactListener, Disposable {
    public static final Vector2 DEFAULT_START_POS = new Vector2(0.0f, 10.0f);
    public static final int MAX_CONCURRENT_ACTIONS = 16;

    protected World world;
    protected Camera camera;
    protected Vector2 startPos;
    protected Array<Player> players;
    protected Player activePlayer;

    // TODO: Flush out Action Queue, List, Map
    protected Queue<Action> actionQueue;
    protected ActionDefLoader actionDefLoader;
    protected Array<ActionDef> curActionDefList;

    public PlayerManager(World world, Camera camera) {
        this.world = world;
        this.camera = camera;
        this.players = new Array<Player>();
        this.startPos = DEFAULT_START_POS;

        // Set us as the contact listener so we can catch player contacts
        world.setContactListener(this);

        this.actionQueue = new ArrayBlockingQueue<Action>(MAX_CONCURRENT_ACTIONS);
        actionDefLoader = new ActionDefLoader();
        actionDefLoader.load();
    }

    protected void updateActionDefList() {
        // TODO: Use configurable ActionModifier
        if (activePlayer != null) {
            curActionDefList = actionDefLoader.getActionDefList(
                    ActionModifier.NORMAL,
                    activePlayer.state);
        } else {
            // Create empty array to use as placeholder
            curActionDefList = new Array<ActionDef>();
        }

    }

    public void addMainPlayer() {
        activePlayer = PlayerFactory.createNewPlayer(world);
        activePlayer.body.setTransform(startPos, 0.0f);
        players.add(activePlayer);
        updateActionDefList();
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
            renderer.updateEnvironmentData("Stamina", activePlayer.getStamina());
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin();

            renderer.drawPlayerControlHelp(activePlayer, curActionDefList);
            renderer.end();
            renderer.drawEnvironmentData(activePlayer);
            renderer.drawPlayerState(activePlayer);
        }
    }

    public void update(float delta) {
        // First, perform all the Actions in the queue
        Action action;
        while (!actionQueue.isEmpty()) {
            action = actionQueue.poll();
            action.execute(delta);
        }

        // Then update all the players
        for (Player player : players) {
            player.update(delta);
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

        // Compute the angles and vectors of the tap relative to the character
        Vector3 worldCoor = camera.unproject(new Vector3(x, y, 0.0f));
        Vector2 playerPos = activePlayer.body.getPosition();
        Vector2 cursorVec = new Vector2(worldCoor.x - playerPos.x, worldCoor.y - playerPos.y);

        for (ActionDef actionDef : curActionDefList) {
            if (actionDef.isInCaptureZone(cursorVec)) {
                // There is an ActionDef for this Tap, create an Action from it
                Action action = new Action(
                        activePlayer,
                        ActionModifier.NORMAL,
                        actionDef,
                        cursorVec.nor()
                );

                // Add it to the ActionQueue
                actionQueue.add(action);
                break;
            }
        }
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
        System.out.println(String.format("Zoom: init = %f ; dist = %f", initialDistance, distance));
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
                    player.setState(PlayerState.FOOT_ACTIVE);
                    updateActionDefList();
                    return;
                }
            }
        } else if (fixB.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixA) {
                    player.contactBodies.add(fixA.getBody());
                    player.setState(PlayerState.FOOT_ACTIVE);
                    updateActionDefList();
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
                        player.setState(PlayerState.AIR_ACTIVE);
                        updateActionDefList();
                    }
                    return;
                }
            }
        } else if (fixB.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixA) {
                    player.contactBodies.remove(fixB.getBody());
                    if (player.contactBodies.isEmpty()) {
                        player.setState(PlayerState.AIR_ACTIVE);
                        updateActionDefList();
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
