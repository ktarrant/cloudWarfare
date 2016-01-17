package com.ktarrant.cloudWarfare.player;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
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
public class PlayerManager implements InputProcessor, ContactListener, Disposable {
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
    protected ActionDef activeActionDef;
    protected float timeSinceLastRepeat;
    protected Vector2 curScreenPos;
    protected ActionModifier curActionModifier;

    public PlayerManager(World world, Camera camera) {
        this.world = world;
        this.camera = camera;
        this.players = new Array<Player>();
        this.startPos = DEFAULT_START_POS;
        this.activePlayer = null;

        // Set us as the contact listener so we can catch player contacts
        world.setContactListener(this);

        this.actionQueue = new ArrayBlockingQueue<Action>(MAX_CONCURRENT_ACTIONS);
        actionDefLoader = new ActionDefLoader();
        actionDefLoader.load();

        // TODO: Make ActionModifier configurable
        updateModifier(ActionModifier.NORMAL);
    }

    protected void addActionToQueue(ActionModifier modifier, ActionDef actionDef) {
        Action action = new Action(
                activePlayer,
                modifier,
                actionDef,
                curScreenPos.nor()
        );
        actionQueue.add(action);
    }

    protected void updateModifier(ActionModifier modifier) {
        curActionModifier = modifier;
        if (activePlayer == null) {
            // Create empty array to use as placeholder
            curActionDefList = new Array<ActionDef>();
            return;
        } else {
            curActionModifier = modifier;
            curActionDefList = actionDefLoader.getActionDefList(
                    curActionModifier,
                    activePlayer.state);
        }
    }

    protected void updateActivePlayer(Player player) {
        activePlayer = player;
        if (activePlayer == null) {
            // Create empty array to use as placeholder
            curActionDefList = new Array<ActionDef>();
            return;
        } else {
            curActionDefList = actionDefLoader.getActionDefList(
                    curActionModifier,
                    activePlayer.state);
        }
    }

    protected void updateCursorChange(Vector2 cursorPos) {
        curScreenPos = cursorPos;
        if (curScreenPos == null) {
            activeActionDef = null;
            timeSinceLastRepeat = 0.0f;
            return;
        }

        if (activePlayer == null) {
            return;
        }

        for (ActionDef actionDef : curActionDefList) {
            if (actionDef.isInCaptureZone(curScreenPos)) {
                // Set this up to be repeated later
                activeActionDef = actionDef;
                break;
            }
        }
    }

    protected void addCurrentActionIfNecessary(float delta, boolean force) {
        // TODO: Use configurable ActionModifier
        if (activeActionDef == null) {
            return;
        }

        timeSinceLastRepeat += delta;
        if (force || (timeSinceLastRepeat > activeActionDef.repeatInterval)) {
            // If we are being forced to add a new Action, or it has been touched down
            // long enough for a repeat, then add a new Action.
            addActionToQueue(ActionModifier.NORMAL, activeActionDef);
            timeSinceLastRepeat = 0.0f;
        }
    }

    public void addMainPlayer() {
        Player player = PlayerFactory.createNewPlayer(world);
        player.body.setTransform(startPos, 0.0f);
        players.add(player);
        updateActivePlayer(player);
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
        Action action;

        addCurrentActionIfNecessary(delta, false);

        // First, perform all the Actions in the queue
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
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (fixA.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixB) {
                    player.contactBodies.add(fixA.getBody());
                    player.setState(PlayerState.FOOT_ACTIVE);
                    if (player == activePlayer) {
                        updateActivePlayer(player);
                    }
                    return;
                }
            }
        } else if (fixB.getBody().getType() == BodyDef.BodyType.StaticBody) {
            for (Player player : players) {
                if (player.fixture == fixA) {
                    player.contactBodies.add(fixA.getBody());
                    player.setState(PlayerState.FOOT_ACTIVE);
                    if (player == activePlayer) {
                        updateActivePlayer(player);
                    }
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
                        if (player == activePlayer) {
                            updateActivePlayer(player);
                        }
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
                        if (player == activePlayer) {
                            updateActivePlayer(player);
                        }
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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (activePlayer == null) {
            return false;
        }

        // Compute the angles and vectors of the tap relative to the character
        Vector3 worldCoor = camera.unproject(new Vector3(screenX, screenY, 0.0f));
        Vector2 playerPos = activePlayer.body.getPosition();
        updateCursorChange(new Vector2(worldCoor.x - playerPos.x, worldCoor.y - playerPos.y));

        // Force adding the action immediately when we touch down
        addCurrentActionIfNecessary(0.0f, true);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        curScreenPos = null;

        updateCursorChange(null);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Compute the angles and vectors of the tap relative to the character
        Vector3 worldCoor = camera.unproject(new Vector3(screenX, screenY, 0.0f));
        Vector2 playerPos = activePlayer.body.getPosition();
        updateCursorChange(new Vector2(worldCoor.x - playerPos.x, worldCoor.y - playerPos.y));

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
